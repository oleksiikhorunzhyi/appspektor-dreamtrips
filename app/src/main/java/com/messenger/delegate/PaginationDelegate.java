package com.messenger.delegate;

import android.support.annotation.Nullable;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.entities.DataConversation;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.listeners.OnLoadedListener;
import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.paginations.PagePagination;
import com.messenger.storage.dao.AttachmentDAO;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.util.DecomposeMessagesHelper;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static com.innahema.collections.query.queriables.Queryable.from;

public class PaginationDelegate {

    public static final int DEFAULT_PAGE_SIZE = 20;

    private final MessengerServerFacade messengerServerFacade;
    private final DecomposeMessagesHelper decomposeMessagesHelper;
    private final UsersDelegate usersDelegate;

    private int pageSize = DEFAULT_PAGE_SIZE;

    private PagePagination<Message> messagePagePagination;

    @Inject PaginationDelegate(MessengerServerFacade messengerServerFacade, DecomposeMessagesHelper decomposeMessagesHelper, UsersDelegate usersDelegate) {
        this.messengerServerFacade = messengerServerFacade;
        this.decomposeMessagesHelper = decomposeMessagesHelper;
        this.usersDelegate = usersDelegate;
    }

    public void setPageSize(int pageSize) {
        stopPaginate();
        this.pageSize = pageSize;
    }

    public void loadConversationHistoryPage(DataConversation conversation, int page, long before,
                                            @Nullable PageLoadedListener loadedListener, @Nullable PageErrorListener errorListener) {
        if (messagePagePagination == null) {
            messagePagePagination = messengerServerFacade.getPaginationManager()
                    .getConversationHistoryPagination(conversation.getId(), pageSize);
        }

        messagePagePagination.setPersister(messages ->
                Observable.just(messages)
                .subscribeOn(Schedulers.io())
                .map(serverMessages -> {
                    DecomposeMessagesHelper.Result result = decomposeMessagesHelper.decomposeMessages(serverMessages);
                    from(result.messages).forEachR(msg -> msg.setSyncTime(System.currentTimeMillis()));
                    return result;
                }).subscribe(result -> decomposeMessagesHelper.saveDecomposeMessage(result),
                        throwable -> Timber.i(throwable, "Error while loading message page")));

        messagePagePagination.setOnEntityLoadedListener(new OnLoadedListener<Message>() {
            @Override
            public void onLoaded(List<Message> entities) {
                processLoadedMessages(entities, page, loadedListener);
            }

            @Override
            public void onError(Exception e) {
                if (errorListener == null) return;
                errorListener.onPageError();
            }
        });
        messagePagePagination.loadPage(page, before);
    }

    private void processLoadedMessages(List<Message> entities, int fromPage, PageLoadedListener loadedListener) {
        List<String> usersIds = Queryable.from(entities).map(Message::getFromId).toList();
        usersDelegate.loadIfNeedUsers(usersIds)
                    .subscribe(dataUsers -> {
                        if (loadedListener == null) return;
                        loadedListener.onPageLoaded(fromPage, entities);
                    }, e -> Timber.e(e, "Failed to update users"));
    }

    public void stopPaginate(){
        if (messagePagePagination != null){
            messagePagePagination.close();
        }
        messagePagePagination = null;
    }

    public interface PageLoadedListener {
        void onPageLoaded(int loadedPage, List<Message> loadedMessage);
    }

    public interface PageErrorListener {
        void onPageError();
    }
}
