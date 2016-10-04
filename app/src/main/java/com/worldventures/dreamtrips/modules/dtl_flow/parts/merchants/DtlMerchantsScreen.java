package com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlScreen;

import java.util.List;

public interface DtlMerchantsScreen extends DtlScreen {

   void updateToolbarLocationTitle(@Nullable DtlLocation dtlLocation);

   void updateToolbarSearchCaption(@Nullable String searchCaption);

   void setRefreshedItems(List<ThinMerchant> merchants);

   void addItems(List<ThinMerchant> merchants);

   void refreshProgress(boolean isShow);

   void loadNextProgress(boolean isShow);

   void updateLoadingState(boolean isLoading);

   void clearMerchants();

   void showError(String error);

   void refreshMerchantsError(boolean isShow);

   void loadNextMerchantsError(boolean isShow);

   void toggleSelection(ThinMerchant merchant);

   void toggleOffersOnly(boolean enabled);

   boolean isToolbarCollapsed();

   void setFilterButtonState(boolean isDefault);

   void showEmpty(boolean isShow);

   void applyViewState(DtlMerchantsState state);

   DtlMerchantsPath getPath();

   DtlMerchantsState provideViewState();

}
