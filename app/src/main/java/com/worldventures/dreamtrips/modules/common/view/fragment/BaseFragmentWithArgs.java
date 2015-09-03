package com.worldventures.dreamtrips.modules.common.view.fragment;

import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

public abstract class BaseFragmentWithArgs<PM extends Presenter, PARAMETER extends Parcelable> extends BaseFragment<PM> {

    public PARAMETER getArgs() {
        return (PARAMETER) getArguments().getParcelable(ComponentPresenter.EXTRA_DATA);
    }

}
