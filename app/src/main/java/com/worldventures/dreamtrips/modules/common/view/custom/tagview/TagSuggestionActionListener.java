package com.worldventures.dreamtrips.modules.common.view.custom.tagview;

import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoTag;

public interface TagSuggestionActionListener extends TagActionListener {

    void onFrameClicked(SuggestionTagView suggestionTagView, PhotoTag tag);
}