package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.model.PickerIrregularPhotoModel;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_attach_photo)
public class PickerIrregularPhotoCell extends AbstractDelegateCell<PickerIrregularPhotoModel, CellDelegate<PickerIrregularPhotoModel>> {

   @InjectView(R.id.icon) ImageView icon;
   @InjectView(R.id.title) TextView title;

   public PickerIrregularPhotoCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      icon.setImageResource(getModelObject().getIconRes());
      title.setText(getModelObject().getTitleRes());
      title.setTextColor(ContextCompat.getColor(itemView.getContext(), getModelObject().getColorRes()));
      itemView.setOnClickListener(v -> cellDelegate.onCellClicked(getModelObject()));
   }
}
