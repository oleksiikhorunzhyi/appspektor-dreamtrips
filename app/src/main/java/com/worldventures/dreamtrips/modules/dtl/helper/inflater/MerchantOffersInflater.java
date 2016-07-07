package com.worldventures.dreamtrips.modules.dtl.helper.inflater;

import android.content.res.Resources;
import android.net.Uri;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.innahema.collections.query.queriables.Queryable;
import com.jakewharton.rxbinding.internal.Preconditions;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.view.ViewLayoutChangeEvent;
import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.core.utils.GraphicUtils;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.view.custom.ShowMoreTextView;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlMerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantMedia;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOffer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOfferMedia;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationDay;
import com.worldventures.dreamtrips.modules.dtl.view.custom.ExpandableOfferView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MerchantOffersInflater extends MerchantDataInflater {

    @InjectView(R.id.merchant_details_merchant_wrapper)
    ViewGroup merchantWrapper;
    @InjectView(R.id.merchant_details_description)
    TextView description;
    @InjectView(R.id.description_header)
    ViewGroup descriptionHeader;
    @InjectView(R.id.scrollView)
    NestedScrollView scrollViewRoot;
    @InjectView(R.id.legal_text)
    ShowMoreTextView legalTextView;
    @InjectView(R.id.merchant_details_cover)
    SimpleDraweeView cover;
    @InjectView(R.id.merchant_details_earn_wrapper)
    ViewGroup earnWrapper;
    @InjectView(R.id.perk_divider)
    View perkDivider;

    @Inject
    protected SessionHolder<UserSession> sessionHolder;
    @Inject
    LocaleHelper localeHelper;

    private List<OfferClickListener> offerClickListeners = new ArrayList<>();
    private Map<Integer, WeakReference<ExpandableOfferView>> cashedViewMap = new HashMap<>();

    public MerchantOffersInflater(Injector injector) {
        injector.inject(this);
    }

    public void registerOfferClickListener(OfferClickListener listener) {
        this.offerClickListeners.add(listener);
    }

    @Override
    protected void onMerchantApply() {
        setType();
        setImage();
        setDescriptions();
        setOffers();
    }

    @Override
    public void release() {
        this.offerClickListeners.clear();
        super.release();
    }

    /***
     * Set expanded offers
     * Call this method before applyMerchant(DtlMerchant merchant) method;
     *
     * @param offers offers to expand
     */
    public void expandOffers(List<Integer> offers) {
        Preconditions.checkNotNull(merchant, "Merchant not set");
        //
        if (offers == null || cashedViewMap.size() == 0) return;
        //
        Queryable.from(offers)
                .filter(offer -> cashedViewMap.get(offer) != null)
                .filter(offer -> cashedViewMap.get(offer).get() != null)
                .forEachR(entry -> cashedViewMap.get(entry).get().showWithoutAnimation());
    }

    public List<Integer> getExpandedOffers() {
        return Queryable.from(cashedViewMap.keySet())
                .filter(index -> cashedViewMap.get(index) != null)
                .filter(index -> cashedViewMap.get(index).get() != null)
                .filter(index -> cashedViewMap.get(index).get().isOpened()).toList();
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
        //
        cover.setController(GraphicUtils.provideFrescoResizingController(
                Uri.parse(media.getImagePath()), cover.getController(),
                cover.getWidth(), cover.getHeight()));
    }

    private void setDescriptions() {
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
        List<DtlOffer> offers = merchant.getOffers();
        for (int index = 0; index < offers.size(); index++) {
            addOffer(offers.get(index), index);
        }
    }

    private void addOffer(DtlOffer offer, int index) {
        View view = (offer.isPerk()) ? createAndBindPerkView(offer, index) : createPointView();
        earnWrapper.addView(view, index);
    }

    private View createPointView() {
        return LayoutInflater.from(rootView.getContext()).inflate(R.layout.item_point_view, earnWrapper, false);
    }

    private View createAndBindPerkView(DtlOffer perk, int index) {
        ExpandableOfferView perkView = (ExpandableOfferView) LayoutInflater.from(rootView.getContext()).inflate(R.layout.item_perk_view, earnWrapper, false);
        bindInfo(ButterKnife.<TextView>findById(perkView, R.id.perk_description), perk.getDescription());
        bindInfo(ButterKnife.<TextView>findById(perkView, R.id.perks_title), perk.getTitle());
        bindInfo(ButterKnife.<TextView>findById(perkView, R.id.perk_disclaimer), perk.getDisclaimer());
        bindImage(ButterKnife.<SimpleDraweeView>findById(perkView, R.id.perk_logo), perk);
        bindOperationDays(ButterKnife.<TextView>findById(perkView, R.id.perks_operation_days), perk, rootView.getResources());
        bindDisclaimer(perkView, perk);
        patchExpiringBar(perkView, perk);
        //
        cashedViewMap.put(index, new WeakReference<>(perkView));
        return perkView;
    }

    private void patchExpiringBar(ViewGroup perkView, DtlOffer offerData) {
        AppCompatTextView expirationBarCaption = ButterKnife.<AppCompatTextView>findById(perkView, R.id.expirationBarCaption);
        if (DtlMerchantHelper.isOfferExpiringSoon(offerData)) {
            ViewUtils.setTextOrHideView(expirationBarCaption, DtlMerchantHelper.
                    getOfferExpiringCaption(perkView.getContext(), offerData,
                            localeHelper.getAccountLocale(sessionHolder.get().get().getUser())));
        }
    }

    private void notifyOfferClickListeners(DtlOffer offer) {
        Queryable.from(offerClickListeners)
                .filter(listener -> listener != null)
                .forEachR(listener -> listener.onOfferClick(offer));
    }

    private void bindDisclaimer(View perkView, DtlOffer offer) {
        if (TextUtils.isEmpty(offer.getDisclaimer())) {
            ButterKnife.findById(perkView, R.id.perk_disclaimer_header).setVisibility(View.GONE);
        } else {
            ButterKnife.<TextView>findById(perkView, R.id.perk_disclaimer).setText(offer.getDisclaimer());
        }
    }

    private void bindImage(SimpleDraweeView image, DtlOffer perk) {
        DtlOfferMedia media = Queryable.from(perk.getImages()).firstOrDefault();
        if (media == null) return;
        //
        image.setImageURI(Uri.parse(media.getImagePath()));
        RxView.clicks(image)
                .compose(RxLifecycle.bindView(image))
                .subscribe(aVoid -> notifyOfferClickListeners(perk));
    }

    private static void bindInfo(TextView view, String description) {
        if (description != null) view.setText(description);
    }

    private static void bindOperationDays(TextView operationDays, DtlOffer perk, Resources resources) {
        List<OperationDay> operDays = perk.getOperationDays();
        if (operationDays == null) return;
        //
        String concatDays = DateTimeUtils.concatOperationDays(resources, operDays);
        operationDays.setText(concatDays);
    }
}
