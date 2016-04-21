package com.techery.spares.adapter.expandable;

import android.view.View;

import com.techery.spares.ui.view.cell.CellDelegate;

public abstract class GroupDelegateCell<G, C, D extends CellDelegate> extends GroupCell<G, C> {

    protected D cellDelegate;

    public GroupDelegateCell(View view) {
        super(view);
    }

    public void setCellDelegate(D cellDelegate) {
        this.cellDelegate = cellDelegate;
    }

}
