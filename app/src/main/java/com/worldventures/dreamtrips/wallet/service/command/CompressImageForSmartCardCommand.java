package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.util.SmartCardAvatarHelper;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;

import java.io.File;

import javax.inject.Inject;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class CompressImageForSmartCardCommand extends SmartCardAvatarCommand implements InjectableAction {

   @Inject SmartCardAvatarHelper smartCardAvatarHelper;

   private final String filePath;
   private final int imageSize;

   public CompressImageForSmartCardCommand(String filePath) {
      this(filePath, DEFAULT_IMAGE_SIZE);
   }

   public CompressImageForSmartCardCommand(String filePath, int imageSize) {
      this.filePath = filePath;
      this.imageSize = imageSize;
   }

   @Override
   protected void run(CommandCallback<SmartCardUserPhoto> callback) throws Throwable {
      try {
         File original = smartCardAvatarHelper.compressPhotoFromFile(filePath, imageSize);
         SmartCardUserPhoto photo = ImmutableSmartCardUserPhoto.builder()
               .original(original)
               .monochrome(smartCardAvatarHelper.toMonochromeFile(original))
               .build();
         callback.onSuccess(photo);
      } catch (Throwable throwable) {
         callback.onFail(throwable);
      }
   }
}
