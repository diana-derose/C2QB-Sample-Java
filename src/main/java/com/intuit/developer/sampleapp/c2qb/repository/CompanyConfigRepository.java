package com.intuit.developer.sampleapp.c2qb.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.intuit.developer.sampleapp.c2qb.domain.CompanyConfig;

/**
 * @author dderose
 *
 */
public interface CompanyConfigRepository extends CrudRepository<CompanyConfig, Integer> {

	CompanyConfig findByRealmId(@Param("realmId") String realmId);
}
