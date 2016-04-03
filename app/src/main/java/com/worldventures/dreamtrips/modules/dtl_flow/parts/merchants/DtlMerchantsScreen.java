package com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowScreen;

import java.util.List;

public interface DtlMerchantsScreen extends FlowScreen {

    void setItems(List<DtlMerchant> dtlMerchants);

    void showProgress();

    void hideProgress();

    void toggleSelection(DtlMerchant DtlMerchant);

    void updateToolbarTitle(@Nullable DtlLocation dtlLocation);

    void openRightDrawer();

    void toggleDiningFilterSwitch(boolean checked);
}
