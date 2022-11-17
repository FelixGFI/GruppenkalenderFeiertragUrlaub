package com.example.gruppenkalenderfeiertragurlaub.speicherklassen;

import java.time.LocalDate;

public class BetriebsurlaubsTag extends TagBasisKlasse {

    private Boolean isBetriebsurlaub;
    private Boolean isFeiertag;

    public BetriebsurlaubsTag(LocalDate datum, Boolean isBetriebsurlaub, Boolean isFeiertag) {
        this.isFeiertag = isFeiertag;
        this.datum = datum;
        this.isBetriebsurlaub = isBetriebsurlaub;
    }

    public Boolean getIsBetriebsurlaub() {
        return isBetriebsurlaub;
    }

    public void setIsBetriebsurlaub(Boolean isBetriebsurlaub) {
        this.isBetriebsurlaub = isBetriebsurlaub;
    }

    public Boolean getIsFeiertag() {
        return isFeiertag;
    }

    public void setIsFeiertag(Boolean feiertag) {
        isFeiertag = feiertag;
    }
}
