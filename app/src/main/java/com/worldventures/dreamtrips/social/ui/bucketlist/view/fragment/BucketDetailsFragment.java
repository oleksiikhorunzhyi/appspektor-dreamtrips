package com.worldventures.dreamtrips.social.ui.bucketlist.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.utils.ui.OrientationUtil;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.util.ViewUtils;
import com.worldventures.core.ui.view.fragment.FragmentUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.core.ui.fragment.BaseImageFragment;
import com.worldventures.dreamtrips.core.ui.fragment.ImageBundle;
import com.worldventures.dreamtrips.core.utils.IntentUtils;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BaseStatePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.social.ui.activity.SocialComponentActivity;
import com.worldventures.dreamtrips.social.ui.bucketlist.bundle.BucketBundle;
import com.worldventures.dreamtrips.social.ui.bucketlist.bundle.BucketViewPagerBundle;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.DiningItem;
import com.worldventures.dreamtrips.social.ui.bucketlist.presenter.BucketItemDetailsPresenter;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.util.TranslateBucketItemViewInjector;
import com.worldventures.dreamtrips.social.util.event_delegate.ImagePresenterClickEventDelegate;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import me.relex.circleindicator.CircleIndicator;

@Layout(R.layout.layout_bucket_item_details)
public class BucketDetailsFragment<T extends BucketItemDetailsPresenter> extends RxBaseFragmentWithArgs<T, BucketBundle> implements BucketItemDetailsPresenter.View {

   @InjectView(R.id.textViewFriends) TextView textViewFriends;
   @InjectView(R.id.textViewTags) TextView textViewTags;
   @InjectView(R.id.textViewCategory) TextView textViewCategory;
   @InjectView(R.id.textViewDate) TextView textViewDate;
   @InjectView(R.id.textViewPlace) TextView textViewPlace;
   @InjectView(R.id.checkBoxDone) CheckBox markAsDone;
   @InjectView(R.id.galleryPlaceHolder) ImageView galleryPlaceHolder;
   @InjectView(R.id.viewPagerBucketGallery) ViewPager viewPagerBucketGallery;
   @InjectView(R.id.circleIndicator) CircleIndicator circleIndicator;
   @InjectView(R.id.bucket_tags_container) View bucketTags;
   @InjectView(R.id.bucket_who_container) View bucketWho;
   @InjectView(R.id.diningName) TextView diningName;
   @InjectView(R.id.diningPriceRange) TextView diningPriceRange;
   @InjectView(R.id.diningAddress) TextView diningAddress;
   @InjectView(R.id.diningSite) TextView diningSite;
   @InjectView(R.id.diningPhone) TextView diningPhone;
   @InjectView(R.id.diningContainer) View diningContainer;
   @InjectView(R.id.diningDivider) View diningDivider;
   @InjectView(R.id.contentView) ViewGroup contentView;
   @InjectView(R.id.toolbar_actionbar) Toolbar toolbar;

   @Inject ImagePresenterClickEventDelegate imagePresenterClickEventDelegate;
   @Inject SessionHolder sessionHolder;

   private int checkedPosition;
   private boolean viewPagerIndicatorInitialized;
   private TranslateBucketItemViewInjector translateBucketItemViewInjector;

   private ViewPager.SimpleOnPageChangeListener onPageSelectedListener = new ViewPager.SimpleOnPageChangeListener() {
      @Override
      public void onPageSelected(int position) {
         checkedPosition = position;
      }
   };

   private BaseStatePagerAdapter adapter;
   private List<BucketPhoto> photos = new ArrayList<>();

   @Override
   public void afterCreateView(View view) {
      super.afterCreateView(view);
      setForeignIntentAction();
      translateBucketItemViewInjector = new TranslateBucketItemViewInjector(view, getContext(), sessionHolder);
      viewPagerBucketGallery.addOnPageChangeListener(onPageSelectedListener);
      subscribeToBucketImagesClicks();
      adapter = new BaseStatePagerAdapter(getChildFragmentManager()) {
         @Override
         public void setArgs(int position, Fragment fragment) {
            BucketPhoto photo = photos.get(position);
            ((BaseImageFragment) fragment).setArgs(new ImageBundle<>(photo));
         }

         @Override
         public int getItemPosition(Object object) {
            // force current page to be recreated each time we call notifyDatasetChanged()
            // TODO: 3/21/17 Implement descendant of BaseStatePagerAdapter to track positions of the fragments
            // and return actual position if item still exists in the adapter
            return POSITION_NONE;
         }
      };
      viewPagerBucketGallery.setAdapter(adapter);
   }

   @Override
   protected T createPresenter(Bundle savedInstanceState) {
      BucketBundle args = getArgs();
      return (T) new BucketItemDetailsPresenter(args.getType(), args.getBucketItem(), args.getOwnerId());
   }

   @Override
   public void onResume() {
      super.onResume();
      if (isVisible() && ViewUtils.isTablet(getActivity()) && !ViewUtils.isLandscapeOrientation(getActivity())) {
         OrientationUtil.lockOrientation(getActivity());
      }
      if (!getArgs().isSlave()) {
         ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
         ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
         ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");
         toolbar.getBackground().mutate().setAlpha(0);
      } else {
         toolbar.setVisibility(View.GONE);
      }
   }

   @OnClick(R.id.translate)
   void onTranslateClicked() {
      translateBucketItemViewInjector.translatePressed();
      getPresenter().onTranslateClicked();
   }

   @Override
   public void onDestroyView() {
      viewPagerBucketGallery.removeOnPageChangeListener(onPageSelectedListener);
      super.onDestroyView();
      OrientationUtil.unlockOrientation(getActivity());
   }

   @Override
   public boolean isVisibleOnScreen() {
      return ViewUtils.isPartVisibleOnScreen(this);
   }

   @Override
   public void setBucketItem(BucketItem bucketItem) {
      //refactor-plan move here logic from other setters to keep code cleaner
      translateBucketItemViewInjector.processTranslation(bucketItem);
   }

   @Override
   public void setTime(String time) {
      textViewDate.setText(time);
   }

   @Override
   public void setPeople(String people) {
      if (!TextUtils.isEmpty(people)) {
         bucketWho.setVisibility(View.VISIBLE);
         textViewFriends.setText(people);
      } else {
         bucketWho.setVisibility(View.GONE);
      }
   }

   @Override
   public void setTags(String tags) {
      if (!TextUtils.isEmpty(tags)) {
         bucketTags.setVisibility(View.VISIBLE);
         textViewTags.setText(tags);
      } else {
         bucketTags.setVisibility(View.GONE);
      }
   }

   @Override
   public void setStatus(boolean completed) {
      markAsDone.setChecked(completed);
   }

   @OnCheckedChanged(R.id.checkBoxDone)
   protected void onCheckedChanged(boolean isChecked) {
      getPresenter().onStatusUpdated(isChecked);
   }

   private void setForeignIntentAction() {
      diningSite.setOnClickListener(v -> {
         Intent intent = IntentUtils.browserIntent(diningSite.getText().toString());
         FragmentUtil.startSafely(this, intent);
      });
      diningPhone.setOnClickListener(v -> {
         Intent intent = IntentUtils.callIntnet(diningPhone.getText().toString());
         FragmentUtil.startSafely(this, intent);
      });
   }

   @Override
   public void enableMarkAsDone() {
      markAsDone.setEnabled(true);
   }

   @Override
   public void disableMarkAsDone() {
      markAsDone.setEnabled(false);
   }

   @Override
   public void setupDiningView(DiningItem diningItem) {
      if (diningItem == null) {
         diningContainer.setVisibility(View.GONE);
         return;
      }
      //
      diningContainer.setVisibility(View.VISIBLE);
      setText(diningName, diningItem.getName());
      setText(diningPriceRange, diningItem.getPriceRange());
      setText(diningAddress, diningItem.getAddress());
      setText(diningPhone, diningItem.getPhoneNumber());
      setText(diningSite, diningItem.getUrl());
      if (TextUtils.isEmpty(diningItem.getUrl()) && TextUtils.isEmpty(diningItem.getPhoneNumber())) {
         diningDivider.setVisibility(View.GONE);
      } else {
         diningDivider.setVisibility(View.VISIBLE);
      }
   }

   @Override
   public void setCategory(String category) {
      setText(textViewCategory, category);
   }

   @Override
   public void setPlace(String place) {
      setText(textViewPlace, place);
   }

   private void setText(TextView view, String text) {
      if (TextUtils.isEmpty(text)) {
         view.setVisibility(View.GONE);
      } else {
         view.setVisibility(View.VISIBLE);
         view.setText(text);
      }
   }

   @Override
   public void done() {
      if (getActivity() instanceof SocialComponentActivity && !ViewUtils.isLandscapeOrientation(getActivity())) {
         getActivity().onBackPressed();
      }
   }

   @Override
   public void openFullscreen(BucketViewPagerBundle data) {
      router.moveTo(BucketPhotoViewPagerFragment.class, NavigationConfigBuilder.forActivity()
            .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
            .data(data)
            .build());
   }

   @Override
   public void setGalleryEnabled(boolean enabled) {
      if (enabled) {
         viewPagerBucketGallery.setVisibility(View.VISIBLE);
         circleIndicator.setVisibility(View.VISIBLE);
         galleryPlaceHolder.setVisibility(View.INVISIBLE);
      } else {
         viewPagerBucketGallery.setVisibility(View.INVISIBLE);
         circleIndicator.setVisibility(View.INVISIBLE);
         galleryPlaceHolder.setVisibility(View.VISIBLE);
      }
   }

   @Override
   public void setImages(List<? extends BucketPhoto> newPhotos) {
      this.photos.clear();
      this.photos.addAll(newPhotos);
      adapter.clear();
      Queryable.from(photos).forEachR(photo -> adapter.add(new FragmentItem(BaseImageFragment.class)));

      // initialize once, initializing with empty list in view pager causes crash
      if (!photos.isEmpty() && !viewPagerIndicatorInitialized) {
         circleIndicator.setViewPager(viewPagerBucketGallery);
         viewPagerIndicatorInitialized = true;
         adapter.registerDataSetObserver(circleIndicator.getDataSetObserver());
      }

      adapter.notifyDataSetChanged();
      viewPagerBucketGallery.setCurrentItem(checkedPosition);
   }

   private void subscribeToBucketImagesClicks() {
      imagePresenterClickEventDelegate.getObservable().compose(bindUntilDropViewComposer())
            .subscribe(imagePathHolder -> {
               if (ViewUtils.isPartVisibleOnScreen(this)) {
                  getPresenter().openFullScreen(viewPagerBucketGallery.getCurrentItem());
               }
            });
   }
}
