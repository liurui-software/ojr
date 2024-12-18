package com.ojr.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class BasicDcConfig {
    private final List<ConcurrentHashMap<String, Object>> instances = new ArrayList<>();

    public List<ConcurrentHashMap<String, Object>> getInstances() {
        return instances;
    }
}
