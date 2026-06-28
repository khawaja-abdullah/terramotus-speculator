package io.github.khawajaabdullah.util;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public final class Constant {

  public static final String SEISMIC_PORTAL_API_RESPONSE_FORMAT_JSON = "json";
  public static final DateTimeFormatter ISO_ZULU_LOCAL_DATE_TIME = new DateTimeFormatterBuilder()
      .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
      .appendLiteral('Z')
      .toFormatter();
  public static final String FEATURE_MESSAGE_ACTION_CREATE = "create";

  private Constant() {
  }

}
