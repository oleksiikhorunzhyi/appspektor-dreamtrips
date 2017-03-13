package com.worldventures.dreamtrips.wallet.service.command;

import android.net.Uri;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.util.SmartCardAvatarHelper;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;

import java.io.File;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class SmartCardAvatarCommand extends Command<SmartCardUserPhoto> implements InjectableAction {

   @Inject SmartCardAvatarHelper smartCardAvatarHelper;

   private final String dataString;
   private final DataSource dataSource;

   private SmartCardAvatarCommand(String dataString, DataSource dataSource) {
      this.dataString = dataString;
      this.dataSource = dataSource;
   }

   public static SmartCardAvatarCommand fromSchemePath(String schemePath) {
      return new SmartCardAvatarCommand(schemePath, DataSource.FILE);
   }

   public static SmartCardAvatarCommand fromUrl(String url) {
      return new SmartCardAvatarCommand(url, DataSource.URL);
   }

   @Override
   protected void run(CommandCallback<SmartCardUserPhoto> callback) throws Throwable {
      getPhotoFileObservable()
            .map(original -> ImmutableSmartCardUserPhoto.builder()
                  .original(original)
                  .photoUrl(constructDataString(original))
                  .build())
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private String constructDataString(File original) {
      Uri dataUri = dataSource == DataSource.FILE
            ? Uri.fromFile(original)
            : Uri.parse(dataString);
      return dataUri.toString();
   }

   private Observable<File> getPhotoFileObservable() {
      if (dataSource == DataSource.FILE) {
         return smartCardAvatarHelper.compressPhotoFromSchemePath(dataString);
      } else {
         return smartCardAvatarHelper.compressPhotoFromUrl(dataString);
      }
   }

   private enum DataSource {FILE, URL}
}
