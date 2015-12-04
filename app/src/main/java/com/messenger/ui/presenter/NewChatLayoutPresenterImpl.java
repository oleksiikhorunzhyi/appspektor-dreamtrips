package com.messenger.ui.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import com.messenger.app.Environment;
import com.messenger.loader.LoaderModule;
import com.messenger.loader.SimpleLoader;
import com.messenger.model.ChatContacts;
import com.messenger.model.ChatConversation;
import com.messenger.model.ChatUser;
import com.messenger.ui.activity.ChatActivity;
import com.messenger.ui.view.NewChatScreen;
import com.messenger.ui.viewstate.NewChatLayoutViewState;
import com.worldventures.dreamtrips.R;

public class NewChatLayoutPresenterImpl extends BaseViewStateMvpPresenter<NewChatScreen>
        implements NewChatLayoutPresenter {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private SimpleLoader<ChatContacts> simpleChatContactsLoader = LoaderModule.getChatContactsLoader();

    @Override public void loadChatContacts() {
        getView().showLoading();
        getViewState().setLoadingState(NewChatLayoutViewState.LoadingState.LOADING);

        simpleChatContactsLoader.loadData(new SimpleLoader.LoadListener<ChatContacts>() {
            @Override public void onLoadSuccess(ChatContacts data) {
                if (isViewAttached()) {
                    getViewState().setChatContacts(data);
                    getViewState().setLoadingState(NewChatLayoutViewState.LoadingState.CONTENT);
                    getView().setContacts(data);
                    getView().showContent();
                }
            }

            @Override public void onError(Throwable error) {
                if (isViewAttached()) {
                    getView().showError(error);
                    getViewState().setLoadingState(NewChatLayoutViewState.LoadingState.ERROR);
                }
            }
        });
    }

    @Override public void onNewViewState() {
        state = new NewChatLayoutViewState();
        loadChatContacts();
    }

    @Override public NewChatLayoutViewState getViewState() {
        return (NewChatLayoutViewState) state;
    }

    @Override public void applyViewState() {
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
        if (getViewState().getChatContacts() != null) {
            getView().setContacts(getViewState().getChatContacts());
        }
        if (getViewState().getSelectedContacts() != null) {
            getView().setSelectedContacts(getViewState().getSelectedContacts());
            refreshSelectedContactsHeader(getViewState().getSelectedContacts());
        }
    }

    @Override public void onSelectedUsersStateChanged(List<ChatUser> selectedContacts) {
        getViewState().setSelectedContacts(selectedContacts);
        refreshSelectedContactsHeader(selectedContacts);
    }

    private void refreshSelectedContactsHeader(List<ChatUser> selectedContacts) {
        StringBuilder sb = new StringBuilder();
        sb.append(getContext().getString(R.string.new_chat_chosen_contacts_header_default_value));
        sb.append(" ");

        List<String> userNames = new ArrayList<>();

        for (int i = 0; i < selectedContacts.size(); i++) {
            ChatUser user = selectedContacts.get(i);
            CharSequence name = user.getName();
            sb.append(name);
            userNames.add(name.toString());
            if (i != selectedContacts.size() - 1) {
                sb.append(", ");
            }
        }

        String resultString = sb.toString();
        SpannableString spannableString = new SpannableString(resultString);
        int spannableColor = getContext().getResources().getColor(R.color.contact_list_header_selected_contacts);
        for (int i = 0; i < userNames.size(); i++) {
            String name = userNames.get(i);
            int spanBeginning = resultString.indexOf(name);
            int underlinedSpanEnding = spanBeginning + name.length();
            int coloredSpanEnding = underlinedSpanEnding;
            // extend colored span to comma after user name
            if (i != userNames.size() - 1) {
                coloredSpanEnding++;
            }
            spannableString.setSpan(new UnderlineSpan(), spanBeginning,
                    underlinedSpanEnding, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(spannableColor), spanBeginning,
                    coloredSpanEnding, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        getView().setSelectedUsersHeaderText(spannableString);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Action Bar
    ///////////////////////////////////////////////////////////////////////////

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = ((AppCompatActivity)getContext()).getMenuInflater();
        inflater.inflate(R.menu.new_chat, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                ChatConversation chatConversation = Environment.newChatConversation();
                chatConversation.setConversationName(getView().getConversationName());
                chatConversation.setConversationOwner(Environment.getCurrentUser());
                ArrayList<ChatUser> chatUsers = new ArrayList<>();
                chatUsers.add(Environment.getCurrentUser());
                chatUsers.addAll(getViewState().getSelectedContacts());
                chatConversation.setChatUsers(chatUsers);

                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("chat_conversation", chatConversation);
                getActivity().startActivity(intent);
                return true;
        }
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // UI Actions
    ///////////////////////////////////////////////////////////////////////////

    @Override public void onHandleTakePictureIntent() {
        // TODO Handle absent camera feature possibility
        if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                getActivity().startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            getView().setConversationIcon(imageBitmap);
        }
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
