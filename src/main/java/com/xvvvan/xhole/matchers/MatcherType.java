package com.xvvvan.xhole.matchers;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// MatcherType is the type of the matcher specified
public enum MatcherType {
    WordsMatcher("word"),
    RegexMatcher("regex"),
    BinaryMatcher("binary"),
    StatusMatcher("status"),
    SizeMatcher("size"),
    DSLMatcher("dsl");

    private final String value;

    MatcherType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
    public static final Map<MatcherType, String> conversionTable = new HashMap<MatcherType, String>(){
        {
            put(MatcherType.StatusMatcher, "status");
            put(MatcherType.SizeMatcher, "size");
            put(MatcherType.WordsMatcher, "word");
            put(MatcherType.RegexMatcher, "regex");
            put(MatcherType.BinaryMatcher, "binary");
            put(MatcherType.DSLMatcher, "dsl");
        }
    };
    // GetSupportedMatcherTypes returns list of supported types
    public List<MatcherType> getSupportedMatcherTypes() {
        List<MatcherType> result = new ArrayList<>();
        for (MatcherType type : MatcherType.values()) {
            result.add(type);
        }
        return result;
    }
    public MatcherType getMatcherType(){
        return this;
    }
    public static MatcherType fromValue(String value) {
        switch (value) {
            case "status":
                return StatusMatcher;
            case "size":
                return SizeMatcher;
            case "word":
                return WordsMatcher;
            case "regex":
                return RegexMatcher;
            case "binary":
                return BinaryMatcher;
            case "dsl":
                return DSLMatcher;
            default:
                return null;
        }
    }


}