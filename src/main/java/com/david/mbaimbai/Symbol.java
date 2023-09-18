package com.david.mbaimbai;

public class Symbol {
    private String name;
    private int rewardMultiplier;
    private String type;
    private int extra;
    private String impact;

    public Symbol(String name, int rewardMultiplier, String type, int extra, String impact) {
        this.name = name;
        this.rewardMultiplier = rewardMultiplier;
        this.type = type;
        this.extra = extra;
        this.impact = impact;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRewardMultiplier() {
        return rewardMultiplier;
    }

    public void setRewardMultiplier(int rewardMultiplier) {
        this.rewardMultiplier = rewardMultiplier;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getExtra() {
        return extra;
    }

    public void setExtra(int extra) {
        this.extra = extra;
    }

    public String getImpact() {
        return impact;
    }

    public void setImpact(String impact) {
        this.impact = impact;
    }

    @Override
    public String toString() {
        return "Symbol{" +
                "name='" + name + '\'' +
                ", rewardMultiplier=" + rewardMultiplier +
                ", type='" + type + '\'' +
                ", extra=" + extra +
                ", impact='" + impact + '\'' +
                '}';
    }
}
