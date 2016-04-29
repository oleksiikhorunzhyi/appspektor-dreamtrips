package com.worldventures.dreamtrips.modules.dtl.helper.inflater;

import android.content.res.Resources;
import android.net.Uri;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.innahema.collections.query.queriables.Queryable;
import com.jakewharton.rxbinding.internal.Preconditions;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.view.ViewLayoutChangeEvent;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.core.utils.GraphicUtils;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.view.custom.ShowMoreTextView;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlMerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantMedia;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOffer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOfferData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOfferMedia;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Offer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationDay;
import com.worldventures.dreamtrips.modules.profile.view.widgets.ExpandableLayout;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.techery.properratingbar.ProperRatingBar;

public class DtlMerchantInfoInflater extends DtlMerchantDataInflater {

    private final static float MERCHANT_MAP_ZOOM = 15f;

    @InjectView(R.id.operational_time) TextView operationalTime;
    @InjectView(R.id.merchant_details_pricing) ProperRatingBar pricing;
    @InjectView(R.id.categories) TextView categories;
    @InjectView(R.id.distance) TextView distance;
    @InjectView(R.id.toolbar_actionbar) Toolbar toolbar;
    @InjectView(R.id.merchant_details_earn_wrapper) ViewGroup earnWrapper;
    @InjectView(R.id.perk_divider) View perkDivider;
    @InjectView(R.id.merchant_details_merchant_wrapper) ViewGroup merchantWrapper;
    @InjectView(R.id.merchant_details_description) TextView description;
    @InjectView(R.id.description_header) ViewGroup descriptionHeader;
    @InjectView(R.id.scrollView) NestedScrollView scrollViewRoot;
    @InjectView(R.id.legal_text) ShowMoreTextView legalTextView;
    @InjectView(R.id.merchant_details_cover) SimpleDraweeView cover;

    private Resources resources;
    private DtlMerchant merchant;


    @Override
    public void setView(View rootView) {
        super.setView(rootView);
        this.resources = rootView.getResources();
    }

    @Override
    protected void onMerchantApply(DtlMerchant merchant) {
        Preconditions.checkNotNull(merchant, "merchant is null");
        this.merchant = merchant;
        //
        setInfo();
        setImage();
        setType();
        setDescriptions();
        setOffers();
    }

    public void provideMap(GoogleMap map) {
        Preconditions.checkNotNull(merchant, "set merchant before binding info inside map");
        //
        int paddingX = resources.getDimensionPixelOffset(R.dimen.spacing_large);
        int paddingY = resources.getDimensionPixelOffset(R.dimen.spacing_normal);
        LatLng pos = new LatLng(merchant.getCoordinates().getLat(), merchant.getCoordinates().getLng());
        //
        map.getUiSettings().setMapToolbarEnabled(false);
        map.setPadding(paddingX, paddingY, paddingX, paddingY);
        map.addMarker(new MarkerOptions().position(pos).icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_pin)));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, MERCHANT_MAP_ZOOM));
    }

    private void setInfo() {
        pricing.setRating(merchant.getBudget());
        //
        boolean hasDistance = merchant.getDistance() != 0.0d;
        boolean hasOperationDays = merchant.hasOffer(DtlOffer.TYPE_POINTS) && merchant.getOperationDays() != null && !merchant.getOperationDays().isEmpty();
        boolean hasCategories = !TextUtils.isEmpty(DtlMerchantHelper.getCategories(merchant));

        CharSequence distanceText = hasDistance ? resources.getString(R.string.distance_caption_format, merchant.getDistance(),
                resources.getString(merchant.getDistanceType() == DistanceType.MILES ? R.string.mi : R.string.km)) : "";

        CharSequence operDays = hasOperationDays ? DtlMerchantHelper.getOperationalTime(rootView.getContext(), merchant) : "";
        CharSequence categoriesText = hasCategories ? DtlMerchantHelper.getCategories(merchant) : "";

        bindTextOrHideView(distance, distanceText);
        bindTextOrHideView(categories, categoriesText);
        bindTextOrHideView(operationalTime, operDays);
    }

    private void setType() {
        ViewUtils.setViewVisibility(earnWrapper, !merchant.hasNoOffers() ? View.VISIBLE : View.GONE);
        ViewUtils.setViewVisibility(merchantWrapper, merchant.hasNoOffers() ? View.VISIBLE : View.GONE);
        ViewUtils.setViewVisibility(perkDivider, merchant.hasNoOffers() ? View.GONE : View.VISIBLE);
    }

    private void setImage() {
        DtlMerchantMedia media = Queryable.from(merchant.getImages()).firstOrDefault();
        if (media == null) return;
        //
        RxView.layoutChangeEvents(cover)
                .compose(RxLifecycle.bindView(rootView))
                .subscribe(event -> onLayoutImage(event, media));
    }

    private void onLayoutImage(ViewLayoutChangeEvent event, DtlMerchantMedia media) {
        if (event.view().getWidth() == 0) return;
        cover.setController(GraphicUtils.provideFrescoResizingController(
                Uri.parse(media.getImagePath()), cover.getController(),
                cover.getWidth(), cover.getHeight()));
    }

    private void setDescriptions() {
        toolbar.setTitle(merchant.getDisplayName());
        //
        description.setText(Html.fromHtml(merchant.getDescription()));
        description.setMovementMethod(new LinkMovementMethod());
        //
        ViewUtils.setViewVisibility(descriptionHeader, TextUtils.isEmpty(merchant.getDescription()) ? View.GONE : View.VISIBLE);
        ViewUtils.setViewVisibility(legalTextView, merchant.getDisclaimers().isEmpty() ? View.GONE : View.VISIBLE);
        //
        if (legalTextView.getVisibility() == View.GONE) return;

        legalTextView.setFullText(TextUtils.join("\n\n", merchant.getDisclaimers()));
        legalTextView.setSimpleListener((view, collapsed) -> {
            if (!collapsed) scrollViewRoot.post(() -> scrollViewRoot.fullScroll(View.FOCUS_DOWN));
        });
    }

    private void setOffers() {
        if (merchant.hasNoOffers()) return;
        //
        List<DtlOfferData> offers = Queryable.from(merchant.getOffers())
                .map(DtlOffer::getOffer).sort().toList();
        for (int index = 0; index < offers.size(); index++) {
            addOffer(offers.get(index), index);
        }
    }

    private void addOffer(DtlOfferData offer, int index) {
        View view = (offer.getType().equals(Offer.PERKS)) ? createAndBindPerkView(offer) : createAndBindPointView(offer);
        earnWrapper.addView(view, index);
    }

    private View createAndBindPointView(DtlOfferData point) {
        View pointView = LayoutInflater.from(rootView.getContext()).inflate(R.layout.item_point_view, earnWrapper, false);
        bindInfo(ButterKnife.<TextView>findById(pointView, R.id.points_description), point.getDescription());
        return pointView;
    }

    //TODO need refactoring
    private View createAndBindPerkView(DtlOfferData perk) {
        ExpandableLayout perkView = (ExpandableLayout) LayoutInflater.from(rootView.getContext()).inflate(R.layout.item_perk_view, earnWrapper, false);
        bindInfo(ButterKnife.<TextView>findById(perkView, R.id.perk_description), perk.getDescription());
        bindInfo(ButterKnife.<TextView>findById(perkView, R.id.perks_title), perk.getDescription());
        bindInfo(ButterKnife.<TextView>findById(perkView, R.id.perk_disclaimer), perk.getDisclaimer());
        bindImage(ButterKnife.<SimpleDraweeView>findById(perkView, R.id.perk_logo), perk);
        bindOperationDays(ButterKnife.<TextView>findById(perkView, R.id.perks_operation_days), perk, resources);
        if (expandedOffer != null && expandedOffer.equals(perk)) perkView.showWithoutAnimation();
        return perkView;
    }

    private static void bindImage(SimpleDraweeView image, DtlOfferData perk) {
        DtlOfferMedia media = Queryable.from(perk.getImages()).firstOrDefault();
        if (media == null) return;
        //
        image.setImageURI(Uri.parse(media.getImagePath()));
    }

    private static void bindInfo(TextView view, String description) {
        if (description != null) view.setText(description);
    }

    private static void bindOperationDays(TextView operationDays, DtlOfferData perk, Resources resources) {
        List<OperationDay> operDays = perk.getOperationDays();
        if (operationDays == null) return;
        //
        String concatDays = DateTimeUtils.concatOperationDays(resources, operDays);
        operationDays.setText(concatDays);
    }

    private static void bindTextOrHideView(TextView textView, CharSequence text) {
        if (!TextUtils.isEmpty(text)) {
            ViewUtils.setViewVisibility(textView, View.VISIBLE);
            textView.setText(text);
        } else ViewUtils.setViewVisibility(textView, View.GONE);
    }
}
