package com.xvvvan.xhole.operators;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Result {
    // Result is a result structure created from operators running on data. public class Result { // Matched is true if any matchers matched  boolean matched;
    public boolean matched;
    
    // Extracted is true if any result type values were extracted
    public boolean extracted;

    // Matches is a map of matcher names that we matched
    public Map<String, List<String>> matches;

    // Extracts contains all the data extracted from inputs
    public Map<String, List<String>> extracts;

    // OutputExtracts is the list of extracts to be displayed on screen.
    public List<String> outputExtracts;
    public Set<String> outputUnique;

    // DynamicValues contains any dynamic values to be templated
    public Map<String, List<String>> dynamicValues;

    // PayloadValues contains payload values provided by user. (Optional)
    public Map<String, Object> payloadValues;

    // Optional lineCounts for file protocol
    public String lineCount;

    public Result() {
        this.matches = new HashMap<>();
//        this.extracts = new HashMap<>();
//        this.outputUnique = new HashSet<>();
//        this.outputExtracts = new ArrayList<>();
//        this.outputUnique = new HashSet<>();
//        this.dynamicValues = new HashMap<>();
//        this.payloadValues = new HashMap<>();


    }

    public boolean hasMatch(String name) {
        return hasItem(name, matches);
    }

    public boolean hasExtract(String name) {
        return hasItem(name, extracts);
    }

     boolean hasItem(String name, Map<String, List<String>> items) {
        for (String itemName : items.keySet()) {
            if (name.equalsIgnoreCase(itemName)) {
                return true;
            }
        }
        return false;
    }

    //替换动态参数的
    public static void makeDynamicValuesCallback(Map<String, List<String>> input, boolean iterateAllValues, Function<Map<String, Object>, Boolean> callback) {
        Map<String, Object> output = new HashMap<>(input.size());

        if (!iterateAllValues) {
            for (String key : input.keySet()) {
                List<String> values = input.get(key);
                if (values != null && !values.isEmpty()) {
                    output.put(key, values.get(0));
                }
            }
            callback.apply(output);
            return;
        }
        Map<String, Integer> inputIndex = new HashMap<>(input.size());
        int maxValue = 0;
        for (List<String> values : input.values()) {
            if (values.size() > maxValue) {
                maxValue = values.size();
            }
        }
        for (int i = 0; i < maxValue; i++) {
            for (String key : input.keySet()) {
                List<String> values = input.get(key);
                if (values == null || values.isEmpty()) {
                    continue;
                }
                if (values.size() == 1) {
                    output.put(key, values.get(0));
                    continue;
                }
                Integer gotIndex = inputIndex.getOrDefault(key, 0);
                if (!inputIndex.containsKey(key)) {
                    inputIndex.put(key, 0);
                    output.put(key, values.get(0));
                } else {
                    int newIndex = gotIndex + 1;
                    if (newIndex >= values.size()) {
                        output.put(key, values.get(values.size() - 1));
                        continue;
                    }
                    output.put(key, values.get(newIndex));
                    inputIndex.put(key, newIndex);
                }
            }
            // skip if the callback says so
            if (callback.apply(output)) {
                return;
            }
        }

    }
//    public void merge(Result result) {
//        if (!matched && result.matched) {
//            matched = result.matched;
//        }
//        if (!extracted && result.extracted) {
//            extracted = result.extracted;
//        }
//        if (matches == null) {
//            matches = new HashMap<>(result.matches);
//        } else {
//            for (String key : result.matches.keySet()) {
//                List<String> values = matches.getOrDefault(key, new ArrayList<>());
//                values.addAll(result.matches.get(key));
//                matches.put(key, SliceUtil.dedupe(values));
//            }
//        }
//        if (extracts == null) {
//            extracts = new HashMap<>(result.extracts);
//        } else {
//            for (String key : result.extracts.keySet()) {
//                List<String> values = extracts.getOrDefault(key, new ArrayList<>());
//                values.addAll(result.extracts.get(key));
//                extracts.put(key, SliceUtil.dedupe(values));
//            }
//        }
//        outputUnique = new HashSet<>();
//        if (outputExtracts == null) {
//            outputExtracts = new ArrayList<>(result.outputExtracts);
//        } else {
//            for (String value : outputExtracts) {
//                if (!outputUnique.contains(value)) {
//                    outputUnique.add(value);
//                }
//            }
//            for (String value : result.outputExtracts) {
//                if (!outputUnique.contains(value)) {
//                    outputExtracts.add(value);
//                    outputUnique.add(value);
//                }
//            }
//        }
//
//        if (dynamicValues == null) {
//            dynamicValues = new HashMap<>(result.dynamicValues);
//        } else {
//            for (String key : result.dynamicValues.keySet()) {
//                List<String> values = dynamicValues.getOrDefault(key, new ArrayList<>());
//                values.addAll(result.dynamicValues.get(key));
//                dynamicValues.put(key, SliceUtil.dedupe(values));
//            }
//        }
//
//        if (payloadValues == null) {
//            payloadValues = new HashMap<>(result.payloadValues);
//        } else {
//            payloadValues.putAll(result.payloadValues);
//        }
//
//        if (lineCount == null) {
//            lineCount = result.lineCount;
//        }
//    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    public boolean isExtracted() {
        return extracted;
    }

    public void setExtracted(boolean extracted) {
        this.extracted = extracted;
    }

    public Map<String, List<String>> getMatches() {
        return matches;
    }

    public void setMatches(Map<String, List<String>> matches) {
        this.matches = matches;
    }

    public Map<String, List<String>> getExtracts() {
        return extracts;
    }

    public void setExtracts(Map<String, List<String>> extracts) {
        this.extracts = extracts;
    }

    public List<String> getOutputExtracts() {
        return outputExtracts;
    }

    public void setOutputExtracts(List<String> outputExtracts) {
        this.outputExtracts = outputExtracts;
    }

    public Set<String> getOutputUnique() {
        return outputUnique;
    }

    public void setOutputUnique(Set<String> outputUnique) {
        this.outputUnique = outputUnique;
    }

    public Map<String, List<String>> getDynamicValues() {
        return dynamicValues;
    }

    public void setDynamicValues(Map<String, List<String>> dynamicValues) {
        this.dynamicValues = dynamicValues;
    }

    public Map<String, Object> getPayloadValues() {
        return payloadValues;
    }

    public void setPayloadValues(Map<String, Object> payloadValues) {
        this.payloadValues = payloadValues;
    }

    public String getLineCount() {
        return lineCount;
    }

    public void setLineCount(String lineCount) {
        this.lineCount = lineCount;
    }

    @Override
    public String toString() {
        String match = matches.entrySet()
                .stream()
                .flatMap(entry -> entry.getValue().stream().map(v -> entry.getKey() + "->" + v))
                .collect(Collectors.joining(", "));
        String extract = extracts.entrySet()
                .stream()
                .flatMap(entry -> entry.getValue().stream().map(v -> entry.getKey() + "->" + v))
                .collect(Collectors.joining(", "));
        return "Result{" +
                "matched=" + matched +
//                ", extracted=" + extracted +
                ", matches=" + match +
                ", extracts=" + extract +
                '}';
    }
}
