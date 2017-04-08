package com.worldventures.dreamtrips.api.dtl.merchants;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.dtl.merchants.model.CommentReview;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.RequestReviewParams;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.ReviewParams;

import java.io.File;
import java.io.IOException;

import io.techery.janet.body.FileBody;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Part;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Response;

import static io.techery.janet.http.annotations.HttpAction.Type.MULTIPART;

@HttpAction(value = "api/review/v1/reviews?brandId={brandId}&productId={productId}", method = HttpAction.Method.POST, type = MULTIPART)
public class AddReviewHttpAction extends AuthorizedHttpAction {

   @Path("brandId") final String brandId;
   @Path("productId") final String productId;

   @Response
   CommentReview response;

   @Part(value = "photo")
   final FileBody fileBody;

   @Part(value = "userEmail")
   final String userEmail;

   @Part(value = "userNickName")
   final String userNickName;

   @Part(value = "reviewText")
   final String reviewText;

   @Part(value = "rating")
   final String rating;

   @Part(value = "verified")
   final String verified;

   @Part(value = "userId")
   final String userId;

   @Part(value = "deviceFingerprint")
   final String deviceFingerprint;

   @Part(value = "authorIpAddress")
   final String authorIpAddress;


   public AddReviewHttpAction(RequestReviewParams requestReviewParams, File imageFile, String userEmail,
         String userNickName, String reviewText, String rating, String verified, String userId, String deviceFingerprint,
         String authorIpAddress) throws IOException {
      this.brandId = requestReviewParams.brandId();
      this.productId = requestReviewParams.productId();
      this.fileBody = new FileBody("image/*", imageFile);
      this.userEmail = userEmail;
      this.userNickName = userNickName;
      this.reviewText = reviewText;
      this.rating = rating;
      this.verified = verified;
      this.userId = userId;
      this.deviceFingerprint = deviceFingerprint;
      this.authorIpAddress = authorIpAddress;
   }

   public CommentReview response() {
      return response;
   }
}
