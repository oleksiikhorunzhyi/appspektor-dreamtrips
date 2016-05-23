package com.messenger.ui.module;

import android.view.View;

public interface ModuleView<P> {

    View getParentView();

    P getPresenter();
}
