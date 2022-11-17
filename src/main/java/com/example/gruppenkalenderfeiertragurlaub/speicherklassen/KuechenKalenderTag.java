package com.example.gruppenkalenderfeiertragurlaub.speicherklassen;

import java.time.LocalDate;

public class KuechenKalenderTag extends TagBasisKlasse {
    private Boolean kuecheGeoeffnet;
    private Boolean isFeiertag;

    public KuechenKalenderTag(LocalDate datum, Boolean kuecheGeoeffnet, Boolean isFeiertag) {
        this.isFeiertag = isFeiertag;
        this.datum = datum;
        this.kuecheGeoeffnet = kuecheGeoeffnet;
    }

    public LocalDate getDatum() {
        return datum;
    }

    public void setDatum(LocalDate datum) {
        this.datum = datum;
    }

    public Boolean getKuecheGeoeffnet() {
        return kuecheGeoeffnet;
    }

    public void setKuecheGeoeffnet(Boolean kuecheGeoeffnet) {
        this.kuecheGeoeffnet = kuecheGeoeffnet;
    }
}
