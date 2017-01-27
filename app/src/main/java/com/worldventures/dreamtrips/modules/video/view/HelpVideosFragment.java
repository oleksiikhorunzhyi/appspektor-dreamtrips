package com.worldventures.dreamtrips.modules.video.view;

import android.os.Bundle;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.reptools.view.fragment.TrainingVideosFragment;
import com.worldventures.dreamtrips.modules.video.presenter.HelpVideosPresenter;

import rx.Observable;
import rx.subjects.PublishSubject;

@Layout(R.layout.fragment_help_videos)
public class HelpVideosFragment extends TrainingVideosFragment<HelpVideosPresenter> implements HelpVideosPresenter.View {

   private PublishSubject<Boolean> visibilityStream = PublishSubject.create();

   @Override
   protected HelpVideosPresenter createPresenter(Bundle savedInstanceState) {
      return new HelpVideosPresenter();
   }

   @Override
   public void onResume() {
      super.onResume();
      visibilityStream.onNext(true);
   }

   @Override
   public void setUserVisibleHint(boolean isVisibleToUser) {
      super.setUserVisibleHint(isVisibleToUser);
      visibilityStream.onNext(isVisibleToUser);
   }

   @Override
   public Observable<Boolean> visibilityStream() {
      return visibilityStream.asObservable();
   }

}
