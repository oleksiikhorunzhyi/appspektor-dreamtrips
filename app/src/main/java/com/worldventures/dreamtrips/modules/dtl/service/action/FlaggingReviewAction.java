package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.dreamtrips.api.dtl.merchants.AddFlaggingReviewAction;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.SdkFlaggingReviewParams;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.FlaggingReviewParams;
import com.worldventures.dreamtrips.modules.dtl.service.action.creator.FlaggingActionCreator;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class FlaggingReviewAction extends Command<FlaggingReviewParams> implements InjectableAction {

   @Inject Janet janet;
   @Inject MapperyContext mapperyContext;
   @Inject FlaggingActionCreator reviewsActionCreator;

   private String merchantId;
   private final SdkFlaggingReviewParams reviewParams;

   public static FlaggingReviewAction create(String merchantId, SdkFlaggingReviewParams reviewParams) {
      return new FlaggingReviewAction(merchantId, reviewParams);
   }

   public FlaggingReviewAction(String merchantId, SdkFlaggingReviewParams reviewParams) {
      this.merchantId = merchantId;
      this.reviewParams = reviewParams;
   }

   @Override
   protected void run(CommandCallback<FlaggingReviewParams> callback) throws Throwable {
      callback.onProgress(0);
      janet.createPipe(AddFlaggingReviewAction.class)
            .createObservableResult(new AddFlaggingReviewAction(merchantId, reviewParams))
            .map(AddFlaggingReviewAction::getFlaggingResponse)
            .map(attributes -> mapperyContext.convert(attributes, FlaggingReviewParams.class))
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
