package com.xvvvan.xhole.matchers;

import java.util.List;

public class MatchWordsResult {
    boolean success;
    List<String> words;

    public MatchWordsResult() {
    }

    public MatchWordsResult(boolean success, List<String> words) {
        this.success = success;
        this.words = words;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }
}
