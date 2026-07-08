package com.example.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class InventoryPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By inventoryContainer = By.id("inventory_container");
    private final By pageTitle = By.cssSelector("[data-test='title']");

    public InventoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public boolean isLoaded() {
        return wait.until(ExpectedConditions
                        .visibilityOfElementLocated(inventoryContainer))
                .isDisplayed();
    }

    public String title() {
        return wait.until(ExpectedConditions
                        .visibilityOfElementLocated(pageTitle))
                .getText();
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }
}
