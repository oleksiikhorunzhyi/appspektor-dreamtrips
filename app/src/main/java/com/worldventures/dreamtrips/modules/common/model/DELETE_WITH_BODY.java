package com.worldventures.dreamtrips.modules.common.model;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import retrofit.http.RestMethod;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(METHOD)
@Retention(RUNTIME)
@RestMethod(value = "DELETE", hasBody = true)
public @interface DELETE_WITH_BODY {
   String value();
}
