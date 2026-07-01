package com.swaglabs.pages;

import com.swaglabs.base.BasePage;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class HomePage extends BasePage {
    private final By inventoryContainer = By.id("inventory_container");
    private final By productCards = By.cssSelector(".inventory_item");
    private final By productNames = By.cssSelector(".inventory_item_name");
    private final By productPrices = By.cssSelector(".inventory_item_price");
    private final By productImages = By.cssSelector(".inventory_item_img img");
    private final By sortDropdown = By.cssSelector(".product_sort_container");
    private final By cartBadge = By.cssSelector(".shopping_cart_badge");
    private final By menuButton = By.id("react-burger-menu-btn");
    private final By logoutLink = By.id("logout_sidebar_link");

    public HomePage(WebDriver driver, long timeoutSeconds) {
        super(driver, timeoutSeconds);
    }

    public boolean isInventoryPageDisplayed() {
        return waitForUrlContaining("/inventory.html") && isDisplayed(inventoryContainer);
    }

    public int getProductCardCount() {
        waitForVisibility(productCards);
        return driver.findElements(productCards).size();
    }

    public boolean areAllProductCardsDisplayed() {
        return areAllElementsDisplayed(productCards);
    }

    public List<String> getProductNames() {
        waitForVisibility(productNames);
        return driver.findElements(productNames).stream()
                .map(WebElement::getText)
                .toList();
    }

    public List<Double> getProductPrices() {
        waitForVisibility(productPrices);
        return driver.findElements(productPrices).stream()
                .map(WebElement::getText)
                .map(price -> Double.parseDouble(price.replace("$", "")))
                .toList();
    }

    public boolean areAllProductImagesDisplayed() {
        waitForVisibility(productImages);
        JavascriptExecutor javascript = (JavascriptExecutor) driver;
        return driver.findElements(productImages).stream()
                .allMatch(image -> image.isDisplayed()
                        && Boolean.TRUE.equals(javascript.executeScript(
                                "return arguments[0].complete && arguments[0].naturalWidth > 0",
                                image)));
    }

    public void addProductToCart(String productId) {
        click(By.cssSelector("[data-test='add-to-cart-" + productId + "']"));
    }

    public void removeProductFromCart(String productId) {
        click(By.cssSelector("[data-test='remove-" + productId + "']"));
    }

    public String getCartBadgeText() {
        return getText(cartBadge);
    }

    public boolean isCartBadgeDisplayed() {
        return isDisplayed(cartBadge);
    }

    public void sortProductsBy(String value) {
        new Select(waitForVisibility(sortDropdown)).selectByValue(value);
    }

    public void openSideMenu() {
        click(menuButton);
        waitForVisibility(logoutLink);
    }

    public boolean isLogoutLinkDisplayed() {
        return isDisplayed(logoutLink);
    }

    public void logout() {
        openSideMenu();
        click(logoutLink);
    }

    private boolean areAllElementsDisplayed(By locator) {
        waitForVisibility(locator);
        List<WebElement> elements = driver.findElements(locator);
        return !elements.isEmpty() && elements.stream().allMatch(WebElement::isDisplayed);
    }

}
