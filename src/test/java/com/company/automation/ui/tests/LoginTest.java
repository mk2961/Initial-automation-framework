package com.company.automation.ui.tests;

import com.company.automation.ui.pages.LoginPage;
import com.company.automation.ui.pages.SecurePage;
import com.company.automation.utils.ScreenshotUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class LoginTest extends BaseUiTest {

    private LoginPage loginPage;
    private SecurePage securePage;

    @BeforeEach
    void setUpPages() {
        loginPage = new LoginPage(page);
        securePage = new SecurePage(page);
    }

    @Test
    void shouldOpenLoginPage() {
        String loginUrl = "https://the-internet.herokuapp.com/login";

        page.navigate(loginUrl);

        assertEquals("The Internet", page.title());

        loginPage.login("tomsmith", "SuperSecretPassword!");

        assertEquals(
                "https://the-internet.herokuapp.com/secure",
                page.url());

        ScreenshotUtils.capture(page, "login-success");

        assertThat(securePage.getSuccessMessage())
                .containsText("You logged into a secure area!");

        securePage.logout();

        assertEquals(loginUrl, page.url());

    }

    @Test
    void shouldNotLogin() {
        String loginUrl = "https://the-internet.herokuapp.com/login";

        page.navigate(loginUrl);

        assertEquals("The Internet", page.title());

        loginPage.login("tomsmith", "abc");

        assertEquals(loginUrl, page.url());

        ScreenshotUtils.capture(page, "login-failed");

        assertThat(loginPage.getFlashMessage())
                .containsText("Your password is invalid!");

    }
}