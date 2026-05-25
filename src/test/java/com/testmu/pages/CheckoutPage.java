package com.testmu.pages;

import com.testmu.base.BasePage;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * CheckoutPage — SauceDemo checkout form.
 * Used for form validation tests.
 */
public class CheckoutPage extends BasePage {

    @FindBy(css = "[data-test='firstName']")
    private WebElement firstNameInput;

    @FindBy(css = "[data-test='lastName']")
    private WebElement lastNameInput;

    @FindBy(css = "[data-test='postalCode']")
    private WebElement postalCodeInput;

    @FindBy(css = "[data-test='continue']")
    private WebElement continueButton;

    @FindBy(css = "[data-test='cancel']")
    private WebElement cancelButton;

    @FindBy(css = "[data-test='error']")
    private WebElement errorMessage;

    // ── State ────────────────────────────────────────────────────────────────

    public boolean isLoaded() {
        return getCurrentUrl().contains("checkout-step-one");
    }

    public boolean isErrorDisplayed() {
        return isDisplayed(errorMessage);
    }

    public String getErrorMessage() {
        return getText(errorMessage);
    }

    // ── Actions ──────────────────────────────────────────────────────────────

    @Step("Fill checkout form: {firstName} {lastName} {postalCode}")
    public CheckoutPage fillForm(String firstName, String lastName, String postalCode) {
        type(firstNameInput, firstName);
        type(lastNameInput, lastName);
        type(postalCodeInput, postalCode);
        return this;
    }

    @Step("Submit checkout form")
    public CheckoutOverviewPage submitForm() {
        click(continueButton);
        return new CheckoutOverviewPage();
    }

    @Step("Submit checkout form — expect validation error")
    public CheckoutPage submitFormExpectingError() {
        click(continueButton);
        return this;
    }

    @Step("Cancel checkout")
    public CartPage cancel() {
        click(cancelButton);
        return new CartPage();
    }
}
