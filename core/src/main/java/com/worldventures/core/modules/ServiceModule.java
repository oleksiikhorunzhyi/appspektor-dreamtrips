package com.worldventures.core.modules;


import android.content.Context;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.core.service.CachedEntityDelegate;
import com.worldventures.core.service.CachedEntityInteractor;
import com.worldventures.core.service.ConnectionInfoProvider;
import com.worldventures.core.service.ConnectionInfoProviderImpl;
import com.worldventures.core.service.DeviceInfoProvider;
import com.worldventures.core.service.DeviceInfoProviderImpl;
import com.worldventures.core.service.DownloadFileInteractor;
import com.worldventures.core.service.FilePathProvider;
import com.worldventures.core.service.UploadingFileManager;
import com.worldventures.core.service.UriPathProvider;
import com.worldventures.core.service.UriPathProviderImpl;
import com.worldventures.core.service.command.DeleteCachedModelCommand;
import com.worldventures.core.service.command.DownloadCachedModelCommand;
import com.worldventures.core.ui.util.DrawableUtil;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {
            DeleteCachedModelCommand.class,
            DownloadCachedModelCommand.class,
      },
      complete = false,
      library = true)
public class ServiceModule {

   @Provides
   @Singleton
   FilePathProvider provideFilePathProvider(Context context) {
      return new FilePathProvider(context);
   }

   @Provides
   @Singleton
   ConnectionInfoProvider connectionInfoProvider(Context context) {
      return new ConnectionInfoProviderImpl(context);
   }

   @Provides
   @Singleton
   UriPathProvider provideUriPathProvider(Context context) {
      return new UriPathProviderImpl(context);
   }

   @Provides
   @Singleton
   DeviceInfoProvider provideProfileInteractor(Context context) {
      return new DeviceInfoProviderImpl(context);
   }

   @Provides
   @Singleton
   DrawableUtil provideDrawableUtil(Context context) {
      return new DrawableUtil(context);
   }

   @Provides
   @Singleton
   UploadingFileManager provideUploadingFileManager(Context context) {
      return new UploadingFileManager(context.getFilesDir());
   }

   @Provides
   @Singleton
   DownloadFileInteractor provideDownloadFileInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new DownloadFileInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   CachedEntityInteractor provideDownloadCachedEntityInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new CachedEntityInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   CachedEntityDelegate provideDownloadFileDelegate(CachedEntityInteractor cachedEntityInteractor) {
      return new CachedEntityDelegate(cachedEntityInteractor);
   }
}
