package de.teatime08;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Add this Annotation to you methods in Spring to measure times with @Banq.
 * This does not work for methods which are not called within a Spring @Component.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Banq {}