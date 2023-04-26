package com.controllers.QuadraticAssignment;

import com.controllers.Status;
import com.implementations.models.QuadraticAssignment.QAPEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.server.ResponseStatusException;

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
        log.info("Received /qap/solve-deferredresult request");
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();

        final QAPEntity qapEntity = repository.findById(id)
                .orElseThrow(() -> new QAPEntityNotFoundException(id));

        output.onCompletion(() -> {
            log.info("onCompletion called");
            qapEntity.setStatus(Status.COMPLETED);
            repository.save(qapEntity);
        });

        output.onError((Throwable t) -> {
            output.setErrorResult(
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("An error occurred."));
            qapEntity.setStatus(Status.EXCEPTION);
            repository.save(qapEntity);
        });

        ForkJoinPool.commonPool().submit(() -> {
            log.info("Processing in separate thread");
            if(qapEntity.getStatus()!= Status.IN_PROGRESS){
                try {
                    qapEntity.setStatus(Status.IN_PROGRESS);
                    repository.save(qapEntity);
                    // Thread.sleep(6000); // Uncomment for testing
                    qapEntity.solve();
                } catch (Exception e) {
                }
                output.setResult(ResponseEntity.ok("ok"));
            } else {
                log.info("QAPEntity solution is already in progress");
                output.setResult(new ResponseEntity<>(HttpStatus.FORBIDDEN));
            }
        });

        log.info("servlet thread freed");
        return output;
    }
}