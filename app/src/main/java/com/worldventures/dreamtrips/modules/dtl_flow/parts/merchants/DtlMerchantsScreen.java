package com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlScreen;

import java.util.List;

import rx.Observable;

public interface DtlMerchantsScreen extends DtlScreen {

    void setItems(List<DtlMerchant> dtlMerchants);

    void showProgress();

    void hideProgress();

    void toggleSelection(DtlMerchant DtlMerchant);

    void updateToolbarTitle(@Nullable DtlLocation dtlLocation);

    void openRightDrawer();

    void toggleDiningFilterSwitch(boolean checked);

    Observable<Boolean> getToggleObservable();
}
