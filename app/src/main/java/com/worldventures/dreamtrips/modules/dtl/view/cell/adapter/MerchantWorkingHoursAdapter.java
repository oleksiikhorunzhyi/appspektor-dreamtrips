package com.worldventures.dreamtrips.modules.dtl.view.cell.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.module.Injector;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationDay;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlWorkingHoursCell;

public class MerchantWorkingHoursAdapter extends BaseArrayListAdapter<OperationDay> {

    private final DtlMerchant merchant;

    public MerchantWorkingHoursAdapter(Context context, DtlMerchant merchant, Injector injector) {
        super(context, injector);
        this.merchant = merchant;
    }

    @Override
    public AbstractCell onCreateViewHolder(ViewGroup parent, int viewType) {
        AbstractCell cell = super.onCreateViewHolder(parent, viewType);
        if (shouldMerchantSet(cell)) {
            bindMerchantHolder(cast(cell));
        }
        return cell;
    }

    private DtlWorkingHoursCell cast(AbstractCell cell) {
        return (DtlWorkingHoursCell) cell;
    }

    private boolean shouldMerchantSet(AbstractCell cell) {
        return cell instanceof DtlWorkingHoursCell;
    }

    private void bindMerchantHolder(DtlWorkingHoursCell cell) {
        cell.setTimezone(merchant.getOffsetHours());
    }
}
