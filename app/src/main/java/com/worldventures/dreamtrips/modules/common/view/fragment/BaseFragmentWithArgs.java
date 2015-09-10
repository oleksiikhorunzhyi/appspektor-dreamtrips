package com.worldventures.dreamtrips.modules.common.view.fragment;

import android.os.Bundle;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

public abstract class BaseFragmentWithArgs<PM extends Presenter, P extends Parcelable> extends BaseFragment<PM> {

    public void setArgs(Parcelable data) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ComponentPresenter.EXTRA_DATA, data);
        setArguments(bundle);
    }

    public P getArgs() {
        return getArguments().getParcelable(ComponentPresenter.EXTRA_DATA);
    }

    public void clearArgs() {
        getArguments().remove(ComponentPresenter.EXTRA_DATA);
    }

}
