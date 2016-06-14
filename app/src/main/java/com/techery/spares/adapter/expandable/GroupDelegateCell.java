package com.techery.spares.adapter.expandable;

import android.view.View;

import com.techery.spares.ui.view.cell.CellDelegate;

public abstract class GroupDelegateCell<GROUP, CELL, DELEGATE extends CellDelegate> extends GroupCell<GROUP, CELL> {

    protected DELEGATE cellDelegate;

    public GroupDelegateCell(View view) {
        super(view);
    }

    public void setCellDelegate(DELEGATE cellDelegate) {
        this.cellDelegate = cellDelegate;
    }

}
