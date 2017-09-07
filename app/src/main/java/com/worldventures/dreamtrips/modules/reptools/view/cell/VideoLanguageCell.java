package com.worldventures.dreamtrips.modules.reptools.view.cell;

import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.modules.video.model.VideoLanguage;

import butterknife.InjectView;

@Layout(android.R.layout.simple_list_item_1)
public class VideoLanguageCell extends AbstractDelegateCell<VideoLanguage, CellDelegate<VideoLanguage>> {

   @InjectView(android.R.id.text1) TextView text;

   public VideoLanguageCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      text.setText(getModelObject().getTitle());
      itemView.setOnClickListener(view -> cellDelegate.onCellClicked(getModelObject()));
   }

   @Override
   public boolean shouldInject() {
      return false;
   }
}
