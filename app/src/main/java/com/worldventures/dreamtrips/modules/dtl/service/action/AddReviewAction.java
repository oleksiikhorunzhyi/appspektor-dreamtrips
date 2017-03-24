package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.dreamtrips.api.dtl.merchants.AddReviewHttpAction;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.RequestReviewParams;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.ReviewParams;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.CommentReview;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.Review;
import com.worldventures.dreamtrips.modules.dtl.service.action.creator.ReviewsActionCreator;
import javax.inject.Inject;
import javax.inject.Named;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class AddReviewAction extends Command<Review> implements InjectableAction {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;
   @Inject MapperyContext mapperyContext;
   @Inject ReviewsActionCreator reviewsActionCreator;

   private final RequestReviewParams actionParams;
   private final ReviewParams reviewParams;

   public static AddReviewAction create(RequestReviewParams params, ReviewParams reviewParams) {
      return new AddReviewAction(params, reviewParams);
   }

   public AddReviewAction(RequestReviewParams params, ReviewParams reviewParams) {
      this.actionParams = params;
      this.reviewParams = reviewParams;
   }

   @Override
   protected void run(CommandCallback<CommentReview> callback) throws Throwable {
      callback.onProgress(0);
      janet.createPipe(AddReviewHttpAction.class)
            .createObservableResult(new AddReviewHttpAction(actionParams, reviewParams))
            .map(AddReviewHttpAction::response)
            .map(attributes -> mapperyContext.convert(attributes, CommentReview.class))
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
