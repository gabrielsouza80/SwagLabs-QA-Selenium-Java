package com.swaglabs.pages;

import com.swaglabs.base.BasePage;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CheckoutPage extends BasePage {
    private final By firstNameField = By.id("first-name");
    private final By lastNameField = By.id("last-name");
    private final By postalCodeField = By.id("postal-code");
    private final By continueButton = By.id("continue");
    private final By cancelButton = By.id("cancel");
    private final By finishButton = By.id("finish");
    private final By backHomeButton = By.id("back-to-products");
    private final By pageTitle = By.cssSelector(".title");
    private final By errorMessage = By.cssSelector("[data-test='error']");
    private final By cartItem = By.cssSelector(".cart_item");
    private final By itemName = By.cssSelector(".inventory_item_name");
    private final By itemPrice = By.cssSelector(".inventory_item_price");
    private final By summaryInfo = By.cssSelector(".summary_info");
    private final By paymentInfo = By.cssSelector("[data-test='payment-info-value']");
    private final By shippingInfo = By.cssSelector("[data-test='shipping-info-value']");
    private final By itemTotal = By.cssSelector("[data-test='subtotal-label']");
    private final By tax = By.cssSelector("[data-test='tax-label']");
    private final By total = By.cssSelector("[data-test='total-label']");
    private final By completeHeader = By.cssSelector(".complete-header");
    private final By completeText = By.cssSelector(".complete-text");
    private final By inventoryContainer = By.id("inventory_container");

    public CheckoutPage(WebDriver driver, long timeoutSeconds) {
        super(driver, timeoutSeconds);
    }

    public boolean isInformationPageDisplayed() {
        return waitForUrlContaining("/checkout-step-one.html")
                && isDisplayed(firstNameField);
    }

    public String getPageTitle() {
        return getText(pageTitle);
    }

    public boolean areInformationFieldsDisplayed() {
        return areAllElementsDisplayed(
                List.of(firstNameField, lastNameField, postalCodeField));
    }

    public void enterFirstName(String firstName) {
        type(firstNameField, firstName);
    }

    public void enterLastName(String lastName) {
        type(lastNameField, lastName);
    }

    public void enterPostalCode(String postalCode) {
        type(postalCodeField, postalCode);
    }

    public void clickContinue() {
        click(continueButton);
    }

    public String getErrorMessage() {
        return getText(errorMessage);
    }

    public void completeInformation(
            String firstName, String lastName, String postalCode) {
        enterFirstName(firstName);
        enterLastName(lastName);
        enterPostalCode(postalCode);
        clickAndWaitForVisibility(continueButton, summaryInfo);
    }

    public boolean isOverviewPageDisplayed() {
        return waitForUrlContaining("/checkout-step-two.html")
                && isDisplayed(summaryInfo);
    }

    public String getItemName() {
        return getText(itemName);
    }

    public String getItemPrice() {
        return getText(itemPrice);
    }

    public String getPaymentInformation() {
        return getText(paymentInfo);
    }

    public String getShippingInformation() {
        return getText(shippingInfo);
    }

    public String getItemTotal() {
        return getText(itemTotal);
    }

    public String getTax() {
        return getText(tax);
    }

    public String getTotal() {
        return getText(total);
    }

    public void cancelFromInformationPage() {
        clickAndWaitForVisibility(cancelButton, cartItem);
        waitForUrlContaining("/cart.html");
    }

    public boolean isCartPageDisplayed() {
        return driver.getCurrentUrl().contains("/cart.html")
                && isDisplayed(cartItem);
    }

    public void cancelFromOverviewPage() {
        clickAndWaitForVisibility(cancelButton, inventoryContainer);
        waitForInventoryPage(inventoryContainer);
    }

    public void finishCheckout() {
        clickAndWaitForVisibility(finishButton, completeHeader);
        waitForUrlContaining("/checkout-complete.html");
    }

    public boolean isCompletePageDisplayed() {
        return driver.getCurrentUrl().contains("/checkout-complete.html")
                && isDisplayed(completeHeader);
    }

    public String getCompleteHeader() {
        return getText(completeHeader);
    }

    public String getCompleteText() {
        return getText(completeText);
    }

    public void returnToInventory() {
        clickAndWaitForVisibility(backHomeButton, inventoryContainer);
        waitForInventoryPage(inventoryContainer);
    }

    public boolean isInventoryPageDisplayed() {
        return waitForInventoryPage(inventoryContainer);
    }

    private boolean areAllElementsDisplayed(List<By> locators) {
        return locators.stream()
                .map(this::waitForVisibility)
                .allMatch(WebElement::isDisplayed);
    }
}

