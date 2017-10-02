package com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.fragments;

import android.view.View;
import android.widget.TextView;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;

import butterknife.InjectView;

@Layout(R.layout.adapter_review_item_post_text)
public class PostReviewCreationTextCell extends BaseAbstractDelegateCell<PostReviewDescription, PostReviewCreationTextCell.Delegate> {

   @InjectView(R.id.post) TextView post;

   public PostReviewCreationTextCell(View view) {
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

   public interface Delegate extends CellDelegate<PostReviewDescription> {

   }
}
