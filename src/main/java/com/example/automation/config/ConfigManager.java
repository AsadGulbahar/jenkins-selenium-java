package com.example.automation.config;

public final class ConfigManager {

    private ConfigManager() {
    }

    public static String baseUrl() {
        return value("baseUrl", "BASE_URL", "https://www.saucedemo.com/");
    }

    public static String browser() {
        return value("browser", "BROWSER", "chrome").toLowerCase();
    }

    public static String remoteUrl() {
        return value("remoteUrl", "SELENIUM_REMOTE_URL", "");
    }

    public static String username() {
        return value("username", "SAUCE_USERNAME", "standard_user");
    }

    public static String password() {
        return value("password", "SAUCE_PASSWORD", "secret_sauce");
    }

    private static String value(String property, String env, String defaultValue) {
        String fromProperty = System.getProperty(property);
        if (fromProperty != null && !fromProperty.isBlank()) {
            return fromProperty.trim();
        }
        String fromEnvironment = System.getenv(env);
        if (fromEnvironment != null && !fromEnvironment.isBlank()) {
            return fromEnvironment.trim();
        }
        return defaultValue;
    }
}
