package com.messenger.ui.presenter;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.messenger.constant.CursorLoaderIds;
import com.messenger.delegate.LoaderDelegate;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.entities.User;
import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.messenger.app.Environment;
import com.messenger.model.ChatConversation;
import com.messenger.model.ChatUser;
import com.messenger.ui.activity.ChatActivity;
import com.messenger.ui.view.NewChatScreen;
import com.messenger.ui.viewstate.NewChatLayoutViewState;
import com.worldventures.dreamtrips.core.session.UserSession;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class NewChatLayoutPresenterImpl extends BaseViewStateMvpPresenter<NewChatScreen, NewChatLayoutViewState>
        implements NewChatLayoutPresenter {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @Inject
    SessionHolder<UserSession> appSessionHolder;
    @Inject
    MessengerServerFacade messengerServerFacade;

    private Activity parentActivity;
    private LoaderDelegate loaderDelegate;
    private final CursorLoaderCallback contactLoader = new CursorLoaderCallback();

    public NewChatLayoutPresenterImpl(Activity activity) {
        this.parentActivity = activity;

        ((Injector) activity.getApplicationContext()).inject(this);
        loaderDelegate = new LoaderDelegate(activity, messengerServerFacade);
    }

    @Override
    public void attachView(NewChatScreen view) {
        super.attachView(view);
        initialCursorLoader();
        loadChatContacts();
    }

    @Override
    public void onNewViewState() {
        state = new NewChatLayoutViewState();
        getView().showLoading();
        getViewState().setLoadingState(NewChatLayoutViewState.LoadingState.LOADING);
    }

    @Override
    public void loadChatContacts() {
        loaderDelegate.loadContacts();
    }

    @Override
    public void applyViewState() {
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
        if (getViewState().getSelectedContacts() != null) {
            getView().setSelectedContacts(getViewState().getSelectedContacts());
            refreshSelectedContactsHeader(getViewState().getSelectedContacts());
        }
    }

    @Override
    public void onSelectedUsersStateChanged(List<ChatUser> selectedContacts) {
        getViewState().setSelectedContacts(selectedContacts);
        refreshSelectedContactsHeader(selectedContacts);
    }

    private void initialCursorLoader() {
        LoaderManager loaderManager = ((AppCompatActivity) parentActivity).getSupportLoaderManager();
        Loader loader = loaderManager.getLoader(CursorLoaderIds.CONTACT_LOADER);
        if (loader == null) {
            loaderManager.initLoader(CursorLoaderIds.CONTACT_LOADER, null, contactLoader);
        } else {
            loaderManager.restartLoader(CursorLoaderIds.CONTACT_LOADER, null, contactLoader);
        }
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        ((AppCompatActivity) parentActivity).getSupportLoaderManager().destroyLoader(CursorLoaderIds.CONTACT_LOADER);
    }

    private void refreshSelectedContactsHeader(List<ChatUser> selectedContacts) {
        StringBuilder sb = new StringBuilder();
        sb.append(parentActivity.getString(R.string.new_chat_chosen_contacts_header_contacts_list_start_value));
        if (!selectedContacts.isEmpty()) {
            sb.append(" (");
            sb.append(String.valueOf(selectedContacts.size()));
            sb.append(")");
        }
        sb.append(": ");

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
        int spannableColor = parentActivity.getResources().getColor(R.color.contact_list_header_selected_contacts);
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
    // Activity related
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = ((AppCompatActivity) parentActivity).getMenuInflater();
        inflater.inflate(R.menu.new_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                if (getViewState().getSelectedContacts() == null || getViewState().getSelectedContacts().isEmpty()) {
                    Toast.makeText(parentActivity, R.string.new_chat_toast_no_users_selected_error,
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
                ChatConversation chatConversation = Environment.newChatConversation();
                chatConversation.setConversationName(getView().getConversationName());
                chatConversation.setConversationOwner(Environment.getCurrentUser());
                ArrayList<ChatUser> chatUsers = new ArrayList<>();
                chatUsers.add(Environment.getCurrentUser());
                chatUsers.addAll(getViewState().getSelectedContacts());
                chatConversation.setChatUsers(chatUsers);

                Intent intent = new Intent(parentActivity, ChatActivity.class);
                intent.putExtra(ChatScreenPresenter.EXTRA_CHAT_CONVERSATION, chatConversation);
                parentActivity.startActivity(intent);
                parentActivity.finish();
                return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {

    }

    ///////////////////////////////////////////////////////////////////////////
    // UI Actions
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onHandleTakePictureIntent() {
        // TODO Handle absent camera feature possibility
        if (parentActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(parentActivity.getPackageManager()) != null) {
                parentActivity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            getView().setConversationIcon(imageBitmap);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Cursor Loader Callback
    ///////////////////////////////////////////////////////////////////////////

    private class CursorLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(parentActivity, User.CONTENT_URI,
                    null, null, null, "userName desc");
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            getViewState().setLoadingState(NewChatLayoutViewState.LoadingState.CONTENT);
            showContacts(data);
            getView().showContent();
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            showContacts(null);
        }

        private void showContacts(Cursor cursor) {
            NewChatScreen screen = getView();
            if (screen == null) return;
            screen.setContacts(cursor);
        }
    }
}
