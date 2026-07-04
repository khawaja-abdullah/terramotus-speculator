# Terramotus Speculator
A real‑time seismic monitoring system that ingests live earthquake data from the [EMSC Seismic Portal](https://www.seismicportal.eu) and broadcasts it to connected clients via Server‑Sent Events (SSE).

 ---
 
## Key Features
- **Live Event Streaming** - Maintains a persistent WebSocket connection to EMSC, broadcasting new seismic events to all connected clients in real time.
- **Automatic Data Recovery** - On startup or after any connection drop, the system backfills the gap between the last recorded event and the current time, ensuring zero data loss. The initial backfill covers the past 24 hours.
- **Resilient Ingestion Pipeline** - If the WebSocket connection fails, the backend:
  - Backfills missing data since the last persisted event.
  - Re‑establishes the WebSocket connection.
  - Resumes broadcasting and persisting new events.
- **Historical Query API** - All ingested events are stored in a database and exposed via a RESTful API, allowing clients to query historical seismic data.
- **Idempotent Persistence** - Events are upserted by their unique ID, preventing duplicates and ensuring data consistency across reconnections and backfills.

---

## Tech Stack

- **Java 21**
- **Maven 3+**
- **Quarkus v3.36.3**

### Quarkus Extensions

- `quarkus-arc` - Dependency injection
- `quarkus-rest` + `quarkus-rest-jackson` - REST API + SSE broadcast
- `quarkus-rest-client-jackson` - REST client for EMSC Query API
- `quarkus-hibernate-orm-panache` - ORM with `StatelessSession` for bulk operations
- `quarkus-jdbc-postgresql` - PostgreSQL driver
- `quarkus-websockets-next` - Outbound WebSocket client

### External Services

- **PostgreSQL** - Primary data store
- **[EMSC Seismic Portal](https://www.seismicportal.eu)** - Data source
  - [FDSN WS-EVENT API](https://www.seismicportal.eu/fdsn-wsevent.html) - Historical queries
  - [(Near) Realtime WebSocket](https://www.seismicportal.eu/realtime.html) - Live event stream
 
---

## System Architecture

```mermaid
flowchart TB
    subgraph EMSC["EMSC Seismic Portal"]
        WS["WebSocket\n/standing_order/websocket"]
        QAPI["REST Query API\n/fdsnws/event/1/query"]
    end

    subgraph Backend["Terramotus Backend · Quarkus 3 · Java 21"]
        subgraph conn["Connection Layer"]
            WSClient["SeismicPortalWebSocketClient\n@WebSocketClient"]
            Manager["SeismicPortalWebSocketConnectionManager\n@ApplicationScoped"]
        end
        subgraph svc["Service Layer"]
            Mapper["EarthquakeMapper\n@ApplicationScoped"]
            EqSvcImpl["EarthquakeServiceImpl\n@ApplicationScoped\n+ BroadcastProcessor"]
            RestClient["SeismicPortalRestClient\n@RegisterRestClient"]
        end
        subgraph data["Data & Output"]
            Repo["EarthquakeRepository\nPanacheRepositoryBase\n+ StatelessSession"]
            DB[("PostgreSQL\nearth quake")]
            Resource["EarthquakeResource\n@Path('/api/v1/earthquakes')"]
        end
    end

    Browser["Web Client"]

    WS      -- "wss:// · FeatureMessage"                     --> WSClient
    QAPI    -- "FeatureCollection"                            --> RestClient

    WSClient <-- "onOpen / onClose"                          --> Manager
    Manager  -- "executeBlocking(backfillGap)\non startup & reconnect" --> EqSvcImpl

    WSClient -- "mapFeatureToEarthquakeDto(feature)"         --> Mapper
    Mapper   -- "EarthquakeRecord"                            --> EqSvcImpl
    RestClient -- "getHistoricalEvents()"                     --> EqSvcImpl

    EqSvcImpl -- "mapEarthquakeDtoToEntity()\n+ StatelessSession.upsert()" --> Repo
    Repo      -- "UPSERT"                                     --> DB
    EqSvcImpl -- "BroadcastProcessor.onNext()"               --> Resource

    Resource  -- "SSE · GET /api/v1/earthquakes/subscribe"       --> Browser
    Resource  -- "JSON · GET /api/v1/earthquakes"                --> Browser
```

---

## Startup & Reconnect Lifecycle

```mermaid
sequenceDiagram
    actor Startup as StartupEvent
    participant Manager as SeismicPortalWebSocketConnectionManager
    participant EqSvc as EarthquakeServiceImpl
    participant RestClient as SeismicPortalRestClient
    participant DB as PostgreSQL
    participant WSClient as SeismicPortalWebSocketClient
    participant Portal as EMSC Seismic Portal

    rect
        Note over Startup, Portal: Application startup
        Startup ->> Manager: onStartup()
        Manager ->> Manager: shuttingDown = false

        Manager ->> EqSvc: vertx.executeBlocking(earthquakeService::backfillGap)
        EqSvc ->> DB: earthquakeRepository.getMaxTime()<br/>SELECT MAX(e.time) FROM EarthquakeEntity
        DB -->> EqSvc: LocalDateTime | null

        alt First boot — table empty
            EqSvc ->> EqSvc: from = now() − 24h
        else Restart after downtime
            EqSvc ->> EqSvc: from = maxTime
        end

        EqSvc ->> RestClient: getHistoricalEvents(from, to)
        RestClient ->> Portal: GET /fdsnws/event/1/query<br/>?format=json&start={from}&end={to}
        Portal -->> RestClient: FeatureCollection
        RestClient -->> EqSvc: List of EarthquakeRecord
        EqSvc ->> DB: StatelessSession.upsertMultiple(entities)
        DB -->> EqSvc: ok
        EqSvc -->> Manager: count (int)

        Manager ->> WSClient: connect(wss://...)
        WSClient ->> Portal: WebSocket handshake
        Portal -->> WSClient: connected
        WSClient -->> Manager: onOpen()
        Manager ->> Manager: store currentWebSocketClientConnection
    end

    rect
        Note over WSClient, Portal: Live streaming
        loop Each incoming event
            Portal ->> WSClient: @OnTextMessage FeatureMessage
            WSClient ->> WSClient: map → conditionally broadcast → upsert
        end
    end

    rect
        Note over Manager, Portal: Connection drop
        Portal --x WSClient: connection closed
        Note right of WSClient: @OnError just logs<br/>@OnClose is the sole reconnect trigger
        WSClient ->> Manager: onClose() → backfillAndConnect()
        Manager ->> Manager: shuttingDown? → false → proceed
        Manager ->> EqSvc: vertx.executeBlocking(earthquakeService::backfillGap)
        EqSvc ->> DB: getMaxTime()
        DB -->> EqSvc: timestamp
        EqSvc ->> RestClient: getHistoricalEvents(from, to)
        RestClient ->> Portal: GET /fdsnws/event/1/query?...
        Portal -->> RestClient: FeatureCollection
        RestClient -->> EqSvc: List of EarthquakeRecord
        EqSvc ->> DB: StatelessSession.upsertMultiple(entities)
        DB -->> EqSvc: ok
        EqSvc -->> Manager: count
        Manager ->> WSClient: connect(wss://...)
        WSClient ->> Portal: reconnect
        Portal -->> WSClient: connected
        Manager ->> Manager: store currentWebSocketClientConnection
    end

    rect
        Note over Startup, Manager: Clean shutdown
        Startup ->> Manager: onShutdown()
        Manager ->> Manager: shuttingDown = true
        Manager ->> Manager: currentWebSocketClientConnection.closeAndAwait()
        Manager ->> Manager: clear currentWebSocketClientConnection
        Note right of Manager: onClose fires but shuttingDown = true → return immediately
    end
```

---

## Event Journey

```mermaid
sequenceDiagram
    participant Portal as EMSC Seismic Portal WS
    participant WSClient as SeismicPortalWebSocketClient
    participant Mapper as EarthquakeMapper
    participant EqSvc as EarthquakeServiceImpl
    participant BP as BroadcastProcessor
    participant Repo as EarthquakeRepository
    participant DB as PostgreSQL
    participant Resource as EarthquakeResource
    participant Browser as Web Client

    Portal ->> WSClient: @OnTextMessage<br/>FeatureMessage { action, feature }

    WSClient ->> Mapper: mapFeatureToEarthquakeDto(feature)
    Note right of Mapper: Reads Feature.id() + Properties:<br/>lastUpdate, time, flynnRegion,<br/>latitude, longitude, depth, magnitude
    Mapper -->> WSClient: EarthquakeRecord

    rect
        Note over WSClient, Resource: Broadcast — create events only, time-gated
        alt action == "create" AND eventTime isAfter latestSeen
            WSClient ->> WSClient: latestSeen.set(currentEventTime)
            WSClient ->> EqSvc: broadcast(earthquakeRecord)
            EqSvc ->> BP: earthquakeRecordBroadcastProcessor.onNext()
            BP -->> Resource: emit EarthquakeRecord
            Resource -->> Browser: SSE · GET /api/v1/earthquakes/subscribe<br/>{ id, time, flynnRegion, lat, lon, depth, magnitude }
            Note right of Browser: EventSource.onmessage<br/>→ buildCard() → prepend to feed
        else action == "update" OR eventTime not after latestSeen
            Note over WSClient: Skip broadcast — still upsert below
        end
    end

    rect
        Note over WSClient, DB: Persist — always, regardless of action or time
        WSClient ->> EqSvc: upsert(earthquakeRecord)
        EqSvc ->> Mapper: mapEarthquakeDtoToEntity(earthquakeRecord)
        Note right of Mapper: Parses ISO strings to LocalDateTime<br/>via OffsetDateTime.parse().toLocalDateTime()
        Mapper -->> EqSvc: EarthquakeEntity
        EqSvc ->> Repo: upsert(earthquakeEntity)
        Repo ->> DB: StatelessSession.upsert(entity)<br/>INSERT ... ON CONFLICT (id) DO UPDATE
        DB -->> Repo: ok
    end
```

---

## Screenshots
<img width="1440" height="812" alt="dashboard-1" src="https://github.com/user-attachments/assets/8c1d6e26-a0cc-42ca-8f44-ccd66ae8bc07" />

<img width="1433" height="768" alt="dashboard-2" src="https://github.com/user-attachments/assets/9d1a8736-1845-4e3b-a6bd-abeb71077b3d" />

