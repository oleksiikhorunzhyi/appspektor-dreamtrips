package com.worldventures.dreamtrips.modules.dtl_flow.parts.map.info;

import android.content.Context;

import com.worldventures.core.janet.Injector;
import com.worldventures.core.service.DeviceInfoProvider;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ReviewSummary;
import com.worldventures.dreamtrips.modules.dtl.service.FullMerchantInteractor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class DtlMapInfoPresenterImplTest {

   @Mock private Context context;
   @Mock private Injector injector;
   @Mock private ThinMerchant merchant;
   @Mock private DtlMapInfoScreen view;
   @Mock private ReviewSummary review;
   @Mock private FullMerchantInteractor interactor;

   private DtlMapInfoPresenterImpl presenter;

   @Before
   public void setup(){
      MockitoAnnotations.initMocks(this);
      presenter = new DtlMapInfoPresenterImpl(context, injector, merchant);
      presenter.attachView(view);
      presenter.deviceInfoProvider = mock(DeviceInfoProvider.class);
      presenter.fullMerchantInteractor = interactor;
   }

   @Test
   public void testClickRatingReviews_NoPendingReviews(){
      when(presenter.deviceInfoProvider.isTablet()).thenReturn(false);
      when(review.userHasPendingReview()).thenReturn(true);
      when(review.total()).thenReturn("0");
      when(merchant.reviewSummary()).thenReturn(review);
      presenter.onClickRatingsReview();
      verify(view, times(1)).showPendingReviewError();
   }


   @Test
   public void testClickRatingReviews_PendingReviews(){
      when(presenter.deviceInfoProvider.isTablet()).thenReturn(false);
      when(review.userHasPendingReview()).thenReturn(false);
      when(review.total()).thenReturn("0");
      when(merchant.id()).thenReturn("id");
      when(merchant.reviewSummary()).thenReturn(review);
      presenter.onClickRatingsReview();
      verify(interactor, times(1)).load(anyString(), eq(review), eq(true));
   }
}
