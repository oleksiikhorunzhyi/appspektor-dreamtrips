package com.worldventures.dreamtrips.social.ui.feed.view.cell.base;

import android.support.v7.widget.CardView;
import android.view.View;

import com.worldventures.core.di.qualifier.ForActivity;
import com.worldventures.core.janet.Injector;
import com.worldventures.core.model.User;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.module.FragmentClassProviderModule;

import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.FragmentClassProvider;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.feed.service.analytics.ViewFeedEntityAction;
import com.worldventures.dreamtrips.social.ui.feed.bundle.FeedItemDetailsBundle;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.util.FeedViewInjector;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.FeedItemDetailsFragment;
import com.worldventures.dreamtrips.social.ui.feed.view.util.FeedItemCommonDataHelper;
import com.worldventures.dreamtrips.social.ui.profile.bundle.UserBundle;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import timber.log.Timber;

public abstract class FeedItemDetailsCell<I extends FeedItem, D extends BaseFeedCell.FeedCellDelegate<I>> extends BaseFeedCell<I, D> {

   FeedItemCommonDataHelper feedItemCommonDataHelper;

   @Inject @Named(FragmentClassProviderModule.PROFILE) FragmentClassProvider<Integer> fragmentClassProvider;
   @Inject @ForActivity protected Provider<Injector> injectorProvider;
   @Inject FeedViewInjector feedViewInjector;

   @InjectView(R.id.card_view_wrapper) CardView cardViewWrapper;

   public FeedItemDetailsCell(View view) {
      super(view);
      ButterKnife.inject(this, view);
      feedItemCommonDataHelper = new FeedItemCommonDataHelper(view.getContext());
      feedItemCommonDataHelper.attachView(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      super.syncUIStateWithModel();

      if (!sessionHolder.get().isPresent()) {
         Timber.e("Something went wrong! Session is absent, but feed item is trying to render");
         return;
      }
      feedItemCommonDataHelper.set(getModelObject(), sessionHolder.get()
            .get()
            .user()
            .getId(), injectorProvider.get());
      feedViewInjector.initCardViewWrapper(cardViewWrapper);
   }

   public final void openItemDetails() {
      onOpenItemDetails();
      trackOpened();
   }

   private void onOpenItemDetails() {
      FeedItemDetailsBundle.Builder bundleBuilder = new FeedItemDetailsBundle.Builder().feedItem(getModelObject())
            .showAdditionalInfo(true);
      if (tabletAnalytic.isTabletLandscape()) {
         bundleBuilder.slave(true);
      }
      router.moveTo(FeedItemDetailsFragment.class, NavigationConfigBuilder.forActivity().manualOrientationActivity(true)
            .data(bundleBuilder.build()).build());
   }

   protected final void openEntityDetails() {
      onOpenEntityDetails();
      trackOpened();
   }

   protected void onOpenEntityDetails() {
      // no implementation by default
   }

   private void trackOpened() {
      analyticsInteractor.analyticsActionPipe().send(ViewFeedEntityAction.view(getModelObject().getType(),
            getModelObject().getItem().getUid()));
   }

   @Optional
   @OnClick(R.id.feed_header_avatar)
   void eventOwnerClicked() {
      User user = getModelObject().getLinks().getUsers().get(0);
      router.moveTo(fragmentClassProvider.provideFragmentClass(user.getId()), NavigationConfigBuilder.forActivity()
            .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
            .data(new UserBundle(user))
            .build());
   }
}
