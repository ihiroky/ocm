package net.ihiroky.ocm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that an annotated variable holds arguments.
 * The variable is an instance of {@code java.util.List<String>}.
 *
 * @author Hiroki Itoh
  */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Arguments {

    /**
     * a property used in a one line usage.
     *
     * @return metaName.
     */
    String metaName() default "";

    /**
     * require flag.
     *
     * @return true if arguments is found in command line arguments.
     */
    boolean required() default false;

}
