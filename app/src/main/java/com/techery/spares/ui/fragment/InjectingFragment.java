package com.techery.spares.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techery.spares.annotations.MenuResource;
import com.techery.spares.module.Injector;
import com.techery.spares.ui.activity.InjectingActivity;

import dagger.ObjectGraph;

public abstract class InjectingFragment extends Fragment implements ConfigurableFragment, Injector {

   @Override
   public void onAttach(Context context) {
      super.onAttach(context);
      inject(this);
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
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      return FragmentHelper.onCreateView(inflater, container, this);
   }

   @Override
   public void onActivityCreated(Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);
      setupMenuIfNeed();
   }

   private MenuResource menuResource;

   private void setupMenuIfNeed() {
      menuResource = this.getClass().getAnnotation(MenuResource.class);
      setHasOptionsMenu(menuResource != null);
   }

   @Override
   public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
      // isAdded use for onCreateOptionsMenu method
      // when we take smth from resource and fragment isn't attached
      if (menuResource != null && isAdded()) {
         menu.clear();
         inflater.inflate(menuResource.value(), menu);
         onMenuInflated(menu);
      }
   }

   protected void onMenuInflated(Menu menu) {

   }

   @Override
   public void afterCreateView(View rootView) {
   }
}
