package com.etu1999.ambovombe.mapping.annotation.more;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
/**
 * Defining that the attribute will be inherited in the child class, 
 * with its dao's property if it has, 
 * however it will be useless
 */
public @interface Inherit {
    boolean value() default true;
}
