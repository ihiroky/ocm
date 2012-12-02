package net.ihiroky.ocm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a field is an option storage.
 *
 * @author Hiroki Itoh
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Option {

    /** A option name (long name). */
    String name();

    /** A option name (short name). */
    String alias() default "";

    /** A option meta name used in usage. */
    String metaName() default "";

    /** usage of this option. */
    String usage() default "";

    /** flag that shows this option is required. */
    boolean required() default false;
}
