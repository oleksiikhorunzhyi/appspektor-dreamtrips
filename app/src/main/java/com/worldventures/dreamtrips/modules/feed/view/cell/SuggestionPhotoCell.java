package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.GraphicUtils;
import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_suggestion_photo)
public class SuggestionPhotoCell extends AbstractDelegateCell<PhotoPickerModel, CellDelegate<PhotoPickerModel>> {

   @InjectView(R.id.iv_photo) SimpleDraweeView photo;
   @InjectView(R.id.pick) ImageView pick;
   @InjectView(R.id.darkened_view) View darkenedView;

   public SuggestionPhotoCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      setImage(getModelObject().getUri(), photo);
      //
      pick.setImageResource(getModelObject().isChecked() ? R.drawable.add_photo_icon_selected : R.drawable.add_photo_icon);
      darkenedView.setVisibility(getModelObject().isChecked() ? View.VISIBLE : View.GONE);
      //
      itemView.setOnClickListener(v -> cellDelegate.onCellClicked(getModelObject()));
   }

   private void setImage(Uri uri, SimpleDraweeView draweeView) {
      if (draweeView.getTag() != null) {
         if (uri.equals(draweeView.getTag())) {
            return;
         }
      }

      draweeView.setController(GraphicUtils.provideFrescoResizingController(uri, draweeView.getController(), 100, 100));
      draweeView.setTag(uri);
   }

   @Override
   public boolean shouldInject() {
      return false;
   }
}
