package com.swaglabs.pages;

import com.swaglabs.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CartPage extends BasePage {
    private final By inventoryContainer = By.id("inventory_container");
    private final By cartLink = By.cssSelector(".shopping_cart_link");
    private final By cartBadge = By.cssSelector(".shopping_cart_badge");
    private final By pageTitle = By.cssSelector(".title");
    private final By cartItems = By.cssSelector(".cart_item");
    private final By cartQuantity = By.cssSelector(".cart_quantity");
    private final By cartItemName = By.cssSelector(".inventory_item_name");
    private final By cartItemPrice = By.cssSelector(".inventory_item_price");
    private final By continueShoppingButton = By.id("continue-shopping");
    private final By checkoutButton = By.id("checkout");

    public CartPage(WebDriver driver, long timeoutSeconds) {
        super(driver, timeoutSeconds);
    }

    public boolean isInventoryPageDisplayed() {
        return waitForInventoryPage(inventoryContainer);
    }

    public void openCart() {
        click(cartLink);
        waitForUrlContaining("/cart.html");
        waitForVisibility(pageTitle);
    }

    public boolean isCartPageDisplayed() {
        return driver.getCurrentUrl().contains("/cart.html")
                && isDisplayed(pageTitle);
    }

    public String getPageTitle() {
        return getText(pageTitle);
    }

    public boolean hasCartItemCount(int expectedCount) {
        if (waitForElementCount(cartItems, expectedCount)) {
            return true;
        }
        driver.navigate().refresh();
        waitForVisibility(pageTitle);
        return waitForElementCount(cartItems, expectedCount);
    }

    public void addProductToCart(String productId) {
        By addButton = By.cssSelector("[data-test='add-to-cart-" + productId + "']");
        By removeButton = By.cssSelector("[data-test='remove-" + productId + "']");
        clickAndWaitForVisibility(addButton, removeButton);
    }

    public String getCartBadgeText() {
        return getText(cartBadge);
    }

    public boolean isCartBadgeDisplayed() {
        return isDisplayed(cartBadge);
    }

    public String getCartItemName() {
        return getText(cartItemName);
    }

    public String getCartItemPrice() {
        return getText(cartItemPrice);
    }

    public String getCartItemQuantity() {
        return getText(cartQuantity);
    }

    public void removeProduct(String productId) {
        By removeButton = By.cssSelector("[data-test='remove-" + productId + "']");
        clickAndWaitForInvisibility(removeButton, removeButton);
    }

    public void continueShopping() {
        click(continueShoppingButton);
        waitForUrlContaining("/inventory.html");
        waitForVisibility(inventoryContainer);
    }

    public boolean isCheckoutButtonDisplayed() {
        return isDisplayed(checkoutButton);
    }

    public boolean isCheckoutButtonEnabled() {
        WebElement button = waitForVisibility(checkoutButton);
        return button.isDisplayed() && button.isEnabled();
    }

    public void proceedToCheckout() {
        click(checkoutButton);
        if (!waitForUrlContaining("/checkout-step-one.html")) {
            clickWithJavaScript(checkoutButton);
            waitForUrlContaining("/checkout-step-one.html");
        }
        waitForVisibility(pageTitle);
    }

    public boolean isCheckoutInformationPageDisplayed() {
        return driver.getCurrentUrl().contains("/checkout-step-one.html")
                && isDisplayed(pageTitle);
    }
}
