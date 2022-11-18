package com.example.gruppenkalenderfeiertragurlaub.speicherklassen;

import java.time.LocalDate;

public class KuechenKalenderTag extends TagBasisKlasse {
    private Integer kuecheGeoeffnet;

    public KuechenKalenderTag(LocalDate datum, Integer kuecheGeoeffnet, Boolean isFeiertag) {
        this.isFeiertag = isFeiertag;
        this.datum = datum;
        this.kuecheGeoeffnet = kuecheGeoeffnet;
    }

    public Integer getKuecheGeoeffnet() {
        return kuecheGeoeffnet;
    }

    public void setKuecheGeoeffnet(Integer kuecheGeoeffnet) {
        this.kuecheGeoeffnet = kuecheGeoeffnet;
    }
}
