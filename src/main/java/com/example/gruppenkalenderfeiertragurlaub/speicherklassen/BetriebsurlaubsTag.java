package com.example.gruppenkalenderfeiertragurlaub.speicherklassen;

import java.time.LocalDate;

public class BetriebsurlaubsTag extends TagBasisKlasse {
    private Integer isBetriebsurlaub;

    public BetriebsurlaubsTag(LocalDate datum, Integer isBetriebsurlaub, Boolean isFeiertag) {
        this.isFeiertag = isFeiertag;
        this.datum = datum;
        this.isBetriebsurlaub = isBetriebsurlaub;
    }

    public Integer getIsBetriebsurlaub() {
        return isBetriebsurlaub;
    }

    public void setIsBetriebsurlaub(Integer isBetriebsurlaub) {
        this.isBetriebsurlaub = isBetriebsurlaub;
    }
}
