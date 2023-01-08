package com.example.application.data.service;

import com.example.application.data.entity.VerifikasiAntrian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VerifikasiAntrianRepository
        extends
            JpaRepository<VerifikasiAntrian, Long>,
            JpaSpecificationExecutor<VerifikasiAntrian> {

}
