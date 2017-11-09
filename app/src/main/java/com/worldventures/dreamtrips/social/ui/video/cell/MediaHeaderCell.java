package com.worldventures.dreamtrips.social.ui.video.cell;

import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.util.GraphicUtils;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;
import com.worldventures.dreamtrips.social.ui.membership.model.MediaHeader;
import com.worldventures.dreamtrips.social.ui.video.cell.delegate.VideoHeaderDelegate;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_media_header)
public class MediaHeaderCell extends BaseAbstractDelegateCell<MediaHeader, VideoHeaderDelegate> {

   @InjectView(R.id.header) TextView header;
   @InjectView(R.id.wrapper_spinner_language) View language;
   @InjectView(R.id.language) TextView languageCaption;
   @InjectView(R.id.spinner_language) SimpleDraweeView flag;

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
      language.setVisibility(getModelObject().getShowLanguage() ? View.VISIBLE : View.INVISIBLE);

      if (getModelObject().getVideoLocale() != null) {
         final int flagWidth = flag.getResources().getDimensionPixelSize(R.dimen.locale_flag_size_width);
         final int flagHeight = flag.getResources().getDimensionPixelSize(R.dimen.locale_flag_size_width);
         flag.setController(GraphicUtils.provideFrescoResizingController(getModelObject().getVideoLocale().getImage(),
               flag.getController(), flagWidth, flagHeight));
         flag.setContentDescription(getModelObject().getVideoLocale().getCountry());
         languageCaption.setText(getModelObject().getVideoLanguage().getTitle());
      } else {
         flag.setImageURI(Uri.EMPTY);
      }
   }

   @OnClick(R.id.wrapper_spinner_language)
   void onLanguageClicked() {
      if (cellDelegate != null) cellDelegate.onLanguageClicked();
   }

   @Override
   public boolean shouldInject() {
      return false;
   }
}
