package com.worldventures.dreamtrips.core.selectable;

public interface SelectableDelegate {

    void toggleSelection(int position);

    boolean isSelected(int position);
}
