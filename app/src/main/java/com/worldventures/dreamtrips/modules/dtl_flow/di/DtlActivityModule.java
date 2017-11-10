package com.worldventures.dreamtrips.modules.dtl_flow.di;

import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.MerchantInfoInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.MerchantMapInfoInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.MerchantOffersInflater;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlPointsEstimationPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlScanQrCodePresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlScanReceiptPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlThrstFlowPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlThrstScanReceiptPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlThrstThankYouScreenPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlThrstTransactionSucceedPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlTransactionSucceedPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlVerifyAmountPresenter;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlFilterAttributeCell;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlLocationCell;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlLocationChangeCell;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlLocationSearchCell;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlLocationSearchHeaderCell;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlMerchantExpandableCell;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlNearbyHeaderCell;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlPerkCell;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlPointsCell;
import com.worldventures.dreamtrips.modules.dtl.view.cell.WorkingHoursCell;
import com.worldventures.dreamtrips.modules.dtl.view.cell.adapter.MerchantWorkingHoursAdapter;
import com.worldventures.dreamtrips.modules.dtl.view.cell.adapter.ThinMerchantsAdapter;
import com.worldventures.dreamtrips.modules.dtl.view.dialog.DtlPointsEstimationFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlScanQrCodeFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlScanReceiptFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlThrstFlowFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlThrstScanReceiptFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlThrstTransactionSucceedFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlTransactionSucceedFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlVerifyAmountFragment;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlActivity;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.DtlCommentReviewPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.DtlCommentReviewScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.fragments.ActionReviewEntityFragment;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.fragments.CreateReviewEntityFragment;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.fragments.CreateReviewEntityPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.fragments.CreateReviewPostFragment;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.fragments.PostReviewCreationTextCell;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.fragments.PostReviewDescription;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.helpers.PhotoReviewPostCreationCell;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.detailReview.DtlDetailReviewPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.detailReview.DtlDetailReviewScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.details.DtlDetailsPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.details.DtlDetailsScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.filter.DtlFilterPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.fullscreen_image.DtlFullscreenImagePresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.fullscreen_image.DtlFullscreenImageScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.location_change.DtlLocationChangePresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.location_change.DtlLocationChangeScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.locations.DtlLocationsPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.locations.DtlLocationsScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.locations_search.DtlLocationsSearchPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.locations_search.DtlLocationsSearchScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.map.DtlMapPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.map.DtlMapScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.map.info.DtlMapInfoPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.map.info.DtlMapInfoScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.master_toolbar.MasterToolbarPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.master_toolbar.MasterToolbarScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants.DtlMerchantsPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants.DtlMerchantsScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.pilot.DtlThankYouScreenFragment;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.DtlReviewsPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.DtlReviewsScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.views.OfferWithReviewView;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.start.DtlStartPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.start.DtlStartScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transaction_detail.DtlTransactionPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transaction_detail.DtlTransactionScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.DtlTransactionListPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.DtlTransactionListScreenImpl;
import com.worldventures.dreamtrips.modules.trips.view.cell.filter.DtlFilterAttributeHeaderCell;

import dagger.Module;

@Module(
      injects = {
            DtlActivity.class,
            DtlLocationsSearchScreenImpl.class,
            DtlLocationsSearchPresenterImpl.class,
            DtlLocationsScreenImpl.class,
            DtlLocationsPresenterImpl.class,
            DtlMapPresenterImpl.class,
            DtlMapScreenImpl.class,
            DtlMapInfoPresenterImpl.class,
            MasterToolbarScreenImpl.class,
            MasterToolbarPresenterImpl.class,
            DtlMapInfoScreenImpl.class,
            DtlStartPresenterImpl.class,
            DtlStartScreenImpl.class,
            DtlMerchantsPresenterImpl.class,
            DtlMerchantsScreenImpl.class,
            DtlLocationChangePresenterImpl.class,
            DtlLocationChangeScreenImpl.class,
            DtlDetailsPresenterImpl.class,
            DtlDetailsScreenImpl.class,
            DtlFullscreenImageScreenImpl.class,
            DtlFullscreenImagePresenterImpl.class,
            ActivityPresenter.class,
            DtlFilterPresenterImpl.class,
            DtlLocationSearchHeaderCell.class,
            DtlNearbyHeaderCell.class,
            DtlLocationCell.class,
            DtlFilterAttributeCell.class,
            DtlFilterAttributeHeaderCell.class,
            DtlMerchantExpandableCell.class,
            DtlLocationSearchCell.class,
            MerchantOffersInflater.class,
            MerchantInfoInflater.class,
            MerchantMapInfoInflater.class,
            DtlPerkCell.class,
            DtlPointsCell.class,
            DtlLocationChangeCell.class,
            WorkingHoursCell.class,
            DtlScanReceiptFragment.class,
            DtlScanReceiptPresenter.class,
            DtlThrstScanReceiptFragment.class,
            DtlThrstScanReceiptPresenter.class,
            DtlPointsEstimationFragment.class,
            DtlPointsEstimationPresenter.class,
            DtlScanQrCodeFragment.class,
            DtlScanQrCodePresenter.class,
            DtlTransactionSucceedFragment.class,
            DtlTransactionSucceedPresenter.class,
            DtlThrstTransactionSucceedFragment.class,
            DtlThrstTransactionSucceedPresenter.class,
            DtlVerifyAmountFragment.class,
            DtlVerifyAmountPresenter.class,
            DtlThrstFlowFragment.class,
            DtlThrstFlowPresenter.class,
            DtlThankYouScreenFragment.class,
            DtlThrstThankYouScreenPresenter.class,
            MerchantWorkingHoursAdapter.class,
            ThinMerchantsAdapter.class,
            DtlReviewsPresenterImpl.class,
            DtlReviewsScreenImpl.class,
            DtlCommentReviewScreenImpl.class,
            DtlCommentReviewPresenterImpl.class,
            DtlDetailReviewPresenterImpl.class,
            DtlDetailReviewScreenImpl.class,
            CreateReviewEntityPresenter.class,
            PostReviewDescription.class,
            PhotoReviewPostCreationCell.class,
            PostReviewCreationTextCell.class,
            ActionReviewEntityFragment.class,
            CreateReviewPostFragment.class,
            CreateReviewEntityFragment.class,
            OfferWithReviewView.class,
            DtlTransactionListScreenImpl.class,
            DtlTransactionListPresenterImpl.class,
            DtlTransactionScreenImpl.class,
            DtlTransactionPresenterImpl.class,
      },
      complete = false, library = true)
public class DtlActivityModule {
}
