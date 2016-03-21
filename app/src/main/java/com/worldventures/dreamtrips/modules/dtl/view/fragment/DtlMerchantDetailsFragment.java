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
import android.widget.ScrollView;
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
import com.worldventures.dreamtrips.modules.common.model.ShareType;
import com.worldventures.dreamtrips.modules.common.view.bundle.ShareBundle;
import com.worldventures.dreamtrips.modules.common.view.custom.ShowMoreTextView;
import com.worldventures.dreamtrips.modules.common.view.dialog.ShareDialog;
import com.worldventures.dreamtrips.modules.dtl.bundle.DtlMapBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.DtlMerchantDetailsBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantIdBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.PointsEstimationDialogBundle;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlMerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.DtlMerchantCommonDataInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.DtlMerchantInfoInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.DtlMerchantManyImagesDataInflater;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantMedia;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOffer;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlMerchantDetailsPresenter;
import com.worldventures.dreamtrips.util.ImageTextItem;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTouch;
import timber.log.Timber;

@Layout(R.layout.fragment_dtl_merchant_details)
public class DtlMerchantDetailsFragment
        extends RxBaseFragmentWithArgs<DtlMerchantDetailsPresenter, DtlMerchantDetailsBundle>
        implements DtlMerchantDetailsPresenter.View {

    private static final int REQUEST_CHECK_SETTINGS = 1489;
    private final static float MERCHANT_MAP_ZOOM = 15f;

    DtlMerchantCommonDataInflater commonDataInflater;
    DtlMerchantInfoInflater merchantInfoInflater;
    DtlMerchantHelper helper;

    @Inject
    ActivityResultDelegate activityResultDelegate;
    @Inject
    BackStackDelegate backStackDelegate;
    //
    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;
    @InjectView(R.id.merchant_details_earn_wrapper)
    ViewGroup earnWrapper;
    @InjectView(R.id.checked_in)
    TextView checkedIn;
    @InjectView(R.id.merchant_details_earn)
    Button earn;
    @InjectView(R.id.merchant_details_estimate_points)
    Button estimatePoints;
    @InjectView(R.id.merchant_details_merchant_wrapper)
    ViewGroup merchantWrapper;
    @InjectView(R.id.merchant_details_suggest_merchant)
    Button merchant;
    @InjectView(R.id.merchant_details_description)
    TextView description;
    @InjectView(R.id.merchant_details_perks_description)
    TextView perksDescription;
    @InjectView(R.id.perks_description_header)
    ViewGroup perksDescriptionHeader;
    @InjectView(R.id.description_header)
    ViewGroup descriptionHeader;
    @InjectView(R.id.merchant_details_additional)
    ViewGroup additionalContainer;
    @InjectView(R.id.merchant_details_share)
    View share;
    @InjectView(R.id.scrollView)
    ScrollView scrollViewRoot;
    @InjectView(R.id.legal_text)
    ShowMoreTextView legalTextView;
    //
    SupportMapFragment destinationMap;

    @Override
    protected DtlMerchantDetailsPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlMerchantDetailsPresenter(getArgs().getId());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        helper = new DtlMerchantHelper(activity);
        commonDataInflater = new DtlMerchantManyImagesDataInflater(helper, getChildFragmentManager());
        merchantInfoInflater = new DtlMerchantInfoInflater(helper);
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
        backStackDelegate.clearListener();
        super.onPause();
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
        merchantInfoInflater.setView(rootView);
        //
        legalTextView.setSimpleListener((view, collapsed) -> {
            if (!collapsed) scrollViewRoot.post(() -> scrollViewRoot.fullScroll(View.FOCUS_DOWN));}
        );
        //
        getPresenter().trackScreen();
    }

    @Override
    public void setMerchant(DtlMerchant merchant) {
        commonDataInflater.apply(merchant);
        merchantInfoInflater.apply(merchant);
        setType(merchant);
        setDescriptions(merchant);
        setAdditional(merchant);
        setMap(merchant);
    }

    private void setType(DtlMerchant DtlMerchant) {
        earnWrapper.setVisibility(DtlMerchant.hasOffer(DtlOffer.TYPE_POINTS) ? View.VISIBLE : View.GONE);
        merchantWrapper.setVisibility(DtlMerchant.hasNoOffers() ? View.VISIBLE : View.GONE);
    }

    private void setDescriptions(DtlMerchant merchant) {
        this.description.setText(Html.fromHtml(merchant.getDescription()));
        this.description.setMovementMethod(new LinkMovementMethod());
        //
        String perksDescription = "";
        if (merchant.hasOffer(DtlOffer.TYPE_PERK))
            perksDescription = merchant.getPerkDescription();
        this.perksDescription.setText(perksDescription);
        //
        this.descriptionHeader.setVisibility(TextUtils.isEmpty(merchant.getDescription()) ? View.GONE : View.VISIBLE);
        this.perksDescriptionHeader.setVisibility(TextUtils.isEmpty(perksDescription) ? View.GONE : View.VISIBLE);
        //
        if (!merchant.getDisclaimers().isEmpty()) {
            this.legalTextView.setVisibility(View.VISIBLE);
            //
            String legalText = TextUtils.join("\n\n", merchant.getDisclaimers());
            this.legalTextView.setFullText(legalText);
        }
    }

    private void setAdditional(DtlMerchant merchant) {
        Queryable.from(helper.getContactsData(merchant)).forEachR(contact -> {
            TextView contactView = (TextView) LayoutInflater.from(getActivity())
                    .inflate(R.layout.list_item_dtl_merchant_contact, additionalContainer, false);
            contactView.setCompoundDrawablesWithIntrinsicBounds(contact.icon, null, null, null);
            contactView.setText(contact.text);
            if (contact.intent != null && contact.intent
                    .resolveActivityInfo(getActivity().getPackageManager(), 0) != null)
                contactView.setOnClickListener(view -> {
                    // this bifurcation below and view->presenter->view ping-pong with
                    // locationDelegate is for analytics solely
                    if (contact.type.equals(ImageTextItem.Type.ADDRESS)) {
                        getPresenter().routeToMerchantRequested(contact.intent);
                    } else {
                        startActivity(contact.intent);
                    }
                });
            additionalContainer.addView(contactView);
        });
    }

    private void setMap(DtlMerchant merchant) {
        GoogleMapOptions mapOptions = new GoogleMapOptions();
        mapOptions.liteMode(true);
        //
        destinationMap = SupportMapFragment.newInstance(mapOptions);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.merchant_details_map, destinationMap)
                .commit();
        //
        destinationMap.getMapAsync(googleMap -> {
            googleMap.getUiSettings().setMapToolbarEnabled(false);
            int padding = getContext().getResources().getDimensionPixelOffset(R.dimen.spacing_large);
            googleMap.setPadding(0, 0, 0, padding);
            LatLng pos = new LatLng(merchant.getCoordinates().getLat(), merchant.getCoordinates().getLng());
            googleMap.addMarker(new MarkerOptions()
                            .position(pos)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_pin))
            );
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, MERCHANT_MAP_ZOOM));
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
        router.moveTo(Route.ENROLL_RESTAURANT, NavigationConfigBuilder.forActivity()
                .data(data)
                .build());
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
    public void share(DtlMerchant merchant) {
        new ShareDialog(getContext(), type -> {
            ShareBundle shareBundle = new ShareBundle();
            shareBundle.setShareType(type);
            shareBundle.setText(getString(merchant.hasOffer(DtlOffer.TYPE_POINTS) ?
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

    @OnClick(R.id.merchant_details_earn)
    void onCheckInClicked() {
        getPresenter().onCheckInClicked();
    }

    @OnClick(R.id.merchant_details_estimate_points)
    void onEstimatorClick() {
        getPresenter().onEstimationClick();
    }

    @OnClick(R.id.merchant_details_suggest_merchant)
    void suggestMerchantClick() {
        getPresenter().onMerchantClick();
    }

    @OnClick(R.id.merchant_details_share)
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
        } else getActivity().finish();
        return true;
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
                activityResultDelegate.clear();
                break;
        }
    }

}
