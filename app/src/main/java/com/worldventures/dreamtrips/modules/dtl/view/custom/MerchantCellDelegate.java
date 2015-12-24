package com.worldventures.dreamtrips.modules.dtl.view.custom;

import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

public interface MerchantCellDelegate extends CellDelegate<DtlMerchant> {

    void onDistanceClicked();
}
