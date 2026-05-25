package com.testmu.pages;

import com.testmu.base.BasePage;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/**
 * InventoryPage — SauceDemo product dashboard (post-login).
 * Selectors use data-test attributes and semantic locators.
 */
public class InventoryPage extends BasePage {

    @FindBy(css = "[data-test='title']")
    private WebElement pageTitle;

    @FindBy(css = "[data-test='inventory-container']")
    private WebElement inventoryContainer;

    @FindBy(css = "[data-test='inventory-item']")
    private List<WebElement> inventoryItems;

    @FindBy(css = "[data-test='shopping-cart-link']")
    private WebElement cartIcon;

    @FindBy(css = "[data-test='product-sort-container']")
    private WebElement sortDropdown;

    @FindBy(id = "react-burger-menu-btn")
    private WebElement burgerMenu;

    @FindBy(css = "[data-test='logout-sidebar-link']")
    private WebElement logoutLink;

    // ── State checks ─────────────────────────────────────────────────────────

    @Step("Verify inventory page is loaded")
    public boolean isLoaded() {
        return isDisplayed(inventoryContainer);
    }

    public String getTitle() {
        return getText(pageTitle);
    }

    public int getProductCount() {
        return inventoryItems.size();
    }

    // ── Actions ──────────────────────────────────────────────────────────────

    @Step("Add first product to cart")
    public InventoryPage addFirstItemToCart() {
        List<WebElement> addButtons = driver.findElements(
                By.cssSelector("[data-test^='add-to-cart']"));
        if (!addButtons.isEmpty()) {
            click(addButtons.get(0));
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("[data-test='shopping-cart-badge']")));
        }
        return this;
    }

    public InventoryPage resetCart() {
        List<WebElement> removeButtons = driver.findElements(
                By.cssSelector("[data-test^='remove']"));
        for (WebElement btn : removeButtons) {
            click(btn);
        }
        return this;
    }

    @Step("Get cart badge count")
    public String getCartBadgeCount() {
        List<WebElement> badge = driver.findElements(
                By.cssSelector("[data-test='shopping-cart-badge']"));
        return badge.isEmpty() ? "0" : getText(badge.get(0));
    }

    @Step("Select sort option: {option}")
    public InventoryPage sortBy(String option) {
        click(sortDropdown);
        driver.findElements(By.tagName("option")).stream()
                .filter(o -> o.getText().equals(option))
                .findFirst()
                .ifPresent(this::click);
        return this;
    }

    @Step("Logout")
    public LoginPage logout() {
        click(burgerMenu);
        waitForElement(By.cssSelector("[data-test='logout-sidebar-link']"));
        click(logoutLink);
        return new LoginPage();
    }
}
