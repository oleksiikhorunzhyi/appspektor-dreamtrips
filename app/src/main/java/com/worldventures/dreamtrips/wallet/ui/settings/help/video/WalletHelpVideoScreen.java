package com.worldventures.dreamtrips.wallet.ui.settings.help.video;

import com.worldventures.dreamtrips.social.ui.video.model.CachedModel;
import com.worldventures.dreamtrips.social.ui.video.model.VideoLocale;
import com.worldventures.dreamtrips.social.ui.video.service.command.GetMemberVideosCommand;
import com.worldventures.dreamtrips.social.ui.video.service.command.GetVideoLocalesCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.model.WalletVideoModel;

import java.util.List;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface WalletHelpVideoScreen extends WalletScreen {

   void provideVideos(List<WalletVideoModel> videos);

   void provideVideoLocales(List<VideoLocale> videoLocales);

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
