package com.swaglabs.pages;

import com.swaglabs.base.BasePage;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ComponentsPage extends BasePage {
    private final By appLogo = By.cssSelector(".app_logo");
    private final By cartLink = By.cssSelector(".shopping_cart_link");
    private final By menuButton = By.id("react-burger-menu-btn");
    private final By sideMenu = By.cssSelector(".bm-menu");
    private final By allItemsLink = By.id("inventory_sidebar_link");
    private final By aboutLink = By.id("about_sidebar_link");
    private final By logoutLink = By.id("logout_sidebar_link");
    private final By resetAppStateLink = By.id("reset_sidebar_link");
    private final By footer = By.cssSelector(".footer");
    private final By twitterLink = By.cssSelector(".social_twitter a");
    private final By facebookLink = By.cssSelector(".social_facebook a");
    private final By linkedInLink = By.cssSelector(".social_linkedin a");
    private final By copyright = By.cssSelector(".footer_copy");
    private final By pageTitle = By.cssSelector(".title");
    private final By inventoryContainer = By.id("inventory_container");

    public ComponentsPage(WebDriver driver, long timeoutSeconds) {
        super(driver, timeoutSeconds);
    }

    public boolean isAppLogoDisplayed() {
        return isDisplayed(appLogo);
    }

    public String getAppLogoText() {
        return getText(appLogo);
    }

    public boolean isCartLinkDisplayed() {
        return isDisplayed(cartLink);
    }

    public boolean isMenuButtonDisplayed() {
        return isDisplayed(menuButton);
    }

    public void openSideMenu() {
        click(menuButton);
        waitForVisibility(sideMenu);
    }

    public boolean isSideMenuDisplayed() {
        return isDisplayed(sideMenu);
    }

    public boolean areSideMenuLinksDisplayed() {
        return areAllElementsDisplayed(List.of(
                allItemsLink,
                aboutLink,
                logoutLink,
                resetAppStateLink));
    }

    public List<String> getSideMenuLinkTexts() {
        return List.of(
                getText(allItemsLink),
                getText(aboutLink),
                getText(logoutLink),
                getText(resetAppStateLink));
    }

    public boolean isFooterDisplayed() {
        return isDisplayed(footer);
    }

    public boolean areSocialLinksDisplayed() {
        return areAllElementsDisplayed(List.of(twitterLink, facebookLink, linkedInLink));
    }

    public String getCopyrightText() {
        return getText(copyright);
    }

    public void openCart() {
        click(cartLink);
        waitForUrlContaining("/cart.html");
    }

    public boolean isCartPageDisplayed(String expectedTitle) {
        return driver.getCurrentUrl().contains("/cart.html")
                && expectedTitle.equals(getText(pageTitle));
    }

    public void returnToInventory() {
        driver.navigate().back();
        waitForUrlContaining("/inventory.html");
        waitForVisibility(inventoryContainer);
    }

    public boolean isInventoryPageDisplayed() {
        return driver.getCurrentUrl().contains("/inventory.html")
                && isDisplayed(inventoryContainer);
    }

    private boolean areAllElementsDisplayed(List<By> locators) {
        return locators.stream()
                .map(this::waitForVisibility)
                .allMatch(WebElement::isDisplayed);
    }
}

