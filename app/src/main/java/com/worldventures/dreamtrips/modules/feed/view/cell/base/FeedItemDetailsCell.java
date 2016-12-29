package com.worldventures.dreamtrips.modules.feed.view.cell.base;

import android.support.v7.widget.CardView;
import android.view.View;

import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedItemDetailsBundle;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.view.cell.util.FeedViewInjector;
import com.worldventures.dreamtrips.modules.feed.view.util.FeedItemCommonDataHelper;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;

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

   @Inject @Named(RouteCreatorModule.PROFILE) RouteCreator<Integer> routeCreator;
   @Inject @ForActivity Provider<Injector> injectorProvider;
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
            .getUser()
            .getId(), injectorProvider.get());
      feedViewInjector.initCardViewWrapper(cardViewWrapper);
   }

   public final void openItemDetails() {
      onOpenItemDetails();
      trackOpened();
   }

   protected void onOpenItemDetails() {
      Route detailsRoute = Route.FEED_ITEM_DETAILS;
      FeedItemDetailsBundle.Builder bundleBuilder = new FeedItemDetailsBundle.Builder().feedItem(getModelObject())
            .showAdditionalInfo(true);
      if (tabletAnalytic.isTabletLandscape()) {
         bundleBuilder.slave(true);
      }
      router.moveTo(detailsRoute, NavigationConfigBuilder.forActivity().data(bundleBuilder.build()).build());
   }

   public final void openEntityDetails() {
      onOpenEntityDetails();
      trackOpened();
   }

   protected void onOpenEntityDetails() {
      // no implementation by default
   }

   private void trackOpened() {
      sendAnalyticEvent(TrackingHelper.ATTRIBUTE_VIEW);
   }

   @Optional
   @OnClick(R.id.feed_header_avatar)
   void eventOwnerClicked() {
      User user = getModelObject().getLinks().getUsers().get(0);
      router.moveTo(routeCreator.createRoute(user.getId()), NavigationConfigBuilder.forActivity()
            .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
            .data(new UserBundle(user))
            .build());
   }
}
