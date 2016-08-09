package com.worldventures.dreamtrips.wallet.domain.converter;

public interface Converter<T, R> {
    R from(T object);

    T to(R object);
}