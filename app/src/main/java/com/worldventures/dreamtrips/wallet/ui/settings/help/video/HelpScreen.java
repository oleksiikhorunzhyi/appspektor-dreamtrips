package com.worldventures.dreamtrips.wallet.ui.settings.help.video;

import com.worldventures.dreamtrips.modules.video.model.CachedEntity;
import com.worldventures.dreamtrips.modules.video.model.Video;
import com.worldventures.dreamtrips.modules.video.model.VideoLocale;
import com.worldventures.dreamtrips.modules.video.service.command.GetMemberVideosCommand;
import com.worldventures.dreamtrips.modules.video.service.command.GetVideoLocalesCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

import java.util.List;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface HelpScreen extends WalletScreen {

   void provideVideos(List<Video> videos);

   void provideVideoLocales(List<VideoLocale> videoLocales);

   OperationView<GetMemberVideosCommand> provideOperationLoadVideos();

   OperationView<GetVideoLocalesCommand> provideOperationLoadLanguages();

   void confirmCancelDownload(CachedEntity entity);

   void confirmDeleteVideo(CachedEntity entity);

   void notifyItemChanged(CachedEntity cachedEntity);

   List<Video> getCurrentItems();

   void showDialogChosenLanguage(VideoLocale videoLocale);

   void setSelectedLocale(int index);
}
