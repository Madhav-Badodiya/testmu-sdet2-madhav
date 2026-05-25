package com.testmu.tests.ui;

import com.testmu.base.BaseTest;
import com.testmu.pages.InventoryPage;
import com.testmu.pages.LoginPage;
import com.testmu.utils.CustomAssertions;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * CrossBrowserSmokeTest — critical smoke tests run on Firefox.
 * testng.xml passes browser=firefox for this test class.
 */
@Epic("Cross-Browser")
@Feature("Smoke")
public class CrossBrowserSmokeTest extends BaseTest {

    @Test(description = "Login flow works on Firefox")
    @Story("Cross-Browser Login")
    @Severity(SeverityLevel.BLOCKER)
    public void testLoginOnFirefox() {
        InventoryPage inventoryPage = new LoginPage()
                .open()
                .loginAs("standard_user", "secret_sauce");

        Assert.assertTrue(inventoryPage.isLoaded(),
                "Inventory page should load on Firefox");
        CustomAssertions.assertPageTitle(inventoryPage.getTitle(), "Products");
    }

    @Test(description = "Product listing renders correctly on Firefox")
    @Story("Cross-Browser Product Listing")
    @Severity(SeverityLevel.NORMAL)
    public void testProductListingOnFirefox() {
        InventoryPage inventoryPage = new LoginPage()
                .open()
                .loginAs("standard_user", "secret_sauce");

        Assert.assertEquals(inventoryPage.getProductCount(), 6,
                "Product count should be 6 on Firefox");
    }
}
