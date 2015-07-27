package com.worldventures.dreamtrips.modules.reptools.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.membership.view.dialog.FilterLanguageDialogFragment;
import com.worldventures.dreamtrips.modules.membership.view.fragment.PresentationVideosFragment;
import com.worldventures.dreamtrips.modules.reptools.model.VideoLocale;
import com.worldventures.dreamtrips.modules.reptools.presenter.TrainingVideosPresenter;

import java.util.ArrayList;

import butterknife.InjectView;

@Layout(R.layout.fragment_training_videos)
public class TrainingVideosFragment extends PresentationVideosFragment<TrainingVideosPresenter> implements TrainingVideosPresenter.View {

    @InjectView(R.id.spinner_language)
    SimpleDraweeView selectedLocaleView;

    @InjectView(R.id.wrapper_spinner_language)
    View wrapperSpinnerLanguage;

    FilterLanguageDialogFragment dialog = new FilterLanguageDialogFragment();

    @Override
    protected TrainingVideosPresenter createPresenter(Bundle savedInstanceState) {
        return new TrainingVideosPresenter();
    }

    @Override
    public void setLocales(ArrayList<VideoLocale> locales, VideoLocale defaultValue) {
        wrapperSpinnerLanguage.setOnClickListener(view -> {
            if (!dialog.isAdded()) {
                dialog.setSelectionListener((locale, language) -> getPresenter().onLanguageSelected(locale, language));
                dialog.show(getChildFragmentManager(), FilterLanguageDialogFragment.class.getSimpleName());
            }
        });
        if (defaultValue != null)
            selectedLocaleView.setImageURI(Uri.parse(defaultValue.getImage()));
        dialog.setData(locales);
    }

}