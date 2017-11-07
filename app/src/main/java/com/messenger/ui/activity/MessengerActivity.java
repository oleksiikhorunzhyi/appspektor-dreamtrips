package com.messenger.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.messenger.delegate.CropImageDelegate;
import com.messenger.di.MessengerActivityModule;
import com.messenger.di.MessengerModule;
import com.messenger.ui.presenter.MessengerActivityPresenter;
import com.messenger.ui.view.chat.ChatPath;
import com.messenger.ui.view.conversation.ConversationsPath;
import com.messenger.util.PickLocationDelegate;
import com.worldventures.core.component.ComponentDescription;
import com.worldventures.core.service.analytics.MonitoringHelper;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.activity.FlowActivity;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayoutDelegate;
import com.worldventures.dreamtrips.social.ui.podcast_player.delegate.PodcastPlayerDelegate;

import java.util.List;

import javax.inject.Inject;

import flow.Flow;
import flow.History;

@Layout(R.layout.activity_base_messenger)
public class MessengerActivity extends FlowActivity<MessengerActivityPresenter> {

   public static final String EXTRA_CHAT_CONVERSATION_ID = "MessengerActivity#EXTRA_CHAT_CONVERSATION_ID";

   @Inject PodcastPlayerDelegate podcastPlayerDelegate;
   @Inject PhotoPickerLayoutDelegate photoPickerLayoutDelegate;
   @Inject PickLocationDelegate pickLocationDelegate;
   @Inject CropImageDelegate cropImageDelegate;

   String conversationId;

   @Override
   protected List<Object> getModules() {
      List<Object> modules = super.getModules();
      modules.add(new MessengerActivityModule());
      return modules;
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      conversationId = getIntent().getStringExtra(EXTRA_CHAT_CONVERSATION_ID);
      super.onCreate(savedInstanceState);
      podcastPlayerDelegate.stop();
      //
      MonitoringHelper.setInteractionName(this);
      //
      navigationDrawerPresenter.setCurrentComponent(rootComponentsProvider.getComponentByKey(MessengerModule.MESSENGER));
   }

   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
      if (pickLocationDelegate.onActivityResult(requestCode, resultCode, data)) {
         return;
      }
      if (cropImageDelegate.onActivityResult(requestCode, resultCode, data)) {
         return;
      }
      super.onActivityResult(requestCode, resultCode, data);
   }

   @Override
   protected ComponentDescription getCurrentComponent() {
      return rootComponentsProvider.getComponentByKey(MessengerModule.MESSENGER);
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
         history = history.buildUpon().push(new ChatPath(conversationId)).build();
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

   @Override
   protected void doOnDispatch(Flow.Traversal traversal) {
      if (!traversal.destination.top().equals(traversal.origin.top())) {
         photoPickerLayoutDelegate.hidePicker();
      }
   }
}
