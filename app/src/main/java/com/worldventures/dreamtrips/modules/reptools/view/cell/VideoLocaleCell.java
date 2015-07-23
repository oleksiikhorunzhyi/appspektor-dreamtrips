package com.worldventures.dreamtrips.modules.reptools.view.cell;

import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.membership.event.VideoLocaleSelectedEvent;
import com.worldventures.dreamtrips.modules.reptools.model.VideoLocale;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_video_locale)
public class VideoLocaleCell extends AbstractCell<VideoLocale> {

    @InjectView(R.id.flag_image)
    SimpleDraweeView flagImage;
    @InjectView(R.id.country_name)
    TextView countryName;

    public VideoLocaleCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        flagImage.setImageURI(Uri.parse(getModelObject().getImage()));
        countryName.setText(getModelObject().getCountry());
        itemView.setOnClickListener(view -> getEventBus().post(new VideoLocaleSelectedEvent(getModelObject())));
    }

    @Override
    public void prepareForReuse() {

    }
}
