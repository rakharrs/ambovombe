package com.etu1999.ambovombe.mapping.annotation.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.etu1999.ambovombe.mapping.fk.ForeignType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
/**
 * <ul>
 * <li>
 *  <strong>reference</strong>
 *  The reference of the column in the table
 *  <ul>
 *      <li>If Many to One : it should be the foreign key from the current dao source table</li>
 *      <li>If One to Many : it should be the foreign key reference of the current dao source table in the foreign table</li>
 *  </ul>
 * </li>
 * <li>
 *  <strong>foreignType</strong>
 *  Indicate the retrieve/fetch type
 * </li>
 * <li>
 *  <strong>initialize</strong>
 *  Indicate if the foreignKey attribute should be initialize manually with initForeignKey function
 * </li>
 * </ul>
 */
public @interface ForeignKey {
    String mappedBy();
    ForeignType foreignType() default ForeignType.ManyToOne;
    boolean initialize() default false;
}