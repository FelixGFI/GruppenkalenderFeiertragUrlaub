package com.example.gruppenkalenderfeiertragurlaub;

import java.time.LocalDate;

public class BetriebsurlaubsTag {

    String datum;
    String isBetriebsurlaub;

    public BetriebsurlaubsTag(String datum, String isBetriebsurlaub) {
        this.datum = datum;
        this.isBetriebsurlaub = isBetriebsurlaub;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public String getIsBetriebsurlaub() {
        return isBetriebsurlaub;
    }

    public void setIsBetriebsurlaub(String isBetriebsurlaub) {
        this.isBetriebsurlaub = isBetriebsurlaub;
    }
}
