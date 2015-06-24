package com.worldventures.dreamtrips.modules.reptools.view.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.membership.view.fragment.PresentationVideosFragment;
import com.worldventures.dreamtrips.modules.reptools.model.VideoLocale;
import com.worldventures.dreamtrips.modules.reptools.presenter.TrainingVideosPresenter;

import java.util.ArrayList;

import butterknife.InjectView;

@Layout(R.layout.fragment_training_videos)
public class TrainingVideosFragment extends PresentationVideosFragment<TrainingVideosPresenter> implements TrainingVideosPresenter.View, AdapterView.OnItemSelectedListener {

    @InjectView(R.id.spinner_language)
    Spinner materialSpinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected TrainingVideosPresenter createPresenter(Bundle savedInstanceState) {
        return new TrainingVideosPresenter();
    }

    @Override
    public void setLocales(ArrayList<VideoLocale> locales, VideoLocale defaultValue) {
        ArrayAdapter<VideoLocale> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, locales);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        materialSpinner.setAdapter(adapter);
        int position = defaultValue != null ? adapter.getPosition(defaultValue) : 0;
        materialSpinner.setSelection(Math.max(position, 0));
        materialSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Object itemAtPosition = adapterView.getItemAtPosition(i);
        if (itemAtPosition instanceof VideoLocale) {
            getPresenter().onLanguageSelected((VideoLocale) itemAtPosition);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}