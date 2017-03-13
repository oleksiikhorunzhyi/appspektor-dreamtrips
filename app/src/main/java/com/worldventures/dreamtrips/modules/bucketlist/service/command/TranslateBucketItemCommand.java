package com.worldventures.dreamtrips.modules.bucketlist.service.command;

import android.text.TextUtils;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.feed.service.TranslationFeedInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.TranslateTextCachedCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.TranslateUidItemCommand;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import rx.schedulers.Schedulers;

@CommandAction
public class TranslateBucketItemCommand extends CommandWithError<BucketItem> implements InjectableAction {

   @Inject Janet janet;
   @Inject TranslationFeedInteractor translationInteractor;

   private BucketItem bucketItem;

   public TranslateBucketItemCommand(BucketItem bucketItem) {
      this.bucketItem = bucketItem;
   }

   @Override
   protected void run(CommandCallback<BucketItem> callback) throws Throwable {
      translationInteractor.translateFeedEntityPipe()
            .createObservableResult(new TranslateUidItemCommand.TranslateFeedEntityCommand(bucketItem,
                  LocaleHelper.getDefaultLocaleFormatted()))
            .flatMap(command -> translateDescription((BucketItem) command.getResult()))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<BucketItem> translateDescription(BucketItem translatedBucketItem) {
      if (TextUtils.isEmpty(translatedBucketItem.getDescription())) return Observable.just(bucketItem);
      return janet.createPipe(TranslateTextCachedCommand.class, Schedulers.io())
            .createObservableResult(new TranslateTextCachedCommand(translatedBucketItem.getDescription(),
                  LocaleHelper.getDefaultLocaleFormatted()))
            .map(TranslateTextCachedCommand::getResult)
            .map(translation -> {
               translatedBucketItem.setTranslationDescription(translation);
               return translatedBucketItem;
            });
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_translate_bucket_item;
   }
}
