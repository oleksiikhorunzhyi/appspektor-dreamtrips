package com.worldventures.dreamtrips.wallet.ui.settings.help.video.delegate;

import com.worldventures.dreamtrips.modules.video.model.CachedModel;
import com.worldventures.dreamtrips.wallet.ui.settings.common.model.WalletVideo;

public interface WalletVideoCallback {

   void sendAnalytic(String action, String name);

   void onDownloadVideo(CachedModel entity);

   void onDeleteVideo(CachedModel entity);

   void onCancelCachingVideo(CachedModel entity);

   void onPlayVideoClicked(WalletVideo entity);
}
