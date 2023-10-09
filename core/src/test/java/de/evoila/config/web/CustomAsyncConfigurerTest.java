package de.evoila.config.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.concurrent.Executor;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CustomAsyncConfigurerTest {

    private  CustomAsyncConfigurer configurer;

    @BeforeEach
    void setUp()
    {
        configurer = new CustomAsyncConfigurer();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void getAsyncExecutor() {
        Executor executor = configurer.getAsyncExecutor();
        assertNotNull(executor);
        assertThat(executor, instanceOf(ThreadPoolTaskExecutor.class));
        ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor)executor;
        assertEquals(15, taskExecutor.getCorePoolSize());
        assertEquals(30, taskExecutor.getMaxPoolSize());
        assertEquals("MyExecutor-", taskExecutor.getThreadNamePrefix());
        Field queueCapacityField = ReflectionUtils.findField(ThreadPoolTaskExecutor.class, "queueCapacity");
        ReflectionUtils.makeAccessible(queueCapacityField);
        assertEquals(15, ReflectionUtils.getField(queueCapacityField, taskExecutor) );
        Field waitField = ReflectionUtils.findField(ThreadPoolTaskExecutor.class, "waitForTasksToCompleteOnShutdown");
        ReflectionUtils.makeAccessible(waitField);
        assertEquals(true, ReflectionUtils.getField(waitField, taskExecutor) );
    }

    @Test
    void getAsyncUncaughtExceptionHandler() {
        AsyncUncaughtExceptionHandler exceptionHandler = configurer.getAsyncUncaughtExceptionHandler();
        assertNotNull(exceptionHandler);
        assertThat(exceptionHandler, instanceOf(SimpleAsyncUncaughtExceptionHandler.class));
    }
}
