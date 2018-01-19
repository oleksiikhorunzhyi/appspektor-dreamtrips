package com.worldventures.dreamtrips.modules.dtl_flow.di;

import com.google.gson.TypeAdapterFactory;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.module.FlowActivityModule;
import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.MerchantInfoInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.MerchantMapInfoInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.MerchantOffersInflater;
import com.worldventures.dreamtrips.modules.dtl.util.RuntimeTypeAdapterFactory;
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
import com.worldventures.dreamtrips.modules.dtl_flow.DtlActivity;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.DtlCommentReviewPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.DtlCommentReviewPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.DtlCommentReviewScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.fragments.PostReviewDescription;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.detail_review.DtlDetailReviewPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.detail_review.DtlDetailReviewPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.detail_review.DtlDetailReviewScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.details.DtlDetailsPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.details.DtlDetailsScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.details.DtlMerchantDetailsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.filter.DtlFilterPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.fullscreen_image.DtlFullscreenImagePath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.fullscreen_image.DtlFullscreenImagePresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.fullscreen_image.DtlFullscreenImageScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.location_change.DtlLocationChangePath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.location_change.DtlLocationChangePresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.location_change.DtlLocationChangeScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.locations.DtlLocationsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.locations.DtlLocationsPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.locations.DtlLocationsScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.locations_search.DtlLocationsSearchPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.locations_search.DtlLocationsSearchPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.locations_search.DtlLocationsSearchScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.map.DtlMapPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.map.DtlMapPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.map.DtlMapScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.map.info.DtlMapInfoPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.map.info.DtlMapInfoPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.map.info.DtlMapInfoScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.master_toolbar.MasterToolbarPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.master_toolbar.MasterToolbarScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants.DtlMerchantsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants.DtlMerchantsPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants.DtlMerchantsScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.DtlReviewsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.DtlReviewsPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.DtlReviewsScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.views.OfferWithReviewView;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.start.DtlStartPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.start.DtlStartPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.start.DtlStartScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transaction_detail.DtlTransactionPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transaction_detail.DtlTransactionPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transaction_detail.DtlTransactionScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.DtlTransactionListPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.DtlTransactionListPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.DtlTransactionListScreenImpl;
import com.worldventures.dreamtrips.modules.trips.view.cell.filter.DtlFilterAttributeHeaderCell;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

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
            MerchantWorkingHoursAdapter.class,
            ThinMerchantsAdapter.class,
            DtlReviewsPresenterImpl.class,
            DtlReviewsScreenImpl.class,
            DtlCommentReviewScreenImpl.class,
            DtlCommentReviewPresenterImpl.class,
            DtlDetailReviewPresenterImpl.class,
            DtlDetailReviewScreenImpl.class,
            PostReviewDescription.class,
            OfferWithReviewView.class,
            DtlTransactionListScreenImpl.class,
            DtlTransactionListPresenterImpl.class,
            DtlTransactionScreenImpl.class,
            DtlTransactionPresenterImpl.class,
      },
      includes = DtlLegacyActivityModule.class,
      complete = false, library = true)
public class DtlActivityModule {

      @Named(FlowActivityModule.LABEL)
      @Provides(type = Provides.Type.SET)
      TypeAdapterFactory provideDtlPathTypeAdapterFactory() {
            return RuntimeTypeAdapterFactory.of(MasterDetailPath.class)
                  .registerSubtype(DtlFullscreenImagePath.class)
                  .registerSubtype(DtlLocationsPath.class)
                  .registerSubtype(DtlMerchantsPath.class)
                  .registerSubtype(DtlCommentReviewPath.class)
                  .registerSubtype(DtlLocationChangePath.class)
                  .registerSubtype(DtlLocationsSearchPath.class)
                  .registerSubtype(DtlStartPath.class)
                  .registerSubtype(DtlDetailReviewPath.class)
                  .registerSubtype(DtlMerchantDetailsPath.class)
                  .registerSubtype(DtlMapPath.class)
                  .registerSubtype(DtlTransactionListPath.class)
                  .registerSubtype(DtlReviewsPath.class)
                  .registerSubtype(DtlMapInfoPath.class)
                  .registerSubtype(DtlTransactionPath.class);
      }
}
