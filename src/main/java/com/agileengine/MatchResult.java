package com.agileengine;

import java.util.ArrayList;
import java.util.List;

public class MatchResult {

    private int score;
    private List<String> matchedAttributes = new ArrayList<>();

    public MatchResult(String matchedAttribute) {
        this.score = 1;
        matchedAttributes.add(matchedAttribute);
    }

    public MatchResult() {
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void incrementScore() {
        this.score = this.score++;
    }

    public List<String> getMatchedAttributes() {
        return this.matchedAttributes;
    }

    public void setMatchedAttributes(List<String> matchedAttributes) {
        this.matchedAttributes = matchedAttributes;
    }
}
