package com.swaglabs.tests;

import com.swaglabs.base.BaseTest;
import com.swaglabs.pages.HomePage;
import com.swaglabs.utils.JsonReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class HomePageTest extends BaseTest {
    private final JsonReader loginData = new JsonReader("testdata/login-test-data.json");
    private final JsonReader homePageData = new JsonReader("testdata/homepage-test-data.json");
    private HomePage homePage;

    @BeforeMethod(alwaysRun = true)
    public void loginBeforeEachTest() {
        loginPage.login(
                loginData.getString("standardUsername"),
                loginData.getString("validPassword"));
        homePage = new HomePage(driver, config.getLong("timeout"));
        Assert.assertTrue(homePage.isInventoryPageDisplayed(),
                "Precondition failed: standard_user should reach the inventory page");
    }

    @Test(description = "HP01 - Inventory page is displayed after login")
    public void inventoryPageIsDisplayed() {
        Assert.assertTrue(homePage.isInventoryPageDisplayed(),
                "The inventory page should be visible after login");
    }

    @Test(description = "HP02 - Product cards are displayed")
    public void productCardsAreDisplayed() {
        Assert.assertEquals(homePage.getProductCardCount(), homePageData.getInt("expectedProductCount"),
                "The inventory should contain the expected number of product cards");
        Assert.assertTrue(homePage.areAllProductCardsDisplayed(),
                "Every product card should be displayed");
    }

    @Test(description = "HP03 - Product names are displayed")
    public void productNamesAreDisplayed() {
        List<String> names = homePage.getProductNames();

        Assert.assertEquals(names.size(), homePageData.getInt("expectedProductCount"),
                "Every product should have a name");
        Assert.assertTrue(names.stream().allMatch(name -> !name.isBlank()),
                "Product names should not be empty");
    }

    @Test(description = "HP04 - Product prices are displayed")
    public void productPricesAreDisplayed() {
        List<Double> prices = homePage.getProductPrices();

        Assert.assertEquals(prices.size(), homePageData.getInt("expectedProductCount"),
                "Every product should have a price");
        Assert.assertTrue(prices.stream().allMatch(price -> price > 0),
                "Every product price should be greater than zero");
    }

    @Test(description = "HP05 - Product images are displayed")
    public void productImagesAreDisplayed() {
        Assert.assertTrue(homePage.areAllProductImagesDisplayed(),
                "Every product image should be visible and loaded");
    }

    @Test(description = "HP06 - Adding one product updates the cart badge")
    public void addingProductUpdatesCartBadge() {
        homePage.addProductToCart(homePageData.getString("productId"));

        Assert.assertEquals(homePage.getCartBadgeText(), homePageData.getString("oneItemBadge"),
                "Adding one product should update the cart badge to one");
    }

    @Test(description = "HP07 - Removing one product removes the cart badge")
    public void removingProductUpdatesCartBadge() {
        String productId = homePageData.getString("productId");
        homePage.addProductToCart(productId);
        Assert.assertEquals(homePage.getCartBadgeText(), homePageData.getString("oneItemBadge"),
                "Precondition failed: the product was not added");

        homePage.removeProductFromCart(productId);

        Assert.assertFalse(homePage.isCartBadgeDisplayed(),
                "Removing the only product should remove the cart badge");
    }

    @Test(description = "HP08 - Products sort by Name A to Z")
    public void productsSortByNameAscending() {
        homePage.sortProductsBy(homePageData.getString("sortNameAscending"));

        List<String> actualNames = homePage.getProductNames();
        List<String> expectedNames = new ArrayList<>(actualNames);
        expectedNames.sort(String.CASE_INSENSITIVE_ORDER);
        Assert.assertEquals(actualNames, expectedNames,
                "Products should be sorted by name from A to Z");
    }

    @Test(description = "HP09 - Products sort by Name Z to A")
    public void productsSortByNameDescending() {
        homePage.sortProductsBy(homePageData.getString("sortNameDescending"));

        List<String> actualNames = homePage.getProductNames();
        List<String> expectedNames = new ArrayList<>(actualNames);
        expectedNames.sort(String.CASE_INSENSITIVE_ORDER.reversed());
        Assert.assertEquals(actualNames, expectedNames,
                "Products should be sorted by name from Z to A");
    }

    @Test(description = "HP10 - Products sort by Price low to high")
    public void productsSortByPriceAscending() {
        homePage.sortProductsBy(homePageData.getString("sortPriceAscending"));

        List<Double> actualPrices = homePage.getProductPrices();
        List<Double> expectedPrices = new ArrayList<>(actualPrices);
        expectedPrices.sort(Comparator.naturalOrder());
        Assert.assertEquals(actualPrices, expectedPrices,
                "Products should be sorted by price from low to high");
    }

    @Test(description = "HP11 - Products sort by Price high to low")
    public void productsSortByPriceDescending() {
        homePage.sortProductsBy(homePageData.getString("sortPriceDescending"));

        List<Double> actualPrices = homePage.getProductPrices();
        List<Double> expectedPrices = new ArrayList<>(actualPrices);
        expectedPrices.sort(Comparator.reverseOrder());
        Assert.assertEquals(actualPrices, expectedPrices,
                "Products should be sorted by price from high to low");
    }

    @Test(description = "HP12 - Side menu opens")
    public void sideMenuOpens() {
        homePage.openSideMenu();

        Assert.assertTrue(homePage.isLogoutLinkDisplayed(),
                "The logout link should be visible when the side menu opens");
    }

    @Test(description = "HP13 - User can logout from the side menu")
    public void userCanLogoutFromSideMenu() {
        homePage.logout();

        Assert.assertEquals(driver.getCurrentUrl(), config.get("baseUrl"),
                "Logout should return the user to the login URL");
        Assert.assertTrue(loginPage.isLoginButtonDisplayed(),
                "The login button should be visible after logout");
    }
}
