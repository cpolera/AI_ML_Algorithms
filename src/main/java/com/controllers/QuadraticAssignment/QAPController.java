package com.controllers.QuadraticAssignment;

import com.implementations.models.QuadraticAssignment.QAPEntity;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class QAPController {

    private final QAPRepository repository;

    private final QAPModelAssembler assembler;


    QAPController(QAPRepository repository, QAPModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @GetMapping("/qap")
    CollectionModel<EntityModel<QAPEntity>> all() {
        List<EntityModel<QAPEntity>> qapEntities = repository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(qapEntities, linkTo(methodOn(QAPController.class).all()).withSelfRel());
    }

    @PostMapping("/qap/create/{filename}")
    EntityModel<QAPEntity> newQAPEntity(@PathVariable String filename) throws FileNotFoundException {
        QAPEntity qapEntity = new QAPEntity("src/main/resources/" + filename + ".txt");
        repository.save(qapEntity);

        return assembler.toModel(qapEntity);
    }

    @GetMapping("/qap/{id}")
    EntityModel<QAPEntity> getQAPEntity(@PathVariable Long id) {

        QAPEntity qapEntity = repository.findById(id)
                .orElseThrow(() -> new QAPEntityNotFoundException(id));

        return assembler.toModel(qapEntity);
    }

    @GetMapping("/qap/solve/{id}")
    EntityModel<QAPEntity> solveQAP(@PathVariable Long id) throws Exception {

        QAPEntity qapEntity = repository.findById(id)
                .orElseThrow(() -> new QAPEntityNotFoundException(id));

        qapEntity.solve();

        repository.save(qapEntity);

        return assembler.toModel(qapEntity);
    }
}
