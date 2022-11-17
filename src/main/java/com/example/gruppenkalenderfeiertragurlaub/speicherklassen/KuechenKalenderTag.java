package com.example.gruppenkalenderfeiertragurlaub.speicherklassen;

import java.time.LocalDate;

public class KuechenKalenderTag extends TagBasisKlasse {
    private Boolean kuecheGeoeffnet;

    public KuechenKalenderTag(LocalDate datum, Boolean kuecheGeoeffnet, Boolean isFeiertag) {
        this.isFeiertag = isFeiertag;
        this.datum = datum;
        this.kuecheGeoeffnet = kuecheGeoeffnet;
    }

    public Boolean getKuecheGeoeffnet() {
        return kuecheGeoeffnet;
    }

    public void setKuecheGeoeffnet(Boolean kuecheGeoeffnet) {
        this.kuecheGeoeffnet = kuecheGeoeffnet;
    }
}
