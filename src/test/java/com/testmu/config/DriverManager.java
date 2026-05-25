package com.testmu.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

/**
 * DriverManager — thread-safe WebDriver management using ThreadLocal.
 * Supports Chrome and Firefox. Headless mode controlled via config.
 */
public class DriverManager {

    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static final ConfigManager config = ConfigManager.getInstance();

    private DriverManager() {}

    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    public static void initDriver(String browser) {
        WebDriver driver;
        boolean headless = config.isHeadless();

        switch (browser.toLowerCase()) {
        case "firefox":
            WebDriverManager.firefoxdriver().setup();
            FirefoxOptions firefoxOptions = new FirefoxOptions();
            firefoxOptions.setBinary("C:\\Program Files\\Mozilla Firefox\\firefox.exe");
            if (headless) firefoxOptions.addArguments("--headless");
            driver = new FirefoxDriver(firefoxOptions);
            break;

            case "chrome":
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                if (headless) {
                    chromeOptions.addArguments("--headless=new");
                    chromeOptions.addArguments("--no-sandbox");
                    chromeOptions.addArguments("--disable-dev-shm-usage");
                    chromeOptions.addArguments("--disable-gpu");
                    chromeOptions.addArguments("--window-size=1920,1080");
                }
                driver = new ChromeDriver(chromeOptions);
                break;
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(config.getImplicitWait()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().window().maximize();

        driverThreadLocal.set(driver);
    }

    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            driver.quit();
            driverThreadLocal.remove();
        }
    }
}
