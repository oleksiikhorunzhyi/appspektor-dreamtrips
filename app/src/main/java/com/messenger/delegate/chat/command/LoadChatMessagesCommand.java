package com.messenger.delegate.chat.command;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.messenger.delegate.command.BaseChatCommand;
import com.messenger.delegate.user.UsersDelegate;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.paginations.ImmutablePaginationResult;
import com.messenger.messengerservers.paginations.PagePagination;
import com.messenger.messengerservers.paginations.PaginationResult;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.util.DecomposeMessagesHelper;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

import static com.innahema.collections.query.queriables.Queryable.from;

@CommandAction
public class LoadChatMessagesCommand extends BaseChatCommand<PaginationResult<Message>> implements InjectableAction {

   @Inject UsersDelegate usersDelegate;
   @Inject MessageDAO messageDAO;
   @Inject MessengerServerFacade messengerServerFacade;
   @Inject DecomposeMessagesHelper decomposeMessagesHelper;

   private int page;
   private int pageSize;
   private long beforeMessageTimestamp;

   public LoadChatMessagesCommand(String conversationId, int page, int pageSize, long beforeMessageTimestamp) {
      super(conversationId);
      this.page = page;
      this.pageSize = pageSize;
      this.beforeMessageTimestamp = beforeMessageTimestamp;
   }

   @Override
   protected void run(CommandCallback<PaginationResult<Message>> callback) throws Throwable {
      PagePagination<Message> pagination = messengerServerFacade.getPaginationManager()
            .getConversationHistoryPagination();
      pagination.setPageSize(pageSize);
      pagination.loadPage(conversationId, page, beforeMessageTimestamp)
            .map(this::filterAndRemoveDeletedMessages)
            .flatMap(this::prepareUsers)
            .doOnNext(paginationResult -> saveMessages(paginationResult.getResult()))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private void saveMessages(List<Message> messages) {
      DecomposeMessagesHelper.Result result = decomposeMessagesHelper.decomposeMessages(messages);
      from(result.messages).forEachR(msg -> msg.setSyncTime(System.currentTimeMillis()));
      decomposeMessagesHelper.saveDecomposeMessage(result);
   }

   private Observable<PaginationResult<Message>> prepareUsers(PaginationResult<Message> paginationResult) {
      List<Message> messages = paginationResult.getResult();
      if (messages.isEmpty()) return Observable.just(paginationResult);
      List<String> usersIds = from(messages).map(Message::getFromId)
            .notNulls()
            .union(from(messages).map(Message::getToId).notNulls())
            .distinct()
            .toList();
      if (!usersIds.isEmpty()) return usersDelegate.loadMissingUsers(usersIds).map(users -> paginationResult);
      return Observable.just(paginationResult);
   }

   private PaginationResult<Message> filterAndRemoveDeletedMessages(PaginationResult<Message> paginationResult) {
      return ImmutablePaginationResult.<Message>builder().from(paginationResult)
            .result(separateNonDeletedMessageAndRemoveDeleted(paginationResult.getResult()))
            .build();
   }

   private List<Message> separateNonDeletedMessageAndRemoveDeleted(@NonNull List<Message> messages) {
      if (messages.isEmpty()) return messages;

      Map<Boolean, Collection<Message>> messageGroup = from(messages).groupToMap(message -> message.getDeleted() != null);
      removeDeletedMessages(messageGroup.get(Boolean.TRUE));
      Collection<Message> nonDeletedMessages = messageGroup.get(Boolean.FALSE);
      return nonDeletedMessages == null ? Collections.emptyList() : new ArrayList<>(nonDeletedMessages);
   }

   private void removeDeletedMessages(@Nullable Collection<Message> messages) {
      if (messages == null || messages.isEmpty()) return;
      messageDAO.deleteMessageByIds(from(messages).map(Message::getId).toList());
   }
}
