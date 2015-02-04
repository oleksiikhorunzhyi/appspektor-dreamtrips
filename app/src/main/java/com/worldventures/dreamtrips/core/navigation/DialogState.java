package com.worldventures.dreamtrips.core.navigation;

import com.worldventures.dreamtrips.view.dialog.BaseDialogFragment;
import com.worldventures.dreamtrips.view.dialog.BookItDialogFragment;

/**
 * Created by Edward on 29.01.15.
 *
 */
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
