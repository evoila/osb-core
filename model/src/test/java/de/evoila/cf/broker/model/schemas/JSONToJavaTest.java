package de.evoila.cf.broker.model.schemas;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.json.JSONException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;


public class JSONToJavaTest {
	
	private Logger log = LoggerFactory.getLogger(JSONToJavaTest.class);
	private static final String PATH_VALID = "." + File.separator + "src" + File.separator + "test" + File.separator
			+ "resources" + File.separator + "jsonTestSchema1.json"; 
	
	@Test
	public void testSchemaIsValid() throws IOException, JSONException, ProcessingException {
    	JsonNode schema = JsonLoader.fromPath(PATH_VALID);
    	JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
    	ProcessingReport r = factory.getSyntaxValidator().validateSchema(schema);
    	assertTrue(r.toString(),r.isSuccess());
    	assertFalse("The schema holds keywords that are unkown. This may cause the validator to skip invalid parts of the schema:\n"+r.toString(), r.toString().contains("warning: the following keywords are unknown and will be ignored:"));
    	log.info(r.toString().trim());
    	
    	
	}
}
