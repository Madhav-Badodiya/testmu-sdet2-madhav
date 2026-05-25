package com.testmu.utils;

import com.testmu.config.ConfigManager;
import com.testmu.config.DriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;

/**
 * WaitUtils — custom waits and retry logic.
 * Addresses flakiness without Thread.sleep.
 */
public class WaitUtils {

    private static final ConfigManager config = ConfigManager.getInstance();

    private WaitUtils() {}

    /**
     * Standard explicit wait for element visibility.
     */
    public static WebElement waitForVisibility(By locator) {
        WebDriverWait wait = new WebDriverWait(
                DriverManager.getDriver(),
                Duration.ofSeconds(config.getExplicitWait()));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Fluent wait — polls every 500ms, ignores NoSuchElementException.
     * Use for elements that appear intermittently.
     */
    public static WebElement fluentWait(By locator) {
        FluentWait<WebDriver> fluentWait = new FluentWait<>(DriverManager.getDriver())
                .withTimeout(Duration.ofSeconds(config.getExplicitWait()))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class);
        return fluentWait.until(driver -> driver.findElement(locator));
    }

    /**
     * Wait for URL to contain a substring.
     */
    public static boolean waitForUrlContains(String urlFragment) {
        WebDriverWait wait = new WebDriverWait(
                DriverManager.getDriver(),
                Duration.ofSeconds(config.getExplicitWait()));
        return wait.until(ExpectedConditions.urlContains(urlFragment));
    }

    /**
     * Retry a callable up to maxAttempts times.
     * Useful for flaky network-dependent assertions.
     */
    public static <T> T retry(Callable<T> action, int maxAttempts) {
        Exception lastException = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return action.call();
            } catch (Exception e) {
                lastException = e;
                try {
                    Thread.sleep(500L * attempt);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        throw new RuntimeException("Action failed after " + maxAttempts + " attempts", lastException);
    }
}
