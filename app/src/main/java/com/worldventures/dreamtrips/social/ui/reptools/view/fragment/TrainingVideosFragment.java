package com.worldventures.dreamtrips.social.ui.reptools.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.core.modules.video.model.Video;
import com.worldventures.core.modules.video.model.VideoLocale;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.adapter.BaseDelegateAdapter;
import com.worldventures.core.ui.view.custom.EmptyRecyclerView;
import com.worldventures.core.ui.view.recycler.RecyclerViewStateDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.membership.model.MediaHeader;
import com.worldventures.dreamtrips.social.ui.membership.view.dialog.FilterLanguageDialogFragment;
import com.worldventures.dreamtrips.social.ui.reptools.presenter.TrainingVideosPresenter;
import com.worldventures.dreamtrips.social.ui.video.cell.MediaHeaderLightCell;
import com.worldventures.dreamtrips.social.ui.video.cell.VideoCell;
import com.worldventures.dreamtrips.social.ui.video.cell.delegate.VideoHeaderDelegate;
import com.worldventures.dreamtrips.social.ui.video.view.VideoBaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

import static com.worldventures.dreamtrips.social.ui.video.view.util.VideoViewFunctionsKt.provideGridLayoutManager;

@Layout(R.layout.fragment_presentation_videos)
public class TrainingVideosFragment extends VideoBaseFragment<TrainingVideosPresenter>
      implements TrainingVideosPresenter.View {

   @InjectView(R.id.lv_items) protected EmptyRecyclerView recyclerView;
   @InjectView(R.id.ll_empty_view) protected ViewGroup emptyView;

   protected BaseDelegateAdapter<Object> adapter;
   private RecyclerViewStateDelegate stateDelegate;
   private FilterLanguageDialogFragment dialog;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      stateDelegate = new RecyclerViewStateDelegate();
      stateDelegate.onCreate(savedInstanceState);
      dialog = new FilterLanguageDialogFragment();
   }

   @Override
   protected TrainingVideosPresenter createPresenter(Bundle savedInstanceState) {
      return new TrainingVideosPresenter();
   }

   @Override
   public void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
      stateDelegate.saveStateIfNeeded(outState);
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      stateDelegate.setRecyclerView(recyclerView);

      adapter = new BaseDelegateAdapter<>(getActivity(), this);
      adapter.registerCell(Video.class, VideoCell.class);
      adapter.registerDelegate(Video.class, this);
      adapter.registerCell(MediaHeader.class, MediaHeaderLightCell.class);
      adapter.registerDelegate(MediaHeader.class, provideVideoHeaderDelegate());

      recyclerView.setAdapter(adapter);
      recyclerView.setLayoutManager(provideGridLayoutManager(getContext(), adapter));
      recyclerView.setEmptyView(emptyView);

      refreshLayout.setOnRefreshListener(this);
      refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
   }

   private VideoHeaderDelegate provideVideoHeaderDelegate() {
      return new VideoHeaderDelegate() {
         @Override
         public void onLanguageClicked() {
            showDialog();
         }

         @Override
         public void onCellClicked(MediaHeader model) {
            // ignore click event
         }
      };
   }

   @Override
   public void setItems(@NonNull List<?> videos) {
      adapter.setItems(new ArrayList<>(videos));
   }

   @Override
   public void notifyItemChanged(@NonNull String uuid) {
      adapter.notifyDataSetChanged();
   }

   @Override
   public void finishLoading() {
      super.finishLoading();
      stateDelegate.restoreStateIfNeeded();
   }

   @Override
   public void setLocales(@NonNull ArrayList<VideoLocale> locales, @Nullable VideoLocale defaultValue) {
      dialog.setData(locales);
   }

   @Override
   public void showDialog() {
      if (!dialog.isAdded()) {
         dialog.setSelectionListener((locale, language) -> getPresenter().onLanguageSelected(locale, language));
         dialog.show(getChildFragmentManager(), FilterLanguageDialogFragment.class.getSimpleName());
      }
   }

   @Override
   protected void trackViewFromViewPagerIfNeeded() {
      getPresenter().trackView();
   }
}
