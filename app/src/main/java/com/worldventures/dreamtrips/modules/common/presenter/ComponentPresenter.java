package com.worldventures.dreamtrips.modules.common.presenter;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.player.delegate.PodcastPlayerDelegate;

import javax.inject.Inject;

public class ComponentPresenter extends ActivityPresenter<ComponentPresenter.View> {

   public static final String ROUTE = "route";

   public static final String COMPONENT_EXTRA = "component_extras";
   public static final String COMPONENT_TOOLBAR_CONFIG = "component_toolbar";
   public static final String EXTRA_DATA = "EXTRA_DATA";
   public static final String DIALOG_GRAVITY = "DIALOG_GRAVITY";

   private Bundle args;
   private Route route;

   private boolean needMove;

   @Inject PodcastPlayerDelegate podcastPlayerDelegate;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      if (savedInstanceState == null) {
         needMove = true;
      }
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      podcastPlayerDelegate.stop();
      if (needMove) view.moveTo(route, args.getParcelable(EXTRA_DATA));
   }

   @Override
   public void onConfigurationChanged(Configuration configuration) {
      super.onConfigurationChanged(configuration);
      activity.recreate();
   }

   public int getTitle() {
      if (route != null) {
         return route.getTitleRes();
      } else {
         return 0;
      }
   }

   public ComponentPresenter(Bundle args) {
      route = (Route) args.getSerializable(ROUTE);
      this.args = args;
   }

   public interface View extends ActivityPresenter.View {

      void moveTo(Route route, Parcelable args);
   }
}
