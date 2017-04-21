package com.techery.spares.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techery.spares.module.Injector;
import com.techery.spares.ui.activity.InjectingActivity;
import com.trello.rxlifecycle.FragmentEvent;
import com.trello.rxlifecycle.RxLifecycle;

import dagger.ObjectGraph;
import rx.Observable;
import rx.subjects.PublishSubject;

public abstract class InjectingDialogFragment extends DialogFragment implements ConfigurableFragment, Injector {

   private final PublishSubject<FragmentEvent> lifecycleSubject = PublishSubject.create();

   protected boolean injectCustomLayout = true;

   @Override
   public void onAttach(Context context) {
      super.onAttach(context);
      inject(this);
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      if (injectCustomLayout) {
         return FragmentHelper.onCreateView(inflater, container, this);
      } else {
         return super.onCreateView(inflater, container, savedInstanceState);
      }
   }

   @Override
   public void inject(Object target) {
      getObjectGraph().inject(target);
   }

   @Override
   public ObjectGraph getObjectGraph() {
      return ((InjectingActivity) getActivity()).getObjectGraph();
   }

   @Override
   @CallSuper
   public void onDestroyView() {
      lifecycleSubject.onNext(FragmentEvent.DESTROY_VIEW);
      super.onDestroyView();
   }

   public final <T> Observable.Transformer<T, T> bindToLifecycle() {
      return RxLifecycle.bindFragment(lifecycleSubject);
   }

   public void afterCreateView(View rootView) {
   }
}
