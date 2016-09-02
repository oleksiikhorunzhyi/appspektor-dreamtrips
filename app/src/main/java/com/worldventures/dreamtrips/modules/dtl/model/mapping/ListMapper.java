package com.worldventures.dreamtrips.modules.dtl.model.mapping;

import java.util.List;

public interface ListMapper<T, R> {

   List<R> map(List<T> list);
}
