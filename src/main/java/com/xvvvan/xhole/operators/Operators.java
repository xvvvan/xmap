package com.xvvvan.xhole.operators;

import com.xvvvan.xhole.ExcludeMatchers;
import com.xvvvan.xhole.Tuple;
import com.xvvvan.xhole.matchers.ConditionType;
import com.xvvvan.xhole.matchers.Matcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xvvvan.xhole.matchers.ConditionType.ANDCondition;
import static com.xvvvan.xhole.matchers.ConditionType.ORCondition;


public class Operators {
    // Matchers contains the detection mechanism for the request to identify
    // whether the request was successful by doing pattern matching
    // on request/responses.
    // Multiple matchers can be combined with `matcher-condition` flag
    // which accepts either `and` or `or` as argument.
    public List<Matcher> matchers;

    // Extractors contains the extraction mechanism for the request to identify
// and extract parts of the response.
    // MatchersCondition is the condition between the matchers. Default is OR.
    public String matchers_condition;

    // cached variables that may be used along with request.
    public ConditionType matchersConditionType;

    // TemplateID is the ID of the template for matcher
    public String templateID;

    // ExcludeMatchers is a list of excludeMatchers items
    public ExcludeMatchers excludeMatchers;

    public Operators(){
        this.matchers = new ArrayList<>();
        this.matchers_condition="or";
        this.templateID = "";

    }

    public static void makeDynamicValuesCallback(HashMap<String, List<String>> input, boolean iterate_all,Callback callback) {

        Map<String, Object> output = new HashMap<>(input.size());

        if (!iterate_all) {
            for (Map.Entry<String, List<String>> entry : input.entrySet()) {
                List<String> v = entry.getValue();
                if (!v.isEmpty()) {
                    output.put(entry.getKey(), v.get(0));
                }
            }
            callback.call(output);
            return;
        }

        Map<String, Integer> inputIndex = new HashMap<>(input.size());

        int maxValue = 0;
        for (List<String> v : input.values()) {
            if (v.size() > maxValue) {
                maxValue = v.size();
            }
        }

        for (int i = 0; i < maxValue; i++) {
            for (Map.Entry<String, List<String>> entry : input.entrySet()) {
                List<String> v = entry.getValue();
                if (v.isEmpty()) {
                    continue;
                }
                if (v.size() == 1) {
                    output.put(entry.getKey(),v.get(0));
                    continue;
                }
                Integer gotIndex = inputIndex.get(entry.getKey());
                if (gotIndex == null) {
                    gotIndex = 0;
                    inputIndex.put(entry.getKey(), 0);
                    output.put(entry.getKey(), v.get(0));
                } else {
                    int newIndex = gotIndex + 1;
                    if (newIndex >= v.size()) {
                        output.put(entry.getKey(), v.get(v.size()-1));
                        continue;
                    }
                    output.put(entry.getKey(), v.get(newIndex));
                    inputIndex.put(entry.getKey(), newIndex);
                }
            }
            if (callback.call(output)) {
                return;
            }
        }
    }
    public interface Callback{
        boolean call(Map<String,Object> map);
    }

    // Compile compiles the operators as well as their corresponding matchers and extractors
    public void compile() throws Exception {
        // Set a default matchers condition type if one isn't specified
        if (this.matchers_condition != null) {
            this.matchersConditionType = ConditionType.fromValue(this.matchers_condition);
        } else {
            this.matchersConditionType = ORCondition;
        }

        // Compile each matcher
        if(this.matchers!=null){
            for (Matcher matcher : this.matchers) {
                matcher.compileMatchers();
            }
        }

    }

    //    public interface MatchFunc { boolean match(Map<String, Object> data, Matcher matcher); }
    @FunctionalInterface
    public interface MatchFunction <A, B, R> {
        //R is like Return, but doesn't have to be last in the list nor named R.
        public R apply (A a, B b);
    }
    //    还未补充内容详情
    // MatchFunc performs matching operation for a matcher on model and returns true or false.

    /**
     Execute executes the operators on data and returns a result structure.
     @return The result of executing the operators.
      * @param data The data to execute the operators on.
     * @param match A matching function.
     * @param isDebug Whether debugging is enabled.*/

    public Tuple<Result, Boolean> execute(Map<String, Object> data, MatchFunction<Map<String,Object>, Matcher, Tuple<Boolean,List<String>>> match, boolean isDebug) {
         // Get the matcher's condition.
        ConditionType matcherCondition = getMatchersConditionType();
        // Initialize the result object.
        Result result = new Result();
        boolean matches = false;

        // Evaluate the matchers.
         for (int matcherIndex = 0; matcherIndex < this.getMatchers().size(); matcherIndex++) {
             // Get the matcher. matcher.
                 Matcher matcher = this.getMatchers().get(matcherIndex);
            // Skip matchers that are in the blocklist.
             if (getExcludeMatchers() != null) {
                 if (getExcludeMatchers().match(getTemplateID(), matcher.getName())) {
                    continue;
                 }
             }
                 // Evaluate the matcher.
             Tuple<Boolean, List<String>> matchResult = match.apply(data, matcher);
             if (matchResult.getFirst()) {
                 if (isDebug) {
                     String matcherName = matcher.name;
                     result.getMatches().put(matcherName, match.apply(data, matcher).getSecond());
                 } else {
//                     ConditionType.fromValue(matcherCondition)==ORCondition &&
                     if (matcher.getName()!=null) {
                        result.getMatches().put(matcher.getName(), matchResult.getSecond());
                     }
                 }
                 matches = true;

//                 ConditionType.fromValue(matcherCondition)
//
             } else if (matchersConditionType==ANDCondition) {
                 if (result.getDynamicValues()!=null) {

                     return new Tuple<>(result,true);
                 }

                     return new Tuple<>(null,false);
                 }
         }

        // Set the result properties.
        result.setMatched(matches);

         if(result.getOutputExtracts()!=null){
             result.setExtracted(!result.getOutputExtracts().isEmpty());
         }


        if (result.getDynamicValues()!=null) {
            return new Tuple<>(result,true);
        }

        // Don't print if we have matchers, and they have not matched, regardless of extractor.
         if (!getMatchers().isEmpty() && !matches) {
             return new Tuple<>(null,false);
         }

         if(matches){
             return new Tuple<>(result,true);
         }

        return new Tuple<>(null,false);
    }

    public List<Matcher> getMatchers() {
        return matchers;
    }

    public void setMatchers(List<Matcher> matchers) {
        this.matchers = matchers;
    }

    public String getMatchersCondition() {
        return matchers_condition;
    }

    public void setMatchersCondition(String matchersCondition) {
        this.matchers_condition = matchersCondition;
    }

    public ConditionType getMatchersConditionType() {
        return matchersConditionType;
    }

    public void setMatchersConditionType(ConditionType matchersConditionType) {
        this.matchersConditionType = matchersConditionType;
    }
    public String getTemplateID() {
        return templateID;
    }

    public void setTemplateID(String templateID) {
        this.templateID = templateID;
    }

    public ExcludeMatchers getExcludeMatchers() {
        return excludeMatchers;
    }

    public void setExcludeMatchers(ExcludeMatchers excludeMatchers) {
        this.excludeMatchers = excludeMatchers;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Operators {");

        if (matchers != null) {
            sb.append("matchers=");
            sb.append(this.matchers.toString());
            sb.append(", ");
        }

        if (matchers_condition != null) {
            sb.append("matchers_condition='");
            sb.append(this.matchers_condition);
            sb.append("', ");
        }

        String str = sb.toString();
        // If the last characters are ", ", remove them.
        if (str.endsWith(", ")) {
            str = str.substring(0, str.length() - 2);
        }
        str += "}";

        return str;
    }
}
