package com.worldventures.dreamtrips.social.ui.reptools.view.cell;

import android.view.View;
import android.widget.TextView;

import com.worldventures.core.modules.video.model.VideoLanguage;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;

import butterknife.InjectView;

@Layout(android.R.layout.simple_list_item_1)
public class VideoLanguageCell extends BaseAbstractDelegateCell<VideoLanguage, CellDelegate<VideoLanguage>> {

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
