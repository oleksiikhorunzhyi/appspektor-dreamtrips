package com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgedTabLayout;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BasePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.modules.common.view.viewpager.SelectablePagerFragment;
import com.worldventures.dreamtrips.social.ui.feed.bundle.CreateEntityBundle;
import com.worldventures.dreamtrips.social.ui.tripsimages.presenter.TripImagesTabPresenter;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.TripImagesArgs;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.inspire_me.InspireMeFragment;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.ysbh.YouShouldBeHereFragment;
import com.worldventures.dreamtrips.social.ui.video.view.ThreeSixtyVideosFragment;
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
         adapter.add(new FragmentItem(MemberImagesFragment.class, getString(R.string.member_images), TripImagesArgs.builder()
               .showTimestamps(true)
               .type(TripImagesArgs.TripImageType.MEMBER_IMAGES)
               .origin(CreateEntityBundle.Origin.MEMBER_TRIP_IMAGES)
               .build()));
         adapter.add(new FragmentItem(TripImagesFragment.class, getString(R.string.my_images), TripImagesArgs.builder()
               .userId(sessionHolder.get().get().user().getId())
               .type(TripImagesArgs.TripImageType.ACCOUNT_IMAGES)
               .origin(CreateEntityBundle.Origin.MY_TRIP_IMAGES)
               .build()));
         adapter.add(new FragmentItem(ThreeSixtyVideosFragment.class, getString(R.string.three_sixty)));
         adapter.add(new FragmentItem(InspireMeFragment.class, getString(R.string.inspire_me)));
         adapter.add(new FragmentItem(YouShouldBeHereFragment.class, getString(R.string.trip_images_you_should_be_here)));
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
