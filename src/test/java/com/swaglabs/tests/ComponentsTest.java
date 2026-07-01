package com.swaglabs.tests;

import com.swaglabs.base.BaseTest;
import com.swaglabs.pages.ComponentsPage;
import com.swaglabs.utils.JsonReader;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ComponentsTest extends BaseTest {
    private final JsonReader componentsData =
            new JsonReader("testdata/components-test-data.json");
    private ComponentsPage componentsPage;

    @BeforeMethod(alwaysRun = true)
    public void loginBeforeEachTest() {
        loginAsStandardUser();
        componentsPage = new ComponentsPage(driver, config.getLong("timeout"));
        Assert.assertTrue(componentsPage.isInventoryPageDisplayed(),
                "Precondition failed: standard_user should reach the inventory page");
    }

    @Test(description = "CP01 - SauceDemo logo is displayed")
    public void appLogoIsDisplayed() {
        Assert.assertTrue(componentsPage.isAppLogoDisplayed(),
                "The application logo should be displayed");
        Assert.assertEquals(componentsPage.getAppLogoText(),
                componentsData.getString("expectedLogo"),
                "The application logo should contain the expected text");
    }

    @Test(description = "CP02 - Cart icon is displayed")
    public void cartIconIsDisplayed() {
        Assert.assertTrue(componentsPage.isCartLinkDisplayed(),
                "The cart icon should be displayed");
    }

    @Test(description = "CP03 - Side menu button is displayed")
    public void sideMenuButtonIsDisplayed() {
        Assert.assertTrue(componentsPage.isMenuButtonDisplayed(),
                "The side menu button should be displayed");
    }

    @Test(description = "CP04 - Side menu opens")
    public void sideMenuOpens() {
        componentsPage.openSideMenu();

        Assert.assertTrue(componentsPage.isSideMenuDisplayed(),
                "The side menu should be displayed after clicking the menu button");
    }

    @Test(description = "CP05 - Side menu contains expected links")
    public void sideMenuContainsExpectedLinks() {
        componentsPage.openSideMenu();

        Assert.assertTrue(componentsPage.areSideMenuLinksDisplayed(),
                "Every expected side menu link should be displayed");
        Assert.assertEquals(componentsPage.getSideMenuLinkTexts(),
                componentsData.getStringList("expectedMenuLinks"),
                "The side menu should contain the expected links in order");
    }

    @Test(description = "CP06 - Footer is displayed")
    public void footerIsDisplayed() {
        Assert.assertTrue(componentsPage.isFooterDisplayed(),
                "The footer should be displayed");
    }

    @Test(description = "CP07 - Footer social links are displayed")
    public void footerSocialLinksAreDisplayed() {
        Assert.assertTrue(componentsPage.areSocialLinksDisplayed(),
                "Twitter/X, Facebook, and LinkedIn links should be displayed");
    }

    @Test(description = "CP08 - Footer copyright text is displayed")
    public void footerCopyrightIsDisplayed() {
        String copyrightText = componentsPage.getCopyrightText();

        Assert.assertFalse(copyrightText.isBlank(),
                "The footer copyright text should not be empty");
        Assert.assertTrue(copyrightText.contains(
                        componentsData.getString("expectedCopyrightText")),
                "The footer should contain the expected copyright text");
    }

    @Test(description = "CP09 - Cart link opens cart and supports returning to inventory")
    public void cartLinkNavigatesToCartAndReturnsToInventory() {
        componentsPage.openCart();
        Assert.assertTrue(componentsPage.isCartPageDisplayed(
                        componentsData.getString("expectedCartTitle")),
                "The cart link should navigate to the cart page");

        componentsPage.returnToInventory();

        Assert.assertTrue(componentsPage.isInventoryPageDisplayed(),
                "Browser back should return to the inventory page");
    }
}
