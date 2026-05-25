package com.testmu.pages;

import com.testmu.base.BasePage;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * CartPage — SauceDemo shopping cart page.
 */
public class CartPage extends BasePage {

    @FindBy(css = "[data-test='cart-item']")
    private List<WebElement> cartItems;

    @FindBy(id = "checkout")
    private WebElement checkoutButton;

    @FindBy(css = "[data-test='continue-shopping']")
    private WebElement continueShoppingButton;

    @FindBy(css = "[data-test='title']")
    private WebElement pageTitle;

    // ── State ────────────────────────────────────────────────────────────────

    public boolean isLoaded() {
        return getCurrentUrl().contains("cart");
    }

    public int getCartItemCount() {
        return cartItems.size();
    }

    public String getTitle() {
        return getText(pageTitle);
    }

    // ── Actions ──────────────────────────────────────────────────────────────

    @Step("Proceed to checkout")
    public CheckoutPage proceedToCheckout() {
        click(checkoutButton);
        return new CheckoutPage();
    }

    @Step("Continue shopping")
    public InventoryPage continueShopping() {
        click(continueShoppingButton);
        return new InventoryPage();
    }
}
