package com.example.gruppenkalenderfeiertragurlaub.speicherklassen;

import java.time.LocalDate;

public class BetriebsurlaubsTag extends TagBasisKlasse {
    private Integer betriebsurlaub;

    public BetriebsurlaubsTag(LocalDate datum, Integer isBetriebsurlaub) {
        this.datum = datum;
        this.betriebsurlaub = isBetriebsurlaub;
    }

    public Integer getBetriebsurlaub() {
        return betriebsurlaub;
    }

    public void setBetriebsurlaub(Integer betriebsurlaub) {
        this.betriebsurlaub = betriebsurlaub;
    }
}
