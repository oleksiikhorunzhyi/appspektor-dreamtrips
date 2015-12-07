package com.messenger.ui.presenter;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.listeners.AuthorizeListener;
import com.messenger.messengerservers.listeners.OnLoadedListener;
import com.messenger.messengerservers.loaders.Loader;
import com.messenger.model.ChatConversation;
import com.messenger.model.ChatUser;
import com.messenger.model.MockChatConversation;
import com.messenger.ui.activity.ChatActivity;
import com.messenger.ui.view.NewChatScreen;
import com.messenger.ui.viewstate.NewChatLayoutViewState;
import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.session.UserSession;

import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;

public class NewChatLayoutPresenterImpl extends BaseViewStateMvpPresenter<NewChatScreen>
        implements NewChatLayoutPresenter {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Activity activity;

    @Inject
    SessionHolder<UserSession> appSessionHolder;
    @Inject
    MessengerServerFacade messengerServerFacade;

    public NewChatLayoutPresenterImpl(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void attachView(NewChatScreen view) {
        super.attachView(view);
        ((Injector)view.getActivity().getApplication()).inject(this);
    }

    @Override
    public void connect() {
        if (messengerServerFacade.isAuthorized()) {
            loadChatContacts();
            return;
        }
        showInputUserNameDialog();
    }

    private void showInputUserNameDialog(){
        final EditText editText = new EditText(getActivity());
        messengerServerFacade.addAuthorizationListener(new AuthorizeListener() {
            @Override
            public void onSuccess() {
                messengerServerFacade.setPresenceStatus(true);
                Log.e("Xmpp server", "Vse normul");
                getActivity().runOnUiThread(NewChatLayoutPresenterImpl.this::loadChatContacts);
            }
        });

        new AlertDialog.Builder(getActivity())
                .setTitle("Input test user's name")
                .setMessage("The format must look like techery_userN, where N is between 1 and 10.")
                .setView(editText)
                .setPositiveButton("Ok", (dialog, possitiveButton) -> {
                    String userName = editText.getText().toString();
                    if (StringUtils.isEmpty(userName)) return;
                    messengerServerFacade.authorizeAsync(userName, userName);
                })
                .setNegativeButton("Cancel", (dialog, whichButton) -> {
                    getActivity().finish();
                })
                .show();
    }

    @Override public void loadChatContacts() {
        if (getView() != null){
            getView().showLoading();
            // TODO: 12/4/15 null
            getViewState().setLoadingState(NewChatLayoutViewState.LoadingState.LOADING);
        }

        Loader<User> loaderContacts = messengerServerFacade.getLoaderManager().getContactLoader();
        loaderContacts.setOnEntityLoadedListener(new OnLoadedListener<ChatUser>() {
            @Override
            public void onLoaded(List<ChatUser> users) {
                if (getView() == null) return;

                Log.i("Xmpp Load contacts", "" + users.size());
                activity.runOnUiThread(() -> {
                    if (isViewAttached()) {
                        getViewState().setChatContacts(users);
                        getViewState().setLoadingState(NewChatLayoutViewState.LoadingState.CONTENT);
                        getView().setContacts(users);
                        getView().showContent();
                    }
                });
            }

            @Override
            public void onFailed() {
                if (getView() == null) return;

                activity.runOnUiThread(() -> {
                    if (isViewAttached()) {
                        getView().showError(new Exception("Server exception"));
                        getViewState().setLoadingState(NewChatLayoutViewState.LoadingState.ERROR);
                    }
                });
            }
        });
        loaderContacts.load();
    }

    @Override public void onNewViewState() {
        state = new NewChatLayoutViewState();
        if (messengerServerFacade.isAuthorized()) loadChatContacts();
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
                List<ChatUser> userList = getViewState().getSelectedContacts();

                if (userList != null && userList.size() != 1){
                    Toast.makeText(activity, "You must provide one user to start 1:1 chat", Toast.LENGTH_SHORT).show();
                    return true;
                }

                ChatConversation chatConversation = new MockChatConversation();
                chatConversation.setChatUsers(userList);
                ChatActivity.start(activity, ChatActivity.CHAT_TYPE_SINGLE, chatConversation);
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
        return activity;
    }

    protected Activity getActivity() {
        return activity;
    }
}
