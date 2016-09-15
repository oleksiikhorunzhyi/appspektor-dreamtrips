package com.worldventures.dreamtrips.modules.feed.view.cell.base;

import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;

import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedItemDetailsBundle;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.view.util.FeedItemCommonDataHelper;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

public abstract class FeedItemDetailsCell<I extends FeedItem, D extends CellDelegate<I>> extends BaseFeedCell<I, D> {

   FeedItemCommonDataHelper feedItemCommonDataHelper;

   @Inject @Named(RouteCreatorModule.PROFILE) RouteCreator<Integer> routeCreator;
   @Inject @ForActivity Provider<Injector> injectorProvider;

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
      feedItemCommonDataHelper.set(getModelObject(), sessionHolder.get()
            .get()
            .getUser()
            .getId(), injectorProvider.get());
      if (ViewUtils.isTablet(itemView.getContext())) {
         cardViewWrapper.setCardElevation(4);
         int m = itemView.getContext().getResources().getDimensionPixelSize(R.dimen.spacing_small);
         ((ViewGroup.MarginLayoutParams) cardViewWrapper.getLayoutParams()).setMargins(m, m, m, m);
      } else {
         cardViewWrapper.setCardElevation(0);
      }
   }

   public void openItemDetails() {
      Route detailsRoute = Route.FEED_ITEM_DETAILS;
      FeedItemDetailsBundle.Builder bundleBuilder = new FeedItemDetailsBundle.Builder().feedItem(getModelObject())
            .showAdditionalInfo(true);
      if (tabletAnalytic.isTabletLandscape()) {
         bundleBuilder.slave(true);
      }
      router.moveTo(detailsRoute, NavigationConfigBuilder.forActivity().data(bundleBuilder.build()).build());
      //
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
