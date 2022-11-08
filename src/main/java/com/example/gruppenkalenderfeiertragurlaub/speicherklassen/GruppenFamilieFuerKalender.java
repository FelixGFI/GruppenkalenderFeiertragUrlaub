package com.example.gruppenkalenderfeiertragurlaub.speicherklassen;

import java.util.ArrayList;

public class GruppenFamilieFuerKalender {

    private ArrayList<GruppeFuerKalender> gruppenDerFamilie;
    private Integer familieId;
    private String familieName;

    public GruppenFamilieFuerKalender(Integer familienId, String familieName, ArrayList<GruppeFuerKalender> gruppenDerFamilie) {
        this.gruppenDerFamilie = gruppenDerFamilie;
        this.familieId = familienId;
        this.familieName = familieName;
    }

    public ArrayList<GruppeFuerKalender> getGruppenDerFamilie() {
        return gruppenDerFamilie;
    }

    public Integer getFamilieId() {
        return familieId;
    }

    public String getFamilieName() {
        return familieName;
    }
}
