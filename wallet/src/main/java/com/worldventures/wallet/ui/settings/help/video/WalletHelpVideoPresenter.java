package com.worldventures.wallet.ui.settings.help.video;

import com.worldventures.core.model.CachedModel;
import com.worldventures.core.modules.video.model.VideoLanguage;
import com.worldventures.core.modules.video.model.VideoLocale;
import com.worldventures.wallet.ui.common.base.WalletPresenter;
import com.worldventures.wallet.ui.settings.help.video.model.WalletVideoModel;

public interface WalletHelpVideoPresenter extends WalletPresenter<WalletHelpVideoScreen> {

   void goBack();

   void fetchVideoAndLocales();

   void refreshVideos();

   void fetchVideos(final VideoLanguage videoLanguage);

   void onSelectedLocale(VideoLocale item);

   void downloadVideo(CachedModel entity);

   void deleteCachedVideo(CachedModel entity);

   void cancelCachingVideo(CachedModel entity);

   void onPlayVideo(WalletVideoModel entity);

   void onCancelAction(CachedModel entity);

   void onDeleteAction(CachedModel entity);

   void onSelectLastLocale();

}
