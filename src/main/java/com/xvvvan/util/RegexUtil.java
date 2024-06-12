package com.xvvvan.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {
    public static String compileMatch(String regex, String text){
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(text);
        if(matcher.find()){
            return matcher.group(1);
        }
        return "Error!";
    }
}
