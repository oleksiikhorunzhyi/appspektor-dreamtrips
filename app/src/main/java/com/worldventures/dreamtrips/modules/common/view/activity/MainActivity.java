package com.worldventures.dreamtrips.modules.common.view.activity;

import android.os.Bundle;
import android.text.TextUtils;

import com.messenger.di.MessengerActivityModule;
import com.messenger.ui.activity.MessengerActivity;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.utils.tracksystem.LifecycleEvent;
import com.worldventures.dreamtrips.modules.common.presenter.MainActivityPresenter;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlActivity;
import com.worldventures.dreamtrips.modules.dtl_flow.di.DtlActivityModule;
import com.worldventures.dreamtrips.modules.feed.FeedModule;
import com.worldventures.dreamtrips.modules.feed.view.activity.FeedActivity;
import com.worldventures.dreamtrips.modules.feed.view.activity.SocialDrawerActivity;
import com.worldventures.dreamtrips.modules.navdrawer.NavigationDrawerView;
import com.worldventures.dreamtrips.modules.navdrawer.NavigationDrawerViewImpl;
import com.worldventures.dreamtrips.modules.profile.ProfileModule;
import com.worldventures.dreamtrips.wallet.di.WalletActivityModule;
import com.worldventures.dreamtrips.wallet.ui.WalletActivity;

import butterknife.InjectView;
import rx.schedulers.Schedulers;

@Layout(R.layout.activity_main)
public class MainActivity extends SocialDrawerActivity<MainActivityPresenter> implements MainActivityPresenter.View {

   @InjectView(R.id.drawer_layout) NavigationDrawerViewImpl navigationDrawerView;

   public static final String COMPONENT_KEY = "MainActivity$ComponentKey";

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
         itemSelected(componentToShow);
      } else {
         updateActionBar(currentComponent);
         navigationDrawerPresenter.setCurrentComponent(currentComponent);
      }
   }

   @Override
   protected void itemSelected(ComponentDescription component) {
      switch (component.getKey()) {
         case MessengerActivityModule.MESSENGER:
            MessengerActivity.startMessenger(this);
            break;
         case DtlActivityModule.DTL:
            DtlActivity.startDtl(this);
            break;
         case WalletActivityModule.WALLET:
            WalletActivity.startWallet(this);
            break;
         case FeedModule.FEED:
         case ProfileModule.ACCOUNT_PROFILE:
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
      if (currentComponent == null && component.shouldFinishMainActivity()) finish();
   }
}
