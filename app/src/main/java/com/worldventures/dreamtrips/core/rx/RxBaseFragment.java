package com.worldventures.dreamtrips.core.rx;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.view.View;

import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;

import rx.Observable;
import rx.subjects.PublishSubject;

public abstract class RxBaseFragment<PM extends Presenter> extends BaseFragment<PM> implements RxView {

   private final PublishSubject<FragmentEvent> lifecycleSubject = PublishSubject.create();

   protected Observable<FragmentEvent> lifecycle() {
      return lifecycleSubject.asObservable();
   }

   public <T> Observable.Transformer<T, T> bindViewComposer() {
      return observable -> bindUntilDropView(observable);
   }

   public <T> Observable.Transformer<T, T> bindViewStoppedComposer() {
      return observable -> bindUntilStop(observable);
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
