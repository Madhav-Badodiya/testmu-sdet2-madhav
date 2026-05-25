package com.testmu.tests.ui;

import com.testmu.base.BaseTest;
import com.testmu.config.DriverManager;
import com.testmu.pages.CartPage;
import com.testmu.pages.CheckoutPage;
import com.testmu.pages.InventoryPage;
import com.testmu.pages.LoginPage;
import com.testmu.utils.CustomAssertions;
import com.testmu.utils.WaitUtils;

import io.qameta.allure.*;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * FormValidationTest — checkout form validation: required fields, partial data, valid data.
 */
@Epic("UI Tests")
@Feature("Form Validation")
public class FormValidationTest extends BaseTest {

	private CheckoutPage navigateToCheckout() {
	    new LoginPage()
	            .open()
	            .loginAs("standard_user", "secret_sauce");
	    
	    DriverManager.getDriver().findElement(
	            By.cssSelector("[data-test='add-to-cart-sauce-labs-backpack']")).click();
	    
	    DriverManager.getDriver().get("https://www.saucedemo.com/checkout-step-one.html");
	    
	    return new CheckoutPage();
	}

    @Test(description = "Submitting empty checkout form shows required field error")
    @Story("Required Field Validation")
    @Severity(SeverityLevel.CRITICAL)
    public void testEmptyFormShowsError() {
        CheckoutPage checkoutPage = navigateToCheckout();

        checkoutPage.submitFormExpectingError();

        Assert.assertTrue(checkoutPage.isErrorDisplayed(),
                "Error message should be displayed for empty form");
        CustomAssertions.assertErrorMessage(
                checkoutPage.getErrorMessage(),
                "Error: First Name is required");
    }

    @Test(description = "Submitting form with missing last name shows error")
    @Story("Required Field Validation")
    @Severity(SeverityLevel.NORMAL)
    public void testMissingLastNameShowsError() {
        CheckoutPage checkoutPage = navigateToCheckout();

        checkoutPage.fillForm("Madhav", "", "").submitFormExpectingError();

        Assert.assertTrue(checkoutPage.isErrorDisplayed());
        CustomAssertions.assertErrorMessage(
                checkoutPage.getErrorMessage(),
                "Error: Last Name is required");
    }

    @Test(description = "Submitting form with missing postal code shows error")
    @Story("Required Field Validation")
    @Severity(SeverityLevel.NORMAL)
    public void testMissingPostalCodeShowsError() {
        CheckoutPage checkoutPage = navigateToCheckout();

        checkoutPage.fillForm("Madhav", "Badodiya", "").submitFormExpectingError();

        Assert.assertTrue(checkoutPage.isErrorDisplayed());
        CustomAssertions.assertErrorMessage(
                checkoutPage.getErrorMessage(),
                "Error: Postal Code is required");
    }

    @Test(description = "Valid checkout form proceeds to order overview")
    @Story("Successful Form Submission")
    @Severity(SeverityLevel.BLOCKER)
    public void testValidFormProceedsToOverview() {
        CheckoutPage checkoutPage = navigateToCheckout();

        var overviewPage = checkoutPage
                .fillForm("Madhav", "Badodiya", "452001")
                .submitForm();

        Assert.assertTrue(overviewPage.isLoaded(),
                "Should navigate to checkout overview after valid form");
        CustomAssertions.assertPageTitle(overviewPage.getTitle(), "Checkout: Overview");
    }
}
