package com.controllers.QuadraticAssignment;

import com.controllers.Status;
import com.implementations.models.QuadraticAssignment.QAPEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class QAPController {

    private final QAPRepository repository;

    private final QAPModelAssembler assembler;

    private static final Logger log = LoggerFactory.getLogger(QAPController.class);

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
    ResponseEntity<?> newQAPEntity(@PathVariable String filename) throws FileNotFoundException {
        QAPEntity qapEntity = new QAPEntity("src/main/resources/" + filename + ".txt");
        repository.save(qapEntity);

        EntityModel<QAPEntity> entityModel = assembler.toModel(qapEntity);

        return ResponseEntity //
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(entityModel);
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

    @GetMapping("/qap/solve-deferredresult/{id}")
    public DeferredResult<ResponseEntity<?>> solveQAPDeferredResult(@PathVariable Long id) {
        QAPEntity qapEntity = repository.findById(id)
                .orElseThrow(() -> new QAPEntityNotFoundException(id));

        qapEntity.setStatus(Status.IN_PROGRESS);
        repository.save(qapEntity);

        log.info("Received async-deferredresult request");
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();

        output.onCompletion(() -> {
            qapEntity.setStatus(Status.COMPLETED);
        });

        ForkJoinPool.commonPool().submit(() -> {
            log.info("Processing in separate thread");
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
            }
            output.setResult(ResponseEntity.ok("ok"));
        });

        log.info("servlet thread freed");
        return output;
    }
}
