package com.worldventures.dreamtrips.modules.dtl.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.ui.fragment.BaseImageFragment;
import com.worldventures.dreamtrips.core.ui.fragment.ImageBundle;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BaseStatePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.modules.dtl.DtlPlaceHelper;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlaceMedia;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlaceType;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlPlaceDetailsPresenter;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.InjectView;
import butterknife.OnClick;
import me.relex.circleindicator.CircleIndicator;

@Layout(R.layout.fragment_dtl_place_details)
public class DtlPlaceDetailsFragment
        extends BaseFragmentWithArgs<DtlPlaceDetailsPresenter, DtlPlace>
        implements DtlPlaceDetailsPresenter.View {


    @InjectView(R.id.place_details_cover_pager)
    ViewPager coverPager;
    @InjectView(R.id.place_details_cover_pager_indicator)
    CircleIndicator coverPagerIndicator;
    @InjectView(R.id.place_details_conver_stub)
    View converStub;
    @InjectView(R.id.place_details_title)
    TextView title;
    @InjectView(R.id.place_details_rating)
    RatingBar rating;
    @InjectView(R.id.place_details_points_badge)
    ImageView earnPointsBadge;
    @InjectView(R.id.place_details_category)
    TextView category;
    @InjectView(R.id.place_details_pricing)
    RatingBar pricing;
    @InjectView(R.id.place_details_earn_wrapper)
    ViewGroup earnWrapper;
    @InjectView(R.id.place_details_earn)
    Button earn;
    @InjectView(R.id.place_details_estimate_points)
    Button estimatePoints;
    @InjectView(R.id.place_details_merchant_wrapper)
    ViewGroup merchantWrapper;
    @InjectView(R.id.place_details_suggest_merchant)
    Button merchant;
    @InjectView(R.id.place_details_description)
    TextView description;
    @InjectView(R.id.place_details_additional)
    ViewGroup additionalContainer;
    SupportMapFragment destinationMap;

    DtlPlaceHelper helper;

    @Inject
    @Named(RouteCreatorModule.DTL_TRANSACTION)
    RouteCreator<DtlTransaction> routeCreator;

    @Override
    protected DtlPlaceDetailsPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlPlaceDetailsPresenter(getArgs());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        helper = new DtlPlaceHelper(activity);
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        destinationMap = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.place_details_map);
    }

    @Override
    public void setPlace(DtlPlace place) {
        title.setText(place.getName());
        description.setText(place.getDescription());
        category.setText(helper.getFirstCategoryName(place));
        pricing.setRating(place.getAvgPrice());
        setImages(place.getMediaList());
        setType(place.getType());
        setAdditional(place);
        setMap(place);
    }

    private void setImages(List<DtlPlaceMedia> mediaList) {
        if (mediaList.isEmpty()) {
            converStub.setVisibility(View.VISIBLE);
            return;
        }
        converStub.setVisibility(View.GONE);
        //
        BaseStatePagerAdapter adapter = new BaseStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public void setArgs(int position, Fragment fragment) {
                DtlPlaceMedia photo = mediaList.get(position);
                ((BaseImageFragment) fragment).setArgs(new ImageBundle<>(photo));
            }
        };
        Queryable.from(mediaList).forEachR(image -> {
            adapter.add(new FragmentItem(BaseImageFragment.class, ""));
        });
        coverPager.setAdapter(adapter);
        if (mediaList.size() > 1) coverPagerIndicator.setViewPager(coverPager);
    }

    @Override
    public void openTransaction(DtlPlace dtlPlace, DtlTransaction dtlTransaction) {
        NavigationBuilder.create()
                .with(activityRouter)
                .data(dtlPlace)
                .move(routeCreator.createRoute(dtlTransaction));
    }

    @Override
    public void setTransaction(DtlTransaction dtlTransaction) {
        earn.setText(dtlTransaction != null ? R.string.dtl_earn : R.string.dtl_check_in);
    }

    @OnClick(R.id.place_details_earn)
    void onCheckInClicked() {
        getPresenter().onCheckInClicked();
    }

    private void setType(DtlPlaceType type) {
        int offerVisibility = (type == DtlPlaceType.OFFER) ? View.VISIBLE : View.GONE;
        int merchantVisibility = (type == DtlPlaceType.DINING) ? View.VISIBLE : View.GONE;
        earnPointsBadge.setVisibility(offerVisibility);
        earnWrapper.setVisibility(offerVisibility);
        merchantWrapper.setVisibility(merchantVisibility);
    }

    private void setAdditional(DtlPlace place) {
        Queryable.from(helper.getContactsData(place)).forEachR(contact -> {
            TextView contactView = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.list_item_dtl_place_contact, additionalContainer, false);
            contactView.setText(contact.text);
            contactView.setCompoundDrawablesWithIntrinsicBounds(contact.icon, null, null, null);
            additionalContainer.addView(contactView);
        });

    }

    private void setMap(DtlPlace place) {
        destinationMap.getMapAsync(googleMap -> {
            LatLng pos = new LatLng(place.getLocation().getLat(), place.getLocation().getLng());
            googleMap.addMarker(new MarkerOptions()
                            .position(pos)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_pin))
            );
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(pos));
        });
    }

    @OnClick(R.id.place_details_estimate_points)
    void onEstimatorClick() {
        getPresenter().onEstimationClick(getChildFragmentManager());
    }


}
