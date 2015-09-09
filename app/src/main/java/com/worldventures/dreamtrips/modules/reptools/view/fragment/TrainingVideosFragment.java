package com.worldventures.dreamtrips.modules.reptools.view.fragment;

import android.os.Bundle;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.membership.view.dialog.FilterLanguageDialogFragment;
import com.worldventures.dreamtrips.modules.video.view.PresentationVideosFragment;
import com.worldventures.dreamtrips.modules.reptools.model.VideoLocale;
import com.worldventures.dreamtrips.modules.reptools.presenter.TrainingVideosPresenter;

import java.util.ArrayList;

@Layout(R.layout.fragment_presentation_videos)
public class TrainingVideosFragment extends PresentationVideosFragment<TrainingVideosPresenter>
        implements TrainingVideosPresenter.View {

    FilterLanguageDialogFragment dialog = new FilterLanguageDialogFragment();

    @Override
    protected TrainingVideosPresenter createPresenter(Bundle savedInstanceState) {
        return new TrainingVideosPresenter();
    }

    @Override
    public void setLocales(ArrayList<VideoLocale> locales, VideoLocale defaultValue) {
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