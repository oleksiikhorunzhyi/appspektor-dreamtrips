package com.worldventures.dreamtrips.modules.trips.view.util;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.ui.fragment.ImageBundle;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BaseStatePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImage;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagePagerFragment;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import me.relex.circleindicator.CircleIndicator;

public class TripDetailsViewDelegate {

    protected MenuItem likeItem;
    protected MenuItem addToBucketItem;

    @Optional
    @InjectView(R.id.viewPagerGallery)
    protected ViewPager viewPagerGallery;
    @Optional
    @InjectView(R.id.circleIndicator)
    protected CircleIndicator circleIndicator;
    @InjectView(R.id.textViewPoints)
    protected TextView textViewPoints;
    @InjectView(R.id.textViewName)
    TextView textViewName;
    @InjectView(R.id.textViewPlace)
    TextView textViewPlace;
    @InjectView(R.id.textViewPrice)
    TextView textViewPrice;
    @InjectView(R.id.textViewDate)
    TextView textViewDate;
    @InjectView(R.id.textViewDescription)
    TextView textViewDescription;
    @Optional
    @InjectView(R.id.textViewScheduleDescription)
    TextView textViewScheduleDescription;
    @InjectView(R.id.sold_out)
    ImageView soldOut;
    @InjectView(R.id.textViewFeatured)
    protected TextView textViewFeatured;
    @InjectView(R.id.pointsCountLayout)
    protected FrameLayout pointsCountLayout;

    public TripDetailsViewDelegate(View rootView) {
        ButterKnife.inject(this, rootView);
    }

    public void initMenuItems(Menu menu) {
        likeItem = menu.findItem(R.id.action_like);
        addToBucketItem = menu.findItem(R.id.action_add_to_bucket);
    }

    public void initGalleryData(FragmentManager fragmentManager, List<TripImage> filteredImages) {
        BaseStatePagerAdapter adapter = new BaseStatePagerAdapter(fragmentManager) {
            @Override
            public void setArgs(int position, Fragment fragment) {
                TripImage photo = filteredImages.get(position);
                ((TripImagePagerFragment) fragment).setArgs(new ImageBundle<>(photo));
            }
        };

        Queryable.from(filteredImages).forEachR(photo ->
                        adapter.add(new FragmentItem(TripImagePagerFragment.class, ""))
        );

        if (viewPagerGallery != null) {
            viewPagerGallery.setAdapter(adapter);
            viewPagerGallery.setCurrentItem(0);
            circleIndicator.setViewPager(viewPagerGallery);
        }
    }

    public void initTripData(TripModel tripModel, User currentUser) {
        if (textViewScheduleDescription != null) {
            Resources resources = textViewScheduleDescription.getResources();
            textViewScheduleDescription.setText(String.format(resources.getString(R.string.duration), tripModel.getDuration()));
        }
        if (likeItem != null) {
            int iconLike = tripModel.isLiked() ? R.drawable.ic_trip_like_selected : R.drawable.ic_trip_like_normal;
            likeItem.setIcon(iconLike);
        }
        if (addToBucketItem != null) {
            int iconBucket = tripModel.isInBucketList() ? R.drawable.ic_trip_add_to_bucket_selected : R.drawable.ic_trip_add_to_bucket_normal;
            addToBucketItem.setIcon(iconBucket);
            addToBucketItem.setEnabled(!tripModel.isInBucketList());
        }
        String reward = tripModel.getRewardsLimit(currentUser);

        if (!TextUtils.isEmpty(reward) && !"0".equals(reward)) {
            textViewPoints.setText(String.valueOf(reward));
            pointsCountLayout.setVisibility(View.VISIBLE);
        } else {
            textViewPoints.setVisibility(View.GONE);
            pointsCountLayout.setVisibility(View.GONE);
        }

        textViewFeatured.setVisibility(tripModel.isFeatured() ? View.VISIBLE : View.GONE);
        textViewName.setText(tripModel.getName());


        soldOut.setVisibility(tripModel.isSoldOut() ? View.VISIBLE : View.GONE);

        textViewPlace.setText(tripModel.getGeoLocation().getName());
        textViewPrice.setText(tripModel.getPrice().toString());
        textViewDate.setText(tripModel.getAvailabilityDates().toString());
        textViewDescription.setText(tripModel.getDescription());
    }

    public int getCurrentActivePhotoPosition() {
        return viewPagerGallery.getCurrentItem();
    }
}

