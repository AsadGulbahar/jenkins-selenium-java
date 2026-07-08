package com.example.automation.tests;

import com.example.automation.driver.DriverFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class BaseTest {

    @BeforeMethod(alwaysRun = true)
    public void startBrowser() {
        DriverFactory.createDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void stopBrowser() {
        DriverFactory.quitDriver();
    }
}
