package de.evoila.cf.broker.model.catalog;

import de.evoila.cf.broker.exception.ServiceDefinitionPlanDoesNotExistException;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServiceDefinitionTest {

    private static String HAPPY_PLAN_ID = "b2a46348-e0bb-4731-bc39-052033e47bc6";
    private ServiceDefinition serviceDefinition = new ServiceDefinition();

    @Test
    void getPlanThrows() throws ServiceDefinitionPlanDoesNotExistException {
        assertThrows(ServiceDefinitionPlanDoesNotExistException.class, () -> {
            serviceDefinition.isPlanBindable(HAPPY_PLAN_ID);
        });
    }

    @Test
    void planIsBindable() throws ServiceDefinitionPlanDoesNotExistException {
        Plan plan = plan();
        plan.setBindable(true);
        serviceDefinition.setPlans(List.of(plan));

        assertTrue(serviceDefinition.isPlanBindable(HAPPY_PLAN_ID), "Should return true");
    }

    @Test
    void serviceDefinitionIsBindable() throws ServiceDefinitionPlanDoesNotExistException {
        Plan plan = plan();
        serviceDefinition.setBindable(true);
        serviceDefinition.setPlans(List.of(plan));

        assertTrue(serviceDefinition.isPlanBindable(HAPPY_PLAN_ID), "Should return true");
    }

    @Test
    void isNotBindable() throws ServiceDefinitionPlanDoesNotExistException {
        Plan plan = plan();
        serviceDefinition.setPlans(List.of(plan));

        assertFalse(serviceDefinition.isPlanBindable(HAPPY_PLAN_ID), "Should be false");
    }


    @Test
    void planIsUpdatable() throws ServiceDefinitionPlanDoesNotExistException {
        Plan plan = plan();
        plan.setPlanUpdateable(true);
        serviceDefinition.setPlans(List.of(plan));

        assert serviceDefinition.isPlanUpdatable(HAPPY_PLAN_ID);
    }

    @Test
    void serviceDefinitionIsUpdatable() throws ServiceDefinitionPlanDoesNotExistException {
        Plan plan = plan();
        serviceDefinition.setPlanUpdateable(true);
        serviceDefinition.setPlans(List.of(plan));

        assert serviceDefinition.isPlanUpdatable(HAPPY_PLAN_ID);
    }

    @Test
    void isNotUpdatable() throws ServiceDefinitionPlanDoesNotExistException {
        Plan plan = plan();
        serviceDefinition.setPlans(List.of(plan));

        assertFalse(serviceDefinition.isPlanUpdatable(HAPPY_PLAN_ID), "Should be false");
    }

    private Plan plan() {
        Plan plan = new Plan();
        plan.setId(HAPPY_PLAN_ID);

        return plan;
    }
}
