package com.worldventures.dreamtrips.modules.reptools.view.cell;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.reptools.model.SuccessStory;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_success_story)
public class SuccessStoryCell extends AbstractDelegateCell<SuccessStory, SuccessStoryCell.Delegate> {

   @InjectView(R.id.tv_title) TextView tvTitle;
   @InjectView(R.id.vg_parent) ViewGroup vgParent;

   public SuccessStoryCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      updateSelection();
      tvTitle.setText(getModelObject().getAuthor());
   }

   private void updateSelection() {
      if (getModelObject().isSelected()) {
         vgParent.setBackgroundColor(vgParent.getResources().getColor(R.color.grey_lighter));
      } else {
         vgParent.setBackgroundColor(Color.WHITE);
      }
   }

   @OnClick(R.id.vg_parent)
   public void onItemClick() {
      cellDelegate.onCellClicked(getModelObject(), getPosition());
   }

   public static abstract class Delegate implements CellDelegate<SuccessStory> {
      @Override
      public void onCellClicked(SuccessStory model) {}

      public abstract void onCellClicked(SuccessStory model, int position);
   }
}
