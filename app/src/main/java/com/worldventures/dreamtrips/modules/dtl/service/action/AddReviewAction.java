package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.dreamtrips.api.dtl.merchants.AddReviewHttpAction;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.RequestReviewParams;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.common.model.BasePhotoPickerModel;
import com.worldventures.dreamtrips.modules.common.presenter.BasePickerPresenter;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.CommentReview;
import com.worldventures.dreamtrips.modules.dtl.service.action.creator.ReviewsActionCreator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
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

      List<BasePhotoPickerModel> selectedImagesList = getSelectedImagesList();

      janet.createPipe(AddReviewHttpAction.class)
            .createObservableResult(new AddReviewHttpActionBuilder().setActionParams(actionParams)
                  .setEmail(userEmail)
                  .setNickname(userNickName)
                  .setReviewText(reviewText)
                  .setRating(rating)
                  .setVerified(verified.toString())
                  .setUserId(userId)
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
      private String email;
      private String nickName;
      private String reviewText;
      private String rating;
      private String verified;
      private String userId;
      private String deviceFingerPrint;
      private String authorIpAddress;
      private List<File> files = new ArrayList<>();

      public AddReviewHttpActionBuilder setActionParams(RequestReviewParams actionParams) {
         this.actionParams = actionParams;
         return this;
      }

      public AddReviewHttpActionBuilder setEmail(String email) {
         this.email = email;
         return this;
      }

      public AddReviewHttpActionBuilder setNickname(String nickName) {
         this.nickName = nickName;
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

      public AddReviewHttpActionBuilder setUserId(String userId) {
         this.userId = userId;
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

      public AddReviewHttpActionBuilder addFiles(List<BasePhotoPickerModel> imagesList) {
         for (BasePhotoPickerModel photo : imagesList) {
            files.add(new File(photo.getAbsolutePath()));
         }
         return this;
      }

      public AddReviewHttpAction build() throws IOException {
         AddReviewHttpAction action = null;

         if (files.size() == 1) {
            action = new AddReviewHttpAction(this.actionParams, this.email, this.nickName, this.reviewText,
                  this.rating, this.verified, this.userId, this.deviceFingerPrint, this.authorIpAddress, files.get(0));
         } else if (files.size() == 2) {
            action = new AddReviewHttpAction(this.actionParams, this.email, this.nickName, this.reviewText,
                  this.rating, this.verified, this.userId, this.deviceFingerPrint, this.authorIpAddress, files.get(0), files
                  .get(1));
         } else if (files.size() == 3) {
            action = new AddReviewHttpAction(this.actionParams, this.email, this.nickName, this.reviewText,
                  this.rating, this.verified, this.userId, this.deviceFingerPrint, this.authorIpAddress, files.get(0), files
                  .get(1), files.get(2));
         } else if (files.size() == 4) {
            action = new AddReviewHttpAction(this.actionParams, this.email, this.nickName, this.reviewText,
                  this.rating, this.verified, this.userId, this.deviceFingerPrint, this.authorIpAddress, files.get(0), files
                  .get(1), files.get(2), files.get(3));
         } else if (files.size() == 5) {
            action = new AddReviewHttpAction(this.actionParams, this.email, this.nickName, this.reviewText,
                  this.rating, this.verified, this.userId, this.deviceFingerPrint, this.authorIpAddress, files.get(0), files
                  .get(1), files.get(2), files.get(3), files.get(4));
         }
         return action;
      }

   }

   private List<BasePhotoPickerModel> getSelectedImagesList() {
      return BasePickerPresenter.getSelectedImagesList();
   }

}
