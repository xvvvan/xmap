package com.xvvvan.xhole.expression;

import com.googlecode.aviator.AviatorEvaluator;
import com.xvvvan.xhole.Types;
import com.xvvvan.xhole.ui.util.Replacer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Expressions {

    public static final Pattern numericalExpressionRegex = Pattern.compile("^[0-9+\\-/\\W]+$");
    public static final Pattern unresolvedVariablesRegex = Pattern.compile("(?:%7[B|b]|\\{){2}([^}]+)(?:%7[D|d]|\\}){2}[\"'\\)\\}]*");
    private static final int MAX_ITERATIONS = 250;

    public static String evaluate(String data, Map<String, Object> base) throws IllegalArgumentException {
        data = Replacer.replace(data, base);
        List<String> expressions = findExpressions(data, Marker.ParenthesisOpen, Marker.ParenthesisClose, base);
        for (String expression : expressions) {
            expression = Replacer.replace(expression, base);
            try {
                Object execute = AviatorEvaluator.execute(expression);
                String result = Types.toString(execute);
                data = Replacer.replaceOne(data, expression, result);
            } catch (RuntimeException e) {

            }
        }
        return data;
    }
    private static List<String> findExpressions(String data, String openMarker, String closeMarker, Map<String, Object> base) {
        List<String> expressions = new ArrayList<>();
        int iterations = 0;
        while (true) { // 已达到最大迭代次数则退出
             if (iterations > MAX_ITERATIONS) {
                 break;
             }
             iterations++;
             int indexOpenMarker = data.indexOf(openMarker);
             if (indexOpenMarker < 0) {
                 break;
             }
        int indexOpenMarkerOffset = indexOpenMarker + openMarker.length();

        boolean shouldSearchCloseMarker = true;
        boolean closeMarkerFound = false;
        String innerData = data;
        String potentialMatch;
        int indexCloseMarker;
        int indexCloseMarkerOffset = 0;
        int skip = indexOpenMarkerOffset;
        while (shouldSearchCloseMarker) {
            indexCloseMarker = indexAt(innerData, closeMarker, skip);
            if (indexCloseMarker < 0) {
                shouldSearchCloseMarker = false;
                continue;
            }
            indexCloseMarkerOffset = indexCloseMarker + closeMarker.length();
            potentialMatch = innerData.substring(indexOpenMarkerOffset, indexCloseMarker);

            // 判断是否为表达式
            if (isExpression(potentialMatch, base)) {
                closeMarkerFound = true;
                shouldSearchCloseMarker = false;
                expressions.add(potentialMatch);
            } else {
                skip = indexCloseMarkerOffset;
            }
        }

        if (closeMarkerFound) {
            data = data.substring(indexCloseMarkerOffset);
        } else {
            data = data.substring(indexOpenMarkerOffset);
        }
    }
        return expressions;
    }
    private static boolean isExpression(String data, Map<String, Object> base) {

        try {
            boolean b = newEvaluableExpression(data);
            if(b){
                if(getFunctionsNames(base).contains(data)){
                    return true;
                }
                return false;
            }

        } catch (Exception ignored) {

        }
        try {
            AviatorEvaluator.compile(data,true);
            return true;
        }catch (Exception ignored) {
            return false;
        }


    }
    private static List<String> getFunctionsNames(Map<String, Object> m) { return new ArrayList<>(m.keySet()); }


    // Returns an error with variable names if the passed input contains unresolved {{<pattern-here>}} variables.
    public static Exception containsUnresolvedVariables(String... items) {
        for (String data : items) {
            final Matcher matcher = unresolvedVariablesRegex.matcher(data);
            if (!matcher.find()) {
                return null;
            }
            final List<String> unresolvedVariables = new ArrayList<>();
            do {
                final String match = matcher.group(1);
                // Skip if the match is an expression
                if (numericalExpressionRegex.matcher(match).find()) {
                    continue;
                }
                // or if it contains only literals (can be solved from expression engine)
                if (hasLiteralsOnly(match)) {
                    continue;
                }
                unresolvedVariables.add(match);
            } while (matcher.find());
            if (!unresolvedVariables.isEmpty()) {
                return new Exception("Unresolved variables found: " + String.join(", ", unresolvedVariables));
            }
        }

        return null;
    }
    // Returns an error with variable names if the passed input contains unresolved {{<pattern-here>}} variables within the provided list
    public static Exception containsVariablesWithNames(Map<String, Object> names, String... items) {
        for (String data : items) {
            final Matcher matcher = unresolvedVariablesRegex.matcher(data);
            if (!matcher.find()) {
                return null;
            }
            final List<String> unresolvedVariables = new ArrayList<>();
            do {
                final String match = matcher.group(1);
                // Skip if the match is an expression
                if (numericalExpressionRegex.matcher(match).find()) {
                    continue;
                }
                // or if it contains only literals (can be solved from expression engine)
                if (hasLiteralsOnly(match)) {
                    continue;
                }
                if (!names.containsKey(match)) {
                    unresolvedVariables.add(match);
                }
            } while (matcher.find());
            if (!unresolvedVariables.isEmpty()) {
                return new Exception("Unresolved variables with values found: " + String.join(", ", unresolvedVariables));
            }
        }

        return null;
    }
    // Returns an error with variable names if the passed input contains unresolved {{<pattern-here>}} other than the ones listed in the ignore list
    public static Exception containsVariablesWithIgnoreList(Map<String, Object> skipNames, String... items) {
        final List<String> unresolvedVariables = new ArrayList<>();
        for (String data : items) {
            final Matcher matcher = unresolvedVariablesRegex.matcher(data);
            while (matcher.find()) {
                final String match = matcher.group(1);
                // Skip if the match is an expression
                if (numericalExpressionRegex.matcher(match).find()) {
                    continue;
                }
                // or if it contains only literals (can be solved from expression engine)
                if (hasLiteralsOnly(match)) {
                    continue;
                }
                if (skipNames.containsKey(match)) {
                    continue;
                }
                unresolvedVariables.add(match);
            }
        }

        if (!unresolvedVariables.isEmpty()) {
            return new Exception("Unresolved variables with values found: " + String.join(", ", unresolvedVariables));
        }

        return null;
    }
    public static boolean hasLiteralsOnly(String data) {
//        try {
//            final Expression expression = HelperFunctions.getDefaultInstance().buildExpression(data);
//            final Object res = expression.evaluate();
//            return res == null || res instanceof String || res instanceof Boolean;
//        } catch (ExpressionFormatException | EvaluationException ignored) {
//            return false;
//        }
        return false;
    }
    public static int indexAt(String s, String sep, int n) {
        int idx = s.indexOf(sep, n);
        return idx < 0 ? -1 : idx;
    }
    public static boolean newEvaluableExpression(String data){
        try {
            AviatorEvaluator.compile(data,false);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
