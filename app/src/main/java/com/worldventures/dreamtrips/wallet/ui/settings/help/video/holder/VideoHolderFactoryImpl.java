package com.worldventures.dreamtrips.wallet.ui.settings.help.video.holder;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.databinding.AdapterItemVideoBinding;
import com.worldventures.dreamtrips.modules.video.utils.CachedModelHelper;
import com.worldventures.dreamtrips.wallet.ui.settings.common.model.WalletVideo;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.BaseHolder;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.delegate.WalletVideoCallback;

public class VideoHolderFactoryImpl implements VideoTypeFactory {

   private final WalletVideoCallback videoActionsCallback;
   private final CachedModelHelper cachedModelHelper;
   private final SessionHolder<UserSession> appSessionHolder;

   public VideoHolderFactoryImpl(CachedModelHelper cachedModelHelper, WalletVideoCallback videoActionsCallback,
         SessionHolder<UserSession> appSessionHolder) {
      this.cachedModelHelper = cachedModelHelper;
      this.videoActionsCallback = videoActionsCallback;
      this.appSessionHolder = appSessionHolder;
   }

   @Override
   public int type(WalletVideo video) {
      return R.layout.adapter_item_video;
   }

   @Override
   public BaseHolder holder(ViewGroup parent, int viewType) {
      switch (viewType) {
         case R.layout.adapter_item_video:
            AdapterItemVideoBinding videoBinding = DataBindingUtil
                  .bind(LayoutInflater
                        .from(parent.getContext()).inflate(viewType, parent, false));
            return new WalletVideoHolder(videoBinding, cachedModelHelper, videoActionsCallback, appSessionHolder);
         default:
            throw new IllegalArgumentException();
      }
   }
}
