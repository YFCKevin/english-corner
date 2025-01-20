package com.gurula.talkyo.azureai;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PartnerServiceImpl implements PartnerService{
    private final PartnerRepository partnerRepository;

    public PartnerServiceImpl(PartnerRepository partnerRepository) {
        this.partnerRepository = partnerRepository;
    }

    @Override
    public List<Partner> findAll() {
        return partnerRepository.findAll();
    }

    @Override
    public List<Partner> saveAll(List<Partner> newPartners) {
        return partnerRepository.saveAll(newPartners);
    }

    @Override
    public List<Partner> getPartnerList() {
        return partnerRepository.findByLocale("en-US");
    }
}
