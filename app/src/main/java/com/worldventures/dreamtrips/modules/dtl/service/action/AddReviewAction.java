package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.dreamtrips.api.dtl.merchants.AddReviewHttpAction;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.RequestReviewParams;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.CommentReview;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.fragments.PhotoReviewCreationItem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class AddReviewAction extends Command<CommentReview> implements InjectableAction {

   @Inject Janet janet;
   @Inject MapperyContext mapperyContext;

   private final RequestReviewParams actionParams;
   private final String reviewText;
   private final String rating;
   private final Boolean verified;
   private final String deviceFingerprint;
   private final String authorIpAddress;
   private final List<PhotoReviewCreationItem> selectedImagesList;

   public static AddReviewAction create(RequestReviewParams params, String reviewText,
         String rating, Boolean verified, String deviceFingerprint, String authorIpAddress, List<PhotoReviewCreationItem> selectedImagesList) {
      return new AddReviewAction(params, reviewText,
            rating, verified, deviceFingerprint, authorIpAddress, selectedImagesList);
   }

   public AddReviewAction(RequestReviewParams params, String reviewText,
         String rating, Boolean verified, String deviceFingerprint, String authorIpAddress, List<PhotoReviewCreationItem> selectedImagesList) {
      this.actionParams = params;
      this.reviewText = reviewText;
      this.rating = rating;
      this.verified = verified;
      this.deviceFingerprint = deviceFingerprint;
      this.authorIpAddress = authorIpAddress;
      this.selectedImagesList = selectedImagesList;
   }

   @Override
   protected void run(CommandCallback<CommentReview> callback) throws Throwable {
      callback.onProgress(0);

      janet.createPipe(AddReviewHttpAction.class)
            .createObservableResult(new AddReviewHttpActionBuilder().setActionParams(actionParams)
                  .setReviewText(reviewText)
                  .setRating(rating)
                  .setVerified(verified.toString())
                  .setDeviceFingerPrint(deviceFingerprint)
                  .setAuthorIpAddress(authorIpAddress)
                  .addFiles(selectedImagesList)
                  .build())
            .map(AddReviewHttpAction::response)
            .map(attributes -> mapperyContext.convert(attributes, CommentReview.class))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   //TODO Refactor AddReviewHttpAction to support constructor with  ellipsis instead of overload
   private class AddReviewHttpActionBuilder {
      private RequestReviewParams actionParams;
      private String reviewText;
      private String rating;
      private String verified;
      private String deviceFingerPrint;
      private String authorIpAddress;
      private List<File> files = new ArrayList<>();

      public AddReviewHttpActionBuilder setActionParams(RequestReviewParams actionParams) {
         this.actionParams = actionParams;
         return this;
      }

      public AddReviewHttpActionBuilder setReviewText(String reviewText) {
         this.reviewText = reviewText;
         return this;
      }

      public AddReviewHttpActionBuilder setRating(String rating) {
         this.rating = rating;
         return this;
      }

      public AddReviewHttpActionBuilder setVerified(String verified) {
         this.verified = verified;
         return this;
      }

      public AddReviewHttpActionBuilder setDeviceFingerPrint(String deviceFingerPrint) {
         this.deviceFingerPrint = deviceFingerPrint;
         return this;
      }

      public AddReviewHttpActionBuilder setAuthorIpAddress(String authorIpAddress) {
         this.authorIpAddress = authorIpAddress;
         return this;
      }

      public AddReviewHttpActionBuilder addFiles(List<PhotoReviewCreationItem> imagesList) {
         if (imagesList != null) {
            for (PhotoReviewCreationItem photo : imagesList) {
               files.add(new File(photo.getFilePath()));
            }
         }
         return this;
      }

      public AddReviewHttpAction build() throws IOException {
         return new AddReviewHttpAction(this.actionParams, this.reviewText,
               this.rating, this.verified, this.deviceFingerPrint, this.authorIpAddress, files);
      }
   }
}
