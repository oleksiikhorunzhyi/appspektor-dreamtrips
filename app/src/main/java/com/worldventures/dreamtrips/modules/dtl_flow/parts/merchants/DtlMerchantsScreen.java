package com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlScreen;

import java.util.List;

import rx.Observable;

public interface DtlMerchantsScreen extends DtlScreen {

    void updateToolbarLocationTitle(@Nullable DtlLocation dtlLocation);

    void updateToolbarSearchCaption(@Nullable String searchCaption);

    void setItems(List<DtlMerchant> dtlMerchants);

    void showProgress();

    void hideProgress();

    void toggleSelection(DtlMerchant DtlMerchant);

    void toggleDiningFilterSwitch(boolean enabled);

    Observable<Boolean> getToggleObservable();

    boolean isToolbarCollapsed();

    void setFilterButtonState(boolean enabled);
}
