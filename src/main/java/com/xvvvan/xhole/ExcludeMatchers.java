package com.xvvvan.xhole;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcludeMatchers {
    private Map<String, String> values = new HashMap<>();
    private Map<String, String> templateIDs = new HashMap<>();
    private Map<String, String> matcherNames = new HashMap<>();

    public ExcludeMatchers(List<String> values) {
        for (String value : values) {
            String[] partValues = value.split(":", 2);
            if (partValues.length < 2) {
                if (!this.templateIDs.containsKey(value)) {
                    this.templateIDs.put(value, "");
                }
                continue;
            }
            String templateID = partValues[0], matcherName = partValues[1];

            // Handle wildcards
            if (templateID.equals("*")) {
                if (!this.matcherNames.containsKey(matcherName)) {
                    this.matcherNames.put(matcherName, "");
                }
            } else if (matcherName.equals("*")) {
                if (!this.templateIDs.containsKey(templateID)) {
                    this.templateIDs.put(templateID, "");
                }
            } else {
                String matchName = String.join(":", templateID, matcherName);
                if (!this.values.containsKey(matchName)) {
                    this.values.put(matchName, "");
                }
            }
        }
    }

    public boolean match(String templateID, String matcherName) {
        if (this.templateIDs.containsKey(templateID)) {
            return true;
        }
        if (this.matcherNames.containsKey(matcherName)) {
            return true;
        }
        String matchName = String.join(":", templateID, matcherName);
        return this.values.containsKey(matchName);
    }
}
