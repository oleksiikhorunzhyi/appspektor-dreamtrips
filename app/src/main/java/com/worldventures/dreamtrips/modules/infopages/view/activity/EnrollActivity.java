package com.worldventures.dreamtrips.modules.infopages.view.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.activity.ActivityWithPresenter;
import com.worldventures.dreamtrips.modules.infopages.presenter.EnrollActivityPresenter;

import butterknife.InjectView;

/**
 * Created by 1 on 06.02.15.
 */
@Layout(R.layout.activity_book_it)
public class EnrollActivity extends ActivityWithPresenter<EnrollActivityPresenter> implements Presenter.View {

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
    protected EnrollActivityPresenter createPresentationModel(Bundle savedInstanceState) {
        return new EnrollActivityPresenter(this);
    }
}
