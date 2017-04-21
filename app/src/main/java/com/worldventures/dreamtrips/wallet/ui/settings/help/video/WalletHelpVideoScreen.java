package com.worldventures.dreamtrips.wallet.ui.settings.help.video;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;

import butterknife.InjectView;

public class WalletHelpVideoScreen extends WalletLinearLayout<WalletHelpVideoPresenter.Screen, WalletHelpVideoPresenter, WalletHelpVideoPath> implements WalletHelpVideoPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.tv_video_coming_soon) TextView tvVideosEmpty;
   @InjectView(R.id.rv_videos) EmptyRecyclerView rvVideos;

   public WalletHelpVideoScreen(Context context) {
      super(context);
   }

   public WalletHelpVideoScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }

   @Override
   public WalletHelpVideoPresenter createPresenter() {
      return new WalletHelpVideoPresenter(getContext(), getInjector());
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
   }

   @Override
   protected void onAttachedToWindow() {
      super.onAttachedToWindow();
   }
}
