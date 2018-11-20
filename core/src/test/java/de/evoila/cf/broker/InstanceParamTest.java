package de.evoila.cf.broker;

import de.evoila.cf.broker.exception.InvalidParametersException;
import de.evoila.cf.broker.model.*;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.util.ParameterValidator;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class InstanceParamTest {
    private Logger log = LoggerFactory.getLogger(InstanceParamTest.class);

    @Test
    public void testCreateInstanceParameterValidationFailsOnValues (ServiceInstanceRequest serviceInstanceRequest, Plan plan) throws Exception {

        assertNotNull(serviceInstanceRequest.getParameters());

        try {
            ParameterValidator.validateParameters(serviceInstanceRequest, plan);
        }catch (InvalidParametersException e){
            assertThat(e.getMessage(), is("Error while processing json schema. Values not allowed"));
        }
    }

    @Test
    public void testCreateInstanceParameterValidationFailsOnKeys (ServiceInstanceRequest serviceInstanceRequest, Plan plan) throws Exception {

        assertNotNull(serviceInstanceRequest.getParameters());

        try {
            ParameterValidator.validateParameters(serviceInstanceRequest, plan);
        }catch (InvalidParametersException e){
            assertThat(e.getMessage(), is("The specified parameters are invalid"));
        }
    }

    @Test
    public void testCreateInstanceParameterValidationSuccess (ServiceInstanceRequest serviceInstanceRequest, Plan plan) throws Exception {

        assertNotNull(serviceInstanceRequest.getParameters());

        try {
            ParameterValidator.validateParameters(serviceInstanceRequest, plan);
        }catch (InvalidParametersException e){
            assertNull(e);
        }
    }
}
