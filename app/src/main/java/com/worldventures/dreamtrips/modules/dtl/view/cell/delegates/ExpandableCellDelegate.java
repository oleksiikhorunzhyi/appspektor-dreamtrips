package com.worldventures.dreamtrips.modules.dtl.view.cell.delegates;

import com.worldventures.core.ui.view.cell.CellDelegate;

public interface ExpandableCellDelegate<T> extends CellDelegate<T> {

   void onToggleExpanded(boolean expanded, T item);
}
