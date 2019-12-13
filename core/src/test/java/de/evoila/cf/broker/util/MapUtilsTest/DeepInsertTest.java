package de.evoila.cf.broker.util.MapUtilsTest;

import de.evoila.cf.broker.util.MapUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

class DeepInsertTest extends BaseTest {
    private Map<String, Object> newMapValue;
    private Map<String, Object> newMapValueCopy;

    @BeforeEach
    void setUpSubClass() {
        newMapValue = new HashMap<>();
        newMapValue.put("newFirstKey", new ClassForTesting(0, "abc", false));
        newMapValue.put("newSecondKey", new ClassForTesting(1, "def", true));

        newMapValueCopy = Map.of("newFirstKey", new ClassForTesting(0, "abc", false),
                "newSecondKey", new ClassForTesting(1, "def", true));
    }

    @Test
    void mapParameterNull() {
        MapUtils.deepInsert(null, "firstKey", "newValue");
    }

    @Test
    void keyParameterNull() {
        MapUtils.deepInsert(destinationMap, null, "newValue");
    }

    @Test
    void valueParameterNull() {
        MapUtils.deepInsert(destinationMap, "newKey", null);

        Map<String, Object> expectedMap = new HashMap<>(originalDestinationMap);
        expectedMap.put("newKey", null);
        assertEquals(expectedMap, destinationMap);
    }

    @Test
    void firstLevelInsertNewKey() {
        MapUtils.deepInsert(destinationMap, "newKey", newMapValue);
        Map<String, Object> expectedMap = new HashMap<>(originalDestinationMap);
        expectedMap.put("newKey", newMapValueCopy);
        assertEquals(expectedMap, destinationMap);
    }

    @Test
    void firstLevelInsertExistingKey() {
        MapUtils.deepInsert(destinationMap, "thirdKey", newMapValue);
        Map<String, Object> expectedMap = new HashMap<>(originalDestinationMap);
        expectedMap.put("thirdKey", newMapValueCopy);
        assertEquals(expectedMap, destinationMap);
    }

    @Test
    void multiLevelInsertFirstKeyDoesNotExist() {
        MapUtils.deepInsert(destinationMap, "newKey.newSubKey", newMapValue);
        Map<String, Object> expectedMap = new HashMap<>(originalDestinationMap);
        Map<String, Object> secondLevelMap = new HashMap<>();
        secondLevelMap.put("newSubKey", newMapValueCopy);
        expectedMap.put("newKey", secondLevelMap);
        assertEquals(expectedMap, destinationMap);
    }

    @Test
    void multiLevelInsertFirstKeyExistsValueNoMap() {
        MapUtils.deepInsert(destinationMap, "forthKey.newSubKey", newMapValue);
        Map<String, Object> expectedMap = new HashMap<>(originalDestinationMap);
        Map<String, Object> secondLevelMap = new HashMap<>();
        secondLevelMap.put("newSubKey", newMapValueCopy);
        expectedMap.put("forthKey", secondLevelMap);
        assertEquals(expectedMap, destinationMap);
    }

    @Test
    void multiLevelInsertSecondKeyDoesNotExist() {
        MapUtils.deepInsert(destinationMap, "thirdKey.newSubKey", newMapValue);
        Map<String, Object> expectedMap = new HashMap<>(originalDestinationMap);
        Map<String, Object> secondLevelMap = new HashMap<>((Map<String, Object>) expectedMap.get("thirdKey"));
        secondLevelMap.put("newSubKey", newMapValueCopy);
        expectedMap.put("thirdKey", secondLevelMap);
        assertEquals(expectedMap, destinationMap);
    }

    @Test
    void multiLevelInsertSecondKeyExists() {
        MapUtils.deepInsert(destinationMap, "thirdKey.secondSubKey", newMapValue);
        Map<String, Object> expectedMap = new HashMap<>(originalDestinationMap);
        Map<String, Object> secondLevelMap = new HashMap<>((Map<String, Object>) expectedMap.get("thirdKey"));
        secondLevelMap.put("secondSubKey", newMapValueCopy);
        expectedMap.put("thirdKey", secondLevelMap);
        assertEquals(expectedMap, destinationMap);
    }
}
