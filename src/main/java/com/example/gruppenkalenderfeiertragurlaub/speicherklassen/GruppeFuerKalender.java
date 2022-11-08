package com.example.gruppenkalenderfeiertragurlaub.speicherklassen;

public class GruppeFuerKalender {

    private Integer gruppeId;
    private String gruppeName;
    private Integer familienId;

    public GruppeFuerKalender(Integer gruppeId, String gruppeName, Integer familienId) {
        this.gruppeId = gruppeId;
        this.gruppeName = gruppeName;
        this.familienId = familienId;
    }

    public Integer getGruppeId() {
        return gruppeId;
    }

    public String getGruppeName() {
        return gruppeName;
    }

    public Integer getFamilienId() {
        return familienId;
    }
}
