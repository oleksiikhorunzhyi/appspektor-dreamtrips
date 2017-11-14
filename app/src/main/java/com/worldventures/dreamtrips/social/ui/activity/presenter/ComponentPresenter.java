package com.worldventures.dreamtrips.social.ui.activity.presenter;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.social.ui.podcast_player.delegate.PodcastPlayerDelegate;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Inject;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public class ComponentPresenter extends ActivityPresenter<ComponentPresenter.View> {

   public static final String FRAGMENT_CLAZZ = "FRAGMENT_CLAZZ";

   public static final String COMPONENT_EXTRA = "component_extras";
   public static final String COMPONENT_TOOLBAR_CONFIG = "component_toolbar";
   public static final String EXTRA_DATA = "EXTRA_DATA";
   public static final String DIALOG_GRAVITY = "DIALOG_GRAVITY";

   private Parcelable args;
   private Class<? extends Fragment> fragmentClazz;

   private boolean needMove;

   @Inject PodcastPlayerDelegate podcastPlayerDelegate;

   public ComponentPresenter(Bundle bundle) {
      fragmentClazz = (Class<? extends Fragment>) bundle.getSerializable(FRAGMENT_CLAZZ);
      this.args = bundle.getParcelable(EXTRA_DATA);
   }

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
      if (needMove) {
         view.moveTo(fragmentClazz, args);
      }
   }

   public int getTitle() {
      return getTitleForRouteName(fragmentClazz);
   }

   private int getTitleForRouteName(Class<? extends Fragment> clazzName) {
      ComponentTitle componentTitle = clazzName.getAnnotation(ComponentTitle.class);
      return componentTitle != null ? componentTitle.value() : 0;
   }

   public interface View extends ActivityPresenter.View {

      void moveTo(Class<? extends Fragment> clazzName, Parcelable args);
   }

   @Retention(RUNTIME)
   @Target(TYPE)
   public @interface ComponentTitle {
      int value();
   }
}
