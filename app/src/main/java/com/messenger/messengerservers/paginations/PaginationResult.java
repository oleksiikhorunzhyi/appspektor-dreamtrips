package com.messenger.messengerservers.paginations;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface PaginationResult<T> {

   List<T> getResult();

   int getLoadedCount();
}
