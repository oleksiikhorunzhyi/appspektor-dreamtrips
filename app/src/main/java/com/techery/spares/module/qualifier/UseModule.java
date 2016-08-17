package com.techery.spares.module.qualifier;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Documented
@Retention(RUNTIME)
public @interface UseModule {
   Class value();
}
