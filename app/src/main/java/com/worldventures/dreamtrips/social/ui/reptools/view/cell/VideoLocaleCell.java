package com.worldventures.dreamtrips.social.ui.reptools.view.cell;

import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.core.modules.video.model.VideoLocale;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.util.GraphicUtils;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_video_locale)
public class VideoLocaleCell extends BaseAbstractDelegateCell<VideoLocale, CellDelegate<VideoLocale>> {

   @InjectView(R.id.flag_image) SimpleDraweeView flagImage;
   @InjectView(R.id.country_name) TextView countryName;

   public VideoLocaleCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      if (getModelObject().getImage() != null) {
         final int imageWidth = flagImage.getResources().getDimensionPixelSize(R.dimen.locale_flag_size_width);
         final int imageHeight = flagImage.getResources().getDimensionPixelSize(R.dimen.locale_flag_size_height);
         flagImage.setController(GraphicUtils.provideFrescoResizingController(getModelObject().getImage(),
               flagImage.getController(), imageWidth, imageHeight));
      }
      countryName.setText(getModelObject().getTitle());
      itemView.setOnClickListener(view -> cellDelegate.onCellClicked(getModelObject()));
   }

   @Override
   public boolean shouldInject() {
      return false;
   }
}
