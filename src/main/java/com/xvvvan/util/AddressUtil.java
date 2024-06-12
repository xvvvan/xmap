package com.xvvvan.util;


import java.util.regex.Pattern;

public class AddressUtil {
    private static final String DOMAIN_REGEX = "^(?:[a-zA-Z0-9]+(?:\\-*[a-zA-Z0-9])*)(?:\\.[a-zA-Z0-9]+(?:\\-*[a-zA-Z0-9])*)*(?:\\.[a-zA-Z]{2,})$";
    private static final String IP_REGEX = "^(\\d{1,3}\\.){3}\\d{1,3}$";;
    private static final Pattern DOMAIN = Pattern.compile(DOMAIN_REGEX);
    private static final Pattern IP = Pattern.compile(IP_REGEX);

    public static boolean isIPAddress(String input) {
        return IP.matcher(input).matches();

    }

    public static String convertIPToC(String ip) {
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return ip;
        }

        return parts[0] + "." + parts[1] + "." + parts[2] + ".0/24";
    }
    public static String extractRootDomain(String domain) {
        if(domain.startsWith("*.")){
            domain = domain.substring(2);
        }

        if(!DOMAIN.matcher(domain).matches()){
            return "";
        }


        String[] parts = domain.split("\\.");
        int numParts = parts.length;

        if (numParts == 1) {
            return "";
        } else if (numParts == 2) {
            if(isDomainSuffix(parts[1])){
                return domain;
            }

        }else if (numParts > 2) {
            if (parts[0].equalsIgnoreCase("www")) {
                return parts[1]+"."+parts[2];
            } else if (isDomainSuffix(parts[numParts - 2])) {
                return parts[numParts - 3] + "." + parts[numParts - 2] + "." + parts[numParts - 1];
            }else {
                return parts[numParts - 2] + "." + parts[numParts - 1];
            }
        }

        return "";
    }
    private static boolean isDomainSuffix(String part) {
        // 假设这里只检查了一些常见的域名后缀，你可以根据实际需求进行扩展
        String[] domainSuffixes = {
                "com", "cn", "net", "org", "gov", "edu", "mil", "ac",
                "co", "me", "top", "vip", "shop", "club", "online", "tech",
                "store", "wang", "site", "xyz", "link"
        };

        for (String suffix : domainSuffixes) {
            if (part.equalsIgnoreCase(suffix)) {
                return true;
            }
        }

        return false;
    }

    public static void main(String[] args) {

        String[] domains = {"baidu.com.cn", "www.baidu.com", "aa", "baidu.com","http://asdasd.asdasdas.dzxczxc.com.cn"};

        for (String domain : domains) {
            String mainDomain = extractRootDomain(domain);
            System.out.println("输入: " + domain + "，主域名: " + mainDomain);

        }
    }
}
