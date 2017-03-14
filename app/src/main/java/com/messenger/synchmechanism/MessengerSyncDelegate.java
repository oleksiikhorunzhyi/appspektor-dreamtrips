package com.messenger.synchmechanism;

import com.messenger.command.LoginToMessengerServerCommand;
import com.messenger.delegate.conversation.command.SyncConversationsCommand;
import com.messenger.delegate.roster.LoadContactsCommand;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.auth.service.ReLoginInteractor;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.Observable;
import rx.schedulers.Schedulers;

public class MessengerSyncDelegate {

   private final ActionPipe<LoadContactsCommand> contactsPipe;
   private final ActionPipe<SyncConversationsCommand> conversationsPipe;
   private final ActionPipe<LoginToMessengerServerCommand> loginToMessengerServerCommandActionPipe;

   @Inject ReLoginInteractor reLoginInteractor;

   public MessengerSyncDelegate(Injector injector, Janet janet) {
      injector.inject(this);
      this.contactsPipe = janet.createPipe(LoadContactsCommand.class, Schedulers.io());
      this.conversationsPipe = janet.createPipe(SyncConversationsCommand.class, Schedulers.io());
      this.loginToMessengerServerCommandActionPipe =
            janet.createPipe(LoginToMessengerServerCommand.class, Schedulers.io());

      connectReLogin();
   }

   public Observable<Boolean> sync() {
      return Observable.zip(syncContacts(), syncConversations(), (loadContactsCommand, loadConversationsCommand) -> true);
   }

   public ActionPipe<LoadContactsCommand> getContactsPipe() {
      return contactsPipe;
   }

   public ActionPipe<SyncConversationsCommand> getConversationsPipe() {
      return conversationsPipe;
   }

   private Observable<LoadContactsCommand> syncContacts() {
      return contactsPipe.createObservableResult(new LoadContactsCommand());
   }

   private Observable<SyncConversationsCommand> syncConversations() {
      return conversationsPipe.createObservableResult(new SyncConversationsCommand());
   }

   private void connectReLogin() {
      reLoginInteractor.loginHttpActionPipe().observeSuccess()
            .subscribe(__ -> loginToMessengerServerCommandActionPipe.send(new LoginToMessengerServerCommand()));
   }
}
