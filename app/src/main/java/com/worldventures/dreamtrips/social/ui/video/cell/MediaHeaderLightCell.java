package com.worldventures.dreamtrips.social.ui.video.cell;

import android.view.View;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.util.ViewUtilsKt;

@Layout(R.layout.adapter_media_header)
public class MediaHeaderLightCell extends MediaHeaderCell {

   public MediaHeaderLightCell(View view) {
      super(view);

      header.setTextColor(ViewUtilsKt.getColor(itemView, R.color.theme_main_darker));
   }
}
