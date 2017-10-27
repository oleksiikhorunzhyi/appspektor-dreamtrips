package com.worldventures.dreamtrips.social.ui.bucketlist.view.util;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

public class TranslateBucketItemViewInjector {

   @InjectView(R.id.textViewName) TextView textViewName;
   @InjectView(R.id.translation_progress) ProgressBar translationProgress;
   @InjectView(R.id.translate) TextView translateButton;
   @InjectView(R.id.translated_from) TextView translatedFrom;
   @Optional @InjectView(R.id.textViewDescription) TextView textViewDescription;

   private Context context;
   private SessionHolder appSessionHolder;

   public TranslateBucketItemViewInjector(View rootView, Context context, SessionHolder appSessionHolder) {
      this.context = context;
      this.appSessionHolder = appSessionHolder;
      ButterKnife.inject(this, rootView);
   }

   public void processTranslation(BucketItem bucketItem) {
      // bucket owner is null for own bucket items
      if (!appSessionHolder.get().isPresent() || bucketItem.getOwner() == null) {
         hideTranslationUi(bucketItem);
         return;
      }
      boolean ownItem = bucketItem.getOwner().getId() == appSessionHolder.get().get().user().getId();
      boolean ownLanguage = LocaleHelper.isOwnLanguage(appSessionHolder, bucketItem.getLanguage());
      boolean emptyLanguage = TextUtils.isEmpty(bucketItem.getLanguage());

      if (ownItem || ownLanguage || emptyLanguage) {
         hideTranslationUi(bucketItem);
      } else {
         if (bucketItem.isTranslated()) {
            textViewName.setText(bucketItem.getTranslation());
            setTextForDescription(bucketItem.getTranslationDescription());
            translateButton.setVisibility(View.VISIBLE);
            translateButton.setText(R.string.show_original);
            translationProgress.setVisibility(View.GONE);
            translatedFrom.setVisibility(View.VISIBLE);
            translatedFrom.setText(context.getString(R.string.translated_from, new Locale(bucketItem.getLanguage()).getDisplayLanguage()));
         } else {
            textViewName.setText(bucketItem.getName());
            setTextForDescription(bucketItem.getDescription());
            translateButton.setVisibility(View.VISIBLE);
            translateButton.setText(R.string.translate);
            translatedFrom.setVisibility(View.GONE);
            translationProgress.setVisibility(View.GONE);
         }
      }
   }

   private void hideTranslationUi(BucketItem bucketItem) {
      textViewName.setText(bucketItem.getName());
      setTextForDescription(bucketItem.getDescription());
      translateButton.setVisibility(View.GONE);
      translationProgress.setVisibility(View.GONE);
      translatedFrom.setVisibility(View.GONE);
   }

   private void setTextForDescription(String text) {
      if (textViewDescription != null) {
         if (TextUtils.isEmpty(text)) {
            textViewDescription.setVisibility(View.GONE);
         } else {
            textViewDescription.setVisibility(View.VISIBLE);
            textViewDescription.setText(text);
         }
      }
   }

   public void translatePressed() {
      translateButton.setVisibility(View.INVISIBLE);
      translationProgress.setVisibility(View.VISIBLE);
   }
}
