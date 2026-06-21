package io.github.khawajaabdullah.dto;

public record Properties(double mag, String place, long time, long updated, String tz, String url, String detail,
                         int felt, double cdi, double mmi, String alert, String status, int tsunami, int sig,
                         String net, String code, String ids, String sources, String types, int nst, double dmin,
                         double rms, int gap, String magType, String type, String title) {
}
