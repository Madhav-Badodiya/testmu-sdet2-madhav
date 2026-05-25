package com.testmu.base;

import com.testmu.config.ConfigManager;
import com.testmu.config.DriverManager;
import com.testmu.utils.ScreenshotUtils;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestResult;
import org.testng.annotations.*;

/**
 * BaseTest — parent for all UI test classes.
 * Manages driver lifecycle and captures screenshots on failure.
 */
public class BaseTest {

    protected ConfigManager config = ConfigManager.getInstance();

    @Parameters("browser")
    @BeforeMethod(alwaysRun = true)
    public void setUp(@Optional("chrome") String browser) {
        DriverManager.initDriver(browser);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
    	if (result.getStatus() == ITestResult.FAILURE) {
    	    if (config.screenshotOnFailure()) {
    	        attachScreenshotToAllure();
    	        captureScreenshotOnFailure(result.getName());
    	    }
    	}
    	DriverManager.quitDriver();
    }

    private void captureScreenshotOnFailure(String testName) {
        ScreenshotUtils.capture(DriverManager.getDriver(), testName);
    }

    @Attachment(value = "Failure Screenshot", type = "image/png")
    private byte[] attachScreenshotToAllure() {
        return ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.BYTES);
    }
}
