package com.example.gruppenkalenderfeiertragurlaub.speicherklassen;

import java.time.LocalDate;

public class BetriebsurlaubsTag extends  SpeicherBasisKlasse {

    private Boolean isBetriebsurlaub;

    public BetriebsurlaubsTag(LocalDate datum, Boolean isBetriebsurlaub) {
        this.datum = datum;
        this.isBetriebsurlaub = isBetriebsurlaub;
    }

    public Boolean getIsBetriebsurlaub() {
        return isBetriebsurlaub;
    }

    public void setIsBetriebsurlaub(Boolean isBetriebsurlaub) {
        this.isBetriebsurlaub = isBetriebsurlaub;
    }
}
