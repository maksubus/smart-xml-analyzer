package com.agileengine;

import java.util.Comparator;

public class MatchResultComparator implements Comparator<MatchResult> {

    @Override
    public int compare(MatchResult o1, MatchResult o2) {
        return o1.getScore() - o2.getScore();
    }
}
