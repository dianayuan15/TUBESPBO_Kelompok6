package com.example.application.data.entity;

import javax.persistence.Entity;
import javax.validation.constraints.Email;

@Entity
public class VerifikasiAntrian extends AbstractEntity {

    private String nik;
    private String nama;
    private String keluhan;
    private String diagnosa;
    private String dosisObat;
    public String getNik() {
        return nik;
    }
    public void setNik(String nik) {
        this.nik = nik;
    }
    public String getNama() {
        return nama;
    }
    public void setNama(String nama) {
        this.nama = nama;
    }
    public String getKeluhan() {
        return keluhan;
    }
    public void setKeluhan(String keluhan) {
        this.keluhan = keluhan;
    }
    public String getDiagnosa() {
        return diagnosa;
    }
    public void setDiagnosa(String diagnosa) {
        this.diagnosa = diagnosa;
    }
    public String getDosisObat() {
        return dosisObat;
    }
    public void setDosisObat(String dosisObat) {
        this.dosisObat = dosisObat;
    }

}
