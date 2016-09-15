package com.messenger.ui.presenter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataParticipant;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.constant.Affiliation;
import com.messenger.messengerservers.constant.ConversationType;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.ui.helper.ConversationHelper;
import com.messenger.ui.view.chat.ChatPath;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;

import java.util.List;

import javax.inject.Inject;

import flow.Flow;
import flow.History;
import rx.Observable;
import timber.log.Timber;

public class AddChatMembersScreenPresenterImpl extends ChatMembersScreenPresenterImpl {

   @Inject ConversationsDAO conversationsDAO;
   @Inject ParticipantsDAO participantsDAO;

   private Observable<DataConversation> conversationStream;
   private Observable<List<DataUser>> participantsStream;

   public AddChatMembersScreenPresenterImpl(Context context, Injector injector, String conversationId) {
      super(context, injector);
      conversationStream = conversationsDAO.getConversation(conversationId).take(1).replay().autoConnect();
      participantsStream = participantsDAO.getParticipantsEntities(conversationId).take(1).replay().autoConnect();
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      getView().setTitle(R.string.chat_add_new_members_title);
      conversationStream.take(1).compose(bindViewIoToMainComposer()).subscribe(conversation -> {
         boolean isSingleChat = ConversationHelper.isSingleChat(conversation);
         getView().setConversationNameEditTextVisibility(isSingleChat ? View.VISIBLE : View.GONE);
      });
   }

   private void tryCreateChat(MenuItem doneButtonItem) {
      List<DataUser> newChatUsers = futureParticipants;
      if (newChatUsers == null || newChatUsers.isEmpty()) {
         Toast.makeText(getContext(), R.string.new_chat_toast_no_users_selected_error, Toast.LENGTH_SHORT).show();
         return;
      }

      if (!isConnectionPresent()) {
         showAbsentConnectionMessage(getContext());
         return;
      }

      doneButtonItem.setEnabled(false);

      // TODO: 1/28/16 improve logic with getViewState().getSelectedContacts();
      conversationStream.flatMap(conversation -> {
         String subject;
         // changing conversation subject is possible
         // oly when adding a member to a single chat
         if (ConversationHelper.isSingleChat(conversation)) {
            subject = getView().getConversationName();
         } else {
            subject = conversation.getSubject();
         }
         return modifyConversation(conversation, newChatUsers, subject);
      })
            .doOnNext(conversationPair -> saveModifiedConversation(conversationPair.first, newChatUsers, conversationPair.second))
            .compose(bindViewIoToMainComposer())
            .map(conversationPair -> conversationPair.first)
            .subscribe(newConversation -> {
               History.Builder history = Flow.get(getContext()).getHistory().buildUpon();
               history.pop();
               history.pop();
               history.push(new ChatPath(newConversation.getId()));
               Flow.get(getContext()).setHistory(history.build(), Flow.Direction.FORWARD);
            }, e -> {
               doneButtonItem.setEnabled(true);
               Timber.e(e, "Could not add chat member");
            });
   }

   private Observable<Pair<DataConversation, String>> modifyConversation(DataConversation conversation, List<DataUser> newChatUsers, String newSubject) {
      return participantsStream.flatMap(currentUsers -> createConversationHelper.modifyConversation(conversation, currentUsers, newChatUsers, newSubject)
            .map(newConversation -> new Pair<>(newConversation, conversation.getType())));
   }

   private void saveModifiedConversation(DataConversation newConversation, List<DataUser> newChatUsers, String previousType) {
      List<DataParticipant> relationships = Queryable.from(newChatUsers).map(user -> new DataParticipant(newConversation
            .getId(), user.getId(), Affiliation.MEMBER)).toList();

      // we are participants too and if conversation is group then we're owner otherwise we're member
      if (TextUtils.equals(previousType, ConversationType.CHAT)) {
         relationships.add(new DataParticipant(newConversation.getId(), user.getId(), Affiliation.OWNER));
      }

      participantsDAO.save(relationships);
      conversationsDAO.save(newConversation);
   }
   ///////////////////////////////////////////////////////////////////////////
   // Menu
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public boolean onToolbarMenuItemClick(MenuItem item) {
      switch (item.getItemId()) {
         case R.id.action_done:
            tryCreateChat(item);
            return true;
      }
      return false;
   }

   protected Observable<Boolean> getChatNameShouldBeVisibleObservable() {
      return conversationStream.compose(bindViewIoToMainComposer()).take(1).map(ConversationHelper::isSingleChat);
   }

   @Override
   protected Observable<List<DataUser>> getExistingParticipants() {
      return participantsStream;
   }
}
