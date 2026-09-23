package com.company.automation.ui.tests;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.company.automation.utils.ScreenshotUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

public abstract class BaseUiTest {

    protected Playwright playwright;
    protected Browser browser;
    protected Page page;

    @RegisterExtension
    final TestExecutionExceptionHandler screenshotOnFailure = new TestExecutionExceptionHandler() {
        @Override
        public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
            if (page != null) {
                String testName = context.getRequiredTestMethod().getName();
                ScreenshotUtils.capture(page, testName + "-failed");
            }
            throw throwable;
        }
    };

    @BeforeEach
    void setUp() {

        playwright = Playwright.create();

        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false));

        page = browser.newPage();

    }

    @AfterEach
    void tearDown() {
        if (browser != null) {
            browser.close();
        }

        if (playwright != null) {
            playwright.close();
        }
    }
}