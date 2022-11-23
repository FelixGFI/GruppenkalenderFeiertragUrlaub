package com.example.gruppenkalenderfeiertragurlaub.speicherklassen;

import java.time.LocalDate;

public class KuechenKalenderTag extends TagBasisKlasse {
    private Integer kuecheCurrentlyGeoeffnet;
    private Boolean tagWasEdited;

    public KuechenKalenderTag(LocalDate datum, Integer kuecheCurrentlyGeoeffnet) {
        this.datum = datum;
        this.kuecheCurrentlyGeoeffnet = kuecheCurrentlyGeoeffnet;
        tagWasEdited = false;
    }

    public Integer getKuecheCurrentlyGeoeffnet() {
        return kuecheCurrentlyGeoeffnet;
    }

    public void setKuecheCurrentlyGeoeffnet(Integer kuecheCurrentlyGeoeffnet) {
        this.kuecheCurrentlyGeoeffnet = kuecheCurrentlyGeoeffnet;
        tagWasEdited = true;
    }
    public Boolean getTagWasEdited() {
        return tagWasEdited;
    }
}
