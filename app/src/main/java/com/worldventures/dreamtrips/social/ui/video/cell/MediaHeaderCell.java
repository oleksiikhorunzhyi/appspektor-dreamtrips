package com.worldventures.dreamtrips.social.ui.video.cell;

import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.core.modules.video.model.VideoLanguage;
import com.worldventures.core.modules.video.model.VideoLocale;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.util.GraphicUtils;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;
import com.worldventures.dreamtrips.social.ui.membership.model.MediaHeader;
import com.worldventures.dreamtrips.social.ui.video.cell.delegate.VideoHeaderDelegate;
import com.worldventures.dreamtrips.social.util.ViewUtilsKt;

import butterknife.InjectView;
import butterknife.OnClick;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

@Layout(R.layout.adapter_media_header)
public class MediaHeaderCell extends BaseAbstractDelegateCell<MediaHeader, VideoHeaderDelegate> {

   @InjectView(R.id.header) TextView header;
   @InjectView(R.id.wrapper_spinner_language) View language;
   @InjectView(R.id.language) TextView languageCaption;
   @InjectView(R.id.spinner_language) SimpleDraweeView flag;

   public MediaHeaderCell(View view) {
      super(view);

      header.setTextColor(ViewUtilsKt.getColor(itemView, R.color.white));
   }

   @Override
   protected void syncUIStateWithModel() {
      MediaHeader mediaHeader = getModelObject();
      VideoLocale videoLocale = mediaHeader.getVideoLocale();
      VideoLanguage videoLanguage = mediaHeader.getVideoLanguage();

      if (TextUtils.isEmpty(mediaHeader.getTitle())) {
         header.setText(itemView.getContext().getString(R.string.recently_added));
      } else {
         header.setText(mediaHeader.getTitle());
      }

      if (videoLocale != null && videoLanguage != null) {
         final int flagWidth = flag.getResources().getDimensionPixelSize(R.dimen.locale_flag_size_width);
         final int flagHeight = flag.getResources().getDimensionPixelSize(R.dimen.locale_flag_size_width);

         flag.setController(GraphicUtils.provideFrescoResizingController(videoLocale.getImage(), flag.getController(),
               flagWidth, flagHeight));
         flag.setContentDescription(videoLocale.getCountry());
         languageCaption.setText(videoLanguage.getTitle());
      } else {
         flag.setImageURI(Uri.EMPTY);
      }

      language.setVisibility(mediaHeader.getShowLanguage() ? VISIBLE : INVISIBLE);
   }

   @OnClick(R.id.wrapper_spinner_language)
   void onLanguageClicked() {
      if (cellDelegate != null) {
         cellDelegate.onLanguageClicked();
      }
   }

   @Override
   public boolean shouldInject() {
      return false;
   }
}
