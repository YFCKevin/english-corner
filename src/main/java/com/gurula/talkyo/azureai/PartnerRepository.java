package com.gurula.talkyo.azureai;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PartnerRepository extends MongoRepository<Partner, String> {
    List<Partner> findByLocale(String locale);
}
