package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.event.AttachPhotoEvent;
import com.worldventures.dreamtrips.modules.feed.model.AttachPhotoModel;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_attach_photo)
public class AttachPhotoCell extends AbstractCell<AttachPhotoModel> {

    @InjectView(R.id.icon)
    ImageView icon;
    @InjectView(R.id.title)
    TextView title;

    public AttachPhotoCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        icon.setImageResource(getModelObject().getIconRes());
        title.setText(getModelObject().getTitleRes());
        title.setTextColor(ContextCompat.getColor(itemView.getContext(), getModelObject().getColorRes()));
        itemView.setOnClickListener(v -> {
            int requestType = -1;

            switch (getModelObject().getType()) {
                case AttachPhotoModel.CAMERA:
                    requestType = PickImageDelegate.REQUEST_CAPTURE_PICTURE;
                    break;

                case AttachPhotoModel.FACEBOOK:
                    requestType = PickImageDelegate.REQUEST_FACEBOOK;
                    break;
            }

            getEventBus().post(new AttachPhotoEvent(requestType));
        });
    }

    @Override
    public void prepareForReuse() {

    }
}