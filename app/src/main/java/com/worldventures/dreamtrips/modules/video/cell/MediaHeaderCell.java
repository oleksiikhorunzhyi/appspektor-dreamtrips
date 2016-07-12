package com.worldventures.dreamtrips.modules.video.cell;

import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.membership.model.MediaHeader;
import com.worldventures.dreamtrips.modules.video.cell.delegate.VideoHeaderDelegate;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_media_header)
public class MediaHeaderCell extends AbstractDelegateCell<MediaHeader, VideoHeaderDelegate> {

    @InjectView(R.id.header)
    TextView header;
    @InjectView((R.id.wrapper_spinner_language))
    View language;
    @InjectView(R.id.language)
    TextView languageCaption;
    @InjectView(R.id.spinner_language)
    SimpleDraweeView flag;

    public MediaHeaderCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        if (TextUtils.isEmpty(getModelObject().getTitle())) {
            header.setText(itemView.getContext().getString(R.string.recently_added));
        } else {
            header.setText(getModelObject().getTitle());
        }

        header.setTextColor(itemView.getResources().getColor(R.color.white));
        language.setVisibility(getModelObject().isShowLanguage() ? View.VISIBLE : View.INVISIBLE);

        if (getModelObject().getVideoLocale() != null) {
            flag.setImageURI(Uri.parse(getModelObject().getVideoLocale().getImage()));
            languageCaption.setText(getModelObject().getVideoLanguage().getTitle());
        } else {
            flag.setImageURI(null);
        }
    }

    @OnClick(R.id.wrapper_spinner_language)
    void onLanguageClicked() {
        if (cellDelegate != null) cellDelegate.onLanguageClicked();
    }

    @Override
    public void prepareForReuse() {
    }
}
