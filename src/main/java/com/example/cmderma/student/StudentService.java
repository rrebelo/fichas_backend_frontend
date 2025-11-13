package com.example.cmderma.student;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository repository;

    @Transactional(readOnly = true)
    public List<Student> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Student findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
    }

    public Student create(Student student) {
        return repository.save(student);
    }

    public Student update(Long id, Student updated) {
        Student existing = findById(id);

        existing.setNome(updated.getNome());
        existing.setMorada(updated.getMorada());
        existing.setTelefone(updated.getTelefone());
        existing.setEntidade(updated.getEntidade());
        existing.setNbeneficia(updated.getNbeneficia());
        existing.setSexo(updated.getSexo());
        existing.setDatanascim(updated.getDatanascim());
        existing.setProfissao(updated.getProfissao());
        existing.setEstadocivi(updated.getEstadocivi());
        existing.setTelemovel(updated.getTelemovel());
        existing.setObs(updated.getObs());

        return repository.save(existing);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new StudentNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
