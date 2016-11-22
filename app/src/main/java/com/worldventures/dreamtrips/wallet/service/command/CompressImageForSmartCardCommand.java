package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.util.SmartCardAvatarHelper;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;

import javax.inject.Inject;

import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class CompressImageForSmartCardCommand extends SmartCardAvatarCommand implements InjectableAction {

   @Inject SmartCardAvatarHelper smartCardAvatarHelper;

   private final String schemePath;
   private final int imageSize;

   public CompressImageForSmartCardCommand(String schemePath) {
      this(schemePath, DEFAULT_IMAGE_SIZE);
   }

   public CompressImageForSmartCardCommand(String schemePath, int imageSize) {
      this.schemePath = schemePath;
      this.imageSize = imageSize;
   }

   @Override
   protected void run(CommandCallback<SmartCardUserPhoto> callback) throws Throwable {
      smartCardAvatarHelper.compressPhotoFromSchemePath(schemePath)
            .flatMap(original -> Observable.fromCallable(() -> ImmutableSmartCardUserPhoto.builder()
                  .original(original)
                  .monochrome(smartCardAvatarHelper.toMonochromeFile(original, imageSize))
                  .build()))
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
