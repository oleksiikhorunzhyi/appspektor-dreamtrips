package com.worldventures.wallet.ui.settings.help.video;

import com.worldventures.core.model.CachedModel;
import com.worldventures.core.modules.video.model.VideoLocale;
import com.worldventures.core.modules.video.service.command.GetMemberVideosCommand;
import com.worldventures.core.modules.video.service.command.GetVideoLocalesCommand;
import com.worldventures.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.wallet.ui.settings.help.video.model.WalletVideoModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface WalletHelpVideoScreen extends WalletScreen {

   void setVideos(@NotNull ArrayList<WalletVideoModel> videos);

   void setVideoLocales(@NotNull ArrayList<VideoLocale> videoLocales);

   OperationView<GetMemberVideosCommand> provideOperationLoadVideos();

   OperationView<GetVideoLocalesCommand> provideOperationLoadLanguages();

   void confirmCancelDownload(CachedModel entity);

   void confirmDeleteVideo(CachedModel entity);

   void notifyItemChanged(CachedModel cachedEntity);

   List<WalletVideoModel> getCurrentItems();

   void showDialogChosenLanguage(VideoLocale videoLocale);

   void setSelectedLocale(int index);

   void showRefreshing(boolean show);
}
