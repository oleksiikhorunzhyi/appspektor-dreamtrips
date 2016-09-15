package com.messenger.ui.view.add_member;

import android.content.Context;
import android.util.AttributeSet;

import com.messenger.ui.presenter.ChatMembersScreenPresenter;
import com.messenger.ui.presenter.NewChatScreenPresenterImpl;

public class NewChatMembersScreenImpl extends ChatMembersScreenImpl<NewChatPath> {

   public NewChatMembersScreenImpl(Context context) {
      super(context);
   }

   public NewChatMembersScreenImpl(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   public ChatMembersScreenPresenter createPresenter() {
      return new NewChatScreenPresenterImpl(getContext(), injector);
   }
}
