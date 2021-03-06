package com.worldventures.dreamtrips.social.ui.feed.view.cell;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.core.modules.picker.model.PhotoPickerModel;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.util.GraphicUtils;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_suggestion_photo)
public class SuggestionPhotoCell extends BaseAbstractDelegateCell<PhotoPickerModel, CellDelegate<PhotoPickerModel>> {

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
      if (draweeView.getTag() != null && uri.equals(draweeView.getTag())) {
         return;
      }

      draweeView.setController(GraphicUtils.provideFrescoResizingController(uri, draweeView.getController(), 100, 100));
      draweeView.setTag(uri);
   }

   @Override
   public boolean shouldInject() {
      return false;
   }
}
