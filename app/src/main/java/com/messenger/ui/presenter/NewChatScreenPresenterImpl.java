package com.messenger.ui.presenter;

import android.content.Context;
import android.view.MenuItem;
import android.widget.Toast;

import com.messenger.delegate.StartChatDelegate;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataUser;
import com.messenger.ui.view.chat.ChatPath;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import flow.Flow;
import flow.History;
import rx.Observable;
import rx.functions.Action1;
import timber.log.Timber;

public class NewChatScreenPresenterImpl extends ChatMembersScreenPresenterImpl {

   private static final int REQUIRED_SELECTED_USERS_TO_SHOW_CHAT_NAME = 2;

   @Inject StartChatDelegate startChatDelegate;

   public NewChatScreenPresenterImpl(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      getView().setTitle(R.string.new_chat_title);
   }

   @Override
   public boolean onToolbarMenuItemClick(MenuItem item) {
      switch (item.getItemId()) {
         case R.id.action_done:

            if (futureParticipants == null || futureParticipants.isEmpty()) {
               Toast.makeText(getContext(), R.string.new_chat_toast_no_users_selected_error, Toast.LENGTH_SHORT).show();
               return true;
            }

            if (!isConnectionPresent() && futureParticipants.size() != 1) {
               showAbsentConnectionMessage(getContext());
               return true;
            }

            Action1<DataConversation> onNextAction = conversation -> {
               History.Builder history = Flow.get(getContext()).getHistory().buildUpon();
               history.pop();
               history.push(new ChatPath(conversation.getId()));
               Flow.get(getContext()).setHistory(history.build(), Flow.Direction.FORWARD);
            };

            Action1<Throwable> errorAction = throwable -> {
               Timber.e(throwable, "Error while creating chat");
               item.setEnabled(true);
            };

            item.setEnabled(false);

            if (futureParticipants.size() == 1) {
               startChatDelegate.startSingleChat(futureParticipants.get(0), onNextAction, errorAction);
            } else {
               startChatDelegate.startNewGroupChat(user.getId(), new ArrayList<>(futureParticipants), getView().getConversationName()
                     .trim(), onNextAction, errorAction);
            }

            return true;
      }
      return false;
   }

   protected Observable<Boolean> getChatNameShouldBeVisibleObservable() {
      return Observable.just(futureParticipants.size() >= REQUIRED_SELECTED_USERS_TO_SHOW_CHAT_NAME);
   }

   @Override
   protected Observable<List<DataUser>> getExistingParticipants() {
      return Observable.just(new ArrayList<>());
   }
}
