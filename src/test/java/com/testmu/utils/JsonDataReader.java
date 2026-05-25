package com.testmu.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

/**
 * JsonDataReader — reads test data from JSON files.
 * Supports data-driven tests with externalized test data.
 */
public class JsonDataReader {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String BASE_PATH = "src/test/resources/testdata/";

    private JsonDataReader() {}

    public static JsonNode read(String fileName) {
        try {
            return mapper.readTree(new File(BASE_PATH + fileName));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read test data file: " + fileName, e);
        }
    }

    /**
     * Returns a DataProvider-compatible Object[][] from a JSON array node.
     * Usage: JsonDataReader.toDataProvider("login_data.json", "valid_users")
     */
    public static Object[][] toDataProvider(String fileName, String arrayKey) {
        JsonNode root = read(fileName);
        JsonNode array = root.get(arrayKey);

        if (array == null || !array.isArray()) {
            throw new RuntimeException("Key '" + arrayKey + "' not found or not an array in " + fileName);
        }

        Object[][] data = new Object[array.size()][1];
        for (int i = 0; i < array.size(); i++) {
            data[i][0] = array.get(i);
        }
        return data;
    }
}
