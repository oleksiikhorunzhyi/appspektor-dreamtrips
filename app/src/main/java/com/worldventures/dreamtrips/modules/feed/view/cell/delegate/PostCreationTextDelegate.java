package com.worldventures.dreamtrips.modules.feed.view.cell.delegate;

import com.techery.spares.ui.view.cell.CellDelegate;

public interface PostCreationTextDelegate extends CellDelegate<String> {

    void onTextChanged(String text);

    void onFocusChanged(boolean hasFocus);
}
