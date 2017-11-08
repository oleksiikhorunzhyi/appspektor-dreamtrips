package com.worldventures.dreamtrips.modules.dtl_flow.parts.details;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
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
import com.trello.rxlifecycle.android.RxLifecycleAndroid;
import com.worldventures.core.service.DeviceInfoProvider;
import com.worldventures.core.ui.util.ViewUtils;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.dtl.merchants.model.OfferType;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.core.utils.ActivityResultDelegate;
import com.worldventures.dreamtrips.modules.common.view.dialog.ShareDialog;
import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantIdBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.PointsEstimationDialogBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.ThrstPaymentCompletedBundle;
import com.worldventures.dreamtrips.modules.dtl.helper.MerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.MerchantInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.MerchantInfoInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.MerchantOffersInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.MerchantWorkingHoursInflater;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlActivity;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.ReviewObject;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.views.OfferWithReviewView;
import com.worldventures.dreamtrips.social.ui.share.bundle.ShareBundle;
import com.worldventures.dreamtrips.util.ImageTextItem;
import com.worldventures.dreamtrips.util.ImageTextItemFactory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTouch;
import cn.pedant.SweetAlert.SweetAlertDialog;
import flow.Flow;
import flow.History;
import timber.log.Timber;

public class DtlDetailsScreenImpl extends DtlLayout<DtlDetailsScreen, DtlDetailsPresenter, DtlMerchantDetailsPath>
      implements DtlDetailsScreen, ActivityResultDelegate.ActivityResultListener {

   private final static float MERCHANT_MAP_ZOOM = 15f;

   public static final String MAP_TAG = "MAP_DETAILS_TAG";

   @Inject ActivityResultDelegate activityResultDelegate;
   @Inject Router router;
   @Inject DeviceInfoProvider deviceInfoProvider;

   @InjectView(R.id.toolbar_actionbar) Toolbar toolbar;
   @InjectView(R.id.merchant_details_earn_wrapper) ViewGroup earnWrapper;
   @InjectView(R.id.merchant_details_merchant_wrapper) ViewGroup merchantWrapper;
   @InjectView(R.id.merchant_details_additional) ViewGroup additionalContainer;
   @InjectView(R.id.merchant_address) TextView merchantAddress;
   @InjectView(R.id.tv_read_all_review) TextView mTvReadAllReviews;
   @InjectView(R.id.ratingBarReviews) RatingBar mRatingBar;
   @InjectView(R.id.text_view_rating) TextView textViewRating;
   @InjectView(R.id.view_points) TextView points;
   @InjectView(R.id.view_perks) TextView perks;
   @InjectView(R.id.view_pay_in_app) TextView payInApp;
   @InjectView(R.id.container_comments) OfferWithReviewView mContainerComments;
   @InjectView(R.id.btn_rate_and_review) TextView rateAndReviewBtn;
   @InjectView(R.id.order_from_menu_divider) View orderFromMenuDivider;
   @InjectView(R.id.order_from_menu) TextView orderFromMenuBtn;

   private MerchantOffersInflater merchantDataInflater;
   private MerchantWorkingHoursInflater merchantHoursInflater;
   private MerchantInflater merchantInfoInflater;
   private Merchant merchant;

   SweetAlertDialog errorDialog;

   @Override
   public DtlDetailsPresenter createPresenter() {
      return new DtlDetailsPresenterImpl(getContext(), injector, getPath().getMerchant(), getPath().getPreExpandOffers());
   }

   @Override
   protected void onPostAttachToWindowView() {
      inflateToolbarMenu(toolbar);

      toolbar.setNavigationIcon(ViewUtils.isTabletLandscape(getContext()) ? R.drawable.back_icon_black : R.drawable.back_icon);
      toolbar.setNavigationOnClickListener(view -> back());

      activityResultDelegate.addListener(this);

      merchantHoursInflater = new MerchantWorkingHoursInflater(injector);
      merchantDataInflater = new MerchantOffersInflater(injector);
      merchantInfoInflater = new MerchantInfoInflater(injector, true);

      merchantDataInflater.registerOfferClickListener(offer -> getPresenter().onOfferClick(offer));
      merchantDataInflater.setView(this);
      merchantInfoInflater.setView(this);
      merchantHoursInflater.setView(this);
      showMessage();

      mContainerComments.loadFirstPage();
   }

   @Override
   public void addNoCommentsAndReviews() {
      mContainerComments.showNoComments();
   }

   @Override
   public void addCommentsAndReviews(float ratingMerchant, int countReview, ArrayList<ReviewObject> listReviews) {
      Bundle bundle = new Bundle();
      bundle.putParcelableArrayList(OfferWithReviewView.ARRAY, listReviews);
      bundle.putFloat(OfferWithReviewView.RATING_MERCHANT, ratingMerchant);
      bundle.putInt(OfferWithReviewView.COUNT_REVIEW, countReview);
      bundle.putString(OfferWithReviewView.MERCHANT_NAME, merchant.displayName());
      bundle.putBoolean(OfferWithReviewView.IS_FROM_LIST_REVIEW, false);
      mContainerComments.resetViewData();
      mContainerComments.loadData(bundle);
      mContainerComments.removeLoadingActions();
   }

   @Override
   public void setTextRateAndReviewButton(int size) {
      mTvReadAllReviews.setText(String.format(getContext().getResources()
            .getString(R.string.total_reviews_text), size));
   }

   @Override
   public void userHasPendingReview() {
      errorDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.NORMAL_TYPE);
      errorDialog.setTitleText(getActivity().getString(R.string.app_name));
      errorDialog.setContentText(getContext().getString(R.string.text_awaiting_approval_review));
      errorDialog.setConfirmText(getActivity().getString(R.string.apptentive_ok));
      errorDialog.showCancelButton(true);
      errorDialog.setConfirmClickListener(listener -> listener.dismissWithAnimation());
      errorDialog.show();
   }

   @OnClick(R.id.tv_read_all_review)
   public void onClickReadAllReviews() {
      if (!mTvReadAllReviews.getText().toString().isEmpty()) getPresenter().showAllReviews();
   }

   @OnClick(R.id.layout_rating_reviews_detail)
   void onClickRatingsReview() {
      if (!deviceInfoProvider.isTablet()) getPresenter().onClickRatingsReview(merchant);
   }

   @OnClick(R.id.order_from_menu)
   void orderFromMenu() {
      SweetAlertDialog openOrderFromMenuDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.NORMAL_TYPE);
      openOrderFromMenuDialog.setTitle(R.string.alert);
      openOrderFromMenuDialog.setContentText(getContext().getString(R.string.open_order_from_menu_msg));
      openOrderFromMenuDialog.setConfirmText(getActivity().getString(R.string.ok));
      openOrderFromMenuDialog.setCancelText(getActivity().getString(R.string.cancel));
      openOrderFromMenuDialog.showCancelButton(true);
      openOrderFromMenuDialog.setConfirmClickListener(listener -> {
         listener.dismiss();
         getPresenter().orderFromMenu();
      });
      openOrderFromMenuDialog.show();
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
   public void setMerchant(Merchant merchant) {
      this.merchant = merchant;
      merchantDataInflater.applyMerchantAttributes(merchant.asMerchantAttributes());
      merchantInfoInflater.applyMerchantAttributes(merchant.asMerchantAttributes());
      merchantHoursInflater.applyMerchantAttributes(merchant.asMerchantAttributes());

      if (ViewUtils.isTabletLandscape(getContext()))
         ((TextView) toolbar.findViewById(R.id.tv_merchant_name_title)).setText(merchant.displayName());
      else
         toolbar.setTitle(merchant.displayName());

      setContacts();
      setLocation();
      setClicks();
      setReviews();
      setRatingAndPerk();
      setOffersSection();
      setThrstFlow();
   }

   @Override
   public void setupMap() {
      GoogleMapOptions mapOptions = new GoogleMapOptions();
      mapOptions.liteMode(true);

      MapFragment mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentByTag(MAP_TAG);
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
   public void expandOffers(List<String> offers) {
      merchantDataInflater.expandOffers(offers);
   }

   @Override
   public void expandHoursView() {
      merchantHoursInflater.preexpand();
   }

   @Override
   public List<String> getExpandedOffersIds() {
      return merchantDataInflater.getExpandedOffers();
   }

   @Override
   public boolean isHoursViewExpanded() {
      return merchantHoursInflater != null && merchantHoursInflater.isViewExpanded();
   }

   private void showMessage() {
      String message = getPath().getMessage();
      if (message != null && message.length() > 0) {
         Snackbar.make(mContainerComments, message, Snackbar.LENGTH_LONG).show();
      }
   }

   private void setContacts() {
      Queryable.from(MerchantHelper.getContactsData(getContext(), merchant))
            .filter(contact -> contact.type != ImageTextItem.Type.ADDRESS)
            .forEachR(contact -> {
               TextView contactView = inflateContactView();
               contactView.setCompoundDrawablesWithIntrinsicBounds(contact.icon, null, null, null);
               contactView.setText(contact.text);

               if (MerchantHelper.contactCanBeResolved(contact, getActivity())) RxView.clicks(contactView)
                     .compose(RxLifecycleAndroid.bindView(contactView))
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
      if (contact.type.equals(ImageTextItem.Type.ADDRESS)) getPresenter().routeToMerchantRequested(contact.intent);
      else getContext().startActivity(contact.intent);
   }

   public void bindMap(GoogleMap map) {
      Preconditions.checkNotNull(merchant, "set merchant before binding info inside map");

      int paddingX = getContext().getResources().getDimensionPixelOffset(R.dimen.spacing_large);
      int paddingY = getContext().getResources().getDimensionPixelOffset(R.dimen.spacing_normal);
      LatLng pos = new LatLng(merchant.coordinates().lat(), merchant.coordinates().lng());

      map.getUiSettings().setMapToolbarEnabled(false);
      map.setPadding(paddingX, paddingY, paddingX, paddingY);
      map.addMarker(new MarkerOptions().position(pos).icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_pin)));
      map.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, MERCHANT_MAP_ZOOM));
   }

   private void setClicks() {
      View earn = ButterKnife.findById(this, R.id.merchant_details_earn);
      View pay = ButterKnife.findById(this, R.id.merchant_details_pay);
      View estimate = ButterKnife.findById(this, R.id.merchant_details_estimate_points);

      if (earn != null)
         RxView.clicks(earn).compose(RxLifecycleAndroid.bindView(this)).subscribe(aVoid -> getPresenter().onCheckInClicked());
      if (estimate != null) RxView.clicks(estimate)
            .compose(RxLifecycleAndroid.bindView(this))
            .subscribe(aVoid -> getPresenter().onEstimationClick());
      if (pay != null)
         RxView.clicks(pay).compose(RxLifecycleAndroid.bindView(this)).subscribe(aVoid -> getPresenter().onClickPay());
   }

   private void setReviews() {
      getPresenter().addNewComments(merchant);
   }

   private void setRatingAndPerk() {
      if (merchant.reviews() != null) {
         String stringTotal = merchant.reviews().total();
         if (mRatingBar != null && merchant.reviews() != null
               && stringTotal != null && !stringTotal.isEmpty()
               && Integer.parseInt(merchant.reviews().total()) > 0) {
            mRatingBar.setRating(Float.parseFloat(merchant.reviews().ratingAverage()));
            textViewRating.setText(ViewUtils.getLabelReviews(
                  Integer.parseInt(merchant.reviews().total()),
                  getContext().getResources().getString(R.string.format_review_text),
                  getContext().getResources().getString(R.string.format_reviews_text)));
         }
      }
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
      router.moveTo(Route.ENROLL_MERCHANT, NavigationConfigBuilder.forActivity().data(data).build());
   }

   @Override
   public void openTransaction(Merchant merchant, DtlTransaction dtlTransaction) {
      router.moveTo(
            merchant.useThrstFlow() ? Route.DTL_THRST_SCAN_RECEIPT : Route.DTL_SCAN_RECEIPT,
            NavigationConfigBuilder.forActivity()
                  .data(new MerchantBundle(merchant))
                  .build()
      );
   }

   @Override
   public void showSucceed(Merchant merchant, DtlTransaction dtlTransaction) {
      router.moveTo(Route.DTL_TRANSACTION_SUCCEED, NavigationConfigBuilder.forDialog()
            .data(new MerchantBundle(merchant))
            .build());
   }

   @Override
   public void showThrstSucceed(Merchant merchant, String earnedPoints, String totalPoints) {
      router.moveTo(Route.DTL_THRST_TRANSACTION_SUCCEED, NavigationConfigBuilder.forDialog()
            .data(new ThrstPaymentCompletedBundle(merchant, earnedPoints, totalPoints))
            .build());
   }

   @Override
   public void setTransaction(DtlTransaction dtlTransaction, boolean isThrstTransaction) {
      Button earn = ButterKnife.findById(this, R.id.merchant_details_earn);
      TextView checkedIn = ButterKnife.findById(this, R.id.checked_in);

      if (earn != null) earn.setText(dtlTransaction != null ? thrstFlow(earn) : getTextNormalFlow(earn));
      if (!isThrstTransaction){
         if (checkedIn != null) ViewUtils.setViewVisibility(checkedIn, dtlTransaction != null ? View.VISIBLE : View.GONE);
      }
   }

   private int getTextNormalFlow(Button earn) {
      ViewUtils.setTextAppearance(getContext(), earn, R.style.DtlButtonGreenTheme);
      return R.string.dtl_check_in;
   }

   private int thrstFlow(Button earn) {
      ViewUtils.setTextAppearance(getContext(), earn, R.style.DtlButtonPurpleTheme);
      return R.string.dtl_merchant_earn_points;
   }

   @Override
   public void share(Merchant merchant) {
      new ShareDialog(getContext(), type -> {
         ShareBundle shareBundle = MerchantHelper.buildShareBundle(getContext(), merchant, type);

         getPresenter().trackSharing(type);

         router.moveTo(Route.SHARE, NavigationConfigBuilder.forActivity().data(shareBundle).build());
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

   @OnClick(R.id.btn_rate_and_review)
   void onClickRateView() {
      getPresenter().onClickRateView();
   }

   @Override
   public void enableCheckinAndPayButtons() {
      View earn = ButterKnife.findById(this, R.id.merchant_details_earn);
      if (earn != null) earn.setEnabled(true);
      View pay = ButterKnife.findById(this, R.id.merchant_details_pay);
      if (pay != null) pay.setEnabled(true);
   }

   @Override
   public void disableCheckinAndPayButtons() {
      View earn = ButterKnife.findById(this, R.id.merchant_details_earn);
      if (earn != null) earn.setEnabled(false);
      View pay = ButterKnife.findById(this, R.id.merchant_details_pay);
      if (pay != null) pay.setEnabled(false);
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

   @Override
   public void showThrstFlowButton() {
      View payBtn = ButterKnife.findById(this, R.id.merchant_details_pay);
      if (null == payBtn) return;
      payBtn.setVisibility(VISIBLE);
   }

   @Override
   public void showEarnFlowButton() {
      View earnBtn = ButterKnife.findById(this, R.id.merchant_details_earn);
      if (null == earnBtn) return;
      earnBtn.setVisibility(VISIBLE);
   }

   @Override
   public void hideEarnFlowButton(){
      View payBtn = ButterKnife.findById(this, R.id.merchant_details_earn);
      if (payBtn != null) payBtn.setVisibility(View.GONE);
   }

   @Override
   public AppCompatActivity getActivity() {
      return super.getActivity();
   }

   @Override
   public void showOrderFromMenu() {
      orderFromMenuBtn.setVisibility(VISIBLE);
      orderFromMenuDivider.setVisibility(VISIBLE);
   }

   @Override
   public void hideOrderFromMenu() {
      orderFromMenuBtn.setVisibility(GONE);
      orderFromMenuDivider.setVisibility(GONE);
   }

   private void setOffersSection() {
      if (!merchant.asMerchantAttributes().hasOffers()) {
         ViewUtils.setViewVisibility(this.perks, View.GONE);
         ViewUtils.setViewVisibility(this.points, View.GONE);
         ViewUtils.setViewVisibility(this.payInApp, View.GONE);
      } else {
         ViewUtils.setViewVisibility(this.perks, View.VISIBLE);
         ViewUtils.setViewVisibility(this.points, View.VISIBLE);
         int perksNumber = merchant.asMerchantAttributes().offersCount(OfferType.PERK);
         setOfferBadges(perksNumber, merchant.asMerchantAttributes().offers().size() - perksNumber);
      }
   }

   private void setOfferBadges(int perks, int points) {
      int perkVisibility = perks > 0 ? View.VISIBLE : View.GONE;
      int pointVisibility = points > 0 ? View.VISIBLE : View.GONE;

      ViewUtils.setViewVisibility(this.perks, perkVisibility);
      ViewUtils.setViewVisibility(this.points, pointVisibility);

      if (perkVisibility == View.VISIBLE) this.perks.setText(getContext().getString(R.string.perks_formatted, perks));

      if (merchant.asMerchantAttributes() == null) return;

      if (merchant.asMerchantAttributes().useThrstFlow()) {
         ViewUtils.setViewVisibility(this.payInApp, View.VISIBLE);
         ViewUtils.setViewVisibility(this.points, View.GONE);
      } else {
         ViewUtils.setViewVisibility(this.payInApp, View.GONE);
         ViewUtils.setViewVisibility(this.points, View.VISIBLE);
      }
   }

   private void setThrstFlow() {
      getPresenter().setThrstFlow();
      getPresenter().setupFullThrstBtn();
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

   @Override
   public void showAllReviews() {
   }

   @Override
   public void hideReviewViewsOnTablets() {
      rateAndReviewBtn.setVisibility(View.GONE);
   }

   /**
    * Allows to manage back preventing multiple instances of this screen,
    * when going back we only need 1 item in the story so we can remove other elements
    *
    * @return
    */
   private boolean back() {
      History history = Flow.get(getContext()).getHistory();
      History.Builder builder = history.buildUpon();
      int screensToDelete = history.size() > 1 ? history.size() - 1 : 0;
      for (int i = 0; i < screensToDelete; i++) {
         builder.pop();
      }
      Flow.get(getContext()).setHistory(builder.build(), Flow.Direction.BACKWARD);
      return true;
   }

   @Override
   public boolean isTablet() {
      return ViewUtils.isTablet(getContext());
   }
}
