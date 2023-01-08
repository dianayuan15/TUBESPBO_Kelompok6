package com.example.application.data.service;

import com.example.application.data.entity.DaftarAntrian;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class DaftarAntrianService {

    private final DaftarAntrianRepository repository;

    public DaftarAntrianService(DaftarAntrianRepository repository) {
        this.repository = repository;
    }

    public Optional<DaftarAntrian> get(Long id) {
        return repository.findById(id);
    }

    public DaftarAntrian update(DaftarAntrian entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<DaftarAntrian> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<DaftarAntrian> list(Pageable pageable, Specification<DaftarAntrian> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
