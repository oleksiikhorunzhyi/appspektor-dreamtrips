package com.worldventures.dreamtrips.modules.feed.view.cell.delegate;

import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.modules.feed.model.PostDescription;

public interface PostCreationTextDelegate extends CellDelegate<PostDescription> {

    void onTextChanged(String text);

    void onFocusChanged(boolean hasFocus);
}
