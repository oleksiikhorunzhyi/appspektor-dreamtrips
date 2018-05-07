package com.worldventures.dreamtrips.modules.dtl.service.action.http;

import android.content.Context;

import com.worldventures.core.model.User;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.dreamtrips.api.dtl.merchants.AddReviewHttpAction;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.CommentReview;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.PostReviewActionParams;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.PostReviewParamsAdapter;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.UserReviewInfoProvider;
import com.worldventures.dreamtrips.modules.dtl.service.action.http.error.PostReviewErrorAdapter;
import com.worldventures.dreamtrips.modules.dtl.service.action.http.error.exception.DuplicatePostException;
import com.worldventures.dreamtrips.modules.dtl.service.action.http.error.exception.ProfanityPostException;
import com.worldventures.dreamtrips.modules.dtl.service.action.http.error.exception.RequestLimitException;
import com.worldventures.dreamtrips.modules.dtl.service.action.http.error.exception.UnknownPostException;
import com.worldventures.dreamtrips.modules.dtl.service.action.http.error.exception.UnrecognizedException;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.storage.ReviewStorage;
import com.worldventures.janet.injection.InjectableAction;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class PostReviewHttpCommand extends Command<CommentReview> implements InjectableAction {

   @Inject Janet janet;
   @Inject MapperyContext mapperyContext;
   @Inject UserReviewInfoProvider userReviewInfoProvider;

   @Inject Context context;
   @Inject SessionHolder appSessionHolder;

   private final PostReviewActionParams postReviewActionParams;

   public PostReviewHttpCommand(PostReviewActionParams postReviewActionParams) {
      this.postReviewActionParams = postReviewActionParams;
   }

   @Override
   protected void run(CommandCallback<CommentReview> callback) throws Throwable {
      janet.createPipe(AddReviewHttpAction.class)
            .createObservableResult(createAction())
            .map(AddReviewHttpAction::response)
            .map(review -> mapperyContext.convert(review, CommentReview.class))
            .doOnNext(this::throwIfError)
            .doOnNext(ignore -> saveReview())
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private AddReviewHttpAction createAction() throws Exception {
      final PostReviewParamsAdapter adapter = new PostReviewParamsAdapter(postReviewActionParams, userReviewInfoProvider);
      return new AddReviewHttpAction(adapter.reviewParams(),
            adapter.comment(),
            adapter.rating(),
            adapter.verified(),
            adapter.fingerprint(),
            adapter.ipAddress(),
            adapter.attachments());
   }

   private void saveReview() {
      User user = appSessionHolder.get().get().user();
      ReviewStorage.saveReviewsPosted(context, String.valueOf(user.getId()), postReviewActionParams.productId());
   }

   private void throwIfError(CommentReview commentReview) {
      final PostReviewErrorAdapter adapter = new PostReviewErrorAdapter(commentReview);
      if (!adapter.isHaveError()) {
         return;
      }

      switch (adapter.errorReason()) {
         case PROFANITY:
            throw new ProfanityPostException();
         case DUPLICATED:
            throw new DuplicatePostException();
         case REQUESTS_LIMIT:
            throw new RequestLimitException();
         case UNKNOWN:
            throw new UnknownPostException();
         default:
            throw new UnrecognizedException();
      }
   }
}

