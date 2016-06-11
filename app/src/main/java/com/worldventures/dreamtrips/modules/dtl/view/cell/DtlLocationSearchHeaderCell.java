package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.view.View;
import android.widget.Button;

import com.jakewharton.rxbinding.view.RxView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;

import java.util.concurrent.TimeUnit;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_dtl_location_header_cell)
public class DtlLocationSearchHeaderCell extends AbstractDelegateCell<DtlLocationSearchHeaderCell.HEADER, CellDelegate<DtlLocationSearchHeaderCell.HEADER>> {

    @InjectView(R.id.autoDetectNearMe)
    Button autoDetectNearMe;

    public DtlLocationSearchHeaderCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        RxView.clicks(autoDetectNearMe)
                .compose(RxLifecycle.bindView(itemView))
                .throttleFirst(3L, TimeUnit.SECONDS)
                .subscribe(aVoid -> cellDelegate.onCellClicked(getModelObject()));

    }

    @Override
    public void prepareForReuse() {
    }

    public static final class HEADER {

        public static final HEADER INSTANCE = new HEADER();
    }
}
