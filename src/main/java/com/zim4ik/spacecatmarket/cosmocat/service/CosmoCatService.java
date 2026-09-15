package com.zim4ik.spacecatmarket.cosmocat.service;

import com.zim4ik.spacecatmarket.feature.aspect.RequiresFeature;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CosmoCatService {

    @RequiresFeature("cosmoCats")
    public List<String> getCosmoCats() {
        return List.of("Nebula", "Comet", "Orion");
    }
}
