package com.xvvvan.xhole.matchers;

// Part is the part of the request to match
public enum Part {
    // BodyPart matches body of the response.
    BodyPart{
        public String getPartType(){
            return "body";
        }
        // HeaderPart matches headers of the response.
    },HeaderPart{
        public String getPartType(){
            return "header";
        }
        // AllPart matches both response body and headers of the response.
    },AllPart{
        public String getPartType(){
            return "all";
        }
    };
    public abstract String getPartType();
}
