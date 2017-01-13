package com.worldventures.dreamtrips.modules.bucketlist.view.util;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;

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
   private SessionHolder<UserSession> appSessionHolder;

   public TranslateBucketItemViewInjector(View rootView, Context context, SessionHolder<UserSession> appSessionHolder) {
      this.context = context;
      this.appSessionHolder = appSessionHolder;
      ButterKnife.inject(this, rootView);
   }

   public void processTranslation(BucketItem bucketItem) {
      boolean ownItem = bucketItem.getOwner().getId() == appSessionHolder.get().get().getUser().getId();
      boolean ownLanguage = LocaleHelper.isOwnLanguage(appSessionHolder, bucketItem.getLanguage());
      boolean emptyLanguage = TextUtils.isEmpty(bucketItem.getLanguage());

      if (ownItem || ownLanguage || emptyLanguage) {
         translateButton.setVisibility(View.GONE);
         translationProgress.setVisibility(View.GONE);
         translatedFrom.setVisibility(View.GONE);
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
