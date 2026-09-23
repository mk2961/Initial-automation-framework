package com.company.automation.utils;

import com.microsoft.playwright.Page;

import java.nio.file.Paths;

public final class ScreenshotUtils {

    private ScreenshotUtils() {
    }

    public static void capture(Page page, String fileName) {

        page.screenshot(
                new Page.ScreenshotOptions()
                        .setPath(Paths.get("target/screenshots/" + fileName + ".png")));
    }
}