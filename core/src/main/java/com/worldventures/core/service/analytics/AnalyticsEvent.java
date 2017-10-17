package com.worldventures.core.service.analytics;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE)
@Retention(RUNTIME)
public @interface AnalyticsEvent {

   String category() default "";

   String action() default "";

   String[] trackers();

   boolean navigationState() default false;
}
