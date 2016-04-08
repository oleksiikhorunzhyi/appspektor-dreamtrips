package com.messenger.ui.presenter;

import android.content.Context;
import android.view.MenuItem;
import android.widget.Toast;

import com.messenger.delegate.StartChatDelegate;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataUser;
import com.messenger.storage.dao.UsersDAO;
import com.messenger.ui.model.SelectableDataUser;
import com.messenger.ui.view.add_member.ChatMembersScreen;
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
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class NewChatScreenPresenterImpl extends ChatMembersScreenPresenterImpl {

    private static final int REQUIRED_SELECTED_USERS_TO_SHOW_CHAT_NAME = 2;

    @Inject
    UsersDAO usersDAO;
    @Inject
    StartChatDelegate startChatDelegate;

    public NewChatScreenPresenterImpl(Context context, Injector injector) {
        super(context, injector);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Activity related
    ///////////////////////////////////////////////////////////////////////////


    @Override
    public void attachView(ChatMembersScreen view) {
        super.attachView(view);
        getView().setTitle(R.string.new_chat_title);
    }

    @Override
    protected Observable<List<DataUser>> createContactListObservable() {
        return usersDAO
                .getFriends(user.getId())
                .subscribeOn(Schedulers.io());
    }

    @Override
    public boolean onToolbarMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:

                if (selectedUsers == null || selectedUsers.isEmpty()) {
                    Toast.makeText(getContext(), R.string.new_chat_toast_no_users_selected_error,
                            Toast.LENGTH_SHORT).show();
                    return true;
                }

                if (!isConnectionPresent() && selectedUsers.size() != 1) {
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

                if (selectedUsers.size() == 1) {
                    startChatDelegate.startSingleChat(selectedUsers.get(0), onNextAction, errorAction);
                } else {
                    startChatDelegate.startNewGroupChat(user.getId(), new ArrayList<>(selectedUsers),
                            getView().getConversationName(), onNextAction, errorAction);
                }

                return true;
        }
        return false;
    }

    @Override
    public void onItemSelectChange(SelectableDataUser item) {
        super.onItemSelectChange(item);
        setConversationNameInputFieldVisible(selectedUsers.size() >= REQUIRED_SELECTED_USERS_TO_SHOW_CHAT_NAME);
    }
}
