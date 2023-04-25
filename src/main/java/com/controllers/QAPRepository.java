package com.controllers;

import com.implementations.models.QuadraticAssignment.QAPEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QAPRepository extends JpaRepository<QAPEntity, Long> {

}