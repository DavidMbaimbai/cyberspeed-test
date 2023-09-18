package com.david.mbaimbai;

import java.util.List;

public class WinCombination {
    private String name;
    private int rewardMultiplier;
    private int count;
    private String group;
    private List<List<String>> coveredAreas; // List of covered areas as a list of strings
    private String when;

    // Constructor
    public WinCombination(String name, int rewardMultiplier, int count, String group, List<List<String>> coveredAreas, String when) {
        this.name = name;
        this.rewardMultiplier = rewardMultiplier;
        this.count = count;
        this.group = group;
        this.coveredAreas = coveredAreas;
        this.when = when;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Setter for name
    public void setName(String name) {
        this.name = name;
    }

    // Getter for rewardMultiplier
    public int getRewardMultiplier() {
        return rewardMultiplier;
    }

    // Setter for rewardMultiplier
    public void setRewardMultiplier(int rewardMultiplier) {
        this.rewardMultiplier = rewardMultiplier;
    }

    // Getter for count
    public int getCount() {
        return count;
    }

    // Setter for count
    public void setCount(int count) {
        this.count = count;
    }

    // Getter for group
    public String getGroup() {
        return group;
    }

    // Setter for group
    public void setGroup(String group) {
        this.group = group;
    }

    // Getter for coveredAreas
    public List<List<String>> getCoveredAreas() {
        return coveredAreas;
    }

    // Setter for coveredAreas
    public void setCoveredAreas(List<List<String>> coveredAreas) {
        this.coveredAreas = coveredAreas;
    }

    // Getter for when
    public String getWhen() {
        return when;
    }

    // Setter for when
    public void setWhen(String when) {
        this.when = when;
    }

    @Override
    public String toString() {
        return "WinCombination{" +
                "name='" + name + '\'' +
                ", rewardMultiplier=" + rewardMultiplier +
                ", count=" + count +
                ", group='" + group + '\'' +
                ", coveredAreas=" + coveredAreas +
                ", when='" + when + '\'' +
                '}';
    }
}

