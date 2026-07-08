package com.example.automation.tests;

import com.example.automation.config.ConfigManager;
import com.example.automation.driver.DriverFactory;
import com.example.automation.pages.InventoryPage;
import com.example.automation.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test(groups = "smoke")
    public void validUserCanLogin() {
        InventoryPage inventory = new LoginPage(DriverFactory.getDriver())
                .open(ConfigManager.baseUrl())
                .loginAs(ConfigManager.username(), ConfigManager.password());

        Assert.assertTrue(inventory.isLoaded(),
                "Inventory page was not displayed after login.");
        Assert.assertEquals(inventory.title(), "Products");
        Assert.assertTrue(inventory.currentUrl().contains("inventory.html"));
    }

    @Test(groups = "regression")
    public void lockedUserCannotLogin() {
        LoginPage loginPage = new LoginPage(DriverFactory.getDriver())
                .open(ConfigManager.baseUrl())
                .loginExpectingFailure(
                        "locked_out_user",
                        ConfigManager.password());

        Assert.assertTrue(
                loginPage.errorText().contains("locked out"),
                "Expected locked-out user error was not displayed.");
    }

    @Test(groups = "regression")
    public void invalidPasswordIsRejected() {
        LoginPage loginPage = new LoginPage(DriverFactory.getDriver())
                .open(ConfigManager.baseUrl())
                .loginExpectingFailure(
                        ConfigManager.username(),
                        "incorrect-password");

        Assert.assertTrue(
                loginPage.errorText().contains(
                        "Username and password do not match"),
                "Expected invalid credentials error was not displayed.");
    }
}
