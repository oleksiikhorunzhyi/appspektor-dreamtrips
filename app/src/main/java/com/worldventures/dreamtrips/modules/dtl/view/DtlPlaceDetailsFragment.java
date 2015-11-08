package com.worldventures.dreamtrips.modules.dtl.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.dtl.bundle.PlaceDetailsBundle;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlCategoryDataInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlPlaceCommonDataInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlPlaceHelper;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlPlaceInfoInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlPlaceManyImagesDataInflater;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlaceType;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlPlaceDetailsPresenter;
import com.worldventures.dreamtrips.util.SpanUtils;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_dtl_place_details)
public class DtlPlaceDetailsFragment
        extends BaseFragmentWithArgs<DtlPlaceDetailsPresenter, PlaceDetailsBundle>
        implements DtlPlaceDetailsPresenter.View {

    DtlPlaceCommonDataInflater commonDataInflater;
    DtlPlaceInfoInflater placeInfoInflater;
    DtlCategoryDataInflater categoryDataInflater;
    DtlPlaceHelper helper;

    @Inject
    @Named(RouteCreatorModule.DTL_TRANSACTION)
    RouteCreator<DtlTransaction> routeCreator;

    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;
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
    @InjectView(R.id.place_details_share)
    View share;
    //
    SupportMapFragment destinationMap;

    @Override
    protected DtlPlaceDetailsPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlPlaceDetailsPresenter(getArgs().getPlace());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        helper = new DtlPlaceHelper(activity);
        commonDataInflater = new DtlPlaceManyImagesDataInflater(helper, getChildFragmentManager());
        placeInfoInflater = new DtlPlaceInfoInflater(helper);
        categoryDataInflater = new DtlCategoryDataInflater(helper);
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        if (!tabletAnalytic.isTabletLandscape()) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setTitle("");
        } else {
            toolbar.setNavigationOnClickListener(v -> popBack(getFragmentManager()));
        }
        commonDataInflater.setView(rootView);
        placeInfoInflater.setView(rootView);
        categoryDataInflater.setView(rootView);
        destinationMap = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.place_details_map);
        destinationMap.getMapAsync(googleMap -> {
            googleMap.getUiSettings().setMapToolbarEnabled(false);
            int padding = getContext().getResources().getDimensionPixelOffset(R.dimen.spacing_large);
            googleMap.setPadding(0, 0, 0, padding);
        });
    }

    public void popBack(FragmentManager fragmentManager) {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        }
    }

    @Override
    public void setPlace(DtlPlace place) {
        commonDataInflater.apply(place);
        placeInfoInflater.apply(place);
        categoryDataInflater.apply(place);
        setType(place.getPartnerStatus());
        setDescription(place.getDescription());
        setAdditional(place);
        setMap(place);
    }

    private void setType(DtlPlaceType type) {
        earnWrapper.setVisibility((type == DtlPlaceType.OFFER) ? View.VISIBLE : View.GONE);
        merchantWrapper.setVisibility((type == DtlPlaceType.DINING) ? View.VISIBLE : View.GONE);
    }

    private void setDescription(String description) {
        this.description.setText(description);
        this.description.setVisibility(TextUtils.isEmpty(description) ? View.GONE : View.VISIBLE);
    }

    private void setAdditional(DtlPlace place) {
        Queryable.from(helper.getContactsData(place)).forEachR(contact -> {
            TextView contactView = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.list_item_dtl_place_contact, additionalContainer, false);
            contactView.setCompoundDrawablesWithIntrinsicBounds(contact.icon, null, null, null);
            contactView.setText(contact.text);
            if (Linkify.addLinks(contactView, Linkify.ALL)) SpanUtils.stripUnderlines(contactView);
            additionalContainer.addView(contactView);
        });

    }

    private void setMap(DtlPlace place) {
        destinationMap.getMapAsync(googleMap -> {
            LatLng pos = new LatLng(place.getCoordinates().getLat(), place.getCoordinates().getLng());
            googleMap.addMarker(new MarkerOptions()
                            .position(pos)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_pin))
            );
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(pos));
        });
    }

    @Override
    public void openTransaction(DtlPlace dtlPlace, DtlTransaction dtlTransaction) {
        Route route = routeCreator.createRoute(dtlTransaction);

        NavigationBuilder navigationBuilder = NavigationBuilder.create();
        if (route == Route.DTL_TRANSACTION_SUCCEED)
            navigationBuilder.forDialog(getChildFragmentManager());
        else
            navigationBuilder.with(activityRouter);

        navigationBuilder.data(dtlPlace).move(route);
    }

    @Override
    public void setTransaction(DtlTransaction dtlTransaction) {
        earn.setText(dtlTransaction != null ? R.string.dtl_earn : R.string.dtl_check_in);
    }

    @OnClick(R.id.place_details_earn)
    void onCheckInClicked() {
        getPresenter().onCheckInClicked();
    }

    @OnClick(R.id.place_details_estimate_points)
    void onEstimatorClick() {
        getPresenter().onEstimationClick(getChildFragmentManager());
    }

    @OnClick(R.id.place_details_suggest_merchant)
    void suggestMerchantClick() {
        getPresenter().onMerchantClick();
    }

    @OnClick(R.id.place_details_share)
    void shareClick() {
        getPresenter().onShareClick();
    }
}
