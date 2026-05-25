package com.testmu.utils;

import com.testmu.config.ConfigManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ScreenshotUtils — captures and saves screenshots on test failure.
 */
public class ScreenshotUtils {

    private static final ConfigManager config = ConfigManager.getInstance();

    private ScreenshotUtils() {}

    public static String capture(WebDriver driver, String testName) {
        String dir = config.getScreenshotDir();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = dir + "/" + testName + "_" + timestamp + ".png";

        try {
            Files.createDirectories(Paths.get(dir));
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(screenshot.toPath(), Paths.get(fileName));
            System.out.println("Screenshot saved: " + fileName);
            return fileName;
        } catch (IOException e) {
            System.err.println("Failed to save screenshot: " + e.getMessage());
            return null;
        }
    }
}
