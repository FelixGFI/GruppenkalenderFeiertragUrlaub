package com.example.gruppenkalenderfeiertragurlaub.speicherklassen;

import java.time.LocalDate;

public class BetriebsurlaubsTag extends Tag {
    private Integer isCurrentlyBetriebsurlaub;
    private Boolean beganAsBetriebsurlaub;

    public BetriebsurlaubsTag(LocalDate datum, Integer isCurrentlyBetriebsurlaub) {
        this.datum = datum;
        this.isCurrentlyBetriebsurlaub = isCurrentlyBetriebsurlaub;
        this.beganAsBetriebsurlaub = (isCurrentlyBetriebsurlaub == 1);
    }
    public Integer getIsCurrentlyBetriebsurlaub() {
        return isCurrentlyBetriebsurlaub;
    }
    public void setIsCurrentlyBetriebsurlaub(Integer isCurrentlyBetriebsurlaub) {
        this.isCurrentlyBetriebsurlaub = isCurrentlyBetriebsurlaub;
    }
    public Boolean getBeganAsBetriebsurlaub() {
        return beganAsBetriebsurlaub;
    }
}
