package com.placideh.rateLimiter.service;



import com.placideh.rateLimiter.exception.ResourceNotFoundException;
import com.placideh.rateLimiter.model.SystemConfig;
import com.placideh.rateLimiter.repository.SystemConfigRepository;
import com.placideh.rateLimiter.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SystemConfigService {

    @Autowired
    private SystemConfigRepository systemConfigRepository;

    public Map<String, String> getAllConfigs() {
        Map<String, String> configs = new HashMap<>();
        systemConfigRepository.findAll().forEach(config ->
                configs.put(config.getConfigKey(), config.getConfigValue())
        );
        return configs;
    }

    public String getConfig(String key) {
        return systemConfigRepository.findByConfigKey(key)
                .map(SystemConfig::getConfigValue)
                .orElseThrow(() -> new ResourceNotFoundException("Config", "key", key));
    }

    public void updateConfig(String key, String value, String updatedBy) {
        SystemConfig config = systemConfigRepository.findByConfigKey(key)
                .orElseThrow(() -> new ResourceNotFoundException("Config", "key", key));

        // Validate algorithm value
        if ("RATE_LIMIT_ALGORITHM".equals(key)) {
            if (!Constants.TOKEN_BUCKET.equals(value) && !Constants.FIXED_WINDOW.equals(value)) {
                throw new IllegalArgumentException(
                        "Invalid algorithm. Must be TOKEN_BUCKET or FIXED_WINDOW");
            }
        }

        config.setConfigValue(value);
        config.setUpdatedBy(updatedBy);
        systemConfigRepository.save(config);
    }

    public String getCurrentAlgorithm() {
        return systemConfigRepository.findByConfigKey("RATE_LIMIT_ALGORITHM")
                .map(SystemConfig::getConfigValue)
                .orElse(Constants.TOKEN_BUCKET);
    }
}
