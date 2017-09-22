package com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment;

import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BaseStatePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.social.ui.tripsimages.presenter.BaseImageViewPagerPresenter;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;

public abstract class BaseImageViewPagerFragment<PM extends BaseImageViewPagerPresenter, P extends Parcelable>
      extends BaseFragmentWithArgs<PM, P>
      implements BaseImageViewPagerPresenter.View {

   @Inject Router router;
   @InjectView(R.id.pager) protected ViewPager pager;
   @InjectView(R.id.toolbar_actionbar) protected Toolbar toolbar;

   protected BaseStatePagerAdapter<FragmentItem> adapter;

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      AppCompatActivity activity = (AppCompatActivity) getActivity();
      activity.setSupportActionBar(toolbar);
      activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_photo_back_rounded);
      activity.getSupportActionBar().setTitle("");
      pager.setAdapter(adapter = new BaseStatePagerAdapter<>(getActivity().getSupportFragmentManager()));
      pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
         public void onPageScrollStateChanged(int state) {
         }

         public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
         }

         public void onPageSelected(int position) {
            getPresenter().pageSelected(position);
         }
      });
   }

   @Override
   public void setSelectedPosition(int position) {
      pager.setCurrentItem(position);
   }

   @Override
   public void setItems(List<FragmentItem> fragmentItems) {
      adapter.clear();
      adapter.addItems(fragmentItems);
      adapter.notifyDataSetChanged();
   }

   @Override
   public void onResume() {
      super.onResume();
      toolbar.getBackground().mutate().setAlpha(0);
   }

   @Override
   public void remove(int position) {
      if (adapter.getCount() == 1) {
         getActivity().onBackPressed();
      } else {
         int currentItem = pager.getCurrentItem();
         adapter.remove(position);
         adapter.notifyDataSetChanged();
         pager.setAdapter(adapter);
         pager.setCurrentItem(Math.min(currentItem, adapter.getCount() - 1));
      }
   }

   @Override
   public void goBack() {
      router.back();
   }
}
