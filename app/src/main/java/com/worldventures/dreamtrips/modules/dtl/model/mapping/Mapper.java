package com.worldventures.dreamtrips.modules.dtl.model.mapping;

public interface Mapper<T, R> {

    R map(T source);
}
