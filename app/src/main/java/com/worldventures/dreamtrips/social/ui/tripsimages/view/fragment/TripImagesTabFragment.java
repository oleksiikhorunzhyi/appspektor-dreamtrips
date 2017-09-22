package com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgedTabLayout;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BasePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.modules.common.view.viewpager.SelectablePagerFragment;
import com.worldventures.dreamtrips.social.ui.feed.bundle.CreateEntityBundle;
import com.worldventures.dreamtrips.social.ui.tripsimages.presenter.TripImagesTabPresenter;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.TripImagesArgs;
import com.worldventures.dreamtrips.util.PageSelectionDetector;

import javax.inject.Inject;

import butterknife.InjectView;

@Layout(R.layout.fragment_trip_images_tabs)
@MenuResource(R.menu.menu_mock)
public class TripImagesTabFragment extends BaseFragment<TripImagesTabPresenter> implements TripImagesTabPresenter.View {

   @InjectView(R.id.tabs) protected BadgedTabLayout tabs;
   @InjectView(R.id.pager) protected ViewPager pager;

   @Inject SessionHolder sessionHolder;

   private BasePagerAdapter adapter;

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      if (adapter == null) {
         this.adapter = new BasePagerAdapter(getChildFragmentManager());
         adapter.add(new FragmentItem(Route.MEMBERS_IMAGES, getString(R.string.member_images), TripImagesArgs.builder()
               .showTimestamps(true)
               .route(Route.MEMBERS_IMAGES)
               .origin(CreateEntityBundle.Origin.MEMBER_TRIP_IMAGES)
               .build()));
         adapter.add(new FragmentItem(Route.ACCOUNT_IMAGES, getString(R.string.my_images), TripImagesArgs.builder()
               .userId(sessionHolder.get().get().getUser().getId())
               .route(Route.ACCOUNT_IMAGES)
               .origin(CreateEntityBundle.Origin.MY_TRIP_IMAGES)
               .build()));
         adapter.add(new FragmentItem(Route.THREE_SIXTY_VIDEOS, getString(R.string.three_sixty)));
         adapter.add(new FragmentItem(Route.INSPIRE_ME_IMAGES, getString(R.string.inspire_me)));
         adapter.add(new FragmentItem(Route.YSBH_IMAGES, getString(R.string.you_should_be_here)));
      }

      PageSelectionDetector.listenPageSelection(pager, pageNumber -> {
         SelectablePagerFragment fragment = (SelectablePagerFragment) adapter.getCurrentFragment();
         fragment.onSelectedFromPager();
      });

      pager.setAdapter(adapter);
      tabs.setupWithPagerBadged(pager);
   }

   @Override
   protected TripImagesTabPresenter createPresenter(Bundle savedInstanceState) {
      return new TripImagesTabPresenter();
   }
}
