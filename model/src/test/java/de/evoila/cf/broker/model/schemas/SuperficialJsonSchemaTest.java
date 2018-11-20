package de.evoila.cf.broker.model.schemas;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import de.evoila.cf.broker.model.catalog.plan.Plan;

/**
 * This Test class checks for simple equality between the given initial json and the generated one, 
 * after converting the initial json into java objects
 * @author Marius Berger
 *
 */
public class SuperficialJsonSchemaTest {
	
	private Logger log = LoggerFactory.getLogger(SuperficialJsonSchemaTest.class);
	
	
	private static final String PATH_SCHEMA_VALID_1 = "." + File.separator + "src" + File.separator + "test"
			+ File.separator + "resources" + File.separator + "testJsonSchema1.json";

	private static final String PATH_SCHEMA_INVALID_1 = "." + File.separator + "src" + File.separator + "test"
			+ File.separator + "resources" + File.separator + "testJsonSchema2Invalid.json";
	
	
	private static final String PATH_PLAN_VALID_1 = "." + File.separator + "src" + File.separator + "test"
			+ File.separator + "resources" + File.separator + "testPlan1.json";

	private static final String PATH_PLAN_VALID_2 = "." + File.separator + "src" + File.separator + "test"
			+ File.separator + "resources" + File.separator + "testPlan2.json";

	private static final String PATH_PLAN_VALID_3 = "." + File.separator + "src" + File.separator + "test"
			+ File.separator + "resources" + File.separator + "testPlan3.json";

	private static final String PATH_PLAN_4_INVALID = "." + File.separator + "src" + File.separator + "test"
			+ File.separator + "resources" + File.separator + "testPlan4Invalid.json";
	
	private static final String PATH_PLAN_5_INVALID = "." + File.separator + "src" + File.separator + "test"
			+ File.separator + "resources" + File.separator + "testPlan5Invalid.json";

		
	@Test
	public void testSchemaIsValid() throws IOException, JSONException, ProcessingException {
		runSchemaTestWithFile(PATH_SCHEMA_VALID_1);
	}
	
	@Test(expected = JsonParseException.class)
	public void testSchema2IsInvalid() throws IOException {
		runSchemaTestWithFile(PATH_SCHEMA_INVALID_1);
	}
	
	public void runSchemaTestWithFile(String path) throws IOException {
    	JsonNode schema = JsonLoader.fromPath(path);
    	JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
    	ProcessingReport r = factory.getSyntaxValidator().validateSchema(schema);
    	assertTrue(r.toString(),r.isSuccess());
    	assertFalse("The schema holds keywords that are unkown. This may cause the validator to skip invalid parts of the schema:\n" +r.toString(),
    			r.toString().contains("warning: the following keywords are unknown and will be ignored:"));
    	log.info(r.toString().trim());
	}
	
	@Test
	public void testPlan1JsonDeserialization() throws JSONException, IOException {
		File file = new File(PATH_PLAN_VALID_1);		
		assertTrue(testPlanFromJson(file, true));
	}	
	
	@Test
	public void testPlan2JsonDeserialization() throws JSONException, IOException {
		File file = new File(PATH_PLAN_VALID_2);		
		assertTrue(testPlanFromJson(file, true));
	}	
	
	@Test
	public void testPlan3JsonDeserialization() throws JSONException, IOException {
		File file = new File(PATH_PLAN_VALID_3);		
		assertTrue("Initial JSON and generated JSON should be equal but are not.", testPlanFromJson(file, true));
	}	
	
	@Test
	public void testPlan4JsonDeserializationFail() throws JSONException, IOException {
		File file = new File(PATH_PLAN_4_INVALID);		
		assertFalse("Initial JSON and generated JSON should be unequal but are equal.", testPlanFromJson(file, false));
	}
	
	@Test (expected = JsonMappingException.class)
	public void testPlan5JsonDeserializationFail() throws JSONException, IOException {
		File file = new File(PATH_PLAN_5_INVALID);		
		assertFalse("Initial JSON and generated JSON should be unequal but are equal.", testPlanFromJson(file, false));
	}
	
	/*
	 * This methos checks for equality of two json Strings!
	 * Hence changes in order of the json elements, although correct, will throw off this test.
	 * Be aware of that, when using the test on other files.
	 */
	public boolean testPlanFromJson(File file, boolean expectsEqual) throws IOException, JSONException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		parsableJSON(jsonFromFile(file));
		Plan p = mapper.readValue(file, Plan.class);
		JsonNode node = mapper.readTree(file);
		String initialJSON = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapper.readValue(node.toString(), Object.class));
		String afterDeserialization = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(p);
		boolean equal = initialJSON.equals(afterDeserialization);
		if (expectsEqual && !equal) {
			log.debug("Encountered a test violating result, therefore logging both JSONs");
			log.debug("Initial JSON from file " + file.getAbsolutePath() + ": " + System.lineSeparator() + initialJSON);
			log.debug("JSON generated from Java objects after deserialization: " + System.lineSeparator() + afterDeserialization);
			log.debug("Logging the initial json until the first mismatch is detected between the two:"+System.lineSeparator());
			log.debug(initialJSON.substring(0, getIndexUntilFirstMismatch(initialJSON, afterDeserialization)));
		}
		return equal;
	}
	
	/*
	 * See testPlanFromJson(File file, boolean expectsEqual) method.
	 */
	public boolean testJson(String json, boolean expectsEqual) throws IOException, JSONException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		parsableJSON(json);
		Plan p = mapper.readValue(json, Plan.class);
		JsonNode node = mapper.readTree(json);
		String initialJSON = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapper.readValue(node.toString(), Object.class));
		String afterDeserialization = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(p);
		boolean equal = initialJSON.equals(afterDeserialization);
		if (expectsEqual && !equal) {
			log.debug("Encountered a test violating result, therefore logging both JSONs");
			log.debug("Initial JSON from String: " + System.lineSeparator() + initialJSON);
			log.debug("JSON generated from Java objects after deserialization: " + System.lineSeparator() + afterDeserialization);
		}
		return equal;
	}
	
	private boolean parsableJSON(String json) throws JSONException {
		//tries to parse the Json String into a JSONObject for validity check
		new JSONObject(json);
		return true;
	}
	
	private String jsonFromFile(File file) throws IOException {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String output = "";
			while (br.ready()) {
				output += br.readLine();
			}
			br.close();
			return output;
		} finally {
			if (br != null)
				br.close();
		}
	}
	
	private int getIndexUntilFirstMismatch(String a, String b) {
		int length = Math.min(a.length(), b.length());
		for (int i = 0; i < length; i++) {
			if (a.charAt(i) != b.charAt(i)) 
				return i;
		}
		if (a.length() != b.length())
			return Math.min(a.length(), b.length());
		return -1;
	}
}
