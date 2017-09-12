package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews;

import android.content.Context;

import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ImmutableReview;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.Review;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ReviewImages;
import com.worldventures.dreamtrips.modules.dtl.service.action.ReviewMerchantsAction;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.ReviewObject;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.views.OfferWithReviewView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DtlReviewsPresenterImplTest {
   private DtlReviewsPresenterImpl presenter;
   @Mock Context context;
   @Mock Injector injector;
   @Mock DtlReviewsScreen screen;
   @Mock Merchant merchant;
   @Mock ReviewMerchantsAction action;
   @Mock OfferWithReviewView mContainerDetail;

   private List<ReviewObject> mockItems = new ArrayList<>();

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);
      generateMockObjects();
      presenter = new DtlReviewsPresenterImpl(context, injector, merchant);
      presenter.attachView(screen);
      screen.setContainerDetail(mContainerDetail);
   }

   @Test
   public void itShouldCleanScreenForFirstLoad() {
      presenter.loadFirstReviews();
      verify(screen).resetViewData();
   }

   @Test
   public void itShouldShowPageLoader() {
      presenter.loadFirstReviews();
      verify(screen).onRefreshProgress();
   }

   @Test
   public void itShouldCheckData() {
      when(screen.getCurrentReviews()).thenReturn(getCurrentItems(10));
      Assert.assertFalse(presenter.isValidReceivedData(screen.getCurrentReviews(), getMockReviews(0, 10)));
   }

   @Test
   public void itShouldAddData() {
      when(screen.getCurrentReviews()).thenReturn(getCurrentItems(10));
      Assert.assertTrue(presenter.isValidReceivedData(screen.getCurrentReviews(),  getMockReviews(10, 10)));
   }

   private ArrayList<ReviewObject> getCurrentItems(int amount) {
      ArrayList<ReviewObject> currentItems = new ArrayList<>();
      currentItems.clear();
      for (int i = 0; i < amount; i++) {
         List<ReviewImages> urlReviewImages = new ArrayList<>();
         currentItems.add(new ReviewObject(String.valueOf(i), String.valueOf(i), String.valueOf(i), 3.2f, String.valueOf(i), String
               .valueOf(i), true, urlReviewImages));
      }
      return currentItems;
   }

   private void generateMockObjects() {
      mockItems.clear();
      for (int i = 0; i < 28; i++) {
         List<ReviewImages> urlReviewImages = new ArrayList<>();
         mockItems.add(new ReviewObject(String.valueOf(i), String.valueOf(i), String.valueOf(i), 3.2f, String.valueOf(i), String
               .valueOf(i), true, urlReviewImages));
      }
   }

   private List<ReviewObject> getMockReviews(int indexOf, int limit) {
      List<ReviewObject> items = new ArrayList<>();
      if (indexOf >= mockItems.size()) return items;

      int maxLimit = indexOf + limit <= mockItems.size() ? indexOf + limit : mockItems.size();

      for (int i = indexOf; i < maxLimit; i++) {
         items.add(mockItems.get(i));
      }
      return items;
   }

}