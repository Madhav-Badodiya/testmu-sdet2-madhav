package com.testmu.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ConfigManager — single source of truth for all configuration.
 * Priority: System property > config.properties
 * Supports environment switching via -Denv=staging etc.
 */
public class ConfigManager {

    private static ConfigManager instance;
    private final Properties properties = new Properties();

    private ConfigManager() {
        loadProperties();
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }

    private void loadProperties() {
        String env = System.getProperty("env", "default");
        String configFile = "src/test/resources/config/config.properties";

        try (InputStream input = new FileInputStream(configFile)) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties: " + e.getMessage());
        }
    }

    /**
     * Returns value from system property first, falls back to config.properties.
     */
    public String get(String key) {
        String systemValue = System.getProperty(key);
        return systemValue != null ? systemValue : properties.getProperty(key);
    }

    public String getUIBaseUrl() {
        return get("ui.base.url");
    }

    public String getAPIBaseUrl() {
        return get("api.base.url");
    }

    public String getApiAuthUrl() {
        return get("api.auth.url");
    }

    public String getApiUsername() {
        return get("api.username");
    }

    public String getApiPassword() {
        return get("api.password");
    }

    public String getBrowser() {
        return get("browser");
    }

    public boolean isHeadless() {
        return Boolean.parseBoolean(get("headless"));
    }

    public int getImplicitWait() {
        return Integer.parseInt(get("implicit.wait"));
    }

    public int getExplicitWait() {
        return Integer.parseInt(get("explicit.wait"));
    }

    public int getRetryCount() {
        return Integer.parseInt(get("retry.count"));
    }

    public boolean screenshotOnFailure() {
        return Boolean.parseBoolean(get("screenshot.on.failure"));
    }

    public String getScreenshotDir() {
        return get("screenshot.dir");
    }
}
