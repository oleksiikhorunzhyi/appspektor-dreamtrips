package com.techery.spares.ui.view.cell;

import android.content.Context;
import android.os.Bundle;

import de.greenrobot.event.EventBus;

public interface BaseCell<T> {
    public void fillWithItem(T item);
    public void prepareForReuse();
    public void setEventBus(EventBus eventBus);
    public void saveState(Bundle b);
    public void restoreState(Bundle bundle);

    public int getLastPosition();
    public void setLastPosition(int position);

    public interface CellBuilder<T> {
        BaseCell<T> build(Context c, T item);
    }
}