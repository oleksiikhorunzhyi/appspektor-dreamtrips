package com.messenger.ui.presenter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.worldventures.dreamtrips.R;
import com.messenger.loader.LoaderModule;
import com.messenger.loader.SimpleLoader;
import com.messenger.model.ChatConversation;
import com.messenger.ui.activity.ChatActivity;
import com.messenger.ui.activity.NewChatActivity;
import com.messenger.ui.view.ConversationListScreen;
import com.messenger.ui.viewstate.ConversationListViewState;

import java.util.List;

public class ConversationListScreenPresenterImpl extends BaseViewStateMvpPresenter<ConversationListScreen,
        ConversationListViewState> implements ConversationListScreenPresenter {

    @Override public void loadConversationList() {
        final SimpleLoader<List<ChatConversation>> loader = LoaderModule.getConversationListLoader();
        getView().showLoading();
        getViewState().setLoadingState(ConversationListViewState.LoadingState.LOADING);

        loader.loadData(new SimpleLoader.LoadListener<List<ChatConversation>>() {
            @Override public void onLoadSuccess(List<ChatConversation> data) {
                if (isViewAttached()) {
                    getViewState().setData(data);
                    getViewState().setLoadingState(ConversationListViewState.LoadingState.CONTENT);
                    getView().setConversationList(data);
                    getView().showContent();
                }
            }

            @Override public void onError(Throwable error) {
                if (isViewAttached()) {
                    getView().showError(error);
                    getViewState().setLoadingState(ConversationListViewState.LoadingState.ERROR);
                }
            }
        });
    }

    @Override public void onNewViewState() {
        state = new ConversationListViewState();
        loadConversationList();
    }

    @Override public void applyViewState() {
        if (!isViewAttached()) {
            return;
        }
        switch (getViewState().getLoadingState()) {
            case LOADING:
                getView().showLoading();
                break;
            case CONTENT:
                getView().showContent();
                break;
            case ERROR:
                getView().showError(getViewState().getError());
                break;
        }
        if (getViewState().getData() != null) {
            getView().setConversationList(getViewState().getData());
        }
    }

    @Override public void onConversationSelected(ChatConversation chatConversation) {
        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra(ChatScreenPresenter.EXTRA_CHAT_CONVERSATION, chatConversation);
        getActivity().startActivity(intent);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Activity related
    ///////////////////////////////////////////////////////////////////////////

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.conversation_list, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent intent = new Intent(getContext(), NewChatActivity.class);
                getActivity().startActivity(intent);
                return true;
        }
        return false;
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override public void onDestroy() {

    }

    ///////////////////////////////////////////////////////////////////////////
    // Helpers
    ///////////////////////////////////////////////////////////////////////////

    protected Context getContext() {
        return getView().getContext();
    }

    protected AppCompatActivity getActivity() {
        return getView().getActivity();
    }
}

