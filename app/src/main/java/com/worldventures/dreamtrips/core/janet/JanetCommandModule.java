package com.worldventures.dreamtrips.core.janet;

import com.messenger.di.MessengerJanetCommandModule;
import com.worldventures.dreamtrips.core.api.uploadery.SimpleUploaderyCommand;
import com.worldventures.dreamtrips.core.api.uploadery.UploaderyImageCommand;
import com.worldventures.dreamtrips.modules.auth.api.command.LoginCommand;
import com.worldventures.dreamtrips.modules.auth.api.command.LogoutCommand;
import com.worldventures.dreamtrips.modules.auth.api.command.UnsubribeFromPushCommand;
import com.worldventures.dreamtrips.modules.auth.api.command.UpdateUserCommand;
import com.worldventures.dreamtrips.modules.common.command.AcceptTermsCommand;
import com.worldventures.dreamtrips.modules.common.command.CleanTempDirectoryCommand;
import com.worldventures.dreamtrips.modules.common.command.ClearStoragesCommand;
import com.worldventures.dreamtrips.modules.common.command.CopyFileCommand;
import com.worldventures.dreamtrips.modules.common.command.DeleteCachedModelCommand;
import com.worldventures.dreamtrips.modules.common.command.DownloadCachedModelCommand;
import com.worldventures.dreamtrips.modules.common.command.GetVideoDurationCommand;
import com.worldventures.dreamtrips.modules.common.command.InitializeCommand;
import com.worldventures.dreamtrips.modules.common.command.MediaCaptureCanceledCommand;
import com.worldventures.dreamtrips.modules.common.command.ResetCachedModelsInProgressCommand;
import com.worldventures.dreamtrips.modules.common.command.SubscribeToPushNotificationsCommand;
import com.worldventures.dreamtrips.modules.common.command.TripsFilterDataCommand;
import com.worldventures.dreamtrips.modules.common.command.UpdateStatusCachedEntityCommand;
import com.worldventures.dreamtrips.social.di.SocialJanetCommandModule;
import com.worldventures.dreamtrips.modules.dtl_flow.di.DtlJanetCommandModule;

import dagger.Module;

@Module(includes = {
      MessengerJanetCommandModule.class,
      DtlJanetCommandModule.class,
      SocialJanetCommandModule.class,
}, injects = {
      CopyFileCommand.class,
      AcceptTermsCommand.class,
      TripsFilterDataCommand.class,
      InitializeCommand.class,
      SubscribeToPushNotificationsCommand.class,
      ClearStoragesCommand.class,
      SubscribeToPushNotificationsCommand.class,
      DeleteCachedModelCommand.class,
      DownloadCachedModelCommand.class,
      ResetCachedModelsInProgressCommand.class,
      UpdateStatusCachedEntityCommand.class,
      CleanTempDirectoryCommand.class,
      MediaCaptureCanceledCommand.class,
      GetVideoDurationCommand.class,
      UploaderyImageCommand.class,
      SimpleUploaderyCommand.class,
      LoginCommand.class,
      UpdateUserCommand.class,
      UnsubribeFromPushCommand.class,
      LogoutCommand.class,

},
        complete = false, library = true)
class JanetCommandModule {
}
