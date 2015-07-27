package com.worldventures.dreamtrips.modules.trips.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.linearlistview.LinearListView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.events.TripImageClickedEvent;
import com.worldventures.dreamtrips.modules.common.view.adapter.ContentAdapter;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BaseStatePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.modules.trips.model.ContentItem;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.presenter.TripDetailsPresenter;
import com.worldventures.dreamtrips.modules.trips.view.activity.DetailTripActivity;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagePagerFragment;

import java.io.Serializable;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import me.relex.circleindicator.CircleIndicator;

@Layout(R.layout.fragment_trip_details)
@MenuResource(R.menu.menu_detailed_trip)
public class TripDetailsFragment extends BaseFragment<TripDetailsPresenter>
        implements TripDetailsPresenter.View {

    @InjectView(R.id.textViewName)
    protected TextView textViewName;
    @InjectView(R.id.textViewReload)
    protected TextView textViewReloadTripDetails;
    @InjectView(R.id.textViewPlace)
    protected TextView textViewPlace;
    @InjectView(R.id.textViewPrice)
    protected TextView textViewPrice;
    @InjectView(R.id.textViewDate)
    protected TextView textViewDate;
    @InjectView(R.id.textViewDescription)
    protected TextView textViewDescription;
    @InjectView(R.id.textViewScheduleDescription)
    protected TextView textViewScheduleDescription;
    @InjectView(R.id.viewPagerGallery)
    protected ViewPager viewPagerGallery;
    @InjectView(R.id.sold_out)
    protected ImageView soldOut;
    @InjectView(R.id.textViewPoints)
    protected TextView textViewPoints;
    @InjectView(R.id.listViewContent)
    protected LinearListView linearListView;
    @InjectView(R.id.progressBarDetailLoading)
    protected ProgressBar progressBarDetailLoading;
    @InjectView(R.id.circleIndicator)
    protected CircleIndicator circleIndicator;
    @InjectView(R.id.pointsCountLayout)
    protected FrameLayout pointsCountLayout;
    @InjectView(R.id.textViewFeatured)
    protected TextView textViewFeatured;
    @InjectView(R.id.layoutBookIt)
    protected View layoutBookIt;
    @InjectView(R.id.textViewBookIt)
    protected TextView textViewBookIt;

    @Optional
    @InjectView(R.id.toolbar_actionbar)
    protected Toolbar toolbar;

    @Optional
    @InjectView(R.id.toolbar_actionbar_landscape)
    protected Toolbar toolbarLanscape;

    protected MenuItem likeItem;
    protected MenuItem addToBucketItem;

    @Override
    protected TripDetailsPresenter createPresenter(Bundle savedInstanceState) {
        return new TripDetailsPresenter();
    }

    @OnClick(R.id.layoutBookIt)
    public void bookIt() {
        getPresenter().actionBookIt();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        likeItem = menu.findItem(R.id.action_like);
        addToBucketItem = menu.findItem(R.id.action_add_to_bucket);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_like:
                getPresenter().likeTrip();
                return true;
            case R.id.action_add_to_bucket:
                getPresenter().addTripToBucket();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");
            toolbar.getBackground().setAlpha(0);
        } else if (toolbarLanscape != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbarLanscape);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbarLanscape.setBackgroundColor(getResources().getColor(R.color.theme_main));
            toolbarLanscape.getBackground().setAlpha(255);
        }

        getPresenter().setTrip((TripModel) getArguments().getSerializable(DetailTripActivity.EXTRA_TRIP));

        BaseStatePagerAdapter adapter =
                new BaseStatePagerAdapter(getChildFragmentManager()) {
                    @Override
                    public void setArgs(int position, Fragment fragment) {
                        Bundle args = new Bundle();
                        Object photo = getPresenter().getFilteredImages().get(position);
                        if (photo instanceof Serializable) {
                            args.putSerializable(TripImagePagerFragment.EXTRA_PHOTO, (Serializable) photo);
                        }
                        fragment.setArguments(args);
                    }
                };

        for (Object photo : getPresenter().getFilteredImages()) {
            adapter.add(new FragmentItem(TripImagePagerFragment.class, ""));
        }

        viewPagerGallery.setAdapter(adapter);
        viewPagerGallery.setCurrentItem(0);
        circleIndicator.setViewPager(viewPagerGallery);
    }

    public void onEvent(TripImageClickedEvent event) {
        getPresenter().onItemClick(viewPagerGallery.getCurrentItem());
    }

    @Override
    public void setDesription(String description) {
        textViewDescription.setText(description);
    }

    @Override
    public void setDates(String dates) {
        textViewDate.setText(dates);
    }

    @Override
    public void setPrice(String price) {
        textViewPrice.setText(price);
    }

    @Override
    public void setLocation(String location) {
        textViewPlace.setText(location);
    }

    @Override
    public void setSoldOut() {
        soldOut.setVisibility(View.VISIBLE);
    }

    @Override
    public void setName(String name) {
        textViewName.setText(name);
        if (toolbarLanscape != null)
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(name);
    }

    @Override
    public void setFeatured() {
        textViewFeatured.setVisibility(View.VISIBLE);
    }

    @Override
    public void setRedemption(String count) {
        textViewPoints.setText(count);
    }

    @Override
    public void setPointsInvisible() {
        pointsCountLayout.setVisibility(View.GONE);
    }

    @Override
    public void setContent(List<ContentItem> contentItems) {
        if (isAdded()) {
            progressBarDetailLoading.setVisibility(View.GONE);
            if (contentItems != null) {
                linearListView.setAdapter(new ContentAdapter(contentItems, getActivity()));
            } else {
                textViewReloadTripDetails.setVisibility(View.VISIBLE);
            }
        }
    }

    @OnClick(R.id.textViewReload)
    void onReloadClicked() {
        textViewReloadTripDetails.setVisibility(View.GONE);
        progressBarDetailLoading.setVisibility(View.VISIBLE);
        getPresenter().loadTripDetails();
    }

    @Override
    public void hideBookIt() {
        layoutBookIt.setEnabled(false);
        textViewBookIt.setBackgroundColor(getResources().getColor(R.color.tripButtonDisabled));
    }

    @Override
    public void setInBucket(boolean inBucket) {
        int icon = inBucket ? R.drawable.ic_trip_add_to_bucket_selected : R.drawable.ic_trip_add_to_bucket_normal;
        addToBucketItem.setIcon(icon);
        addToBucketItem.setEnabled(!inBucket);
    }

    @Override
    public void setLike(boolean liked) {
        int icon = liked ? R.drawable.ic_trip_like_selected : R.drawable.ic_trip_like_normal;
        likeItem.setIcon(icon);
    }

    @Override
    public void setDuration(int count) {
        textViewScheduleDescription.setText(String.format(getString(R.string.duration), count));
    }

}
