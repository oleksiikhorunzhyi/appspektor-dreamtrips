package com.worldventures.dreamtrips.core.janet;

import com.messenger.di.MessengerJanetCommandModule;
import com.worldventures.core.modules.auth.api.command.LoginCommand;
import com.worldventures.core.modules.auth.api.command.LogoutCommand;
import com.worldventures.core.modules.auth.api.command.UpdateUserCommand;
import com.worldventures.core.modules.picker.command.CopyFileCommand;
import com.worldventures.core.modules.picker.command.GetVideoDurationCommand;
import com.worldventures.core.modules.picker.command.MediaCaptureCanceledCommand;
import com.worldventures.core.modules.video.service.command.ResetCachedModelsInProgressCommand;
import com.worldventures.core.modules.video.service.command.UpdateStatusCachedEntityCommand;
import com.worldventures.core.service.command.DeleteCachedModelCommand;
import com.worldventures.core.service.command.DownloadCachedModelCommand;
import com.worldventures.dreamtrips.modules.common.command.CleanTempDirectoryCommand;
import com.worldventures.dreamtrips.modules.common.command.ClearStoragesCommand;
import com.worldventures.dreamtrips.modules.common.command.InitializeCommand;
import com.worldventures.dreamtrips.modules.common.command.SubscribeToPushNotificationsCommand;
import com.worldventures.dreamtrips.modules.trips.service.command.GetTripsFilterDataCommand;
import com.worldventures.dreamtrips.modules.dtl_flow.di.DtlJanetCommandModule;
import com.worldventures.dreamtrips.modules.gcm.command.UnsubscribeFromPushCommand;
import com.worldventures.dreamtrips.social.di.SocialJanetCommandModule;

import dagger.Module;

@Module(includes = {
      MessengerJanetCommandModule.class,
      DtlJanetCommandModule.class,
      SocialJanetCommandModule.class,
}, injects = {
      CopyFileCommand.class,
      GetTripsFilterDataCommand.class,
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
      LoginCommand.class,
      UpdateUserCommand.class,
      UnsubscribeFromPushCommand.class,
      LogoutCommand.class,

},
        complete = false, library = true)
class JanetCommandModule {
}
