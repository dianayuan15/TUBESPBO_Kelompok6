package com.example.application.data.entity;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.validation.constraints.Email;

@Entity
public class DaftarAntrian extends AbstractEntity {

    private String nik;
    private String nama;
    private String nmrTlpn;
    private LocalDate tanggalLahir;
    private String jenisKelamin;
    private String alamat;
    private String keluhan;
    public String getNik() {
        return nik;
    }
    public void setNik(String Nik) {
        this.nik = nik;
    }
    public String getNama() {return nama;}
    public void setNama(String nama) {
        this.nama = nama;
    }
    public String getNmrTlpn() {return nmrTlpn;}
    public void setNmrTlpn(String nmrTlpn) {
        this.nmrTlpn = nmrTlpn;
    }

    public LocalDate getTanggalLahir() {
        return tanggalLahir;
    }
    public void setTanggalLahir(LocalDate tanggalLahir) {
        this.tanggalLahir = tanggalLahir;
    }
    public String getJenisKelamin() {
        return jenisKelamin;
    }
    public void setJenisKelamin(String jenisKelamin) {
        this.jenisKelamin = jenisKelamin;
    }
    public String getAlamat() {
        return alamat;
    }
    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }
    public String getKeluhan() {
        return keluhan;
    }
    public void setkeluhan(String keluhan) {
        this.keluhan = keluhan;
    }

}
