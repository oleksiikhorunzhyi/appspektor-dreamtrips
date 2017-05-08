package com.worldventures.dreamtrips.modules.dtl_flow.parts.details;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.PhotoUploadingManagerS3;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.session.acl.FeatureManager;
import com.worldventures.dreamtrips.modules.common.model.ShareType;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.dtl.analytics.CheckinEvent;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;
import com.worldventures.dreamtrips.modules.dtl.analytics.MerchantDetailsViewCommand;
import com.worldventures.dreamtrips.modules.dtl.analytics.MerchantDetailsViewEvent;
import com.worldventures.dreamtrips.modules.dtl.analytics.MerchantMapDestinationEvent;
import com.worldventures.dreamtrips.modules.dtl.analytics.PointsEstimatorViewEvent;
import com.worldventures.dreamtrips.modules.dtl.analytics.ShareEventProvider;
import com.worldventures.dreamtrips.modules.dtl.analytics.SuggestMerchantEvent;
import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantIdBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.PointsEstimationDialogBundle;
import com.worldventures.dreamtrips.modules.dtl.event.DtlTransactionSucceedEvent;
import com.worldventures.dreamtrips.modules.dtl.event.ToggleMerchantSelectionAction;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.MerchantMedia;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Offer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.Reviews;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.ImmutableDtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.PresentationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlTransactionAction;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowUtil;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.DtlCommentReviewPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.fullscreen_image.DtlFullscreenImagePath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.DtlReviewsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.ReviewObject;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.storage.ReviewStorage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import flow.Flow;
import flow.History;
import flow.path.Path;
import io.techery.janet.helper.ActionStateSubscriber;
import timber.log.Timber;

public class DtlDetailsPresenterImpl extends DtlPresenterImpl<DtlDetailsScreen, DtlMerchantDetailsState> implements DtlDetailsPresenter {

   @Inject FeatureManager featureManager;
   @Inject LocationDelegate locationDelegate;
   @Inject DtlTransactionInteractor transactionInteractor;
   @Inject PhotoUploadingManagerS3 photoUploadingManagerS3;
   @Inject PresentationInteractor presentationInteractor;
   @Inject MerchantsInteractor merchantInteractor;

   @Inject SessionHolder<UserSession> appSessionHolder;

   private final Merchant merchant;
   private User user;
   private final List<String> preExpandOffers;
   private static final int MAX_SIZE_TO_SHOW_BUTTON = 2;

   public DtlDetailsPresenterImpl(Context context, Injector injector, Merchant merchant, List<String> preExpandOffers) {
      super(context);
      injector.inject(this);
      this.merchant = merchant;
      this.preExpandOffers = preExpandOffers;
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();

      analyticsInteractor.dtlAnalyticsCommandPipe()
            .send(new MerchantDetailsViewCommand(new MerchantDetailsViewEvent(merchant.asMerchantAttributes())));
      getView().setMerchant(merchant);
      preExpandOffers();
      tryHideSuggestMerchantButton();
   }

   @Override
   public void onNewViewState() {
      state = new DtlMerchantDetailsState();
   }

   @Override
   public void onSaveInstanceState(Bundle bundle) {
      state.setOffersIds(getView().getExpandedOffersIds());
      state.setHoursViewExpanded(getView().isHoursViewExpanded());
      super.onSaveInstanceState(bundle);
   }

   @Override
   public void onRestoreInstanceState(Bundle instanceState) {
      super.onRestoreInstanceState(instanceState);
      preExpandHours();
   }

   @Override
   public void onVisibilityChanged(int visibility) {
      super.onVisibilityChanged(visibility);
      if (visibility == View.VISIBLE) {
         getView().setupMap();
      }
      if (visibility == View.VISIBLE && merchant.asMerchantAttributes().hasOffers()) {
         processTransaction();
      }
   }

   @Override
   public int getToolbarMenuRes() {
      return R.menu.menu_detailed_merchant;
   }

   @Override
   public boolean onToolbarMenuItemClick(MenuItem item) {
      if (item.getItemId() == R.id.action_share) onShareClick();
      return super.onToolbarMenuItemClick(item);
   }

   protected void preExpandOffers() {
      boolean isRestore = getViewState().getOffersIds() != null;
      final List<String> offers = isRestore ? getViewState().getOffersIds() : this.preExpandOffers;

      getView().expandOffers(offers);
   }

   protected void preExpandHours() {
      if (getViewState().isHoursViewExpanded()) {
         getView().expandHoursView();
      }
   }

   private void tryHideSuggestMerchantButton() {
      boolean repToolsAvailable = featureManager.available(Feature.REP_SUGGEST_MERCHANT);
      if (!merchant.asMerchantAttributes().hasOffers()) {
         getView().setSuggestMerchantButtonAvailable(repToolsAvailable);
      } else processTransaction();
   }

   private void processTransaction() {
      transactionInteractor.transactionActionPipe()
            .createObservable(DtlTransactionAction.get(merchant))
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<DtlTransactionAction>().onFail(apiErrorPresenter::handleActionError)
                  .onSuccess(action -> {
                     DtlTransaction transaction = action.getResult();
                     if (transaction != null) {
                        checkSucceedEvent(transaction);
                        checkTransactionOutOfDate(transaction);
                     }
                     getView().setTransaction(transaction);
                  }));
   }

   private void checkSucceedEvent(DtlTransaction transaction) {
      DtlTransactionSucceedEvent event = EventBus.getDefault().getStickyEvent(DtlTransactionSucceedEvent.class);
      if (event != null) {
         EventBus.getDefault().removeStickyEvent(event);
         getView().showSucceed(merchant, transaction);
      }
   }

   private void checkTransactionOutOfDate(DtlTransaction transaction) {
      if (transaction.isOutOfDate(Calendar.getInstance().getTimeInMillis())) {
         transactionInteractor.transactionActionPipe()
               .createObservable(DtlTransactionAction.delete(merchant))
               .compose(bindViewIoToMainComposer())
               .subscribe(new ActionStateSubscriber<DtlTransactionAction>().onFail(apiErrorPresenter::handleActionError)
                     .onSuccess(action -> getView().setTransaction(action.getResult())));
      }
   }

   @Override
   public void onCheckInClicked() {
      transactionInteractor.transactionActionPipe()
            .createObservable(DtlTransactionAction.get(merchant))
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<DtlTransactionAction>().onFail(apiErrorPresenter::handleActionError)
                  .onSuccess(action -> {
                     if (action.getResult() != null) {
                        DtlTransaction dtlTransaction = action.getResult();
                        if (dtlTransaction.getUploadTask() != null) {
                           photoUploadingManagerS3.cancelUploading(dtlTransaction.getUploadTask());
                        }
                        transactionInteractor.transactionActionPipe().send(DtlTransactionAction.clean(merchant));
                        getView().openTransaction(merchant, dtlTransaction);
                     } else {
                        getView().disableCheckinButton();
                        locationDelegate.requestLocationUpdate()
                              .compose(bindViewIoToMainComposer())
                              .subscribe(this::onLocationObtained, this::onLocationError);
                     }
                  }));
   }

   @Override
   public void locationNotGranted() {
      getView().enableCheckinButton();
      getView().informUser(R.string.dtl_checkin_location_error);
   }

   private void onLocationError(Throwable e) {
      if (e instanceof LocationDelegate.LocationException)
         getView().locationResolutionRequired(((LocationDelegate.LocationException) e).getStatus());
      else {
         locationNotGranted();
         Timber.e(e, "Something went wrong while location update");
      }
   }

   private void onLocationObtained(Location location) {
      getView().enableCheckinButton();

      DtlTransaction dtlTransaction = ImmutableDtlTransaction.builder()
            .lat(location.getLatitude())
            .lng(location.getLongitude())
            .build();
      transactionInteractor.transactionActionPipe().send(DtlTransactionAction.save(merchant, dtlTransaction));

      getView().setTransaction(dtlTransaction);

      analyticsInteractor.dtlAnalyticsCommandPipe()
            .send(DtlAnalyticsCommand.create(new CheckinEvent(merchant.asMerchantAttributes(), location)));
   }

   @Override
   public void onEstimationClick() {
      getView().showEstimationDialog(new PointsEstimationDialogBundle(merchant));
   }

   @Override
   public void onMerchantClick() {
      analyticsInteractor.dtlAnalyticsCommandPipe()
            .send(DtlAnalyticsCommand.create(new SuggestMerchantEvent(merchant.asMerchantAttributes())));
      getView().openSuggestMerchant(new MerchantIdBundle(merchant.id()));
   }

   @Override
   public void onOfferClick(Offer offer) {
      MerchantMedia imageUrl = Queryable.from(offer.images()).firstOrDefault();
      if (imageUrl == null) return;
      Flow.get(getContext()).set(new DtlFullscreenImagePath(imageUrl.getImagePath()));
   }

   @Override
   public void onBackPressed() {
      presentationInteractor.toggleSelectionPipe().send(ToggleMerchantSelectionAction.clear());
   }

   @Override
   public void showAllReviews() {
      Flow.get(getContext()).set(new DtlReviewsPath(merchant, ""));
   }

   @Override
   public void addNewComments(Merchant merchant) {
      //List Review have not to be null
      Reviews reviews = merchant.reviews();
      if (reviews != null && !reviews.total().isEmpty()) {
         ArrayList<ReviewObject> listReviews = ReviewObject.getReviewList(reviews.reviews());
         if (listReviews != null && !listReviews.isEmpty()) {
            //Business logic: If the size is equals than 0, so we need to show an screen without info
            int countReview = Integer.parseInt(reviews.total());
            float ratingMerchant = Float.parseFloat(reviews.ratingAverage());
            if (getView() != null) {
               if (countReview == 0) {
                  getView().addNoCommentsAndReviews();
               } else if (countReview > MAX_SIZE_TO_SHOW_BUTTON) {
                  //If list size is major or equals 3, must be show read all message button
                  getView().addCommentsAndReviews(ratingMerchant, countReview, getListReviewByBusinessRule(listReviews));
                  getView().showButtonAllRateAndReview();
                  getView().setTextRateAndReviewButton(countReview);
               } else {
                  //if it doesn't, only show the comment in the same screen
                  getView().addCommentsAndReviews(ratingMerchant, countReview, listReviews);
                  getView().hideButtonAllRateAndReview();
               }
            }
         }
      }
   }

   @Override
   public void onClickRatingsReview(Merchant merchant) {
      if (!merchant.reviews().total().isEmpty() && Integer.parseInt(merchant.reviews().total()) > 0) {
         Flow.get(getContext()).set(new DtlReviewsPath(merchant, ""));
      } else {
         Path path = new DtlCommentReviewPath(merchant);
         History.Builder historyBuilder = Flow.get(getContext()).getHistory().buildUpon();
         historyBuilder.pop();
         historyBuilder.pop();
         historyBuilder.push(path);
         Flow.get(getContext()).setHistory(historyBuilder.build(), Flow.Direction.FORWARD);
      }
   }

   @Override
   public void onClickRateView() {
      this.user = appSessionHolder.get().get().getUser();
      if (ReviewStorage.exists(getContext(), String.valueOf(user.getId()), merchant.id())) {
         getView().userHasPendingReview();
      } else {
         Flow.get(getContext()).set(new DtlCommentReviewPath(merchant));
      }
   }

   private ArrayList<ReviewObject> getListReviewByBusinessRule(@NonNull ArrayList<ReviewObject> reviews) {
      ArrayList<ReviewObject> newListReviews = new ArrayList<>();
      for (int i = 0; i < MAX_SIZE_TO_SHOW_BUTTON; i++) {
         newListReviews.add(reviews.get(i));
      }
      return newListReviews;
   }

   public void onShareClick() {
      getView().share(merchant);
   }

   @Override
   public void trackSharing(@ShareType String type) {
      analyticsInteractor.dtlAnalyticsCommandPipe()
            .send(DtlAnalyticsCommand.create(
                  ShareEventProvider.provideMerchantShareEvent(merchant.asMerchantAttributes(), type)));
   }

   @Override
   public void trackPointEstimator() {
      analyticsInteractor.dtlAnalyticsCommandPipe()
            .send(DtlAnalyticsCommand.create(new PointsEstimatorViewEvent(merchant.asMerchantAttributes())));
   }

   @Override
   public void routeToMerchantRequested(@Nullable final Intent intent) {
      locationDelegate.getLastKnownLocation().compose(bindViewIoToMainComposer()).subscribe(location -> {
         analyticsInteractor.dtlAnalyticsCommandPipe()
               .send(DtlAnalyticsCommand.create(new MerchantMapDestinationEvent(location, merchant)));
         getView().showMerchantMap(intent);
      }, e -> {
         analyticsInteractor.dtlAnalyticsCommandPipe()
               .send(DtlAnalyticsCommand.create(new MerchantMapDestinationEvent(null, merchant)));
         getView().showMerchantMap(intent);
      });
   }
}
