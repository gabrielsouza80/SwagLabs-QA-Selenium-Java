package com.swaglabs.listeners;

import com.swaglabs.base.BaseTest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {
    private static final Path SCREENSHOT_DIRECTORY = Path.of("screenshots");
    private static final DateTimeFormatter TIMESTAMP =
            DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

    @Override
    public void onTestFailure(ITestResult result) {
        Object instance = result.getInstance();
        if (!(instance instanceof BaseTest baseTest)) {
            return;
        }

        WebDriver driver = baseTest.getDriver();
        if (!(driver instanceof TakesScreenshot screenshotDriver)) {
            return;
        }

        try {
            Files.createDirectories(SCREENSHOT_DIRECTORY);
            String filename = sanitize(result.getMethod().getMethodName())
                    + "-" + LocalDateTime.now().format(TIMESTAMP)
                    + "-" + UUID.randomUUID().toString().substring(0, 8) + ".png";
            Files.copy(
                    screenshotDriver.getScreenshotAs(OutputType.FILE).toPath(),
                    SCREENSHOT_DIRECTORY.resolve(filename),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException | RuntimeException exception) {
            System.err.println("Unable to save failure screenshot: " + exception.getMessage());
        }
    }

    private String sanitize(String value) {
        return value.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
