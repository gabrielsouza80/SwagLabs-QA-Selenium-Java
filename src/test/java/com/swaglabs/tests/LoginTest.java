package com.swaglabs.tests;

import com.swaglabs.base.BaseTest;
import com.swaglabs.utils.JsonReader;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {
    private final JsonReader data = new JsonReader("testdata/login-test-data.json");

    @Test(description = "TC01 - Successful login with standard_user")
    public void standardUserCanLogin() {
        loginAs("standardUsername", "validPassword");
        assertAuthenticated("standard_user should reach the inventory page");
    }

    @Test(description = "TC02 - Empty username validation")
    public void emptyUsernameIsRejected() {
        loginPage.login(data.getString("emptyValue"), data.getString("validPassword"));
        Assert.assertEquals(loginPage.getErrorMessage(), data.getString("requiredUsernameMessage"),
                "An empty username should show the required username validation");
    }

    @Test(description = "TC03 - Empty password validation")
    public void emptyPasswordIsRejected() {
        loginPage.login(data.getString("standardUsername"), data.getString("emptyValue"));
        Assert.assertEquals(loginPage.getErrorMessage(), data.getString("requiredPasswordMessage"),
                "An empty password should show the required password validation");
    }

    @Test(description = "TC04 - Logout after login")
    public void authenticatedUserCanLogout() {
        loginAs("standardUsername", "validPassword");
        assertAuthenticated("Precondition failed: standard_user was not logged in");

        loginPage.logout();

        Assert.assertEquals(driver.getCurrentUrl(), config.get("baseUrl"),
                "Logout should return the user to the login URL");
        Assert.assertTrue(loginPage.isLoginButtonDisplayed(),
                "The login button should be visible after logout");
    }

    @Test(description = "TC05 - Non-existing username")
    public void nonExistingUsernameIsRejected() {
        loginAs("invalidUsername", "validPassword");
        assertInvalidCredentials("A non-existing username should be rejected");
    }

    @Test(description = "TC06 - Incorrect password")
    public void incorrectPasswordIsRejected() {
        loginAs("standardUsername", "wrongPassword");
        assertInvalidCredentials("An incorrect password should be rejected");
    }

    @Test(description = "TC07 - Username and password above 255 characters")
    public void credentialsAbove255CharactersAreRejected() {
        String oversizedValue = data.getString("longInputCharacter")
                .repeat(data.getInt("maxLength") + 1);
        loginPage.login(oversizedValue, oversizedValue);
        assertInvalidCredentials("Credentials above 255 characters should be rejected");
    }

    @Test(description = "TC08 - Multiple wrong password attempts")
    public void multipleWrongPasswordAttemptsRemainRejected() {
        for (int attempt = 1; attempt <= data.getInt("invalidAttempts"); attempt++) {
            loginAs("standardUsername", "wrongPassword");
            Assert.assertEquals(loginPage.getErrorMessage(), data.getString("invalidCredentialsMessage"),
                    "Wrong password attempt " + attempt + " should be rejected");
            Assert.assertFalse(driver.getCurrentUrl().contains(data.getString("inventoryPath")),
                    "Wrong password attempt " + attempt + " must not authenticate the user");
        }
    }

    @Test(description = "TC09 - locked_out_user cannot login")
    public void lockedOutUserCannotLogin() {
        loginAs("lockedUsername", "validPassword");
        Assert.assertEquals(loginPage.getErrorMessage(), data.getString("lockedUserMessage"),
                "The locked user should see the locked-out error");
        Assert.assertFalse(driver.getCurrentUrl().contains(data.getString("inventoryPath")),
                "The locked user must not reach the inventory page");
    }

    @Test(description = "TC10 - problem_user can login")
    public void problemUserCanLogin() {
        loginAs("problemUsername", "validPassword");
        assertAuthenticated("problem_user should reach the inventory page");
    }

    @Test(description = "TC11 - performance_glitch_user can login")
    public void performanceGlitchUserCanLogin() {
        loginAs("performanceUsername", "validPassword");
        assertAuthenticated("performance_glitch_user should reach the inventory page");
    }

    @Test(description = "TC12 - Unauthenticated user cannot access checkout page")
    public void unauthenticatedUserCannotAccessCheckout() {
        driver.get(data.getString("unauthorizedCheckoutUrl"));

        Assert.assertTrue(loginPage.isLoginButtonDisplayed(),
                "The login page should remain available after the blocked checkout request");
        Assert.assertEquals(loginPage.getErrorMessage(),
                data.getString("unauthorizedCheckoutMessage"),
                "Direct checkout access should explain that authentication is required");
    }

    private void loginAs(String usernameKey, String passwordKey) {
        loginPage.login(data.getString(usernameKey), data.getString(passwordKey));
    }

    private void assertAuthenticated(String message) {
        Assert.assertTrue(loginPage.isInventoryPageDisplayed(), message);
        Assert.assertTrue(driver.getCurrentUrl().contains(data.getString("inventoryPath")), message);
    }

    private void assertInvalidCredentials(String message) {
        Assert.assertEquals(loginPage.getErrorMessage(), data.getString("invalidCredentialsMessage"), message);
        Assert.assertFalse(driver.getCurrentUrl().contains(data.getString("inventoryPath")), message);
    }
}
