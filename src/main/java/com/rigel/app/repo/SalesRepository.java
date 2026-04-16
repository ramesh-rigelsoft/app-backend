package com.rigel.app.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rigel.app.model.Invoice;
import com.rigel.app.model.SalesInfo;

@Repository
public interface SalesRepository extends JpaRepository<SalesInfo, String> {
}
