package com.worldventures.dreamtrips.social.ui.activity;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.view.activity.ToolbarActivity;
import com.worldventures.dreamtrips.social.ui.activity.presenter.ComponentPresenter;

@Layout(R.layout.activity_component)
public class SocialComponentActivity extends ToolbarActivity<ComponentPresenter> implements ComponentPresenter.View {

   Bundle extras;

   @Override
   protected int getToolbarTitle() {
      return getPresentationModel().getTitle();
   }

   @Override
   protected void beforeCreateView(Bundle savedInstanceState) {
      extras = getIntent().getBundleExtra(ComponentPresenter.COMPONENT_EXTRA);
      super.beforeCreateView(savedInstanceState);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      initToolbar();
   }

   private void initToolbar() {
      ToolbarConfig toolbarConfig = (ToolbarConfig) extras.getSerializable(ComponentPresenter.COMPONENT_TOOLBAR_CONFIG);

      if (toolbarConfig != null) {
         toolbar.setVisibility(toolbarConfig.isVisible() ? View.VISIBLE : View.GONE);
         toolbar.setAlpha(toolbarConfig.getAlpha());
      }
   }

   @Override
   protected ComponentPresenter createPresentationModel(Bundle savedInstanceState) {
      return new ComponentPresenter(extras);
   }

   @Override
   public void moveTo(Route route, Parcelable args) {
      router.moveTo(route, NavigationConfigBuilder.forFragment()
            .fragmentManager(getSupportFragmentManager())
            .containerId(R.id.container_main)
            .backStackEnabled(true)
            .data(args)
            .build());
   }
}
