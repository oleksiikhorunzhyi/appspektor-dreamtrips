package com.worldventures.dreamtrips.core.navigation;

import com.worldventures.dreamtrips.modules.common.view.dialog.BaseDialogFragment;
import com.worldventures.dreamtrips.modules.trips.view.dialog.BookItDialogFragment;

public enum DialogState {

    BOOK_IT(BookItDialogFragment.class);

    private Class<? extends BaseDialogFragment> fragmentClazz;

    DialogState(Class<? extends BaseDialogFragment> fragmentClazz) {
        this.fragmentClazz = fragmentClazz;
    }

    public String getClazzName() {
        return fragmentClazz.getName();
    }
}
