package com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview.viewgroup;

import com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview.TagActionListener;
import com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview.viewgroup.newio.model.PhotoTag;

public interface ExistsTagViewListener extends TagActionListener {
   void onTagDeleted(PhotoTag photoTag);
}