package com.etu1999.ambovombe.mapping.annotation.misc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
/**
 * select t from table join tabl2 t2 on t.id = t2.id
 */
public @interface Join { 
    String reference() default "";
}
