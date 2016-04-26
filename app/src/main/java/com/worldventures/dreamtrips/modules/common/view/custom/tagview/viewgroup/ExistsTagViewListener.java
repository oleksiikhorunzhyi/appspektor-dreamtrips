package com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup;

import com.worldventures.dreamtrips.modules.common.view.custom.tagview.TagActionListener;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;

public interface ExistsTagViewListener extends TagActionListener {
        void onTagDeleted(PhotoTag photoTag);
    }