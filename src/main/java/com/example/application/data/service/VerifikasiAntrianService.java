package com.example.application.data.service;

import com.example.application.data.entity.VerifikasiAntrian;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class VerifikasiAntrianService {

    private final VerifikasiAntrianRepository repository;

    public VerifikasiAntrianService(VerifikasiAntrianRepository repository) {
        this.repository = repository;
    }

    public Optional<VerifikasiAntrian> get(Long id) {
        return repository.findById(id);
    }

    public VerifikasiAntrian update(VerifikasiAntrian entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<VerifikasiAntrian> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<VerifikasiAntrian> list(Pageable pageable, Specification<VerifikasiAntrian> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
