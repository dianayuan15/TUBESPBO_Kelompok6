package com.example.application.data.service;

import com.example.application.data.entity.AkunAdmin;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class AkunAdminService {

    private final AkunAdminRepository repository;

    public AkunAdminService(AkunAdminRepository repository) {
        this.repository = repository;
    }

    public Optional<AkunAdmin> get(Long id) {
        return repository.findById(id);
    }

    public AkunAdmin update(AkunAdmin entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<AkunAdmin> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<AkunAdmin> list(Pageable pageable, Specification<AkunAdmin> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
