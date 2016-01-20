package com.messenger.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import com.messenger.ui.presenter.NewChatScreenPresenter;
import com.messenger.ui.presenter.NewChatScreenPresenterImpl;

/**
 * Keep this silly *new new* name until we rename base NewChatMembersScreenImpl to
 * ChatMembersScreenImpl
 */
public class NewNewChatMembersScreenImpl extends NewChatMembersScreenImpl {

    public NewNewChatMembersScreenImpl(Context context) {
        super(context);
    }

    public NewNewChatMembersScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public NewChatScreenPresenter createPresenter() {
        return new NewChatScreenPresenterImpl(getActivity());
    }
}
