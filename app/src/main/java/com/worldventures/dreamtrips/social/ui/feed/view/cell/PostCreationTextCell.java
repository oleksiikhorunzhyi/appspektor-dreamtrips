package com.worldventures.dreamtrips.social.ui.feed.view.cell;

import android.view.View;
import android.widget.TextView;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;
import com.worldventures.dreamtrips.social.ui.feed.model.PostDescription;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_post_text)
public class PostCreationTextCell extends BaseAbstractDelegateCell<PostDescription, PostCreationTextCell.Delegate> {

   @InjectView(R.id.post) TextView post;

   public PostCreationTextCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      post.setText(getModelObject().getDescription());
      post.setOnClickListener(v -> cellDelegate.onCellClicked(getModelObject()));
   }

   @Override
   public void clearResources() {
      super.clearResources();
      post.setOnFocusChangeListener(null);
   }

   @Override
   public boolean shouldInject() {
      return false;
   }

   public interface Delegate extends CellDelegate<PostDescription> {

   }
}
