package com.testmu.base;

import com.testmu.config.ConfigManager;
import com.testmu.config.DriverManager;
import com.testmu.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * BasePage — parent for all Page Object classes.
 * Encapsulates driver access, explicit waits, and common actions.
 * Tests never touch WebDriver directly — all interactions go through page methods.
 */
public abstract class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected ConfigManager config;

    public BasePage() {
        this.driver = DriverManager.getDriver();
        this.config = ConfigManager.getInstance();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(config.getExplicitWait()));
        PageFactory.initElements(driver, this);
    }

    // ── Navigation ──────────────────────────────────────────────────────────

    public void navigateTo(String url) {
        driver.get(url);
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    // ── Element Interactions ─────────────────────────────────────────────────

    protected void click(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();
    }

    protected void type(WebElement element, String text) {
        wait.until(ExpectedConditions.visibilityOf(element));
        element.clear();
        element.sendKeys(text);
    }

    protected String getText(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
        return element.getText().trim();
    }

    protected boolean isDisplayed(WebElement element) {
        try {
            wait.until(ExpectedConditions.visibilityOf(element));
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected void clickByLocator(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator));
        driver.findElement(locator).click();
    }

    protected WebElement waitForElement(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    // ── Page Load Check ──────────────────────────────────────────────────────

    protected void waitForPageLoad() {
        wait.until(driver -> ((JavascriptExecutor) driver)
                .executeScript("return document.readyState").equals("complete"));
    }
}
