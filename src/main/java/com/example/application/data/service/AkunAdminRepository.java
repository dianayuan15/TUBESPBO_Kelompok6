package com.example.application.data.service;

import com.example.application.data.entity.AkunAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AkunAdminRepository extends JpaRepository<AkunAdmin, Long>, JpaSpecificationExecutor<AkunAdmin> {

}
