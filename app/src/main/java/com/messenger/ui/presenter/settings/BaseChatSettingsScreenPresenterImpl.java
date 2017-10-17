package com.messenger.ui.presenter.settings;

import android.content.Context;
import android.util.Pair;
import android.view.MenuItem;

import com.messenger.delegate.chat.ChatExtensionInteractor;
import com.messenger.delegate.chat.command.ClearChatServerCommand;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.ConnectionException;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.ui.presenter.MessengerPresenterImpl;
import com.messenger.ui.view.settings.ChatSettingsScreen;
import com.messenger.ui.viewstate.ChatLayoutViewState;
import com.messenger.ui.viewstate.ChatSettingsViewState;
import com.worldventures.core.janet.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;

public abstract class BaseChatSettingsScreenPresenterImpl<C extends ChatSettingsScreen> extends MessengerPresenterImpl<C, ChatSettingsViewState> implements ChatSettingsScreenPresenter<C> {

   protected String conversationId;
   protected Observable<DataConversation> conversationObservable;
   protected Observable<List<DataUser>> participantsObservable;

   @Inject DataUser currentUser;
   @Inject MessengerServerFacade facade;
   @Inject ConversationsDAO conversationsDAO;
   @Inject ChatExtensionInteractor chatExtensionInteractor;

   public BaseChatSettingsScreenPresenterImpl(Context context, Injector injector, String conversationId) {
      super(context, injector);
      this.conversationId = conversationId;
   }

   @Override
   public void onNewViewState() {
      state = new ChatSettingsViewState();
      state.setLoadingState(ChatLayoutViewState.LoadingState.CONTENT);
      getView().showContent();
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      connectToConversation();

      // TODO Implement this
      // getView().setNotificationSettingStatus();
   }

   private void connectToConversation() {
      Observable<Pair<DataConversation, List<DataUser>>> conversationWithParticipantObservable = conversationsDAO.getConversationWithParticipants(conversationId)
            .compose(new NonNullFilter<>())
            .compose(bindViewIoToMainComposer());

      conversationWithParticipantObservable.subscribe(conversationPair -> onConversationChanged(conversationPair.first, conversationPair.second));

      Observable<Pair<DataConversation, List<DataUser>>> conversationWithParticipantReplayObservable = conversationWithParticipantObservable
            .take(1)
            .replay(1)
            .autoConnect();

      conversationObservable = conversationWithParticipantReplayObservable.map(conversationWithParticipant -> conversationWithParticipant.first);

      participantsObservable = conversationWithParticipantReplayObservable.map(conversationWithParticipant -> conversationWithParticipant.second);
   }

   @Override
   public void applyViewState() {
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
   }

   protected void onConversationChanged(DataConversation conversation, List<DataUser> participants) {
      ChatSettingsScreen screen = getView();
      screen.setConversation(conversation);
      screen.setParticipants(conversation, participants);
   }

   //////////////////////////////////////////////////////////////////////////
   // Settings UI actions
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public void onClearChatHistoryClicked() {
      getView().showClearChatDialog();
   }

   @Override
   public void onClearChatHistory() {
      chatExtensionInteractor.getClearChatServerCommandActionPipe()
            .createObservable(new ClearChatServerCommand(conversationId))
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<ClearChatServerCommand>().onStart(clearChatServerCommand -> getView().showProgressDialog())
                  .onSuccess(clearChatCommand -> revertSucceed())
                  .onFail((clearChatCommand, throwable) -> clearFailed(throwable.getCause())));
   }

   private void revertSucceed() {
      getView().dismissProgressDialog();
   }

   private void clearFailed(Throwable throwable) {
      getView().dismissProgressDialog();
      if (throwable instanceof ConnectionException) {
         getView().showErrorDialog(R.string.error_no_connection);
      } else {
         getView().showErrorDialog(R.string.error_something_went_wrong);
      }
   }

   @Override
   public void onNotificationsSwitchClicked(boolean isChecked) {
   }

   ///////////////////////////////////////////////////////////////////////////
   // Menu
   ///////////////////////////////////////////////////////////////////////////


   @Override
   public int getToolbarMenuRes() {
      return R.menu.menu_chat_settings;
   }

   @Override
   public boolean onToolbarMenuItemClick(MenuItem item) {
      switch (item.getItemId()) {
         case R.id.action_overflow:
            // overflow menu click, do nothing, wait for actual actions clicks
            return true;
      }
      return false;
   }

}
