package com.techery.spares.ui.view.cell;

import android.view.View;

public abstract class AbstractDelegateCell<T, V extends CellDelegate<T>> extends AbstractCell<T> {

    protected V cellDelegate;

    public AbstractDelegateCell(View view) {
        super(view);
    }

    public void setCellDelegate(V cellDelegate) {
        this.cellDelegate = cellDelegate;
    }
}
