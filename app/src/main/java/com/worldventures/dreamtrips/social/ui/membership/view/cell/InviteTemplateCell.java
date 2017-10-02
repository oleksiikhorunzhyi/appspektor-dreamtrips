package com.worldventures.dreamtrips.social.ui.membership.view.cell;

import android.net.Uri;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;
import com.worldventures.dreamtrips.social.ui.membership.model.InviteTemplate;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_inventation_template)
public class InviteTemplateCell extends BaseAbstractDelegateCell<InviteTemplate, CellDelegate<InviteTemplate>> {

   @InjectView(R.id.imageViewPhoto) SimpleDraweeView imageViewPhoto;

   public InviteTemplateCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      imageViewPhoto.setImageURI(Uri.parse(getModelObject().getCoverUrl()));
   }

   @OnClick(R.id.btn_select)
   public void onSelectAction() {
      cellDelegate.onCellClicked(getModelObject());
   }

   @Override
   public boolean shouldInject() {
      return false;
   }
}
