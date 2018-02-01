package com.worldventures.dreamtrips.core.rx;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.view.View;

import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;

import rx.Observable;
import rx.subjects.PublishSubject;

public abstract class RxBaseFragmentWithArgs<PM extends Presenter, P extends Parcelable> extends BaseFragmentWithArgs<PM, P> implements RxView {

   private final PublishSubject<FragmentEvent> lifecycleSubject = PublishSubject.create();

   protected final Observable<FragmentEvent> lifecycle() {
      return lifecycleSubject.asObservable();
   }

   @Override
   public <T> Observable<T> bind(Observable<T> observable) {
      return bindUntilDropView(observable);
   }

   @Override
   public <T> Observable<T> bindUntilStop(Observable<T> observable) {
      return observable.compose(RxLifecycle.bindUntilEvent(lifecycle(), FragmentEvent.STOP));
   }

   @Override
   public <T> Observable<T> bindUntilDropView(Observable<T> observable) {
      return observable.compose(RxLifecycle.bindUntilEvent(lifecycle(), FragmentEvent.DESTROY_VIEW));
   }

   protected <T> Observable.Transformer<T, T> bindUntilStopViewComposer() {
      return RxLifecycle.bindUntilEvent(lifecycle(), FragmentEvent.STOP);
   }

   protected <T> Observable.Transformer<T, T> bindUntilDropViewComposer() {
      return RxLifecycle.bindUntilEvent(lifecycle(), FragmentEvent.DESTROY_VIEW);
   }

   protected <T> Observable.Transformer<T, T> bindUntilPauseComposer() {
      return RxLifecycle.bindUntilEvent(lifecycle(), FragmentEvent.PAUSE);
   }

   @Override
   @CallSuper
   public void onAttach(android.app.Activity activity) {
      super.onAttach(activity);
      lifecycleSubject.onNext(FragmentEvent.ATTACH);
   }

   @Override
   @CallSuper
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      lifecycleSubject.onNext(FragmentEvent.CREATE);
   }

   @Override
   @CallSuper
   public void onViewCreated(View view, Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);
      lifecycleSubject.onNext(FragmentEvent.CREATE_VIEW);
   }

   @Override
   @CallSuper
   public void onStart() {
      super.onStart();
      lifecycleSubject.onNext(FragmentEvent.START);
   }

   @Override
   @CallSuper
   public void onResume() {
      super.onResume();
      lifecycleSubject.onNext(FragmentEvent.RESUME);
   }

   @Override
   @CallSuper
   public void onPause() {
      lifecycleSubject.onNext(FragmentEvent.PAUSE);
      super.onPause();
   }

   @Override
   @CallSuper
   public void onStop() {
      lifecycleSubject.onNext(FragmentEvent.STOP);
      super.onStop();
   }

   @Override
   @CallSuper
   public void onDestroyView() {
      lifecycleSubject.onNext(FragmentEvent.DESTROY_VIEW);
      super.onDestroyView();
   }

   @Override
   @CallSuper
   public void onDestroy() {
      lifecycleSubject.onNext(FragmentEvent.DESTROY);
      super.onDestroy();
   }

   @Override
   @CallSuper
   public void onDetach() {
      lifecycleSubject.onNext(FragmentEvent.DETACH);
      super.onDetach();
   }
}
