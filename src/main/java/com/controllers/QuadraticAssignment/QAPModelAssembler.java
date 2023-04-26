package com.controllers.QuadraticAssignment;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.implementations.models.QuadraticAssignment.QAPEntity;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
class QAPModelAssembler implements RepresentationModelAssembler<QAPEntity, EntityModel<QAPEntity>> {

    @Override
    public EntityModel<QAPEntity> toModel(QAPEntity qapEntity) {

        return EntityModel.of(qapEntity, //
                linkTo(methodOn(QAPController.class).getQAPEntity(qapEntity.getId())).withSelfRel(),
                linkTo(methodOn(QAPController.class).all()).withRel("employees"));
    }
}
