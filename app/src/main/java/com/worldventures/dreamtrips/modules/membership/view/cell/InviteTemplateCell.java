package com.worldventures.dreamtrips.modules.membership.view.cell;

import android.net.Uri;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.membership.event.TemplateSelectedEvent;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_inventation_template)
public class InviteTemplateCell extends AbstractCell<InviteTemplate> {

    @InjectView(R.id.imageViewPhoto) SimpleDraweeView imageViewPhoto;

    public InviteTemplateCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        imageViewPhoto.setImageURI(Uri.parse(getModelObject().getCoverImage().getUrl()));
    }

    @OnClick(R.id.btn_select)
    public void onSelectAction() {
        getEventBus().post(new TemplateSelectedEvent(getModelObject()));
    }
}
