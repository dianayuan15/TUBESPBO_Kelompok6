package com.example.application.data.entity;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.validation.constraints.Email;

@Entity
public class Setting extends AbstractEntity {

    @Lob
    @Column(length = 1000000)
    private byte[] gambar;
    private String tentang;
    private String namaDokter;
    private String lokasi;
    @Email
    private String email;
    private String noTelp;

    public byte[] getGambar() {
        return gambar;
    }
    public void setGambar(byte[] gambar) {
        this.gambar = gambar;
    }
    public String getTentang() {
        return tentang;
    }
    public void setTentang(String tentang) {
        this.tentang = tentang;
    }
    public String getNamaDokter() {
        return namaDokter;
    }
    public void setNamaDokter(String namaDokter) {
        this.namaDokter = namaDokter;
    }
    public String getLokasi() {
        return lokasi;
    }
    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getNoTelp() {
        return noTelp;
    }
    public void setNoTelp(String noTelp) {
        this.noTelp = noTelp;
    }

}
