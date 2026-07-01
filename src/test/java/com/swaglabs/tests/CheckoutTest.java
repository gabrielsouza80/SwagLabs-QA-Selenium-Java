package com.swaglabs.tests;

import com.swaglabs.base.BaseTest;
import com.swaglabs.pages.CartPage;
import com.swaglabs.pages.CheckoutPage;
import com.swaglabs.utils.JsonReader;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CheckoutTest extends BaseTest {
    private final JsonReader checkoutData =
            new JsonReader("testdata/checkout-test-data.json");
    private CheckoutPage checkoutPage;

    @BeforeMethod(alwaysRun = true)
    public void openCheckoutInformationBeforeEachTest() {
        loginAsStandardUser();

        CartPage cartPage = new CartPage(driver, config.getLong("timeout"));
        Assert.assertTrue(cartPage.isInventoryPageDisplayed(),
                "Precondition failed: standard_user should reach the inventory page");
        cartPage.addProductToCart(checkoutData.getString("productId"));
        Assert.assertEquals(cartPage.getCartBadgeText(),
                checkoutData.getString("oneItemBadge"),
                "Precondition failed: the product was not added");
        cartPage.openCart();
        Assert.assertTrue(cartPage.hasCartItemCount(
                        checkoutData.getInt("expectedCartItemCount")),
                "Precondition failed: the cart should contain one product");
        cartPage.proceedToCheckout();

        checkoutPage = new CheckoutPage(driver, config.getLong("timeout"));
        Assert.assertTrue(checkoutPage.isInformationPageDisplayed(),
                "Precondition failed: checkout information should open");
    }

    @Test(description = "CO01 - Checkout information page opens with expected title")
    public void checkoutInformationPageOpens() {
        Assert.assertTrue(checkoutPage.isInformationPageDisplayed(),
                "The checkout information page should be displayed");
        Assert.assertEquals(checkoutPage.getPageTitle(),
                checkoutData.getString("informationTitle"),
                "The checkout information page should display the expected title");
    }

    @Test(description = "CO02 - Checkout information fields are displayed")
    public void checkoutInformationFieldsAreDisplayed() {
        Assert.assertTrue(checkoutPage.areInformationFieldsDisplayed(),
                "First name, last name, and postal code fields should be displayed");
    }

    @Test(description = "CO03 - First name is required")
    public void firstNameIsRequired() {
        checkoutPage.clickContinue();

        Assert.assertEquals(checkoutPage.getErrorMessage(),
                checkoutData.getString("requiredFirstNameMessage"),
                "Submitting empty information should require the first name");
    }

    @Test(description = "CO04 - Last name is required")
    public void lastNameIsRequired() {
        checkoutPage.enterFirstName(checkoutData.getString("firstName"));
        checkoutPage.clickContinue();

        Assert.assertEquals(checkoutPage.getErrorMessage(),
                checkoutData.getString("requiredLastNameMessage"),
                "Submitting without a last name should show the required error");
    }

    @Test(description = "CO05 - Postal code is required")
    public void postalCodeIsRequired() {
        checkoutPage.enterFirstName(checkoutData.getString("firstName"));
        checkoutPage.enterLastName(checkoutData.getString("lastName"));
        checkoutPage.clickContinue();

        Assert.assertEquals(checkoutPage.getErrorMessage(),
                checkoutData.getString("requiredPostalCodeMessage"),
                "Submitting without a postal code should show the required error");
    }

    @Test(description = "CO06 - Valid information opens checkout overview")
    public void validInformationOpensOverview() {
        completeCheckoutInformation();

        Assert.assertTrue(checkoutPage.isOverviewPageDisplayed(),
                "Valid customer information should open the checkout overview");
        Assert.assertEquals(checkoutPage.getPageTitle(),
                checkoutData.getString("overviewTitle"),
                "The overview page should display the expected title");
    }

    @Test(description = "CO07 - Checkout overview displays product and totals")
    public void overviewDisplaysProductAndTotals() {
        completeCheckoutInformation();

        Assert.assertEquals(checkoutPage.getItemName(),
                checkoutData.getString("productName"),
                "The overview should display the selected product");
        Assert.assertEquals(checkoutPage.getItemPrice(),
                checkoutData.getString("productPrice"),
                "The overview should display the product price");
        Assert.assertEquals(checkoutPage.getPaymentInformation(),
                checkoutData.getString("paymentInformation"),
                "The overview should display payment information");
        Assert.assertEquals(checkoutPage.getShippingInformation(),
                checkoutData.getString("shippingInformation"),
                "The overview should display shipping information");
        Assert.assertEquals(checkoutPage.getItemTotal(),
                checkoutData.getString("itemTotal"),
                "The overview should display the item total");
        Assert.assertEquals(checkoutPage.getTax(), checkoutData.getString("tax"),
                "The overview should display tax");
        Assert.assertEquals(checkoutPage.getTotal(), checkoutData.getString("total"),
                "The overview should display the final total");
    }

    @Test(description = "CO08 - Cancel from information returns to cart")
    public void cancelFromInformationReturnsToCart() {
        checkoutPage.cancelFromInformationPage();

        Assert.assertTrue(checkoutPage.isCartPageDisplayed(),
                "Cancel from checkout information should return to the cart");
        Assert.assertEquals(checkoutPage.getPageTitle(),
                checkoutData.getString("cartTitle"),
                "The cart should display the expected title");
    }

    @Test(description = "CO09 - Cancel from overview returns to inventory")
    public void cancelFromOverviewReturnsToInventory() {
        completeCheckoutInformation();
        checkoutPage.cancelFromOverviewPage();

        Assert.assertTrue(checkoutPage.isInventoryPageDisplayed(),
                "Cancel from checkout overview should return to inventory");
    }

    @Test(description = "CO10 - Checkout completes and returns home")
    public void checkoutCompletesAndReturnsHome() {
        completeCheckoutInformation();
        checkoutPage.finishCheckout();

        Assert.assertTrue(checkoutPage.isCompletePageDisplayed(),
                "Finishing checkout should open the complete page");
        Assert.assertEquals(checkoutPage.getPageTitle(),
                checkoutData.getString("completeTitle"),
                "The complete page should display the expected title");
        Assert.assertEquals(checkoutPage.getCompleteHeader(),
                checkoutData.getString("completeHeader"),
                "The complete page should display the success header");
        Assert.assertTrue(checkoutPage.getCompleteText().contains(
                        checkoutData.getString("completeTextContains")),
                "The complete page should display the order confirmation message");

        checkoutPage.returnToInventory();

        Assert.assertTrue(checkoutPage.isInventoryPageDisplayed(),
                "Back Home should return to the inventory page");
    }

    private void completeCheckoutInformation() {
        checkoutPage.completeInformation(
                checkoutData.getString("firstName"),
                checkoutData.getString("lastName"),
                checkoutData.getString("postalCode"));
        Assert.assertTrue(checkoutPage.isOverviewPageDisplayed(),
                "Valid customer information should open the checkout overview");
    }
}
