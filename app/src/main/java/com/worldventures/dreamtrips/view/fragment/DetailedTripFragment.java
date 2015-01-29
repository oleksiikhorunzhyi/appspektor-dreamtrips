package com.worldventures.dreamtrips.view.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.common.collect.Collections2;
import com.linearlistview.LinearListView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.ContentItem;
import com.worldventures.dreamtrips.core.model.Trip;
import com.worldventures.dreamtrips.core.model.TripImage;
import com.worldventures.dreamtrips.presentation.DetailedTripFragmentPM;
import com.worldventures.dreamtrips.utils.UniversalImageLoader;
import com.worldventures.dreamtrips.utils.busevents.TripImageClickedEvent;
import com.worldventures.dreamtrips.view.activity.DetailTripActivity;
import com.worldventures.dreamtrips.view.adapter.BasePagerAdapter;
import com.worldventures.dreamtrips.view.adapter.ContentAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import me.relex.circleindicator.CircleIndicator;

/**
 * Created by Edward on 19.01.15.
 * fragment to show detailed trip
 */
@Layout(R.layout.fragment_detailed_trip)
public class DetailedTripFragment extends BaseFragment<DetailedTripFragmentPM> implements DetailedTripFragmentPM.View {

    @InjectView(R.id.textViewName)
    TextView textViewName;
    @InjectView(R.id.imageViewLike)
    ImageView imageViewLike;
    @InjectView(R.id.textViewReload)
    TextView textViewReloadTripDetails;
    @InjectView(R.id.textViewPlace)
    TextView textViewPlace;
    @InjectView(R.id.textViewPrice)
    TextView textViewPrice;
    @InjectView(R.id.textViewDate)
    TextView textViewDate;
    @InjectView(R.id.textViewDescription)
    TextView textViewDescription;
    @InjectView(R.id.textViewScheduleDescription)
    TextView textViewScheduleDescription;
    @InjectView(R.id.viewPagerGallery)
    ViewPager viewPagerGallery;
    @InjectView(R.id.textViewPoints)
    TextView textViewPoints;
    @InjectView(R.id.listViewContent)
    LinearListView linearListView;
    @InjectView(R.id.progressBarDetailLoading)
    ProgressBar progressBarDetailLoading;
    @InjectView(R.id.circleIndicator)
    CircleIndicator circleIndicator;

    @Inject
    UniversalImageLoader universalImageLoader;

    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;

    @Override
    protected DetailedTripFragmentPM createPresentationModel(Bundle savedInstanceState) {
        return new DetailedTripFragmentPM(this);
    }

    @OnClick(R.id.imageViewLike)
    void onLike() {
        getPresentationModel().actionLike();
    }

    @OnClick(R.id.textViewBookIt)
    public void bookIt() {
        getPresentationModel().actionBookIt();
    }

    @Override
    public void showErrorMessage() {
        if (isAdded()) {
            ((DetailTripActivity) getActivity()).informUser(getString(R.string.smth_went_wrong));
            getActivity().invalidateOptionsMenu();
        }
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        getPresentationModel().setTrip((Trip) getArguments().getSerializable(DetailTripActivity.EXTRA_TRIP));
        getPresentationModel().onCreate();

        ((ActionBarActivity) getActivity()).setSupportActionBar(toolbar);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle("");
        toolbar.getBackground().setAlpha(0);


        BasePagerAdapter<DetailedImagePagerFragment> adapter = new BasePagerAdapter<DetailedImagePagerFragment>(getFragmentManager()) {
            @Override
            public void setArgs(int position, DetailedImagePagerFragment fragment) {
                Bundle args = new Bundle();
                Object photo = getPresentationModel().getFilteredImages().get(position);
                if (photo instanceof Serializable) {
                    args.putSerializable(DetailedImagePagerFragment.EXTRA_PHOTO, (Serializable) photo);
                }
                fragment.setArguments(args);
            }
        };

        for (Object photo : getPresentationModel().getFilteredImages()) {
            adapter.add(new BasePagerAdapter.FragmentItem<>(DetailedImagePagerFragment.class, ""));
        }

        viewPagerGallery.setAdapter(adapter);
        viewPagerGallery.setCurrentItem(0);
        circleIndicator.setViewPager(viewPagerGallery);
    }

    public void onEvent(TripImageClickedEvent event) {
        getPresentationModel().onItemClick(viewPagerGallery.getCurrentItem());
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
    public void setName(String name) {
        textViewName.setText(name);
    }

    @Override
    public void setRedemption(String count) {
        textViewPoints.setText(count);
    }

    @Override
    public void setContent(List<ContentItem> contentItems) {
        progressBarDetailLoading.setVisibility(View.GONE);
        if (contentItems != null) {
            linearListView.setAdapter(new ContentAdapter(contentItems, getActivity()));
        } else {
            textViewReloadTripDetails.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.textViewReload)
    void onReloadClicked() {
        textViewReloadTripDetails.setVisibility(View.GONE);
        progressBarDetailLoading.setVisibility(View.VISIBLE);
        getPresentationModel().loadTripDetails();
    }

    @Override
    public void setDuration(int count) {
        textViewScheduleDescription.setText(String.format(getString(R.string.duration), count));
    }

    @Override
    public void setLike(boolean like) {
        imageViewLike.setImageResource(like ? R.drawable.ic_bucket_like_selected : R.drawable.ic_heart_1);
    }
}
