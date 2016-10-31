package com.worldventures.dreamtrips.modules.reptools.view.fragment;

import android.os.Bundle;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.membership.model.MediaHeader;
import com.worldventures.dreamtrips.modules.membership.view.dialog.FilterLanguageDialogFragment;
import com.worldventures.dreamtrips.modules.reptools.presenter.TrainingVideosPresenter;
import com.worldventures.dreamtrips.modules.video.cell.delegate.VideoHeaderDelegate;
import com.worldventures.dreamtrips.modules.video.model.VideoLocale;
import com.worldventures.dreamtrips.modules.video.view.PresentationVideosFragment;

import java.util.List;

@Layout(R.layout.fragment_presentation_videos)
public class TrainingVideosFragment extends PresentationVideosFragment<TrainingVideosPresenter> implements TrainingVideosPresenter.View {

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
   protected TrainingVideosPresenter createPresenter(Bundle savedInstanceState) {
      return new TrainingVideosPresenter();
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
