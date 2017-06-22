package com.worldventures.dreamtrips.modules.dtl_flow.parts.details;

import android.content.Context;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.api.dtl.merchants.model.MerchantType;
import com.worldventures.dreamtrips.api.dtl.merchants.model.PartnerStatus;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Coordinates;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ImmutableMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ImmutableReview;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ImmutableReviews;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.Review;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ReviewImages;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.Reviews;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

public class DtlDetailsPresenterImplTest {

   private MockDetailPresenter mPresenterDetail;
   private Merchant merchant;

   private static final int DEFAULT_SIZE_REVIEW = 5;

   @Mock Injector injector;
   @Mock Context context;
   @Mock DtlDetailsScreen view;
   @Mock Reviews review;
   @Mock User user;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);
      mPresenterDetail = new MockDetailPresenter(context, injector, getMerchant(), null);
      mPresenterDetail.attachView(view);
   }

   @Test
   public void itShouldNotShowReview() throws Exception {
      initMerchant(false);
      mPresenterDetail.addNewComments(getMerchant());
      Mockito.verify(view).addNoCommentsAndReviews();
   }

   @Test
   public void itShouldShowButtonToSeeAllReviews() throws Exception {
      initMerchant(true, 4);
      mPresenterDetail.addNewComments(getMerchant());
      Mockito.verify(view).showButtonAllRateAndReview();
   }

   @Test
   public void itShouldShowMinimumAllowedReviews() throws Exception {
      initMerchant(true, 2);
      mPresenterDetail.addNewComments(getMerchant());
      Mockito.verify(view).hideButtonAllRateAndReview();
   }

   @Test
   public void itMustShowUserHasPendingReview() throws Exception {
      initMerchant(false);
      mPresenterDetail.setReviewCached(true);
      mPresenterDetail.setUserHasReviews(false);
      mPresenterDetail.onClickRatingsReview(getMerchant());
      Mockito.verify(view).userHasPendingReview();
   }

   private void initMerchant(){
      initMerchant(true);
   }

   private void initMerchant(boolean withReview){
      initMerchant(withReview, DEFAULT_SIZE_REVIEW);
   }

   private void initMerchant(boolean withReview, int countReview) {
      merchant = ImmutableMerchant.builder()
            .id("3d6de8c3-4f04-4845-99a0-8df4d99a4a54")
            .type(MerchantType.BAR)
            .partnerStatus(PartnerStatus.PARTICIPANT)
            .displayName("Noho Wine & Spirits, INC")
            .address("639 Broadway")
            .city("New York")
            .state("New York")
            .country("United States of America")
            .description("Noho Wine & Spirits specializes in small batch production of organic and uncommon wines. The promotion is specific to the Wines only.")
            .budget(2)
            .distance(1.726304931640625d)
            .zip("10012")
            .rating(0.0d)
            .phone("6466414510")
            .email("")
            .website("http://www.amarachi325.com")
            .timeZone("-04")
            .coordinates(new Coordinates() {
               @Override
               public Double lat() {
                  return 40.6980805;
               }

               @Override
               public Double lng() {
                  return -73.9846833;
               }
            })
            .offers(null)
            .images(null)
            .operationDays(null)
            .disclaimers(null)
            .currencies(null)
            .categories(null)
            .amenities(null)
            .reviews(withReview ? getReviews(countReview) : null)
            .build();
   }

   private Merchant getMerchant() {
      return merchant;
   }

   private Reviews getReviews(int countReview) {
      return ImmutableReviews.builder()
            .total(String.valueOf(countReview))
            .userHasPendingReview(false)
            .ratingAverage("3.7")
            .reviews(getReview(countReview))
            .reviewSettings(null)
            .build();
   }

   private ArrayList<Review> getReview(int countReview) {
      ArrayList<Review> reviewObjectList = new ArrayList<>();
      for (int i=0; i<countReview; i++){
         reviewObjectList.add(getReviewObject());
      }
      return reviewObjectList;
   }

   private Review getReviewObject() {
      return ImmutableReview.builder()
            .lastModeratedTimeUtc("2017-06-08T18:00:17.000Z UTC")
            .rating(4)
            .reviewId("5f3dd087-2357-4f86-b332-1d8de2977f73")
            .reviewImagesList(null)
            .reviewText("asd aoisd aosid asoid asoid aosd asoid aosid aosid aoisd a")
            .userImage(null)
            .userNickName("WV DEMO Account")
            .verified(false)
            .brand(1)
            .build();
   }

   private Review getReviewObject(String reviewId, String userNickName, int rating, String lastModerateTimeUtc,
                                        String reviewText, boolean verified, List<ReviewImages> reviewImagesList) {
      return ImmutableReview.builder()
               .lastModeratedTimeUtc(lastModerateTimeUtc)
               .rating(rating)
               .reviewId(reviewId)
               .reviewImagesList(reviewImagesList)
               .reviewText(reviewText)
               .userImage(null)
               .userNickName(userNickName)
               .verified(verified)
               .brand(1)
            .build();
   }

   class MockDetailPresenter extends DtlDetailsPresenterImpl {

      private boolean isReviewCached;
      private boolean isUserHasReviews;

      public MockDetailPresenter(Context context, Injector injector, Merchant merchant, List<String> preExpandOffers) {
         super(context, injector, merchant, preExpandOffers);
      }

      @Override
      public boolean isReviewCached() {
         return isReviewCached;
      }

      public void setReviewCached(boolean reviewCached) {
         isReviewCached = reviewCached;
      }

      @Override
      public boolean userHasReviews() {
         return isUserHasReviews;
      }

      public void setUserHasReviews(boolean hasUserReviews) {
         isUserHasReviews = hasUserReviews;
      }
   }
}