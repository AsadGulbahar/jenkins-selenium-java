package com.example.automation.listeners;

import com.example.automation.driver.DriverFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestListener implements ITestListener {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    @Override
    public void onTestFailure(ITestResult result) {
        try {
            WebDriver driver = DriverFactory.getDriver();
            if (!(driver instanceof TakesScreenshot screenshotDriver)) {
                return;
            }

            Path directory = Path.of("target", "screenshots");
            Files.createDirectories(directory);

            String safeName = result.getName()
                    .replaceAll("[^a-zA-Z0-9._-]", "_");
            String timestamp = LocalDateTime.now().format(FORMATTER);
            Path destination = directory.resolve(
                    safeName + "-" + timestamp + ".png");

            Files.copy(
                    screenshotDriver.getScreenshotAs(OutputType.FILE).toPath(),
                    destination,
                    StandardCopyOption.REPLACE_EXISTING);

            System.out.println("Failure screenshot: " + destination);
        } catch (IllegalStateException | IOException exception) {
            System.err.println("Unable to capture screenshot: "
                    + exception.getMessage());
        }
    }
}
