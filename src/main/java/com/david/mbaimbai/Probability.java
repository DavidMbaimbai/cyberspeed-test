package com.david.mbaimbai;

import java.util.Map;

public class Probability {
    private Map<String, Integer> standardSymbols;
    private Map<String, Integer> bonusSymbols;

    public Probability(Map<String, Integer> standardSymbols, Map<String, Integer> bonusSymbols) {
        this.standardSymbols = standardSymbols;
        this.bonusSymbols = bonusSymbols;
    }

    public Map<String, Integer> getStandardSymbols() {
        return standardSymbols;
    }


    public void setStandardSymbols(Map<String, Integer> standardSymbols) {
        this.standardSymbols = standardSymbols;
    }

    public Map<String, Integer> getBonusSymbols() {
        return bonusSymbols;
    }

    public void setBonusSymbols(Map<String, Integer> bonusSymbols) {
        this.bonusSymbols = bonusSymbols;
    }

    @Override
    public String toString() {
        return "Probability{" +
                "standardSymbols=" + standardSymbols +
                ", bonusSymbols=" + bonusSymbols +
                '}';
    }

    public Map<String, WinCombination> getWinCombinations() {
        return getWinCombinations();
    }

}

