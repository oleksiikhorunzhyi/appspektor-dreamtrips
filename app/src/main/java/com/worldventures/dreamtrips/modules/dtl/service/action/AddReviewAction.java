package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.dtl.merchants.AddReviewHttpAction;
import com.worldventures.dreamtrips.api.dtl.merchants.model.Errors;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.RequestReviewParams;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.ReviewParams;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.CommentReview;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.Review;
import com.worldventures.dreamtrips.modules.dtl.service.action.creator.ReviewsActionCreator;
import com.worldventures.dreamtrips.modules.trips.view.cell.filter.BoolCell;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.body.FileBody;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.http.annotations.Part;
import io.techery.mappery.MapperyContext;

@CommandAction
public class AddReviewAction extends Command<CommentReview> implements InjectableAction {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;
   @Inject MapperyContext mapperyContext;
   @Inject ReviewsActionCreator reviewsActionCreator;

   private final RequestReviewParams actionParams;
   private final String userEmail;
   private final String userNickName;
   private final String reviewText;
   private final String rating;
   private final Boolean verified;
   private final String userId;
   private final String deviceFingerprint;
   private final String authorIpAddress;

   public static AddReviewAction create(RequestReviewParams params, String userEmail, String userNickName, String reviewText,
                                       String rating, Boolean verified, String userId, String deviceFingerprint, String authorIpAddress) {
      return new AddReviewAction(params, userEmail, userNickName, reviewText,
            rating, verified, userId, deviceFingerprint, authorIpAddress);
   }

   public AddReviewAction(RequestReviewParams params, String userEmail, String userNickName, String reviewText,
         String rating, Boolean verified, String userId, String deviceFingerprint, String authorIpAddress) {
      this.actionParams = params;
      this.userEmail = userEmail;
      this.userNickName = userNickName;
      this.reviewText = reviewText;
      this.rating = rating;
      this.verified = verified;
      this.userId = userId;
      this.deviceFingerprint = deviceFingerprint;
      this.authorIpAddress = authorIpAddress;
   }

   @Override
   protected void run(CommandCallback<CommentReview> callback) throws Throwable {
      callback.onProgress(0);
      janet.createPipe(AddReviewHttpAction.class)
            .createObservableResult(new AddReviewHttpAction(actionParams, getFile(), userEmail, userNickName, reviewText,
                  rating, verified.toString(), userId, deviceFingerprint, authorIpAddress))
            .map(AddReviewHttpAction::response)
            .map(attributes -> mapperyContext.convert(attributes, CommentReview.class))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   public File getFile() {
      return new File("/mnt/sdcard/DCIM/Camera/IMG_20170405_100106.jpg");
   }
}
