package com.testmu.utils;

import org.testng.Assert;

/**
 * CustomAssertions — domain-specific assertion helpers.
 * Produces clear failure messages without repeating assertion logic in tests.
 */
public class CustomAssertions {

    private CustomAssertions() {}

    public static void assertPageTitle(String actual, String expected) {
        Assert.assertEquals(actual, expected,
                "Page title mismatch. Expected: [" + expected + "] but got: [" + actual + "]");
    }

    public static void assertUrlContains(String actualUrl, String fragment) {
        Assert.assertTrue(actualUrl.contains(fragment),
                "URL should contain [" + fragment + "] but was: [" + actualUrl + "]");
    }

    public static void assertErrorMessage(String actual, String expected) {
        Assert.assertEquals(actual, expected,
                "Error message mismatch. Expected: [" + expected + "] but got: [" + actual + "]");
    }

    public static void assertResponseTime(long actualMs, long maxMs) {
        Assert.assertTrue(actualMs <= maxMs,
                "Response time [" + actualMs + "ms] exceeded max allowed [" + maxMs + "ms]");
    }

    public static void assertStatusCode(int actual, int expected) {
        Assert.assertEquals(actual, expected,
                "HTTP status code mismatch. Expected: [" + expected + "] but got: [" + actual + "]");
    }

    public static void assertFieldNotEmpty(String fieldName, String value) {
        Assert.assertNotNull(value, fieldName + " should not be null");
        Assert.assertFalse(value.trim().isEmpty(), fieldName + " should not be empty");
    }
}
