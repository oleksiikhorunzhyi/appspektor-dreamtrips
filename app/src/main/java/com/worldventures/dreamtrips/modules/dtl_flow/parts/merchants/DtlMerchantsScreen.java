package com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlScreen;

import java.util.List;

import rx.Observable;

public interface DtlMerchantsScreen extends DtlScreen {

   void updateToolbarLocationTitle(@Nullable DtlLocation dtlLocation);

   void updateToolbarSearchCaption(@Nullable String searchCaption);

   void setItems(List<ThinMerchant> merchants);

   void showProgress();

   void hideProgress();

   void showError(String error);

   void toggleSelection(ThinMerchant merchant);

   void toggleDiningFilterSwitch(boolean enabled);

   boolean isToolbarCollapsed();

   void setFilterButtonState(boolean enabled);

   void showEmptyMerchantView(boolean show);

   DtlMerchantsPath getPath();

   Observable<Boolean> getToggleObservable();
}
