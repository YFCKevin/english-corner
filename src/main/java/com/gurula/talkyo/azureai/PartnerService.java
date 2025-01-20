package com.gurula.talkyo.azureai;

import java.util.List;

public interface PartnerService {
    List<Partner> findAll();

    List<Partner> saveAll(List<Partner> newPartners);

    List<Partner> getPartnerList();
}
