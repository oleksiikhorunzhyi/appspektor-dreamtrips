package com.worldventures.dreamtrips.social.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;

import com.messenger.di.MessengerModule;
import com.messenger.ui.activity.MessengerActivity;
import com.worldventures.core.component.ComponentDescription;
import com.worldventures.core.service.analytics.LifecycleEvent;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.utils.CrashlyticsTracker;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.util.ComponentDescriptionException;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlActivity;
import com.worldventures.dreamtrips.modules.dtl_flow.di.DtlModule;
import com.worldventures.dreamtrips.modules.navdrawer.NavigationDrawerView;
import com.worldventures.dreamtrips.modules.navdrawer.NavigationDrawerViewImpl;
import com.worldventures.dreamtrips.social.di.SocialAppModule;
import com.worldventures.dreamtrips.social.ui.activity.presenter.MainActivityPresenter;
import com.worldventures.dreamtrips.wallet.di.SmartCardModule;
import com.worldventures.dreamtrips.wallet.ui.WalletActivity;

import butterknife.InjectView;
import rx.schedulers.Schedulers;

@Layout(R.layout.activity_main)
public class SocialMainActivity extends SocialDrawerActivity<MainActivityPresenter> implements MainActivityPresenter.View {

   @InjectView(R.id.drawer_layout) NavigationDrawerViewImpl navigationDrawerView;

   public static final String COMPONENT_KEY = "SocialMainActivity$ComponentKey";

   @Override
   protected MainActivityPresenter createPresentationModel(Bundle savedInstanceState) {
      return new MainActivityPresenter();
   }

   @Override
   public void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
      analyticsInteractor.analyticsActionPipe()
            .send(new LifecycleEvent(LifecycleEvent.ACTION_ONSAVESTATE, outState), Schedulers.immediate());
   }

   @Override
   protected void onRestoreInstanceState(Bundle savedInstanceState) {
      super.onRestoreInstanceState(savedInstanceState);
      analyticsInteractor.analyticsActionPipe()
            .send(new LifecycleEvent(LifecycleEvent.ACTION_ONRESTORESTATE, savedInstanceState), Schedulers.immediate());
   }

   @Override
   protected NavigationDrawerView getNavigationDrawer() {
      return navigationDrawerView;
   }

   @Override
   protected void afterCreateView(Bundle savedInstanceState) {
      super.afterCreateView(savedInstanceState);
      String keyComponent = null;

      if (getIntent().getExtras() != null) {
         Bundle bundle = getIntent().getBundleExtra(ActivityRouter.EXTRA_BUNDLE);
         keyComponent = bundle.getString(COMPONENT_KEY);
      }

      BaseFragment currentFragment = (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.container_main);
      ComponentDescription componentToShow;
      if (currentComponent == null && currentFragment != null) {
         componentToShow = rootComponentsProvider.getComponentByFragment(currentFragment.getClass());
      } else if (currentComponent == null && !TextUtils.isEmpty(keyComponent)) {
         componentToShow = rootComponentsProvider.getComponentByKey(keyComponent);
      } else {
         componentToShow = rootComponentsProvider.getActiveComponents().get(0);
      }

      if (currentFragment == null) {

         //todo delete this block after release 1.22
         if (componentToShow == null) {
            logComponentDescriptionException(keyComponent);
            componentToShow = rootComponentsProvider.getActiveComponents().get(0);
         }

         itemSelected(componentToShow);
      } else {
         updateActionBar(currentComponent);
         navigationDrawerPresenter.setCurrentComponent(currentComponent);
      }
   }

   private void logComponentDescriptionException(String keyComponent) {
      StringBuilder exceptionMessage = new StringBuilder("The key ").append(keyComponent).append(" is not found in");
      for (ComponentDescription componentDescription : rootComponentsProvider.getActiveComponents()) {
         exceptionMessage.append(" ").append(componentDescription.getKey()).append(",");
      }
      CrashlyticsTracker.trackError(new ComponentDescriptionException(exceptionMessage.toString()));
   }

   @Override
   protected void itemSelected(ComponentDescription component) {
      switch (component.getKey()) {
         case MessengerModule.MESSENGER:
            MessengerActivity.startMessenger(this);
            break;
         case DtlModule.DTL:
            DtlActivity.startDtl(this);
            break;
         case SmartCardModule.WALLET:
            WalletActivity.startWallet(this);
            break;
         case SocialAppModule.FEED:
         case SocialAppModule.ACCOUNT_PROFILE:
            FeedActivity.startFeed(this, component);
            break;
         default:
            currentComponent = component;
            disableRightDrawer();
            updateActionBar(component);
            openComponent(component);
            break;
      }

      closeLeftDrawer();
      if (currentComponent == null && component.shouldFinishMainActivity()) {
         finish();
      }
   }
}
