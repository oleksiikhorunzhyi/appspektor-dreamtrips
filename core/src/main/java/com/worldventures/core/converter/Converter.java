package com.worldventures.core.converter;

public interface Converter<S, T> extends io.techery.mappery.Converter<S, T> {

   Class<S> sourceClass();

   Class<T> targetClass();

}
