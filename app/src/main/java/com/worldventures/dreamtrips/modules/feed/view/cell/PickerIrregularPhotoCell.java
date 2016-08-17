package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.event.AttachPhotoEvent;
import com.worldventures.dreamtrips.modules.feed.event.OpenFacebookEvent;
import com.worldventures.dreamtrips.modules.feed.model.PickerIrregularPhotoModel;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_attach_photo)
public class PickerIrregularPhotoCell extends AbstractCell<PickerIrregularPhotoModel> {

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
        itemView.setOnClickListener(v -> {
            int requestType = -1;

            switch (getModelObject().getType()) {
                case PickerIrregularPhotoModel.CAMERA:
                    requestType = PickImageDelegate.CAPTURE_PICTURE;
                    getEventBus().post(new AttachPhotoEvent(requestType));
                    break;

                case PickerIrregularPhotoModel.FACEBOOK:
                    getEventBus().post(new OpenFacebookEvent());
                    break;
            }
        });
    }
}
