package com.worldventures.dreamtrips.social.ui.membership.view.cell;

import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.util.GraphicUtils;
import com.worldventures.core.ui.util.ViewUtils;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;
import com.worldventures.dreamtrips.social.domain.entity.InviteTemplate;

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
      ViewUtils.runTaskAfterMeasure(imageViewPhoto,
            () -> imageViewPhoto.setController(GraphicUtils.provideFrescoResizingController(getModelObject().getCoverUrl(),
                  imageViewPhoto.getController(), imageViewPhoto.getWidth(), imageViewPhoto.getHeight()))
      );
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
