package com.worldventures.dreamtrips.modules.dtl.service;

import com.newrelic.agent.android.NewRelic;
import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.ImmutableDtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.service.action.AddReviewAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.FlaggingReviewAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.GetTransactionsCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.LocationCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.MerchantsAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.NewRelicTrackableAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.ReviewMerchantsAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.SendEmailAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.TakeScreenshotAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.TransactionPilotAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.UrlTokenAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.http.PostReviewHttpCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MerchantsInteractor {

   private final DtlLocationInteractor dtlLocationInteractor;
   private final ClearMemoryInteractor clearMemoryInteractor;

   private final ActionPipe<MerchantsAction> thinMerchantsPipe;
   private final ActionPipe<ReviewMerchantsAction> reviewsMerchantsPipe;
   private final ActionPipe<AddReviewAction> addReviewsPipe;
   private final ActionPipe<PostReviewHttpCommand> postReviewPipe;
   private final ActionPipe<FlaggingReviewAction> addFlaggingPipe;
   private final ActionPipe<UrlTokenAction> addUrlTokenPipe;
   private final ActionPipe<TransactionPilotAction> addTransactionPipe;
   private final ActionPipe<GetTransactionsCommand> getTransactionsPipe;
   private final ActionPipe<SendEmailAction> sendEmailPipe;
   private final ActionPipe<TakeScreenshotAction> takeScreenshotPipe;

   public MerchantsInteractor(SessionActionPipeCreator sessionActionPipeCreator, DtlLocationInteractor dtlLocationInteractor,
         ClearMemoryInteractor clearMemoryInteractor) {

      this.dtlLocationInteractor = dtlLocationInteractor;
      this.clearMemoryInteractor = clearMemoryInteractor;

      this.thinMerchantsPipe = sessionActionPipeCreator.createPipe(MerchantsAction.class, Schedulers.io());
      this.reviewsMerchantsPipe = sessionActionPipeCreator.createPipe(ReviewMerchantsAction.class, Schedulers.io());
      this.addReviewsPipe = sessionActionPipeCreator.createPipe(AddReviewAction.class, Schedulers.io());
      this.postReviewPipe = sessionActionPipeCreator.createPipe(PostReviewHttpCommand.class, Schedulers.io());
      this.addFlaggingPipe = sessionActionPipeCreator.createPipe(FlaggingReviewAction.class, Schedulers.io());
      this.addUrlTokenPipe = sessionActionPipeCreator.createPipe(UrlTokenAction.class, Schedulers.io());
      this.addTransactionPipe = sessionActionPipeCreator.createPipe(TransactionPilotAction.class, Schedulers.io());
      this.getTransactionsPipe = sessionActionPipeCreator.createPipe(GetTransactionsCommand.class, Schedulers.io());
      this.sendEmailPipe = sessionActionPipeCreator.createPipe(SendEmailAction.class, Schedulers.io());
      this.takeScreenshotPipe = sessionActionPipeCreator.createPipe(TakeScreenshotAction.class, AndroidSchedulers.mainThread());

      connectNewRelicTracking();
      connectForLocationUpdates();
      connectMemoryClear();
   }

   private void connectMemoryClear() {
      dtlLocationInteractor.locationSourcePipe().observe()
            .subscribe(new ActionStateSubscriber<LocationCommand>()
                  .onStart(action -> clearMemoryInteractor.clearMerchantsMemoryCache()));
   }

   private void connectNewRelicTracking() {
      thinMerchantsPipe.observeSuccess()
            .cast(NewRelicTrackableAction.class)
            .map(NewRelicTrackableAction::getMetricStart)
            .subscribe(startTime ->
                  NewRelic.recordMetric("GetMerchants", "Profiler", System.currentTimeMillis() - startTime));
   }

   private void connectForLocationUpdates() {
      thinMerchantsPipe.observeSuccess()
            .map(MerchantsAction::getResult)
            .filter(thinMerchants -> !thinMerchants.isEmpty())
            .map(thinMerchants -> thinMerchants.get(0))
            .subscribe(thinMerchant -> {
               dtlLocationInteractor.locationSourcePipe().observeSuccessWithReplay()
                     .take(1)
                     .filter(LocationCommand::isResultDefined)
                     .map(LocationCommand::getResult)
                     .filter(dtlLocation -> dtlLocation.locationSourceType() == LocationSourceType.FROM_MAP
                           || dtlLocation.locationSourceType() == LocationSourceType.NEAR_ME)
                     .subscribe(dtlLocation ->
                           dtlLocationInteractor.changeFacadeLocation(buildManualLocation(thinMerchant, dtlLocation))
                     );
            });
   }

   public ActionPipe<MerchantsAction> thinMerchantsHttpPipe() {
      return thinMerchantsPipe;
   }

   public ActionPipe<ReviewMerchantsAction> reviewsMerchantsHttpPipe() {
      return reviewsMerchantsPipe;
   }

   // TODO Remove after new implementation
   public ActionPipe<AddReviewAction> addReviewsHttpPipe() {
      return addReviewsPipe;
   }

   public ActionPipe<PostReviewHttpCommand> reviewHttpPipe() {
      return postReviewPipe;
   }

   public ActionPipe<FlaggingReviewAction> flaggingReviewHttpPipe() {
      return addFlaggingPipe;
   }

   public ActionPipe<UrlTokenAction> urlTokenThrstHttpPipe() {
      return addUrlTokenPipe;
   }

   public ActionPipe<TransactionPilotAction> transactionThrstHttpPipe() {
      return addTransactionPipe;
   }

   public ActionPipe<GetTransactionsCommand> getTransactionsPipe() {
      return getTransactionsPipe;
   }

   public ActionPipe<SendEmailAction> sendEmailPipe() {
      return sendEmailPipe;
   }

   public ActionPipe<TakeScreenshotAction> takeScreenshotPipe() {
      return takeScreenshotPipe;
   }

   private static DtlLocation buildManualLocation(ThinMerchant thinMerchant, DtlLocation dtlLocation) {
      return ImmutableDtlLocation.copyOf(dtlLocation)
            .withLongName(dtlLocation.locationSourceType() == LocationSourceType.FROM_MAP ? thinMerchant.city() : dtlLocation
                  .longName())
            .withAnalyticsName(thinMerchant.asMerchantAttributes().provideAnalyticsName());
   }
}
