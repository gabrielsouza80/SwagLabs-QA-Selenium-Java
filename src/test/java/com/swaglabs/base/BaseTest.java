package com.swaglabs.base;

import com.swaglabs.pages.LoginPage;
import com.swaglabs.utils.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class BaseTest {
    protected WebDriver driver;
    protected LoginPage loginPage;
    protected ConfigReader config;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        config = new ConfigReader();
        if (!"chrome".equalsIgnoreCase(config.get("browser"))) {
            throw new IllegalArgumentException("Unsupported browser: " + config.get("browser"));
        }

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        if (config.getBoolean("headless")) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
        }
        options.addArguments("--window-size=1920,1080");
        options.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.IGNORE);

        driver = new ChromeDriver(options);
        driver.get(config.get("baseUrl"));
        loginPage = new LoginPage(driver, config.getLong("timeout"));
        loginPage.dismissUnexpectedPopupIfPresent();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    public WebDriver getDriver() {
        return driver;
    }
}
