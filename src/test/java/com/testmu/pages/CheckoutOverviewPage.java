package com.testmu.pages;

import com.testmu.base.BasePage;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * CheckoutOverviewPage — order summary before final confirmation.
 */
public class CheckoutOverviewPage extends BasePage {

    @FindBy(css = "[data-test='finish']")
    private WebElement finishButton;

    @FindBy(css = "[data-test='cancel']")
    private WebElement cancelButton;

    @FindBy(css = "[data-test='total-label']")
    private WebElement totalLabel;

    @FindBy(css = "[data-test='title']")
    private WebElement pageTitle;

    public boolean isLoaded() {
        return getCurrentUrl().contains("checkout-step-two");
    }

    public String getTitle() {
        return getText(pageTitle);
    }

    public String getTotalLabel() {
        return getText(totalLabel);
    }

    @Step("Finish order")
    public OrderConfirmationPage finishOrder() {
        click(finishButton);
        return new OrderConfirmationPage();
    }
}
