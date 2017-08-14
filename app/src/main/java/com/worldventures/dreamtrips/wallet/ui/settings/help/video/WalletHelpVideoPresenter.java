package com.worldventures.dreamtrips.wallet.ui.settings.help.video;

import com.worldventures.dreamtrips.modules.video.model.CachedModel;
import com.worldventures.dreamtrips.modules.video.model.VideoLanguage;
import com.worldventures.dreamtrips.modules.video.model.VideoLocale;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.model.WalletVideoModel;

import java.util.List;

public interface WalletHelpVideoPresenter extends WalletPresenter<WalletHelpVideoScreen> {
   void goBack();

   void refreshVideos();

   void onSelectedLocale(VideoLocale item);

   void downloadVideo(CachedModel entity);

   void deleteCachedVideo(CachedModel entity);

   void cancelCachingVideo(CachedModel entity);

   void onPlayVideo(WalletVideoModel entity);

   void fetchSmartCardVideosForDefaultLocale(List<VideoLocale> videoLocales);

   void fetchVideoLocales();

   void onCancelAction(CachedModel entity);

   void onDeleteAction(CachedModel entity);

   void fetchSmartCardVideos(VideoLanguage videoLanguage);

   void onSelectLastLocale();

}
