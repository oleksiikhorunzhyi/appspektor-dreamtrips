package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
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
import com.worldventures.dreamtrips.modules.dtl.bundle.DtlMapBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.DtlMerchantDetailsBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantIdBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.PointsEstimationDialogBundle;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlPlaceHelper;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.DtlPlaceCommonDataInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.DtlPlaceInfoInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.DtlPlaceManyImagesDataInflater;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantMedia;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOffer;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlPlaceDetailsPresenter;
import com.worldventures.dreamtrips.util.ImageTextItem;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTouch;
import timber.log.Timber;

@Layout(R.layout.fragment_dtl_place_details)
public class DtlPlaceDetailsFragment
        extends RxBaseFragmentWithArgs<DtlPlaceDetailsPresenter, DtlMerchantDetailsBundle>
        implements DtlPlaceDetailsPresenter.View {

    private static final int REQUEST_CHECK_SETTINGS = 1489;
    private final static float PLACE_MAP_ZOOM = 15f;

    DtlPlaceCommonDataInflater commonDataInflater;
    DtlPlaceInfoInflater placeInfoInflater;
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
        return new DtlPlaceDetailsPresenter(getArgs().getId());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        helper = new DtlPlaceHelper(activity);
        commonDataInflater = new DtlPlaceManyImagesDataInflater(helper, getChildFragmentManager());
        placeInfoInflater = new DtlPlaceInfoInflater(helper);
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

        getPresenter().trackScreen();
    }

    @Override
    public void setPlace(DtlMerchant place) {
        commonDataInflater.apply(place);
        placeInfoInflater.apply(place);
        setType(place);
        setDescriptions(place);
        setAdditional(place);
        setMap(place);
    }

    private void setType(DtlMerchant DtlMerchant) {
        earnWrapper.setVisibility(DtlMerchant.hasOffer(DtlOffer.TYPE_POINTS) ? View.VISIBLE : View.GONE);
        merchantWrapper.setVisibility(DtlMerchant.hasNoOffers() ? View.VISIBLE : View.GONE);
    }

    private void setDescriptions(DtlMerchant place) {
        this.description.setText(Html.fromHtml(place.getDescription()));
        this.description.setMovementMethod(new LinkMovementMethod());

        //
        String perksDescription = "";
        if (place.hasOffer(DtlOffer.TYPE_PERK))
            perksDescription = place.getPerkDescription();
        this.perksDescription.setText(perksDescription);
        //
        this.descriptionHeader.setVisibility(TextUtils.isEmpty(place.getDescription()) ? View.GONE : View.VISIBLE);
        this.perksDescriptionHeader.setVisibility(TextUtils.isEmpty(perksDescription) ? View.GONE : View.VISIBLE);
    }

    private void setAdditional(DtlMerchant place) {
        Queryable.from(helper.getContactsData(place)).forEachR(contact -> {
            TextView contactView = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.list_item_dtl_place_contact, additionalContainer, false);
            contactView.setCompoundDrawablesWithIntrinsicBounds(contact.icon, null, null, null);
            contactView.setText(contact.text);
            if (contact.intent != null && contact.intent
                    .resolveActivityInfo(getActivity().getPackageManager(), 0) != null)
                contactView.setOnClickListener(view -> {
                    // this bifurcation below and view->presenter->view ping-pong with
                    // locationDelegate is for analytics solely
                    if (contact.type.equals(ImageTextItem.Type.ADDRESS)) {
                        getPresenter().routeToPlaceRequested(contact.intent);
                    } else {
                        startActivity(contact.intent);
                    }
                });
            additionalContainer.addView(contactView);
        });
    }

    private void setMap(DtlMerchant place) {
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
        getPresenter().trackPointEstimator();
        router.moveTo(Route.DTL_POINTS_ESTIMATION, NavigationConfigBuilder.forDialog()
                .data(data)
                .fragmentManager(getChildFragmentManager())
                .build());
    }

    @Override
    public void openSuggestMerchant(MerchantIdBundle data) {
        router.moveTo(Route.DTL_SUGGEST_MERCHANT, NavigationConfigBuilder.forActivity()
                .data(data)
                .build());
    }

    @Override
    public void openTransaction(DtlMerchant dtlMerchant, DtlTransaction dtlTransaction) {
        router.moveTo(Route.DTL_SCAN_RECEIPT, NavigationConfigBuilder.forActivity()
                .data(new MerchantIdBundle(dtlMerchant.getId()))
                .build());
    }

    @Override
    public void showSucceed(DtlMerchant dtlMerchant, DtlTransaction dtlTransaction) {
        router.moveTo(Route.DTL_TRANSACTION_SUCCEED, NavigationConfigBuilder.forDialog()
                .data(new MerchantIdBundle(dtlMerchant.getId()))
                .build());
        getPresenter().trackEarnFlowView();
    }

    @Override
    public void setTransaction(DtlTransaction dtlTransaction) {
        earn.setText(dtlTransaction != null ? R.string.dtl_earn : R.string.dtl_check_in);
        checkedIn.setVisibility(dtlTransaction != null ? View.VISIBLE : View.GONE);
    }

    @Override
    public void openMap() {
        router.moveTo(Route.DTL_MAP, NavigationConfigBuilder.forFragment()
                .containerId(R.id.dtl_landscape_slave_container)
                .backStackEnabled(false)
                .fragmentManager(getFragmentManager())
                .data(new DtlMapBundle(true))
                .build());
    }

    @Override
    public void share(DtlMerchant place) {
        new ShareDialog(activityRouter.getContext(), type -> {
            ShareBundle shareBundle = new ShareBundle();
            shareBundle.setShareType(type);
            shareBundle.setText(getString(place.hasOffer(DtlOffer.TYPE_POINTS) ?
                            R.string.dtl_details_share_title :
                            R.string.dtl_details_share_title_without_points,
                    place.getDisplayName()));
            shareBundle.setShareUrl(place.getWebsite());
            // don't attach media is website is attached, this image will go nowhere
            if (TextUtils.isEmpty(place.getWebsite())) {
                DtlMerchantMedia media = Queryable.from(place.getImages()).firstOrDefault();
                if (media != null) shareBundle.setImageUrl(media.getImagePath());
            }
            //
            getPresenter().trackSharing(type);
            //
            router.moveTo(Route.SHARE, NavigationConfigBuilder.forActivity()
                    .data(shareBundle)
                    .build());
        }).show();
    }

    @OnTouch(R.id.dtl_place_details_map_click_interceptor)
    boolean onMapTouched() {
        getPresenter().routeToPlaceRequested(null);
        return false;
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
    public void showMerchantMap(@Nullable Intent intent) {
        if (intent != null) startActivity(intent);
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
