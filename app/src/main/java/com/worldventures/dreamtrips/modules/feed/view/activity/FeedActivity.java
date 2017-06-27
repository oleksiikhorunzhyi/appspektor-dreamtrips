package com.worldventures.dreamtrips.modules.feed.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.modules.feed.FeedModule;
import com.worldventures.dreamtrips.modules.navdrawer.NavigationDrawerView;
import com.worldventures.dreamtrips.modules.navdrawer.NavigationDrawerViewImpl;
import com.worldventures.dreamtrips.modules.profile.ProfileModule;

import butterknife.InjectView;

@Layout(R.layout.activity_feed)
public class FeedActivity extends SocialDrawerActivity<ActivityPresenter> {

   public static final String BUNDLE = "FeedActivityBundle";

   @InjectView(R.id.drawer_layout) NavigationDrawerViewImpl navigationDrawerView;
   @InjectView(R.id.drawer_layout_landscape) NavigationDrawerViewImpl navigationDrawerViewLand;

   @Override
   protected void afterCreateView(Bundle savedInstanceState) {
      super.afterCreateView(savedInstanceState);
      FeedActivityBundle feedActivityBundle = getIntent().getParcelableExtra(BUNDLE);
      if (feedActivityBundle != null) {
         itemSelected(rootComponentsProvider.getComponentByKey(feedActivityBundle.componentDescriptionKey));
      } else {
         itemSelected(rootComponentsProvider.getComponentByKey(FeedModule.FEED));
      }
   }

   @Override
   public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
      toolbar.requestLayout();
      supportInvalidateOptionsMenu();
      setupDrawerLayout();
      navigationDrawerPresenter.attachView(getNavigationDrawer(), rootComponentsProvider.getActiveComponents());
      updateActionBar(currentComponent);
      navigationDrawerPresenter.setCurrentComponent(currentComponent);
   }

   @Override
   protected void setupDrawerLayout() {
      super.setupDrawerLayout();
      if (ViewUtils.isLandscapeOrientation(this)) {
         navigationDrawerView.setVisibility(View.GONE);
         navigationDrawerViewLand.setupViews();
         navigationDrawerViewLand.setVisibility(View.VISIBLE);
      } else {
         navigationDrawerView.setupViews();
         navigationDrawerView.setVisibility(View.VISIBLE);
         navigationDrawerViewLand.setVisibility(View.GONE);
      }
   }

   @Override
   protected NavigationDrawerView getNavigationDrawer() {
      return ViewUtils.isLandscapeOrientation(this) ? navigationDrawerViewLand : navigationDrawerView;
   }

   @Override
   protected ActivityPresenter createPresentationModel(Bundle savedInstanceState) {
      return new ActivityPresenter();
   }

   @Override
   protected void itemSelected(ComponentDescription component) {
      switch (component.getKey()) {
         case FeedModule.FEED:
         case ProfileModule.ACCOUNT_PROFILE:
            currentComponent = component;
            makeActionBarGone(component.skipGeneralToolbar());
            updateActionBar(component);
            openComponent(component);
            break;
         default:
            activityRouter.openMainWithComponent(component.getKey());
            break;
      }
      closeLeftDrawer();
   }

   public static void startFeed(Context context, ComponentDescription componentDescription) {
      Intent intent = new Intent(context, FeedActivity.class);
      intent.putExtra(BUNDLE, FeedActivityBundle.of(componentDescription));
      context.startActivity(intent);
   }

   public static class FeedActivityBundle implements Parcelable {
      String componentDescriptionKey;

      private FeedActivityBundle(String componentDescriptionKey) {
         this.componentDescriptionKey = componentDescriptionKey;
      }

      public static FeedActivityBundle of(ComponentDescription componentDescription) {
         return new FeedActivityBundle(componentDescription.getKey());
      }

      protected FeedActivityBundle(Parcel in) {
         componentDescriptionKey = in.readString();
      }

      @Override
      public void writeToParcel(Parcel dest, int flags) {
         dest.writeString(componentDescriptionKey);
      }

      @Override
      public int describeContents() {
         return 0;
      }

      public static final Creator<FeedActivityBundle> CREATOR = new Creator<FeedActivityBundle>() {
         @Override
         public FeedActivityBundle createFromParcel(Parcel in) {
            return new FeedActivityBundle(in);
         }

         @Override
         public FeedActivityBundle[] newArray(int size) {
            return new FeedActivityBundle[size];
         }
      };

   }
}
