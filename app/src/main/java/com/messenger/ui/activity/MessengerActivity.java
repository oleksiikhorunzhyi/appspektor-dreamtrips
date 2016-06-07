package com.messenger.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;

import com.messenger.delegate.CropImageDelegate;
import com.messenger.di.MessengerActivityModule;
import com.messenger.synchmechanism.MessengerConnector;
import com.messenger.ui.presenter.MessengerActivityPresenter;
import com.messenger.ui.view.chat.ChatPath;
import com.messenger.ui.view.conversation.ConversationsPath;
import com.messenger.util.PickLocationDelegate;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.flow.activity.FlowActivity;
import com.worldventures.dreamtrips.core.flow.path.AttributedPath;
import com.worldventures.dreamtrips.core.flow.path.PathAttrs;
import com.worldventures.dreamtrips.core.utils.tracksystem.MonitoringHelper;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayout;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayoutDelegate;

import javax.inject.Inject;

import butterknife.InjectView;
import flow.Flow;
import flow.History;
import flow.path.Path;

@Layout(R.layout.activity_base_messenger)
public class MessengerActivity extends FlowActivity<MessengerActivityPresenter> {

    public static final String EXTRA_CHAT_CONVERSATION_ID = "MessengerActivity#EXTRA_CHAT_CONVERSATION_ID";

    @Inject
    PhotoPickerLayoutDelegate photoPickerLayoutDelegate;
    @Inject
    PickLocationDelegate pickLocationDelegate;
    @Inject
    CropImageDelegate cropImageDelegate;

    @InjectView(R.id.chat_photo_picker)
    PhotoPickerLayout photoPickerLayout;

    String conversationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        MonitoringHelper.setInteractionName(this);
        //
        conversationId = getIntent().getStringExtra(EXTRA_CHAT_CONVERSATION_ID);
        // if we launch activity from push we don't load global configurations.
        // So we should notify MessengerConnector that there won't be loading configs
        if (!TextUtils.isEmpty(conversationId)) {
            MessengerConnector.getInstance().connectAfterGlobalConfig();
        }
        //
        initPickerLayout();
        //
        navigationDrawerPresenter.setCurrentComponent(rootComponentsProvider
                .getComponentByKey(MessengerActivityModule.MESSENGER));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (pickLocationDelegate.onActivityResult(requestCode, resultCode, data)) return;
        if (cropImageDelegate.onActivityResult(requestCode, resultCode, data)) return;
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected ComponentDescription getCurrentComponent() {
        return rootComponentsProvider
                .getComponentByKey(MessengerActivityModule.MESSENGER);
    }

    //TODO photo picker should be fully reworked to fit UI needs
    private void initPickerLayout() {
        inject(photoPickerLayout);
        photoPickerLayoutDelegate.setPhotoPickerLayout(photoPickerLayout);
        photoPickerLayoutDelegate.initPicker(getSupportFragmentManager());
        photoPickerLayoutDelegate.hidePicker();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        conversationId = intent.getStringExtra(EXTRA_CHAT_CONVERSATION_ID);
        super.onNewIntent(intent);
    }

    @Override
    protected History provideDefaultHistory() {
        History history = History.single(ConversationsPath.MASTER_PATH);

        if (!TextUtils.isEmpty(conversationId)) {
            history = history
                    .buildUpon()
                    .push(new ChatPath(conversationId))
                    .build();
        }

        return history;
    }

        //TODO refactor after merge with social and update social router
    public static void startMessenger(Context context) {
        context.startActivity(new Intent(context, MessengerActivity.class));
    }

    //TODO refactor after merge with social and update social router
    public static void startMessengerWithConversation(Context context, String conversationId) {
        Intent resultIntent = new Intent(context, MessengerActivity.class);
        //set args to pending intent
        resultIntent.putExtra(MessengerActivity.EXTRA_CHAT_CONVERSATION_ID, conversationId);
        //
        context.startActivity(resultIntent);
    }


    @Override
    protected MessengerActivityPresenter createPresentationModel(Bundle savedInstanceState) {
        return new MessengerActivityPresenter();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Flow
    ///////////////////////////////////////////////////////////////////////////
    void setNavigation(Path path) {
        boolean enabled = false;
        if (path instanceof AttributedPath) {
            PathAttrs attrs = ((AttributedPath) path).getAttrs();
            enabled = attrs.isDrawerEnabled();
        }
        //
        drawerLayout.setDrawerLockMode(enabled ?
                DrawerLayout.LOCK_MODE_UNLOCKED :
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    protected void doOnDispatch(Flow.Traversal traversal) {
        if (!traversal.destination.top().equals(traversal.origin.top())) {
            photoPickerLayoutDelegate.hidePicker();
        }
    }
}
