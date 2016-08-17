package com.worldventures.dreamtrips.core.rx.composer;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

public class ListMapper<T, R> implements Observable.Transformer<List<T>, List<R>> {

   private final Func1<T, R> mapFunc;

   public ListMapper(Func1<T, R> mapFunc) {
      this.mapFunc = mapFunc;
   }

   @Override
   public Observable<List<R>> call(Observable<List<T>> source) {
      return source.<List<R>>flatMap(items -> Observable.from(items).map(mapFunc).toList());
   }
}
