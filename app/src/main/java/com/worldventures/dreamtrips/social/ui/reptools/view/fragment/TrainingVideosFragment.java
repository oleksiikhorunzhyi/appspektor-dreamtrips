package com.worldventures.dreamtrips.social.ui.reptools.view.fragment;

import android.os.Bundle;
import android.view.View;

import com.worldventures.core.modules.video.model.VideoLocale;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.membership.model.MediaHeader;
import com.worldventures.dreamtrips.social.ui.membership.view.dialog.FilterLanguageDialogFragment;
import com.worldventures.dreamtrips.social.ui.reptools.presenter.TrainingVideosPresenter;
import com.worldventures.dreamtrips.social.ui.video.cell.delegate.VideoHeaderDelegate;
import com.worldventures.dreamtrips.social.ui.video.view.PresentationVideosFragment;

import java.util.List;

@Layout(R.layout.fragment_presentation_videos)
public class TrainingVideosFragment<T extends TrainingVideosPresenter> extends PresentationVideosFragment<T> implements TrainingVideosPresenter.View {

   private FilterLanguageDialogFragment dialog = new FilterLanguageDialogFragment();

   private VideoHeaderDelegate videoHeaderDelegate = new VideoHeaderDelegate() {
      @Override
      public void onLanguageClicked() {
         showDialog();
      }

      @Override
      public void onCellClicked(MediaHeader model) { }
   };

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      adapter.registerDelegate(MediaHeader.class, videoHeaderDelegate);
   }

   @Override
   protected T createPresenter(Bundle savedInstanceState) {
      return (T) new TrainingVideosPresenter();
   }

   @Override
   public void setLocales(List<VideoLocale> locales, VideoLocale defaultValue) {
      dialog.setData(locales);
   }

   @Override
   public void localeLoaded() {
      adapter.notifyDataSetChanged();
   }

   @Override
   public void showDialog() {
      if (!dialog.isAdded()) {
         dialog.setSelectionListener((locale, language) -> getPresenter().onLanguageSelected(locale, language));
         dialog.show(getChildFragmentManager(), FilterLanguageDialogFragment.class.getSimpleName());
      }
   }
}
