package com.company.automation.ui.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.Locator;

public class SecurePage {

    private final Page page;

    public SecurePage(Page page) {
        this.page = page;
    }

    public Locator getSuccessMessage() {
        return page.locator("#flash");
    }

    public void logout() {
        page.getByRole(
                AriaRole.LINK,
                new Page.GetByRoleOptions().setName("Logout")).click();
    }
}