package de.evoila.cf.broker.model.annotations;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseAdvice {

}