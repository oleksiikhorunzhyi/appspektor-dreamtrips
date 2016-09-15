package com.messenger.delegate.command.avatar;

import com.messenger.delegate.command.BaseChatCommand;
import com.messenger.entities.DataConversation;
import com.messenger.synchmechanism.MessengerConnector;
import com.messenger.synchmechanism.SyncStatus;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class SetChatAvatarCommand extends BaseChatCommand<DataConversation> implements InjectableAction {

   @Inject Janet janet;
   @Inject MessengerConnector connector;
   private String imagePath;

   public SetChatAvatarCommand(String conversationId, String imagePath) {
      super(conversationId);
      this.imagePath = imagePath;
   }

   @Override
   protected void run(CommandCallback<DataConversation> callback) throws Throwable {
      janet.createPipe(UploadChatAvatarCommand.class)
            .createObservableResult(new UploadChatAvatarCommand(imagePath))
            .map(Command::getResult)
            .flatMap(avatar ->
                  // TODO Remove this when sync is improved and we have custom camera.
                  // Delay setting avatar till sync is finished to avoid scenario when its value in
                  // our database is overridden by cached data from sync
                  connector.status().filter(status -> status == SyncStatus.CONNECTED).take(1).map(syncStatus -> avatar))
            .flatMap(avatar -> janet.createPipe(SendChatAvatarCommand.class)
                  .createObservableResult(new SendChatAvatarCommand(conversationId, avatar))
                  .map(Command::getResult))
            .subscribe(callback::onSuccess, callback::onFail);
   }
}