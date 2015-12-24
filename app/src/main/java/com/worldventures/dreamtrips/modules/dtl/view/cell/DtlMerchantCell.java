package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.selectable.SelectableCell;
import com.worldventures.dreamtrips.core.selectable.SelectableDelegate;
import com.worldventures.dreamtrips.modules.dtl.event.MerchantClickedEvent;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.DtlMerchantCommonDataInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlMerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.DtlMerchantInfoInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.DtlMerchantSingleImageDataInflater;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

import butterknife.OnClick;

@Layout(R.layout.adapter_item_dtl_place)
public class DtlMerchantCell extends AbstractCell<DtlMerchant> implements SelectableCell {

    DtlMerchantCommonDataInflater commonDataInflater;
    DtlMerchantInfoInflater categoryDataInflater;

    private SelectableDelegate selectableDelegate;

    public DtlMerchantCell(View view) {
        super(view);
        DtlMerchantHelper helper = new DtlMerchantHelper(view.getContext());
        commonDataInflater = new DtlMerchantSingleImageDataInflater(helper);
        categoryDataInflater = new DtlMerchantInfoInflater(helper);
        commonDataInflater.setView(view);
        categoryDataInflater.setView(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        commonDataInflater.apply(getModelObject());
        categoryDataInflater.apply(getModelObject());
        itemView.setSelected(selectableDelegate.isSelected(getAdapterPosition()));
    }

    @OnClick(R.id.place_details_root)
    void merchantClicked() {
        if (!selectableDelegate.isSelected(getAdapterPosition()))
            selectableDelegate.toggleSelection(getAdapterPosition());
        //
        getEventBus().post(new MerchantClickedEvent(getModelObject().getId()));
    }

    @Override
    public void prepareForReuse() {
        //
    }

    @Override
    public void setSelectableDelegate(SelectableDelegate selectableDelegate) {
        this.selectableDelegate = selectableDelegate;
    }
}
