package com.worldventures.dreamtrips.wallet.ui.settings.help.video.holder;

import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.BaseHolder;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.delegate.WalletVideoCallback;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.model.WalletVideoModel;

import static android.databinding.DataBindingUtil.bind;
import static android.view.LayoutInflater.from;

public class VideoHolderFactoryImpl implements VideoTypeFactory {

   private final WalletVideoCallback videoActionsCallback;
   private final WalletVideoHolderDelegate videoHolderDelegate;

   public VideoHolderFactoryImpl(WalletVideoCallback videoActionsCallback, WalletVideoHolderDelegate videoHolderDelegate) {
      this.videoHolderDelegate = videoHolderDelegate;
      this.videoActionsCallback = videoActionsCallback;
   }

   @Override
   public int type(WalletVideoModel video) {
      return R.layout.adapter_item_video;
   }

   @Override
   public BaseHolder holder(ViewGroup parent, int viewType) {
      if (viewType == R.layout.adapter_item_video) {
         return new WalletVideoHolder(bind(from(parent.getContext()).inflate(viewType, parent, false)),
               videoActionsCallback, videoHolderDelegate);
      } else {
         throw new IllegalArgumentException();
      }
   }
}
