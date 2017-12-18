package com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlScreen;

import java.util.List;

public interface DtlMerchantsScreen extends DtlScreen {

   void updateToolbarLocationTitle(@Nullable DtlLocation dtlLocation);

   void updateToolbarSearchQuery(@Nullable String searchQuery);

   void setRefreshedItems(List<ThinMerchant> merchants);

   void connectToggleUpdate();

   void toggleOffersOnly(boolean enabled);

   void setFilterButtonState(boolean isDefault);

   void showEmpty(boolean isShow);

   void showNoMerchantsCaption(boolean isFilterDefault, boolean isOffersOnly);

   void clearMerchants();

   void applyViewState(DtlMerchantsState state);

   void showLoadMerchantError(String error);

   DtlMerchantsPath getPath();

   DtlMerchantsState provideViewState();

   void toggleSelection(ThinMerchant merchant);

   void clearSelection();

   void onRefreshSuccess();

   void onRefreshProgress();

   void onRefreshError(String error);

   void onLoadNextSuccess();

   void onLoadNextProgress();

   void onLoadNextError();

   void onClickEntertainment();

   void onClickSpa();

   void onClickFood();

   void onClickTransaction();

   void updateMerchantType(List<String> type);

   int getMerchantType();

   void loadMerchantsAndAmenities(List<String> merchantType, int stringResource);

   void showNoTransactionMessage();

   void goToTransactionPage();

}
