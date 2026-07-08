package com.example.automation.driver;

import com.example.automation.config.ConfigManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URI;

public final class DriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static void createDriver() {
        String browser = ConfigManager.browser();
        String remoteUrl = ConfigManager.remoteUrl();

        WebDriver driver = remoteUrl.isBlank()
                ? createLocalDriver(browser)
                : createRemoteDriver(browser, remoteUrl);

        DRIVER.set(driver);
    }

    public static WebDriver getDriver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("WebDriver has not been created.");
        }
        return driver;
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        try {
            if (driver != null) {
                driver.quit();
            }
        } finally {
            DRIVER.remove();
        }
    }

    private static WebDriver createLocalDriver(String browser) {
        return switch (browser) {
            case "firefox" -> new FirefoxDriver(firefoxOptions());
            case "chrome" -> new ChromeDriver(chromeOptions());
            default -> throw new IllegalArgumentException(
                    "Unsupported browser: " + browser);
        };
    }

    private static WebDriver createRemoteDriver(String browser, String remoteUrl) {
        try {
            return switch (browser) {
                case "firefox" -> new RemoteWebDriver(
                        URI.create(remoteUrl).toURL(), firefoxOptions());
                case "chrome" -> new RemoteWebDriver(
                        URI.create(remoteUrl).toURL(), chromeOptions());
                default -> throw new IllegalArgumentException(
                        "Unsupported browser: " + browser);
            };
        } catch (MalformedURLException exception) {
            throw new IllegalArgumentException(
                    "Invalid Selenium remote URL: " + remoteUrl, exception);
        }
    }

    private static ChromeOptions chromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.setAcceptInsecureCerts(true);
        return options;
    }

    private static FirefoxOptions firefoxOptions() {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("-headless");
        options.setAcceptInsecureCerts(true);
        return options;
    }
}
