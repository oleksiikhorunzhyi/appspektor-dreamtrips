package com.worldventures.dreamtrips.modules.dtl_flow.parts.details;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.innahema.collections.query.queriables.Queryable;
import com.jakewharton.rxbinding.internal.Preconditions;
import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.core.utils.ActivityResultDelegate;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.model.ShareType;
import com.worldventures.dreamtrips.modules.common.view.bundle.ShareBundle;
import com.worldventures.dreamtrips.modules.common.view.dialog.ShareDialog;
import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantIdBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.PointsEstimationDialogBundle;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlMerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.MerchantWorkingHoursInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.MerchantInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.MerchantInfoInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.MerchantOffersInflater;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantMedia;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlActivity;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;
import com.worldventures.dreamtrips.util.ImageTextItem;
import com.worldventures.dreamtrips.util.ImageTextItemFactory;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTouch;
import timber.log.Timber;

public class DtlDetailsScreenImpl
        extends DtlLayout<DtlDetailsScreen, DtlDetailsPresenter, DtlMerchantDetailsPath>
        implements DtlDetailsScreen, ActivityResultDelegate.ActivityResultListener {

    private final static float MERCHANT_MAP_ZOOM = 15f;

    public static final String MAP_TAG = "MAP_DETAILS_TAG";

    @Inject ActivityResultDelegate activityResultDelegate;
    @Inject Router router;
    //
    @InjectView(R.id.toolbar_actionbar) Toolbar toolbar;
    @InjectView(R.id.merchant_details_earn_wrapper) ViewGroup earnWrapper;
    @InjectView(R.id.merchant_details_merchant_wrapper) ViewGroup merchantWrapper;
    @InjectView(R.id.merchant_details_additional) ViewGroup additionalContainer;
    @InjectView(R.id.merchant_address) TextView merchantAddress;
    //
    MerchantOffersInflater merchantDataInflater;
    MerchantWorkingHoursInflater merchantHoursInflater;
    MerchantInflater merchantInfoInflater;
    DtlMerchant merchant;

    @Override
    public DtlDetailsPresenter createPresenter() {
        return new DtlDetailsPresenterImpl(getContext(), injector, getPath().getMerchant(),
                getPath().getPreExpandOffers());
    }

    @Override
    protected void onPostAttachToWindowView() {
        inflateToolbarMenu(toolbar);
        //
        toolbar.setNavigationIcon(R.drawable.back_icon);
        toolbar.setNavigationOnClickListener(view -> getActivity().onBackPressed());
        //
        activityResultDelegate.addListener(this);
        //
        merchantHoursInflater = new MerchantWorkingHoursInflater(injector);
        merchantDataInflater = new MerchantOffersInflater(injector);
        merchantInfoInflater = new MerchantInfoInflater();
        //
        merchantDataInflater.registerOfferClickListener(offer -> getPresenter().onOfferClick(offer));
        merchantDataInflater.setView(this);
        merchantInfoInflater.setView(this);
        merchantHoursInflater.setView(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (merchantDataInflater != null) merchantDataInflater.release();
        if (merchantInfoInflater != null) merchantInfoInflater.release();
        if (merchantHoursInflater != null) merchantHoursInflater.release();
        activityResultDelegate.removeListener(this);
        super.onDetachedFromWindow();
    }

    @Override
    public void setMerchant(DtlMerchant merchant) {
        this.merchant = merchant;
        merchantDataInflater.applyMerchant(merchant);
        merchantInfoInflater.applyMerchant(merchant);
        merchantHoursInflater.applyMerchant(merchant);
        //
        toolbar.setTitle(merchant.getDisplayName());
        //
        setContacts();
        setLocation();
        setClicks();
    }

    @Override
    public void setMap(DtlMerchant merchant) {
        GoogleMapOptions mapOptions = new GoogleMapOptions();
        mapOptions.liteMode(true);
        //
        MapFragment mapFragment = (MapFragment) getActivity().getFragmentManager()
                .findFragmentByTag(MAP_TAG);
        if (mapFragment == null || !mapFragment.isAdded()) {
            mapFragment = MapFragment.newInstance(mapOptions);
            getActivity().getFragmentManager()
                    .beginTransaction()
                    .add(R.id.merchant_details_map, mapFragment, MAP_TAG)
                    .commit();
        }
        mapFragment.getMapAsync(this::bindMap);
    }

    @Override
    public void expandOffers(List<Integer> offers) {
        merchantDataInflater.expandOffers(offers);
    }

    @Override
    public void expandHoursView() {
        merchantHoursInflater.preexpand();
    }

    @Override
    public List<Integer> getExpandedOffers() {
        return merchantDataInflater.getExpandedOffers();
    }

    @Override
    public boolean isHoursViewExpanded() {
        return merchantHoursInflater != null && merchantHoursInflater.isViewExpanded();
    }

    private void setContacts() {
        Queryable.from(DtlMerchantHelper.getContactsData(getContext(), merchant)).filter(contact -> contact.type != ImageTextItem.Type.ADDRESS).forEachR(contact -> {
            TextView contactView = inflateContactView();
            contactView.setCompoundDrawablesWithIntrinsicBounds(contact.icon, null, null, null);
            contactView.setText(contact.text);
            //
            if (DtlMerchantHelper.contactCanBeResolved(contact, getActivity()))
                RxView.clicks(contactView)
                        .compose(RxLifecycle.bindView(contactView))
                        .subscribe(aVoid -> onContactClick(contact));

            additionalContainer.addView(contactView);
        });
    }

    private void setLocation() {
        ImageTextItem contact = ImageTextItemFactory.create(getContext(), merchant, ImageTextItem.Type.ADDRESS);
        if (contact != null) merchantAddress.setText(contact.text);
    }

    protected TextView inflateContactView() {
        return (TextView) LayoutInflater.from(getActivity())
                .inflate(R.layout.list_item_dtl_merchant_contact, additionalContainer, false);
    }

    private void onContactClick(ImageTextItem contact) {
        if (contact.type.equals(ImageTextItem.Type.ADDRESS))
            getPresenter().routeToMerchantRequested(contact.intent);
        else getContext().startActivity(contact.intent);
    }

    public void bindMap(GoogleMap map) {
        Preconditions.checkNotNull(merchant, "set merchant before binding info inside map");
        //
        int paddingX = getContext().getResources().getDimensionPixelOffset(R.dimen.spacing_large);
        int paddingY = getContext().getResources().getDimensionPixelOffset(R.dimen.spacing_normal);
        LatLng pos = new LatLng(merchant.getCoordinates().getLat(), merchant.getCoordinates().getLng());
        //
        map.getUiSettings().setMapToolbarEnabled(false);
        map.setPadding(paddingX, paddingY, paddingX, paddingY);
        map.addMarker(new MarkerOptions().position(pos).icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_pin)));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, MERCHANT_MAP_ZOOM));
    }

    private void setClicks() {
        View earn = ButterKnife.findById(this, R.id.merchant_details_earn);
        View estimate = ButterKnife.findById(this, R.id.merchant_details_estimate_points);
        //
        if (earn != null)
            RxView.clicks(earn).compose(RxLifecycle.bindView(this)).subscribe(aVoid -> getPresenter().onCheckInClicked());
        if (estimate != null)
            RxView.clicks(estimate).compose(RxLifecycle.bindView(this)).subscribe(aVoid -> getPresenter().onEstimationClick());
    }

    @Override
    public void showEstimationDialog(PointsEstimationDialogBundle data) {
        getPresenter().trackPointEstimator();
        router.moveTo(Route.DTL_POINTS_ESTIMATION, NavigationConfigBuilder.forDialog()
                .data(data)
                .fragmentManager(getActivity().getSupportFragmentManager())
                .build());
    }

    @Override
    public void openSuggestMerchant(MerchantIdBundle data) {
        router.moveTo(Route.ENROLL_MERCHANT, NavigationConfigBuilder.forActivity()
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
    }

    @Override
    public void setTransaction(DtlTransaction dtlTransaction) {
        Button earn = ButterKnife.findById(this, R.id.merchant_details_earn);
        TextView checkedIn = ButterKnife.findById(this, R.id.checked_in);
        //
        if (earn != null)
            earn.setText(dtlTransaction != null ? R.string.dtl_earn : R.string.dtl_check_in);
        if (checkedIn != null)
            ViewUtils.setViewVisibility(checkedIn, dtlTransaction != null ? View.VISIBLE : View.GONE);
    }

    @Override
    public void share(DtlMerchant merchant) {
        new ShareDialog(getContext(), type -> {
            ShareBundle shareBundle = new ShareBundle();
            shareBundle.setShareType(type);
            shareBundle.setText(getContext().getString(merchant.hasPoints() ?
                            R.string.dtl_details_share_title :
                            R.string.dtl_details_share_title_without_points,
                    merchant.getDisplayName()));
            shareBundle.setShareUrl(merchant.getWebsite());
            // don't attach media if website is attached, this image will go nowhere
            if (TextUtils.isEmpty(merchant.getWebsite()) || type.equals(ShareType.TWITTER)) {
                DtlMerchantMedia media = Queryable.from(merchant.getImages()).firstOrDefault();
                if (media != null) shareBundle.setImageUrl(media.getImagePath());
                // for twitter: sharing image via web (not official app) currently not supported (android sdk v1.9.1)
            }
            //
            getPresenter().trackSharing(type);
            //
            router.moveTo(Route.SHARE, NavigationConfigBuilder.forActivity()
                    .data(shareBundle)
                    .build());
        }).show();
    }

    @OnTouch(R.id.dtl_merchant_details_map_click_interceptor)
    boolean onMapTouched() {
        getPresenter().routeToMerchantRequested(null);
        return false;
    }

    @OnClick(R.id.merchant_details_suggest_merchant)
    void suggestMerchantClick() {
        getPresenter().onMerchantClick();
    }

    @Override
    public void setSuggestMerchantButtonAvailable(boolean available) {
        merchantWrapper.setVisibility(available ? View.VISIBLE : View.GONE);
    }

    @Override
    public void enableCheckinButton() {
        View earn = ButterKnife.findById(this, R.id.merchant_details_earn);
        if (earn != null) earn.setEnabled(true);
    }

    @Override
    public void disableCheckinButton() {
        View earn = ButterKnife.findById(this, R.id.merchant_details_earn);
        if (earn != null) earn.setEnabled(false);
    }

    @Override
    public void showMerchantMap(@Nullable Intent intent) {
        if (intent != null) getContext().startActivity(intent);
    }

    @Override
    public void locationResolutionRequired(Status status) {
        try {
            status.startResolutionForResult(getActivity(), DtlActivity.GPS_LOCATION_RESOLUTION_REQUEST);
        } catch (IntentSender.SendIntentException th) {
            Timber.e(th, "Error opening settings activity.");
        }
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DtlActivity.GPS_LOCATION_RESOLUTION_REQUEST) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    // All required changes were successfully made
                    getPresenter().onCheckInClicked();
                    break;
                case Activity.RESULT_CANCELED:
                    // The user was asked to change settings, but chose not to
                    getPresenter().locationNotGranted();
                    break;
            }
            return true;
        }
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Boilerplate stuff
    ///////////////////////////////////////////////////////////////////////////

    public DtlDetailsScreenImpl(Context context) {
        super(context);
    }

    public DtlDetailsScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
