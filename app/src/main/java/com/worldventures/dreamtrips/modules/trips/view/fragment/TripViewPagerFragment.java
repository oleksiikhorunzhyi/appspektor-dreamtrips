package com.worldventures.dreamtrips.modules.trips.view.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BasePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.modules.trips.view.bundle.TripViewPagerBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImage;

import butterknife.InjectView;

@Layout(R.layout.fragment_full_screen_photo_wrapper)
public class TripViewPagerFragment extends BaseFragmentWithArgs<Presenter, TripViewPagerBundle> {

   @InjectView(R.id.pager) protected ViewPager pager;
   @InjectView(R.id.toolbar_actionbar) protected Toolbar toolbar;

   private BasePagerAdapter<FragmentItem> adapter;

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      AppCompatActivity activity = (AppCompatActivity) getActivity();
      activity.setSupportActionBar(toolbar);
      activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_photo_back_rounded);
      activity.getSupportActionBar().setTitle("");

      pager.setAdapter(adapter = new BasePagerAdapter<>(getActivity().getSupportFragmentManager()));
      for (TripImage tripImage : getArgs().getTripImages()) {
         adapter.add(new FragmentItem(Route.TRIP_PHOTO_FULLSCREEN, "", tripImage));
      }
      pager.setCurrentItem(getArgs().getPosition());
      adapter.notifyDataSetChanged();
   }

   @Override
   public void onResume() {
      super.onResume();
      toolbar.getBackground().mutate().setAlpha(0);
   }

   @Override
   protected Presenter createPresenter(Bundle savedInstanceState) {
      return new Presenter();
   }
}
