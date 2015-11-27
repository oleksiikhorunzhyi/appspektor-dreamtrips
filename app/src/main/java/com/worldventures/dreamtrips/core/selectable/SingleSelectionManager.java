package com.worldventures.dreamtrips.core.selectable;

import android.support.v7.widget.RecyclerView;

public class SingleSelectionManager extends SimpleSelectionManager {

    public SingleSelectionManager(RecyclerView recyclerView) {
        super(recyclerView);
    }

    @Override
    protected void toggle(int position) {
        selectableWrapperAdapter.clearSelections();
        super.toggle(position);
    }
}
