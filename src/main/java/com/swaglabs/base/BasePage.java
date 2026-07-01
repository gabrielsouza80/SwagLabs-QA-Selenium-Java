package com.swaglabs.base;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BasePage {
    private static final Duration POPUP_TIMEOUT = Duration.ofMillis(200);
    private static final By HTML_DIALOG =
            By.cssSelector("[role='dialog'], [aria-modal='true']");
    private static final List<By> DISMISS_BUTTONS = List.of(
            By.cssSelector(
                    "button[data-test='ok'], button[data-test='accept'], "
                            + "button[data-test='close'], button[aria-label='Close'], "
                            + "button[title='Close'], [role='button'][aria-label='Close']"),
            By.xpath(
                    ".//button[translate(normalize-space(.), "
                            + "'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')="
                            + "'ok' or translate(normalize-space(.), "
                            + "'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')="
                            + "'accept' or translate(normalize-space(.), "
                            + "'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')="
                            + "'close' or normalize-space(.)='×']"));

    protected final WebDriver driver;
    private final WebDriverWait wait;

    protected BasePage(WebDriver driver, long timeoutSeconds) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
    }

    protected void click(By locator) {
        try {
            clickWithAlertRecovery(locator);
        } catch (ElementClickInterceptedException exception) {
            if (!closeModalIfPresent()) {
                throw exception;
            }
            clickWithAlertRecovery(locator);
        }
    }

    protected void type(By locator, String text) {
        WebElement element = waitForVisibility(locator);
        element.clear();
        element.sendKeys(text);
    }

    protected String getText(By locator) {
        return waitForVisibility(locator).getText();
    }

    protected boolean isDisplayed(By locator) {
        try {
            return waitForVisibility(locator).isDisplayed();
        } catch (TimeoutException exception) {
            return false;
        }
    }

    protected WebElement waitForVisibility(By locator) {
        return executeWithAlertRecovery(
                () -> wait.until(ExpectedConditions.visibilityOfElementLocated(locator)));
    }

    protected boolean waitForUrlContaining(String value) {
        try {
            return executeWithAlertRecovery(
                    () -> wait.until(ExpectedConditions.urlContains(value)));
        } catch (TimeoutException exception) {
            return false;
        }
    }

    public boolean dismissUnexpectedPopupIfPresent() {
        boolean alertAccepted = acceptAlertIfPresent();
        boolean modalClosed = closeModalIfPresent();
        return alertAccepted || modalClosed;
    }

    private boolean acceptAlertIfPresent() {
        try {
            Alert alert = new WebDriverWait(driver, POPUP_TIMEOUT)
                    .until(ExpectedConditions.alertIsPresent());
            if (alert == null) {
                return false;
            }
            alert.accept();
            return true;
        } catch (TimeoutException | NoAlertPresentException exception) {
            return false;
        }
    }

    private boolean closeModalIfPresent() {
        try {
            WebElement dialog = driver.findElements(HTML_DIALOG).stream()
                    .filter(WebElement::isDisplayed)
                    .findFirst()
                    .orElse(null);
            if (dialog == null) {
                return false;
            }

            WebElement dismissButton = new WebDriverWait(driver, POPUP_TIMEOUT)
                    .until(ignored -> findDismissButton(dialog));
            dismissButton.click();
            return true;
        } catch (TimeoutException | StaleElementReferenceException exception) {
            return false;
        }
    }

    private WebElement findDismissButton(WebElement dialog) {
        return DISMISS_BUTTONS.stream()
                .flatMap(locator -> dialog.findElements(locator).stream())
                .filter(WebElement::isDisplayed)
                .filter(WebElement::isEnabled)
                .findFirst()
                .orElse(null);
    }

    private void clickWithAlertRecovery(By locator) {
        executeWithAlertRecovery(() -> {
            wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
            return null;
        });
    }

    private <T> T executeWithAlertRecovery(Supplier<T> action) {
        try {
            return action.get();
        } catch (UnhandledAlertException exception) {
            if (!acceptAlertIfPresent()) {
                throw exception;
            }
            return action.get();
        }
    }
}
