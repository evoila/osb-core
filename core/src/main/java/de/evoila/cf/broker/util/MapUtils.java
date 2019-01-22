package de.evoila.cf.broker.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Johannes Hiemer-
 */
public class MapUtils {

    public static void deepMerge(Map<String, Object> map1, Map<String, Object> map2) {
        for(String key : map2.keySet()) {
            Object value2 = map2.get(key);
            if (map1.containsKey(key)) {
                Object value1 = map1.get(key);
                if (value1 instanceof Map && value2 instanceof Map)
                    deepMerge((Map<String, Object>) value1, (Map<String, Object>) value2);
                else if (value1 instanceof List && value2 instanceof List)
                    map1.put(key, merge((List) value1, (List) value2));
                else map1.put(key, value2);
            } else map1.put(key, value2);
        }
    }

    /**
     * We do not merge on individual list elements here, but instead replace
     * the complete list with the new values.
     * @param list1
     * @param list2
     * @return
     */
    private static List merge(List list1, List list2) {
        list1.clear();
        list1.addAll(list2);
        return list1;
    }

    /**
     * Sadly, Spring Boot Cloud Config is not capable of reading List elements
     * properly from a YAML file. Instead it parses it as LinkedHashMap. To properly
     * merge them, we need to convert the LinkedHashMap to a List.
     * @param hashMap
     * @return
     */
    private static List convertToList(LinkedHashMap<String, Object> hashMap) {
        List list = new ArrayList();
        for (Map.Entry<String, Object> element : hashMap.entrySet())
            list.add(element.getValue());

        return list;
    }

    public static Map<String, Object> introspect(Object obj) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        BeanInfo info = Introspector.getBeanInfo(obj.getClass());
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            Method reader = pd.getReadMethod();
            if (reader != null)
                result.put(pd.getName(), reader.invoke(obj));
        }
        return result;
    }
}
