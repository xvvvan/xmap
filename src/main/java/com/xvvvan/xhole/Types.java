package com.xvvvan.xhole;

import java.math.BigDecimal;

public class Types {
    public static String proxyURL;
    // ProxySocksURL is the URL for the proxy socks server
    public static String proxySocksURL;
//    public static final String HTTP_PROXY_ENV = "HTTP_PROXY";
    static {
        proxySocksURL = "";
        proxyURL="";
    }
    public static String toString(Object data) {
        if (data == null) {
            return "";
        } else if (data instanceof String) {
            return (String) data;
        } else if (data instanceof Boolean) {
            return Boolean.toString((Boolean) data);
        } else if (data instanceof Float || data instanceof Double) {
            return BigDecimal.valueOf(((Number) data).doubleValue()).stripTrailingZeros().toPlainString();
        } else if (data instanceof Integer || data instanceof Long || data instanceof Short || data instanceof Byte) {
            return String.valueOf(((Number) data).intValue());
        } else if (data instanceof Character) {
            return String.valueOf(data);
        } else if (data instanceof byte[]) {
            return new String((byte[]) data);
//        } else if (data instanceof severity.Holder || data instanceof Severity) {
//            return data.toString();
        } else if (data instanceof Error || data instanceof Exception) {
            return ((Throwable) data).getMessage();
        } else {
            return String.valueOf(data);
        }
    }
}
