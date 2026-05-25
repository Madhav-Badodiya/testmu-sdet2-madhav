package com.testmu.pages;

import com.testmu.base.BasePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * OrderConfirmationPage — final order success screen.
 */
public class OrderConfirmationPage extends BasePage {

    @FindBy(css = "[data-test='complete-header']")
    private WebElement completeHeader;

    @FindBy(css = "[data-test='complete-text']")
    private WebElement completeText;

    @FindBy(css = "[data-test='back-to-products']")
    private WebElement backToProductsButton;

    public boolean isOrderConfirmed() {
        return isDisplayed(completeHeader);
    }

    public String getConfirmationHeader() {
        return getText(completeHeader);
    }

    public String getConfirmationText() {
        return getText(completeText);
    }

    public InventoryPage backToProducts() {
        click(backToProductsButton);
        return new InventoryPage();
    }
}
