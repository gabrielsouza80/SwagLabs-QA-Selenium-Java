package com.swaglabs.tests;

import com.swaglabs.base.BaseTest;
import com.swaglabs.pages.CartPage;
import com.swaglabs.utils.JsonReader;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CartTest extends BaseTest {
    private final JsonReader loginData = new JsonReader("testdata/login-test-data.json");
    private final JsonReader cartData = new JsonReader("testdata/cart-test-data.json");
    private CartPage cartPage;

    @BeforeMethod(alwaysRun = true)
    public void loginBeforeEachTest() {
        loginPage.login(
                loginData.getString("standardUsername"),
                loginData.getString("validPassword"));
        cartPage = new CartPage(driver, config.getLong("timeout"));
        Assert.assertTrue(cartPage.isInventoryPageDisplayed(),
                "Precondition failed: standard_user should reach the inventory page");
    }

    @Test(description = "CT01 - Cart page opens from inventory")
    public void cartPageOpensFromInventory() {
        cartPage.openCart();

        Assert.assertTrue(cartPage.isCartPageDisplayed(),
                "The cart link should open the cart page");
        Assert.assertTrue(driver.getCurrentUrl().contains(cartData.getString("cartPath")),
                "The cart URL should contain the expected path");
    }

    @Test(description = "CT02 - Cart page title is displayed")
    public void cartPageTitleIsDisplayed() {
        cartPage.openCart();

        Assert.assertEquals(cartPage.getPageTitle(), cartData.getString("cartTitle"),
                "The cart should display the expected title");
    }

    @Test(description = "CT03 - Empty cart contains no products")
    public void emptyCartContainsNoProducts() {
        cartPage.openCart();

        Assert.assertEquals(cartPage.getCartItemCount(), cartData.getInt("emptyCartItemCount"),
                "A new user session should open an empty cart");
        Assert.assertFalse(cartPage.isCartBadgeDisplayed(),
                "An empty cart should not display a cart badge");
    }

    @Test(description = "CT04 - Added product details are displayed in cart")
    public void addedProductDetailsAreDisplayedInCart() {
        cartPage.addProductToCart(cartData.getString("productId"));
        Assert.assertEquals(cartPage.getCartBadgeText(), cartData.getString("oneItemBadge"),
                "Adding one product should update the cart badge");

        cartPage.openCart();

        Assert.assertEquals(cartPage.getCartItemCount(), cartData.getInt("oneCartItemCount"),
                "The cart should contain one product");
        Assert.assertEquals(cartPage.getCartItemName(), cartData.getString("productName"),
                "The cart should display the added product name");
        Assert.assertEquals(cartPage.getCartItemPrice(), cartData.getString("productPrice"),
                "The cart should display the added product price");
        Assert.assertEquals(cartPage.getCartItemQuantity(), cartData.getString("productQuantity"),
                "The cart should display the added product quantity");
    }

    @Test(description = "CT05 - Removing product empties cart and removes badge")
    public void removingProductEmptiesCartAndRemovesBadge() {
        String productId = cartData.getString("productId");
        cartPage.addProductToCart(productId);
        Assert.assertEquals(cartPage.getCartBadgeText(), cartData.getString("oneItemBadge"),
                "Precondition failed: the product was not added");
        cartPage.openCart();

        cartPage.removeProduct(productId);

        Assert.assertEquals(cartPage.getCartItemCount(), cartData.getInt("emptyCartItemCount"),
                "Removing the only product should empty the cart");
        Assert.assertFalse(cartPage.isCartBadgeDisplayed(),
                "Removing the only product should remove the cart badge");
    }

    @Test(description = "CT06 - Continue Shopping returns to inventory")
    public void continueShoppingReturnsToInventory() {
        cartPage.openCart();
        cartPage.continueShopping();

        Assert.assertTrue(cartPage.isInventoryPageDisplayed(),
                "Continue Shopping should return to the inventory page");
        Assert.assertTrue(driver.getCurrentUrl().contains(cartData.getString("inventoryPath")),
                "The inventory URL should contain the expected path");
    }

    @Test(description = "CT07 - Checkout button is displayed and enabled")
    public void checkoutButtonIsDisplayedAndEnabled() {
        cartPage.addProductToCart(cartData.getString("productId"));
        cartPage.openCart();

        Assert.assertTrue(cartPage.isCheckoutButtonDisplayed(),
                "The Checkout button should be displayed");
        Assert.assertTrue(cartPage.isCheckoutButtonEnabled(),
                "The Checkout button should be enabled");
    }

    @Test(description = "CT08 - Checkout button opens information page")
    public void checkoutButtonOpensInformationPage() {
        cartPage.addProductToCart(cartData.getString("productId"));
        cartPage.openCart();
        cartPage.proceedToCheckout();

        Assert.assertTrue(cartPage.isCheckoutInformationPageDisplayed(),
                "Checkout should open the information page");
        Assert.assertEquals(cartPage.getPageTitle(),
                cartData.getString("checkoutInformationTitle"),
                "The checkout information page should display the expected title");
        Assert.assertTrue(driver.getCurrentUrl().contains(
                        cartData.getString("checkoutInformationPath")),
                "The checkout information URL should contain the expected path");
    }
}

