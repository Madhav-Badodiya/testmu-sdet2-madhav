package com.testmu.utils;

import com.testmu.config.ConfigManager;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * RetryAnalyzer — retries failed tests up to retry.count from config.
 * Attach via @Test(retryAnalyzer = RetryAnalyzer.class) or RetryListener.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;
    private final int maxRetry = ConfigManager.getInstance().getRetryCount();

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < maxRetry) {
            retryCount++;
            System.out.println("Retrying test [" + result.getName() + "] — attempt " + retryCount + " of " + maxRetry);
            return true;
        }
        return false;
    }
}
