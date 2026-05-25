package com.testmu.tests.ui;

import com.testmu.base.BaseTest;
import com.testmu.config.DriverManager;

import org.openqa.selenium.By;
import com.testmu.pages.InventoryPage;
import com.testmu.pages.LoginPage;
import com.testmu.utils.CustomAssertions;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * DashboardTest — verifies inventory page state, product listing, cart interactions.
 */
@Epic("UI Tests")
@Feature("Dashboard / Inventory")
public class DashboardTest extends BaseTest {

    private InventoryPage loginAsStandardUser() {
        return new LoginPage()
                .open()
                .loginAs("standard_user", "secret_sauce");
    }

    @Test(description = "Inventory page loads with 6 products")
    @Story("Product Listing")
    @Severity(SeverityLevel.CRITICAL)
    public void testInventoryPageLoads() {
        InventoryPage inventoryPage = loginAsStandardUser();

        Assert.assertTrue(inventoryPage.isLoaded(), "Inventory container should be visible");
        Assert.assertEquals(inventoryPage.getProductCount(), 6,
                "SauceDemo should display 6 products");
    }

    @Test(description = "Adding product to cart updates badge count")
    @Story("Cart Interaction")
    @Severity(SeverityLevel.CRITICAL)
    public void testAddToCartUpdatesBadge() {
        InventoryPage inventoryPage = loginAsStandardUser();
        
        DriverManager.getDriver().findElement(
                By.cssSelector("[data-test='add-to-cart-sauce-labs-backpack']")).click();
        
        String badge = DriverManager.getDriver().findElement(
                By.cssSelector("[data-test='shopping-cart-badge']")).getText();
        Assert.assertEquals(badge, "1", "Cart badge should show 1 after adding item");
    }

    @Test(description = "User can log out from inventory page")
    @Story("Logout")
    @Severity(SeverityLevel.NORMAL)
    public void testLogout() {
        LoginPage loginPage = loginAsStandardUser().logout();

        CustomAssertions.assertUrlContains(loginPage.getCurrentUrl(), "saucedemo.com");
    }
}
