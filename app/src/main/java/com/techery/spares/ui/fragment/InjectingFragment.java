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
import com.techery.spares.module.qualifier.Global;
import com.techery.spares.ui.activity.InjectingActivity;

import javax.inject.Inject;

import dagger.ObjectGraph;
import de.greenrobot.event.EventBus;
import timber.log.Timber;

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

   ///////////////////////////////////////////////////////////////////////////
   // Event handling
   ///////////////////////////////////////////////////////////////////////////

   @Inject @Global public EventBus eventBus;

   public EventBus getEventBus() {
      return eventBus;
   }

   @Override
   public void onResume() {
      super.onResume();
      try {
         this.eventBus.registerSticky(this, getEventBusPriority());
      } catch (Exception e) {
         Timber.v(e, "Can't register");
      }
   }

   @Override
   public void onPause() {
      super.onPause();
      if (this.eventBus.isRegistered(this)) {
         this.eventBus.unregister(this);
      }
   }

   public int getEventBusPriority() {
      return 0;
   }

}
