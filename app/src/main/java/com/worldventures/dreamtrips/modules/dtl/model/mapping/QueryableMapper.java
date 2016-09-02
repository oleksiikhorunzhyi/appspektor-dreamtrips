package com.worldventures.dreamtrips.modules.dtl.model.mapping;

import com.innahema.collections.query.functions.Converter;
import com.innahema.collections.query.queriables.Queryable;

import java.util.Collections;
import java.util.List;

public class QueryableMapper<T, R> implements ListMapper<T, R> {

   public final Converter<T, R> converter;

   public QueryableMapper(Converter<T, R> converter) {
      this.converter = converter;
   }

   @Override
   public List<R> map(List<T> list) {
      if (list == null) return null;
      return Collections.unmodifiableList(Queryable.from(list).map(converter).toList());
   }
}
