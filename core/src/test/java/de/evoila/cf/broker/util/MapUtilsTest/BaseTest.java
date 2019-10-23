package de.evoila.cf.broker.util.MapUtilsTest;

import org.junit.jupiter.api.BeforeEach;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BaseTest
{
    Map<String, Object> destinationMap;
    Map<String, Object> originalDestinationMap; // immutable

    @BeforeEach
    void setUp()
    {
        destinationMap = new HashMap<>();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("firstSubKey", new ClassForTesting(0, "a", true) );
        map1.put("secondSubKey", new ClassForTesting(1, "bc", false) );
        map1.put("firstKey", new ClassForTesting(2, "de", true) );
        destinationMap.put("firstKey", map1);
        destinationMap.put("secondKey", new HashMap<>() );
        Map<String, Object> map3 = new HashMap<>();
        map3.put("firstSubKey", new ClassForTesting(23, "def", true) );
        map3.put("secondSubKey", new ClassForTesting(45, "ghi", false) );
        map3.put("thirdSubKey" , new ClassForTesting(67, "jkl" ,false) );
        destinationMap.put("thirdKey", map3);
        destinationMap.put("forthKey", "StringValue");

        originalDestinationMap = Map.of("firstKey", Map.of("firstSubKey", new ClassForTesting(0, "a", true),
                                        "secondSubKey", new ClassForTesting(1, "bc", false),
                                        "firstKey", new ClassForTesting(2, "de", true) ),
                                        "secondKey", Collections.EMPTY_MAP,
                                        "thirdKey", Map.of("firstSubKey", new ClassForTesting(23, "def", true),
                                                                "secondSubKey", new ClassForTesting(45, "ghi", false),
                                                                "thirdSubKey" , new ClassForTesting(67, "jkl" ,false) ),
                                        "forthKey", "StringValue");
    }

    class ClassForTesting
    {
        Integer number;
        String text;
        Boolean flag;

        ClassForTesting(Integer number, String text, Boolean flag)
        {
            this.number = number;
            this.text = text;
            this.flag = flag;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DeepMergeTest.ClassForTesting that = (DeepMergeTest.ClassForTesting) o;
            return Objects.equals(number, that.number) &&
                    Objects.equals(text, that.text) &&
                    Objects.equals(flag, that.flag);
        }

        @Override
        public int hashCode() {
            return Objects.hash(number, text, flag);
        }
    }
}
