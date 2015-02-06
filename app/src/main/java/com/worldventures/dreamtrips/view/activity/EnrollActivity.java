package com.worldventures.dreamtrips.view.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.presentation.BasePresentation;
import com.worldventures.dreamtrips.presentation.EnrollActivityPresentation;

import butterknife.InjectView;

/**
 * Created by 1 on 06.02.15.
 */
@Layout(R.layout.activity_book_it)
public class EnrollActivity extends PresentationModelDrivenActivity<EnrollActivityPresentation> implements BasePresentation.View{

    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;


    @Override
    protected void afterCreateView(Bundle savedInstanceState) {
        super.afterCreateView(savedInstanceState);
        getPresentationModel().onCreate();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_enroll_it);
    }

    @Override
    protected EnrollActivityPresentation createPresentationModel(Bundle savedInstanceState) {
        return new EnrollActivityPresentation(this);
    }
}
