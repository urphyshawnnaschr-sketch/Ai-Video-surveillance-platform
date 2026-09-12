package com.yihecode.camera.ai.service;

import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class AlgorithmDataForGitCacheService {

    private static Map<Long, Double> gitVersionMap;

    public static Double getValue(Long key) {
        if (gitVersionMap.get(key) == null) {
            return null;
        }
        return gitVersionMap.get(key);
    }

    public static void setValue(Long key, Double value) {
        gitVersionMap.put(key, value);
    }

    public static Map<Long, Double> getMap() {
        return gitVersionMap;
    }

    public static void setMap(Map<Long, Double> map) {
        gitVersionMap = map;
    }

}