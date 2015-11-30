package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.BackStackDelegate;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.core.utils.ActivityResultDelegate;
import com.worldventures.dreamtrips.modules.common.view.bundle.ShareBundle;
import com.worldventures.dreamtrips.modules.common.view.dialog.ShareDialog;
import com.worldventures.dreamtrips.modules.dtl.bundle.PlaceDetailsBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.PlacesBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.PlacesMapBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.PointsEstimationDialogBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.SuggestPlaceBundle;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlCategoryDataInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlPlaceCommonDataInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlPlaceHelper;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlPlaceInfoInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlPlaceManyImagesDataInflater;
import com.worldventures.dreamtrips.modules.dtl.model.DtlOffer;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlaceMedia;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlPlaceDetailsPresenter;
import com.worldventures.dreamtrips.util.SpanUtils;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

@Layout(R.layout.fragment_dtl_place_details)
public class DtlPlaceDetailsFragment
        extends RxBaseFragmentWithArgs<DtlPlaceDetailsPresenter, PlaceDetailsBundle>
        implements DtlPlaceDetailsPresenter.View {

    private static final int REQUEST_CHECK_SETTINGS = 1489;
    private final static float PLACE_MAP_ZOOM = 15f;

    DtlPlaceCommonDataInflater commonDataInflater;
    DtlPlaceInfoInflater placeInfoInflater;
    DtlCategoryDataInflater categoryDataInflater;
    DtlPlaceHelper helper;

    @Inject
    ActivityResultDelegate activityResultDelegate;

    @Inject
    BackStackDelegate backStackDelegate;

    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;
    @InjectView(R.id.place_details_earn_wrapper)
    ViewGroup earnWrapper;
    @InjectView(R.id.checked_in)
    TextView checkedIn;
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
    @InjectView(R.id.place_details_perks_description)
    TextView perksDescription;
    @InjectView(R.id.perks_description_header)
    ViewGroup perksDescriptionHeader;
    @InjectView(R.id.description_header)
    ViewGroup descriptionHeader;
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
    public void onResume() {
        super.onResume();
        backStackDelegate.setListener(this::onBackPressed);
        activityResult(activityResultDelegate.getRequestCode(),
                activityResultDelegate.getResultCode(), activityResultDelegate.getData());
    }

    @Override
    public void onPause() {
        super.onPause();
        backStackDelegate.setListener(null);
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        if (!tabletAnalytic.isTabletLandscape()) {
            toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            toolbar.setNavigationOnClickListener(view -> getActivity().onBackPressed());
        } else {
            toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());
        }
        commonDataInflater.setView(rootView);
        placeInfoInflater.setView(rootView);
        categoryDataInflater.setView(rootView);
    }

    @Override
    public void setPlace(DtlPlace place) {
        commonDataInflater.apply(place);
        placeInfoInflater.apply(place);
        categoryDataInflater.apply(place);
        setType(place);
        setDescriptions(place);
        setAdditional(place);
        setMap(place);
    }

    private void setType(DtlPlace dtlPlace) {
        earnWrapper.setVisibility(dtlPlace.hasOffer(DtlOffer.TYPE_POINTS) ? View.VISIBLE : View.GONE);
        merchantWrapper.setVisibility(dtlPlace.hasNoOffers() ? View.VISIBLE : View.GONE);
    }

    private void setDescriptions(DtlPlace place) {
        this.description.setText(Html.fromHtml(place.getDescription()));
        //
        String perksDescription = "";
        if (place.hasOffer(DtlOffer.TYPE_PERK))
            perksDescription = place.getPerkDescription();
        this.perksDescription.setText(perksDescription);
        //
        this.descriptionHeader.setVisibility(TextUtils.isEmpty(place.getDescription()) ? View.GONE : View.VISIBLE);
        this.perksDescriptionHeader.setVisibility(TextUtils.isEmpty(perksDescription) ? View.GONE : View.VISIBLE);
    }

    private void setAdditional(DtlPlace place) {
        Queryable.from(helper.getContactsData(place)).forEachR(contact -> {
            TextView contactView = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.list_item_dtl_place_contact, additionalContainer, false);
            contactView.setCompoundDrawablesWithIntrinsicBounds(contact.icon, null, null, null);
            contactView.setText(contact.text);
            if (Linkify.addLinks(contactView, Linkify.ALL)) SpanUtils.stripUnderlines(contactView);
            if (contact.intent != null && contact.intent
                    .resolveActivityInfo(getActivity().getPackageManager(), 0) != null)
                contactView.setOnClickListener(view -> startActivity(contact.intent));
            additionalContainer.addView(contactView);
        });

    }

    private void setMap(DtlPlace place) {
        GoogleMapOptions mapOptions = new GoogleMapOptions();
        mapOptions.liteMode(true);
        //
        destinationMap = SupportMapFragment.newInstance(mapOptions);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.place_details_map, destinationMap)
                .commit();
        //
        destinationMap.getMapAsync(googleMap -> {
            googleMap.getUiSettings().setMapToolbarEnabled(false);
            int padding = getContext().getResources().getDimensionPixelOffset(R.dimen.spacing_large);
            googleMap.setPadding(0, 0, 0, padding);
            LatLng pos = new LatLng(place.getCoordinates().getLat(), place.getCoordinates().getLng());
            googleMap.addMarker(new MarkerOptions()
                            .position(pos)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_pin))
            );
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, PLACE_MAP_ZOOM));
        });
    }

    @Override
    public void showEstimationDialog(PointsEstimationDialogBundle data) {
        router.moveTo(Route.DTL_POINTS_ESTIMATION, NavigationConfigBuilder.forDialog()
                .data(data)
                .fragmentManager(getChildFragmentManager())
                .build());
    }

    @Override
    public void openSuggestMerchant(SuggestPlaceBundle data) {
        router.moveTo(Route.DTL_SUGGEST_MERCHANT, NavigationConfigBuilder.forActivity()
                .data(data)
                .build());
    }

    @Override
    public void openTransaction(DtlPlace dtlPlace, DtlTransaction dtlTransaction) {
        router.moveTo(Route.DTL_SCAN_RECEIPT, NavigationConfigBuilder.forActivity().data(dtlPlace).build());
    }

    @Override
    public void showSucceed(DtlPlace dtlPlace, DtlTransaction dtlTransaction) {
        router.moveTo(Route.DTL_TRANSACTION_SUCCEED, NavigationConfigBuilder.forDialog().data(dtlPlace).build());
    }

    @Override
    public void setTransaction(DtlTransaction dtlTransaction) {
        earn.setText(dtlTransaction != null ? R.string.dtl_earn : R.string.dtl_check_in);
        checkedIn.setVisibility(dtlTransaction != null ? View.VISIBLE : View.GONE);
    }

    @Override
    public void openMap(PlacesBundle placesBundle) {
        router.moveTo(Route.DTL_MAP, NavigationConfigBuilder.forFragment()
                .containerId(R.id.dtl_landscape_slave_container)
                .backStackEnabled(false)
                .fragmentManager(getFragmentManager())
                .data(new PlacesMapBundle(placesBundle.getLocation(), true))
                .build());
    }

    @Override
    public void share(DtlPlace place) {
        new ShareDialog(activityRouter.getContext(), type -> {
            ShareBundle shareBundle = new ShareBundle();
            shareBundle.setShareType(type);
            shareBundle.setText(getString(R.string.dtl_details_share_title, place.getDisplayName()));
            shareBundle.setShareUrl(place.getWebsite());
            DtlPlaceMedia media = Queryable.from(place.getImages()).firstOrDefault();
            if (media != null) shareBundle.setImageUrl(media.getImagePath());
            //
            router.moveTo(Route.SHARE, NavigationConfigBuilder.forActivity()
                    .data(shareBundle)
                    .build());
        }).show();
    }

    @OnClick(R.id.place_details_earn)
    void onCheckInClicked() {
        getPresenter().onCheckInClicked();
    }

    @OnClick(R.id.place_details_estimate_points)
    void onEstimatorClick() {
        getPresenter().onEstimationClick();
    }

    @OnClick(R.id.place_details_suggest_merchant)
    void suggestMerchantClick() {
        getPresenter().onMerchantClick();
    }

    @OnClick(R.id.place_details_share)
    void shareClick() {
        getPresenter().onShareClick();
    }

    @Override
    public void setSuggestMerchantButtonAvailable(boolean available) {
        merchantWrapper.setVisibility(available ? View.VISIBLE : View.GONE);
    }

    @Override
    public void enableCheckinButton() {
        earn.setEnabled(true);
    }

    @Override
    public void disableCheckinButton() {
        earn.setEnabled(false);
    }

    private boolean onBackPressed() {
        if (isTabletLandscape() && getArgs().isSlave()) {
            getPresenter().onBackPressed();
            return true;
        } else return false;
    }

    @Override
    public void resolutionRequired(Status status) {
        try {
            status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
        } catch (IntentSender.SendIntentException th) {
            Timber.e(th, "Error opening settings activity.");
        }
    }

    public void activityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        getPresenter().onCheckInClicked();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        getPresenter().locationNotGranted();
                        break;
                    default:
                        break;
                }
                break;
        }
    }

}
