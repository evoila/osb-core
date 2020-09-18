package de.evoila.cf.broker.model.catalog;

import de.evoila.cf.broker.exception.ServiceDefinitionPlanDoesNotExistException;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ServiceDefinitionTest {

    private static String HAPPY_PLAN_ID = "b2a46348-e0bb-4731-bc39-052033e47bc6";
    private ServiceDefinition serviceDefinition = new ServiceDefinition();

    @Test(expected = ServiceDefinitionPlanDoesNotExistException.class)
    public void getPlanThrows() throws ServiceDefinitionPlanDoesNotExistException {
        serviceDefinition.isPlanBindable(HAPPY_PLAN_ID);
    }

    @Test
    public void planIsBindable() throws ServiceDefinitionPlanDoesNotExistException {
        Plan plan = plan();
        plan.setBindable(true);
        serviceDefinition.setPlans(List.of(plan));

        Assert.assertTrue("Should return true", serviceDefinition.isPlanBindable(HAPPY_PLAN_ID));
    }

    @Test
    public void serviceDefinitionIsBindable() throws ServiceDefinitionPlanDoesNotExistException {
        Plan plan = plan();
        serviceDefinition.setBindable(true);
        serviceDefinition.setPlans(List.of(plan));

        Assert.assertTrue("Should return true", serviceDefinition.isPlanBindable(HAPPY_PLAN_ID));
    }

    @Test
    public void isNotBindable() throws ServiceDefinitionPlanDoesNotExistException {
        Plan plan = plan();
        serviceDefinition.setPlans(List.of(plan));

        Assert.assertFalse("Should be false", serviceDefinition.isPlanBindable(HAPPY_PLAN_ID));
    }


    @Test
    public void planIsUpdatable() throws ServiceDefinitionPlanDoesNotExistException {
        Plan plan = plan();
        plan.setPlanUpdateable(true);
        serviceDefinition.setPlans(List.of(plan));

        assert serviceDefinition.isPlanUpdatable(HAPPY_PLAN_ID);
    }

    @Test
    public void serviceDefinitionIsUpdatable() throws ServiceDefinitionPlanDoesNotExistException {
        Plan plan = plan();
        serviceDefinition.setPlanUpdateable(true);
        serviceDefinition.setPlans(List.of(plan));

        assert serviceDefinition.isPlanUpdatable(HAPPY_PLAN_ID);
    }

    @Test
    public void isNotUpdatable() throws ServiceDefinitionPlanDoesNotExistException {
        Plan plan = plan();
        serviceDefinition.setPlans(List.of(plan));

        Assert.assertFalse("Should be false", serviceDefinition.isPlanUpdatable(HAPPY_PLAN_ID));
    }

    private Plan plan() {
        Plan plan = new Plan();
        plan.setId(HAPPY_PLAN_ID);

        return plan;
    }
}
