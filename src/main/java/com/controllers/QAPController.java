package com.controllers;

import com.implementations.models.QuadraticAssignment.QAPEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
public class QAPController {

    private final QAPRepository repository;

    QAPController(QAPRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/qap")
    List<QAPEntity> all() {
        return repository.findAll();
    }

    @PostMapping("/qap/create/{filename}")
    QAPEntity newQAPEntity(@PathVariable String filename) throws FileNotFoundException {
        QAPEntity qapEntity = new QAPEntity("src/test/java/QAP/resources/" + filename + ".txt");
        return repository.save(qapEntity);
    }

    @GetMapping("/qap/{id}")
    QAPEntity getQAPEntity(@PathVariable Long id) {

        return repository.findById(id)
                .orElseThrow(() -> new QAPEntityNotFoundException(id));
    }

    @GetMapping("/qap/solve/{id}")
    QAPEntity solveQAP(@PathVariable Long id) throws Exception {

        QAPEntity qapEntity = repository.findById(id)
                .orElseThrow(() -> new QAPEntityNotFoundException(id));

        qapEntity.solve();

        return repository.save(qapEntity);
    }
}
