package com.example.application.data.entity;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.validation.constraints.Email;

@Entity
public class AkunAdmin extends AbstractEntity {

    @Lob
    @Column(length = 1000000)
    private byte[] gambar;
    private String nama;
    @Email
    private String email;

    private String nomorTelepon;
    private String jenisKelamin;
    private String alamat;

    public byte[] getGambar() {
        return gambar;
    }
    public void setGambar(byte[] gambar) {
        this.gambar = gambar;
    }
    public String getNama() {
        return nama;
    }
    public void setNama(String nama) {
        this.nama = nama;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) { this.email = email; }
    public String getNomorTelepon() {return nomorTelepon;}
    public void setNomorTelepon(String nomorTelepon) {
        this.nomorTelepon = nomorTelepon;
    }
    public String getJenisKelamin() {
        return jenisKelamin;
    }
    public void setJenisKelamin(String jenisKelamin) {
        this.jenisKelamin = jenisKelamin;
    }
    public String getAlamat() {return alamat; }
    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

}
