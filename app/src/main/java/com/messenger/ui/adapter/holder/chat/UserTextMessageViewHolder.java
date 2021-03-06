package com.messenger.ui.adapter.holder.chat;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.messenger.util.TruncateUtils;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.messenger.messengerservers.constant.TranslationStatus.ERROR;
import static com.messenger.messengerservers.constant.TranslationStatus.REVERTED;
import static com.messenger.messengerservers.constant.TranslationStatus.TRANSLATED;
import static com.messenger.messengerservers.constant.TranslationStatus.TRANSLATING;


@Layout(R.layout.list_item_chat_user_text_message)
public class UserTextMessageViewHolder extends TextMessageViewHolder {

   @InjectView(R.id.translation_progress) ProgressBar translationProgress;
   @InjectView(R.id.translation_status) TextView translationStatus;

   public UserTextMessageViewHolder(View itemView) {
      super(itemView);
   }

   @Override
   public void showMessage() {
      applyTranslationStatus();
   }

   public void applyTranslationStatus() {
      setTranslationUiState();

      if (dataTranslation == null) { // means not translated message
         setNotTranslated();
         return;
      }

      switch (dataTranslation.getTranslateStatus()) {
         case ERROR:
            setTranslationError();
            break;
         case TRANSLATING:
            setTranslating();
            break;
         case TRANSLATED:
            setIsTranslated();
            break;
         case REVERTED:
            setNotTranslated();
            break;
         default:
            break;
      }
   }

   private void setTranslationUiState() {
      int status = dataTranslation == null ? -10 : dataTranslation.getTranslateStatus();
      translationProgress.setVisibility(status == TRANSLATING ? VISIBLE : GONE);
      messageTextView.setVisibility(status == TRANSLATING ? INVISIBLE : VISIBLE);
      translationStatus.setVisibility(status == TRANSLATING ? GONE : VISIBLE);
   }

   public void setTranslating() {
      messageTextView.setText(dataMessage.getText());
   }

   public void setNotTranslated() {
      translationStatus.setVisibility(GONE);
      messageTextView.setText(dataMessage.getText());
   }

   public void setTranslationError() {
      translationStatus.setText(itemView.getResources().getText(R.string.translate_error));
      translationStatus.setTextColor(itemView.getResources().getColor(R.color.translation_state_error));

      messageTextView.setText(TruncateUtils.truncate(dataMessage.getText(), messageTextView.getResources()
            .getInteger(R.integer.messenger_max_message_length)));
   }

   public void setIsTranslated() {
      translationStatus.setText(itemView.getResources().getString(R.string.translate_from));
      translationStatus.setTextColor(itemView.getResources().getColor(R.color.translation_state_translated));

      messageTextView.setText(TruncateUtils.truncate(dataTranslation.getTranslation(), messageTextView.getResources()
            .getInteger(R.integer.messenger_max_message_length)));
   }
}
