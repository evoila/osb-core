package de.evoila.cf.broker.model.json.schema.utils;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.evoila.cf.broker.model.json.schema.JsonSchema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonSchemaUtilsTest {

    @SuppressWarnings("InnerClassMayBeStatic")
    @Nested
    class exceptionThrown {

        @Test
        void withSchemaPropertiesNull() {
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                                                      () -> JsonSchemaUtils.mergeMaps(null,
                                                                                      new HashMap<>() {{
                                                                                          put("IKey", "IValue");
                                                                                      }},
                                                                                      new HashMap<>() {{
                                                                                          put("RKey", "RValue");
                                                                                      }}));
            assertEquals("schemaProperties is null", e.getMessage());
        }

        @Test
        void withInstanceGroupPropertiesNull() {
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                                                      () -> JsonSchemaUtils.mergeMaps(new HashMap<>() {{
                                                                                          put("SKey", new JsonSchema());
                                                                                      }},
                                                                                      null,
                                                                                      new HashMap<>() {{
                                                                                          put("RKey", "RValue");
                                                                                      }}));
            assertEquals("instanceGroupProperties is null", e.getMessage());
        }

        @Test
        void withResultParameterNull() {
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                                                      () -> JsonSchemaUtils.mergeMaps(new HashMap<>() {{
                                                                                          put("SKey", new JsonSchema());
                                                                                      }},
                                                                                      new HashMap<>() {{
                                                                                          put("IKey", "IValue");
                                                                                      }},
                                                                                      null));
            assertEquals("result is null", e.getMessage());
        }

    }

    @SuppressWarnings("InnerClassMayBeStatic")
    @Nested
    class withoutChangingResultParameter {

        private Map<String, Object> inputMap = new HashMap<>() {{
           put("RKey1", "RValue1");
           put("RKey2", "RValue2");
        }};

        private Map<String,Object> originalInputMap = new HashMap<>() {{
            put("RKey1", "RValue1");
            put("RKey2", "RValue2");
        }};

        @Test
        void withEmptySchemaProperties() {
            Map<String, Object> result = JsonSchemaUtils.mergeMaps(Collections.emptyMap(),
                                                                   new HashMap<>() {{
                                                                       put("IKey", "IValue");
                                                                   }},
                                                                   inputMap);
            assertSame(inputMap, result);
            assertEquals(originalInputMap, result);
        }

    }

    @SuppressWarnings("InnerClassMayBeStatic")
    @Nested
    class withOverridingExistingKeyInResult {

        @Nested
        class withSchemaValueWithoutProperties {

            @Test
            void withoutKeyInInstanceGroupProperties() {
                JsonFormatTypes[] types = JsonFormatTypes.values();
                Map<String, Object> expectedResult = Collections.unmodifiableMap(new HashMap<>() {{
                    for (JsonFormatTypes type : types) {
                        put(type.name(), null);
                    }
                    put("ARRAY",   new ArrayList<>());
                    put("STRING",  "");
                    put("BOOLEAN", false);
                    put("NUMBER",  0);
                    put("INTEGER", 0);
                }});
                Map<String, JsonSchema> jsonSchemaMap = Collections.unmodifiableMap(new HashMap<>() {{
                    for (JsonFormatTypes type : types) {
                        put(type.name(), new JsonSchema() {{
                            setType(type);
                        }});
                    }
                }});
                Map<String, Object> inputMap = new HashMap<>() {{
                    for (JsonFormatTypes type : types) {
                        put(type.name(), null);
                    }
                }};
                Map<String, Object> result = JsonSchemaUtils.mergeMaps(jsonSchemaMap,
                                                                       Collections.emptyMap(),
                                                                       inputMap);
                assertSame(inputMap, result);
                assertEquals(expectedResult, result);
            }

            @Test
            void withKeyInInstanceGroupProperties() {
                Map<String, Object> expectedResult = Map.of("Key1", "Value1",
                                                            "Key2", "Value2",
                                                            "Key3", "Value3");
                Map<String, Object> instanceGroupProperties = Map.of("Key1", "Value1",
                                                                     "Key2", "Value2",
                                                                     "Key3", "Value3");
                Map<String, JsonSchema> jsonSchemaMap = Map.of("Key1", new JsonSchema(),
                                                               "Key2", new JsonSchema(),
                                                               "Key3", new JsonSchema());
                Map<String, Object> inputMap = new HashMap<>() {{
                    put("Key1", null);
                    put("Key2", null);
                    put("Key3", null);
                }};
                Map<String, Object> result = JsonSchemaUtils.mergeMaps(jsonSchemaMap,
                                                                       instanceGroupProperties,
                                                                       inputMap);
                assertSame(inputMap, result);
                assertEquals(expectedResult, result);
            }

        }

        @Nested
        class withSchemaValueWithEmptyProperties {

            @Test
            void withoutKeyInInstanceGroupProperties() {
                JsonFormatTypes[] types = JsonFormatTypes.values();
                Map<String, Object> expectedResult = Collections.unmodifiableMap(new HashMap<>() {{
                    for (JsonFormatTypes type : types) {
                        put(type.name(), null);
                    }
                    put("ARRAY",   new ArrayList<>());
                    put("STRING",  "");
                    put("BOOLEAN", false);
                    put("NUMBER",  0);
                    put("INTEGER", 0);
                }});
                Map<String, JsonSchema> jsonSchemaMap = Collections.unmodifiableMap(new HashMap<>() {{
                    for (JsonFormatTypes type : types) {
                        put(type.name(), new JsonSchema() {{
                            setType(type);
                            setProperties(Collections.emptyMap());
                        }});
                    }
                }});
                Map<String, Object> inputMap = new HashMap<>() {{
                    for (JsonFormatTypes type : types) {
                        put(type.name(), null);
                    }
                }};
                Map<String, Object> result = JsonSchemaUtils.mergeMaps(jsonSchemaMap,
                                                                       Collections.emptyMap(),
                                                                       inputMap);
                assertSame(inputMap, result);
                assertEquals(expectedResult, result);
            }

            @Test
            void withKeyInInstanceGroupProperties() {
                Map<String, Object> expectedResult = Map.of("Key1", "Value1",
                                                            "Key2", "Value2",
                                                            "Key3", "Value3");
                Map<String, Object> instanceGroupProperties = Map.of("Key1", "Value1",
                                                                     "Key2", "Value2",
                                                                     "Key3", "Value3");
                Map<String, JsonSchema> jsonSchemaMap = Map.of("Key1", new JsonSchema() {{
                                                                   setProperties(Collections.emptyMap());
                                                               }},
                                                               "Key2", new JsonSchema() {{
                                                                   setProperties(Collections.emptyMap());
                                                               }},
                                                               "Key3", new JsonSchema() {{
                                                                   setProperties(Collections.emptyMap());
                                                               }});
                Map<String, Object> inputMap = new HashMap<>() {{
                    put("Key1", null);
                    put("Key2", null);
                    put("Key3", null);
                }};
                Map<String, Object> result = JsonSchemaUtils.mergeMaps(jsonSchemaMap,
                                                                       instanceGroupProperties,
                                                                       inputMap);
                assertSame(inputMap, result);
                assertEquals(expectedResult, result);
            }

        }

        /**
         * For avoiding excessive amounts of tests, we only test the part of the method which puts a
         * value of the instanceGroupProperties at the JsonSchema key when handling the recursive call.
         */
        @Nested
        class withSchemaValueWithProperties {

            @Test
            void withNoInstanceGroupProperty() {
                Map<String, Object> expectedResult = Map.of("Map1", Map.of("Key11", "",
                                                                           "Key12", "",
                                                                           "Key13", ""),
                                                            "Map2", Map.of("Key21", "",
                                                                           "Key22", "",
                                                                           "Key23", ""),
                                                            "Map3", Map.of("Key31", "",
                                                                           "Key32", "",
                                                                           "Key33", ""));
                Map<String, JsonSchema> jsonSchemaMap = Map.of("Map1", new JsonSchema() {{
                                                                   setProperties(Map.of("Key11", new JsonSchema() {{
                                                                                            setType(JsonFormatTypes.STRING);
                                                                                        }},
                                                                                        "Key12", new JsonSchema() {{
                                                                                            setType(JsonFormatTypes.STRING);
                                                                                        }},
                                                                                        "Key13", new JsonSchema() {{
                                                                                            setType(JsonFormatTypes.STRING);
                                                                                        }}));
                                                               }},
                                                               "Map2", new JsonSchema() {{
                                                                   setProperties(Map.of("Key21", new JsonSchema() {{
                                                                                            setType(JsonFormatTypes.STRING);
                                                                                        }},
                                                                                        "Key22", new JsonSchema() {{
                                                                                            setType(JsonFormatTypes.STRING);
                                                                                        }},
                                                                                        "Key23", new JsonSchema() {{
                                                                                            setType(JsonFormatTypes.STRING);
                                                                                        }}));
                                                               }},
                                                               "Map3", new JsonSchema() {{
                                                                   setProperties(Map.of("Key31", new JsonSchema() {{
                                                                                            setType(JsonFormatTypes.STRING);
                                                                                        }},
                                                                                        "Key32", new JsonSchema() {{
                                                                                            setType(JsonFormatTypes.STRING);
                                                                                        }},
                                                                                        "Key33", new JsonSchema() {{
                                                                                            setType(JsonFormatTypes.STRING);
                                                                                        }}));
                                                               }});
                Map<String, Object> inputMap = new HashMap<>() {{
                    put("Map1", null);
                    put("Map2", null);
                    put("Map3", null);
                }};
                Map<String, Object> result = JsonSchemaUtils.mergeMaps(jsonSchemaMap,
                                                                       Collections.emptyMap(),
                                                                       inputMap);
                assertSame(inputMap, result);
                assertEquals(expectedResult, result);
            }

            @Test
            void withNoMapAsInstanceGroupProperty() {
                Map<String, Object> expectedResult = Map.of("Map1", Map.of("Key11", "",
                                                                           "Key12", "",
                                                                           "Key13", ""),
                                                            "Map2", Map.of("Key21", "",
                                                                           "Key22", "",
                                                                           "Key23", ""),
                                                            "Map3", Map.of("Key31", "",
                                                                           "Key32", "",
                                                                           "Key33", ""));
                Map<String, Object> instanceGroupProperties = Map.of("Map1", List.of("Value11",
                                                                                     "Value12",
                                                                                     "Value13"),
                                                                     "Map2", List.of("Value21",
                                                                                     "Value22",
                                                                                     "Value23"),
                                                                     "Map3", List.of("Value31",
                                                                                     "Value32",
                                                                                     "Value33"));
                Map<String, JsonSchema> jsonSchemaMap = Map.of("Map1", new JsonSchema() {{
                                                                   setProperties(Map.of("Key11", new JsonSchema() {{
                                                                                            setType(JsonFormatTypes.STRING);
                                                                                        }},
                                                                                        "Key12", new JsonSchema() {{
                                                                                            setType(JsonFormatTypes.STRING);
                                                                                        }},
                                                                                        "Key13", new JsonSchema() {{
                                                                                            setType(JsonFormatTypes.STRING);
                                                                                        }}));
                                                               }},
                                                               "Map2", new JsonSchema() {{
                                                                   setProperties(Map.of("Key21", new JsonSchema() {{
                                                                                            setType(JsonFormatTypes.STRING);
                                                                                        }},
                                                                                        "Key22", new JsonSchema() {{
                                                                                            setType(JsonFormatTypes.STRING);
                                                                                        }},
                                                                                        "Key23", new JsonSchema() {{
                                                                                            setType(JsonFormatTypes.STRING);
                                                                                        }}));
                                                               }},
                                                               "Map3", new JsonSchema() {{
                                                                   setProperties(Map.of("Key31", new JsonSchema() {{
                                                                                            setType(JsonFormatTypes.STRING);
                                                                                        }},
                                                                                        "Key32", new JsonSchema() {{
                                                                                            setType(JsonFormatTypes.STRING);
                                                                                        }},
                                                                                        "Key33", new JsonSchema() {{
                                                                                            setType(JsonFormatTypes.STRING);
                                                                                        }}));
                                                               }});
                Map<String, Object> inputMap = new HashMap<>() {{
                    put("Map1", null);
                    put("Map2", null);
                    put("Map3", null);
                }};
                Map<String, Object> result = JsonSchemaUtils.mergeMaps(jsonSchemaMap,
                                                                       instanceGroupProperties,
                                                                       inputMap);
                assertSame(inputMap, result);
                assertEquals(expectedResult, result);
            }

            @Test
            void withInstanceGroupProperty() {
                Map<String, Object> expectedResult = Map.of("Map1", Map.of("Key11", "Value11",
                                                                           "Key12", "Value12",
                                                                           "Key13", "Value13"),
                                                            "Map2", Map.of("Key21", "Value21",
                                                                           "Key22", "Value22",
                                                                           "Key23", "Value23"),
                                                            "Map3", Map.of("Key31", "Value31",
                                                                           "Key32", "Value32",
                                                                           "Key33", "Value33"));
                Map<String, Object> instanceGroupProperties = Map.of("Map1", Map.of("Key11", "Value11",
                                                                                    "Key12", "Value12",
                                                                                    "Key13", "Value13"),
                                                                     "Map2", Map.of("Key21", "Value21",
                                                                                    "Key22", "Value22",
                                                                                    "Key23", "Value23"),
                                                                     "Map3", Map.of("Key31", "Value31",
                                                                                    "Key32", "Value32",
                                                                                    "Key33", "Value33"));
                Map<String, JsonSchema> jsonSchemaMap = Map.of("Map1", new JsonSchema() {{
                                                                   setProperties(Map.of("Key11", new JsonSchema(),
                                                                                        "Key12", new JsonSchema(),
                                                                                        "Key13", new JsonSchema()));
                                                               }},
                                                               "Map2", new JsonSchema() {{
                                                                   setProperties(Map.of("Key21", new JsonSchema(),
                                                                                     "Key22", new JsonSchema(),
                                                                                     "Key23", new JsonSchema()));
                                                               }},
                                                               "Map3", new JsonSchema() {{
                                                                   setProperties(Map.of("Key31", new JsonSchema(),
                                                                                     "Key32", new JsonSchema(),
                                                                                     "Key33", new JsonSchema()));
                                                               }});
                Map<String, Object> inputMap = new HashMap<>() {{
                    put("Map1", null);
                    put("Map2", null);
                    put("Map3", null);
                }};
                Map<String, Object> result = JsonSchemaUtils.mergeMaps(jsonSchemaMap,
                                                                       instanceGroupProperties,
                                                                       inputMap);
                assertSame(inputMap, result);
                assertEquals(expectedResult, result);

            }

        }

    }

    @SuppressWarnings("InnerClassMayBeStatic")
    @Nested
    class withoutOverridingExistingKeyInResult {

        @Nested
        class withSchemaValueWithoutProperties {

            @Test
            void withoutKeyInInstanceGroupProperties() {
                JsonFormatTypes[] types = JsonFormatTypes.values();
                Map<String, Object> expectedResult = Map.of("ARRAY", new ArrayList<>(),
                                                            "STRING", "",
                                                            "BOOLEAN", false,
                                                            "NUMBER", 0,
                                                            "INTEGER", 0);
                Map<String, JsonSchema> jsonSchemaMap = Collections.unmodifiableMap(new HashMap<>() {{
                    for (JsonFormatTypes type : types) {
                        put(type.name(), new JsonSchema() {{
                            setType(type);
                        }});
                    }
                }});
                Map<String, Object> inputMap = new HashMap<>();
                Map<String, Object> result = JsonSchemaUtils.mergeMaps(jsonSchemaMap,
                                                                       Collections.emptyMap(),
                                                                       inputMap);
                assertSame(inputMap, result);
                assertEquals(expectedResult, result);
            }

            @Test
            void withKeyInInstanceGroupProperties() {
                Map<String, Object> expectedResult = Map.of("Key1", "Value1",
                                                            "Key2", "Value2",
                                                            "Key3", "Value3");
                Map<String, Object> instanceGroupProperties = Map.of("Key1", "Value1",
                                                                     "Key2", "Value2",
                                                                     "Key3", "Value3");
                Map<String, JsonSchema> jsonSchemaMap = Map.of("Key1", new JsonSchema(),
                                                               "Key2", new JsonSchema(),
                                                               "Key3", new JsonSchema());
                Map<String, Object> inputMap = new HashMap<>();
                Map<String, Object> result = JsonSchemaUtils.mergeMaps(jsonSchemaMap,
                                                                       instanceGroupProperties,
                                                                       inputMap);
                assertSame(inputMap, result);
                assertEquals(expectedResult, result);
            }

        }

        @Nested
        class withSchemaValueWithEmptyProperties {

            @Test
            void withoutKeyInInstanceGroupProperties() {
                JsonFormatTypes[] types = JsonFormatTypes.values();
                Map<String, Object> expectedResult = Map.of("ARRAY", new ArrayList<>(),
                                                            "STRING", "",
                                                            "BOOLEAN", false,
                                                            "NUMBER", 0,
                                                            "INTEGER", 0);
                Map<String, JsonSchema> jsonSchemaMap = Collections.unmodifiableMap(new HashMap<>() {{
                    for (JsonFormatTypes type : types) {
                        put(type.name(), new JsonSchema() {{
                            setType(type);
                            setProperties(Collections.emptyMap());
                        }});
                    }
                }});
                Map<String, Object> inputMap = new HashMap<>();
                Map<String, Object> result = JsonSchemaUtils.mergeMaps(jsonSchemaMap,
                                                                       Collections.emptyMap(),
                                                                       inputMap);
                assertSame(inputMap, result);
                assertEquals(expectedResult, result);
            }

            @Test
            void withKeyInInstanceGroupProperties() {
                Map<String, Object> expectedResult = Map.of("Key1", "Value1",
                                                            "Key2", "Value2",
                                                            "Key3", "Value3");
                Map<String, Object> instanceGroupProperties = Map.of("Key1", "Value1",
                                                                     "Key2", "Value2",
                                                                     "Key3", "Value3");
                Map<String, JsonSchema> jsonSchemaMap = Map.of("Key1", new JsonSchema() {{
                                                                   setProperties(Collections.emptyMap());
                                                               }},
                                                               "Key2", new JsonSchema() {{
                                                                   setProperties(Collections.emptyMap());
                                                               }},
                                                               "Key3", new JsonSchema() {{
                                                                   setProperties(Collections.emptyMap());
                                                               }});
                Map<String, Object> inputMap = new HashMap<>();
                Map<String, Object> result = JsonSchemaUtils.mergeMaps(jsonSchemaMap,
                                                                       instanceGroupProperties,
                                                                       inputMap);
                assertSame(inputMap, result);
                assertEquals(expectedResult, result);
            }

        }

        /**
         * For avoiding excessive amounts of tests, we only test the part of the method which puts a
         * value of the instanceGroupProperties at the JsonSchema key when handling the recursive call.
         */
        @Nested
        class withSchemaValueWithProperties {

            @Test
            void withNoInstanceGroupProperty() {
                Map<String, Object> expectedResult = Map.of("Map1", Map.of("Key11", "",
                                                                           "Key12", "",
                                                                           "Key13", ""),
                                                            "Map2", Map.of("Key21", "",
                                                                           "Key22", "",
                                                                           "Key23", ""),
                                                            "Map3", Map.of("Key31", "",
                                                                           "Key32", "",
                                                                           "Key33", ""));
                Map<String, JsonSchema> jsonSchemaMap = Map.of("Map1", new JsonSchema() {{
                                                                   setProperties(Map.of("Key11", new JsonSchema() {{
                                                                                            setType(JsonFormatTypes.STRING);
                                                                                        }},
                                                                                        "Key12", new JsonSchema() {{
                                                                                            setType(JsonFormatTypes.STRING);
                                                                                        }},
                                                                                        "Key13", new JsonSchema() {{
                                                                                            setType(JsonFormatTypes.STRING);
                                                                                        }}));
                                                               }},
                                                               "Map2", new JsonSchema() {{
                                                                   setProperties(Map.of("Key21", new JsonSchema() {{
                                                                                            setType(JsonFormatTypes.STRING);
                                                                                        }},
                                                                                        "Key22", new JsonSchema() {{
                                                                                            setType(JsonFormatTypes.STRING);
                                                                                        }},
                                                                                        "Key23", new JsonSchema() {{
                                                                                            setType(JsonFormatTypes.STRING);
                                                                                        }}));
                                                               }},
                                                               "Map3", new JsonSchema() {{
                                                                   setProperties(Map.of("Key31", new JsonSchema() {{
                                                                                            setType(JsonFormatTypes.STRING);
                                                                                        }},
                                                                                        "Key32", new JsonSchema() {{
                                                                                            setType(JsonFormatTypes.STRING);
                                                                                        }},
                                                                                        "Key33", new JsonSchema() {{
                                                                                            setType(JsonFormatTypes.STRING);
                                                                                        }}));
                                                               }});
                Map<String, Object> inputMap = new HashMap<>();
                Map<String, Object> result = JsonSchemaUtils.mergeMaps(jsonSchemaMap,
                                                                       Collections.emptyMap(),
                                                                       inputMap);
                assertSame(inputMap, result);
                assertEquals(expectedResult, result);
            }

            @Test
            void withNoMapAsInstanceGroupProperty() {
                Map<String, Object> expectedResult = Map.of("Map1", Map.of("Key11", "",
                                                                           "Key12", "",
                                                                           "Key13", ""),
                                                            "Map2", Map.of("Key21", "",
                                                                           "Key22", "",
                                                                           "Key23", ""),
                                                            "Map3", Map.of("Key31", "",
                                                                           "Key32", "",
                                                                           "Key33", ""));
                Map<String, Object> instanceGroupProperties = Map.of("Map1", List.of("Value11",
                                                                                     "Value12",
                                                                                     "Value13"),
                                                                     "Map2", List.of("Value21",
                                                                                     "Value22",
                                                                                     "Value23"),
                                                                     "Map3", List.of("Value31",
                                                                                     "Value32",
                                                                                     "Value33"));
                Map<String, JsonSchema> jsonSchemaMap = Map.of("Map1", new JsonSchema() {{
                                                                   setProperties(Map.of("Key11", new JsonSchema() {{
                                                                                            setType(JsonFormatTypes.STRING);
                                                                                        }},
                                                                                        "Key12", new JsonSchema() {{
                                                                                            setType(JsonFormatTypes.STRING);
                                                                                        }},
                                                                                        "Key13", new JsonSchema() {{
                                                                                            setType(JsonFormatTypes.STRING);
                                                                                        }}));
                                                               }},
                                                               "Map2", new JsonSchema() {{
                                                                   setProperties(Map.of("Key21", new JsonSchema() {{
                                                                                            setType(JsonFormatTypes.STRING);
                                                                                        }},
                                                                                        "Key22", new JsonSchema() {{
                                                                                            setType(JsonFormatTypes.STRING);
                                                                                        }},
                                                                                        "Key23", new JsonSchema() {{
                                                                                            setType(JsonFormatTypes.STRING);
                                                                                        }}));
                                                               }},
                                                               "Map3", new JsonSchema() {{
                                                                   setProperties(Map.of("Key31", new JsonSchema() {{
                                                                                            setType(JsonFormatTypes.STRING);
                                                                                        }},
                                                                                        "Key32", new JsonSchema() {{
                                                                                            setType(JsonFormatTypes.STRING);
                                                                                        }},
                                                                                        "Key33", new JsonSchema() {{
                                                                                            setType(JsonFormatTypes.STRING);
                                                                                        }}));
                                                               }});
                Map<String, Object> inputMap = new HashMap<>();
                Map<String, Object> result = JsonSchemaUtils.mergeMaps(jsonSchemaMap,
                                                                       instanceGroupProperties,
                                                                       inputMap);
                assertSame(inputMap, result);
                assertEquals(expectedResult, result);
            }

            @Test
            void withInstanceGroupProperty() {
                Map<String, Object> expectedResult = Map.of("Map1", Map.of("Key11", "Value11",
                                                                           "Key12", "Value12",
                                                                           "Key13", "Value13"),
                                                            "Map2", Map.of("Key21", "Value21",
                                                                           "Key22", "Value22",
                                                                           "Key23", "Value23"),
                                                            "Map3", Map.of("Key31", "Value31",
                                                                           "Key32", "Value32",
                                                                           "Key33", "Value33"));
                Map<String, Object> instanceGroupProperties = Map.of("Map1", Map.of("Key11", "Value11",
                                                                                    "Key12", "Value12",
                                                                                    "Key13", "Value13"),
                                                                     "Map2", Map.of("Key21", "Value21",
                                                                                    "Key22", "Value22",
                                                                                    "Key23", "Value23"),
                                                                     "Map3", Map.of("Key31", "Value31",
                                                                                    "Key32", "Value32",
                                                                                    "Key33", "Value33"));
                Map<String, JsonSchema> jsonSchemaMap = Map.of("Map1", new JsonSchema() {{
                                                                   setProperties(Map.of("Key11", new JsonSchema(),
                                                                                        "Key12", new JsonSchema(),
                                                                                        "Key13", new JsonSchema()));
                                                               }},
                                                               "Map2", new JsonSchema() {{
                                                                   setProperties(Map.of("Key21", new JsonSchema(),
                                                                                        "Key22", new JsonSchema(),
                                                                                        "Key23", new JsonSchema()));
                                                               }},
                                                               "Map3", new JsonSchema() {{
                                                                   setProperties(Map.of("Key31", new JsonSchema(),
                                                                                        "Key32", new JsonSchema(),
                                                                                        "Key33", new JsonSchema()));
                                                               }});
                Map<String, Object> inputMap = new HashMap<>();
                Map<String, Object> result = JsonSchemaUtils.mergeMaps(jsonSchemaMap,
                                                                       instanceGroupProperties,
                                                                       inputMap);
                assertSame(inputMap, result);
                assertEquals(expectedResult, result);

            }

        }

    }

}
