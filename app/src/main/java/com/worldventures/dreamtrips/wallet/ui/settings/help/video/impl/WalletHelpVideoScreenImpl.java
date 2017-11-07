package com.worldventures.dreamtrips.wallet.ui.settings.help.video.impl;

import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.core.model.CachedModel;
import com.worldventures.core.modules.video.model.VideoLocale;
import com.worldventures.core.modules.video.service.command.GetMemberVideosCommand;
import com.worldventures.core.modules.video.service.command.GetVideoLocalesCommand;
import com.worldventures.core.ui.view.custom.EmptyRecyclerView;
import com.worldventures.core.utils.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.MultiHolderAdapter;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.SimpleMultiHolderAdapter;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.http.HttpErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.WalletHelpVideoPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.WalletHelpVideoScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.adapter.VideoLanguagesAdapter;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.adapter.VideoLocaleAdapter;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.delegate.WalletVideoCallback;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.holder.VideoHolderFactoryImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.holder.WalletVideoHolderDelegate;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.model.WalletVideoModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

public class WalletHelpVideoScreenImpl extends WalletBaseController<WalletHelpVideoScreen, WalletHelpVideoPresenter> implements WalletHelpVideoScreen {

   @Inject WalletHelpVideoPresenter presenter;
   @Inject WalletVideoHolderDelegate videoHolderDelegate;
   @Inject HttpErrorHandlingUtil httpErrorHandlingUtil;

   private EmptyRecyclerView rvVideos;
   private SwipeRefreshLayout refreshLayout;
   private MultiHolderAdapter<WalletVideoModel> adapter;
   private VideoLocaleAdapter localeAdapter;
   private Spinner videoLocales;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final TextView tvVideosEmpty = view.findViewById(R.id.tv_video_coming_soon);
      rvVideos = view.findViewById(R.id.rv_videos);
      rvVideos.setEmptyView(tvVideosEmpty);
      initRefreshLayout(view);
      initAdapter();
      initToolbar(view);
   }

   private void initRefreshLayout(View view) {
      refreshLayout = view.findViewById(R.id.refresh_layout);
      refreshLayout.setOnRefreshListener(() -> getPresenter().refreshVideos());
   }

   private void initToolbar(View view) {
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
      toolbar.inflateMenu(R.menu.wallet_settings_videos);

      final MenuItem actionVideoLanguage = toolbar.getMenu().findItem(R.id.action_video_language);
      videoLocales = (Spinner) MenuItemCompat.getActionView(actionVideoLanguage);

      localeAdapter = new VideoLocaleAdapter(getContext(), new ArrayList<>());

      videoLocales.setAdapter(localeAdapter);
      videoLocales.setOnItemSelectedListener(itemLocaleSelectedListener);
   }

   private final AdapterView.OnItemSelectedListener itemLocaleSelectedListener = new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
         getPresenter().onSelectedLocale(localeAdapter.getItem(position));
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {}
   };

   private void initAdapter() {
      adapter = new SimpleMultiHolderAdapter<>(new ArrayList<>(), new VideoHolderFactoryImpl(videoActionsCallback, videoHolderDelegate));
      rvVideos.setAdapter(adapter);
   }

   private WalletVideoCallback videoActionsCallback = new WalletVideoCallback() {

      @Override
      public void onDownloadMedia(WalletVideoModel video) {
         getPresenter().downloadVideo(video.getVideo().getCacheEntity());
      }

      @Override
      public void onDeleteMedia(WalletVideoModel video) {
         getPresenter().deleteCachedVideo(video.getVideo().getCacheEntity());
      }

      @Override
      public void onCancelCachingMedia(WalletVideoModel video) {
         getPresenter().cancelCachingVideo(video.getVideo().getCacheEntity());
      }

      @Override
      public void onPlayVideoClicked(WalletVideoModel video) {
         getPresenter().onPlayVideo(video);
      }
   };

   @Override
   public void provideVideos(List<WalletVideoModel> videos) {
      adapter.clear();
      adapter.addItems(videos);
   }

   @Override
   public void provideVideoLocales(List<VideoLocale> videoLocales) {
      if (localeAdapter.isEmpty()) {
         getPresenter().fetchSmartCardVideosForDefaultLocale(videoLocales);
      }
      localeAdapter.clear();
      localeAdapter.addAll(videoLocales);
   }

   @Override
   public OperationView<GetMemberVideosCommand> provideOperationLoadVideos() {
      return new ComposableOperationView<>(
            new SimpleDialogProgressView<>(getContext(), R.string.wallet_settings_help_video_loading, false),
            ErrorViewFactory.<GetMemberVideosCommand>builder()
                  .addProvider(new HttpErrorViewProvider<>(getContext(), httpErrorHandlingUtil,
                        command -> getPresenter().fetchSmartCardVideosForDefaultLocale(null),
                        command -> getPresenter().goBack())
                  ).build()
      );
   }

   @Override
   public OperationView<GetVideoLocalesCommand> provideOperationLoadLanguages() {
      return new ComposableOperationView<>(
            new SimpleDialogProgressView<>(getContext(), R.string.wallet_settings_help_video_locales_loading, false),
            ErrorViewFactory.<GetVideoLocalesCommand>builder()
                  .addProvider(new HttpErrorViewProvider<>(getContext(), httpErrorHandlingUtil,
                        command -> getPresenter().fetchVideoLocales(),
                        command -> getPresenter().goBack())
                  ).build()
      );
   }

   @Override
   public void confirmCancelDownload(CachedModel entity) {
      new MaterialDialog.Builder(getContext())
            .content(R.string.wallet_settings_help_video_cancel_cached_video_text)
            .positiveText(R.string.wallet_label_yes)
            .onPositive((dialog, which) -> getPresenter().onCancelAction(entity))
            .negativeText(R.string.wallet_label_no)
            .onNegative((dialog, which) -> dialog.dismiss())
            .build()
            .show();
   }

   @Override
   public void confirmDeleteVideo(CachedModel entity) {
      new MaterialDialog.Builder(getContext())
            .content(R.string.wallet_settings_help_video_delete_cached_video_text)
            .positiveText(R.string.wallet_label_delete)
            .onPositive((dialog, which) -> getPresenter().onDeleteAction(entity))
            .negativeText(R.string.wallet_label_no)
            .onNegative((dialog, which) -> dialog.dismiss())
            .build()
            .show();
   }

   @Override
   public void notifyItemChanged(CachedModel cachedEntity) {
      adapter.notifyDataSetChanged();
   }

   @Override
   public List<WalletVideoModel> getCurrentItems() {
      return adapter.getItemCount() == 0 ? new ArrayList<>() : adapter.getItems();
   }

   @Override
   public void showDialogChosenLanguage(VideoLocale videoLocale) {
      new MaterialDialog.Builder(getContext())
            .adapter(new VideoLanguagesAdapter(getContext(), videoLocale.getLanguages()),
                  (dialog, itemView, which, text) -> {
                     getPresenter().fetchSmartCardVideos(videoLocale.getLanguages().get(which));
                     dialog.dismiss();
                  })
            .cancelListener(dialog -> getPresenter().onSelectLastLocale())
            .build()
            .show();
   }

   @Override
   public void setSelectedLocale(int index) {
      if (videoLocales != null) videoLocales.setSelection(index);
   }

   @Override
   public void showRefreshing(boolean show) {
      refreshLayout.setRefreshing(show);
   }

   @Override
   public WalletHelpVideoPresenter getPresenter() {
      return presenter;
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_settings_help_video, viewGroup, false);
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return false;
   }
}
