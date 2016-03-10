package com.messenger.delegate;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataTranslation;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.constant.TranslationStatus;
import com.messenger.messengerservers.listeners.OnLoadedListener;
import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.paginations.PagePagination;
import com.messenger.storage.dao.AttachmentDAO;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.storage.dao.TranslationsDAO;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;

import java.util.LinkedList;
import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;

import static com.innahema.collections.query.queriables.Queryable.from;

public class PaginationDelegate {

    public static final int DEFAULT_PAGE_SIZE = 20;

    private final MessengerServerFacade messengerServerFacade;
    private final MessageDAO messageDAO;
    private final AttachmentDAO attachmentDAO;
    private final TranslationsDAO translationsDAO;
    private final LocaleHelper localeHelper;

    private final String nativeLanguage;
    private int pageSize = DEFAULT_PAGE_SIZE;

    private PagePagination<Message> messagePagePagination;

    public PaginationDelegate(MessengerServerFacade messengerServerFacade, MessageDAO messageDAO, AttachmentDAO attachmentDAO,
                              TranslationsDAO translationsDAO, LocaleHelper localeHelper, SessionHolder<UserSession> userSessionHolder) {
        this.messengerServerFacade = messengerServerFacade;
        this.messageDAO = messageDAO;
        this.attachmentDAO = attachmentDAO;
        this.translationsDAO = translationsDAO;
        this.localeHelper = localeHelper;

        String userLocaleName = userSessionHolder.get().get().getUser().getLocale();
        this.nativeLanguage = localeHelper.obtainLanguageCode(userLocaleName);
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
                    List<DataMessage> msgs = from(serverMessages).map(DataMessage::new).toList();
                    List<DataAttachment> attachments = getDataAttachment(serverMessages);
                    from(msgs).forEachR(msg -> msg.setSyncTime(System.currentTimeMillis()));
                    List<DataTranslation> translations = from(msgs).filter(msg -> TextUtils.equals(nativeLanguage, localeHelper.obtainLanguageCode(msg.getLocaleName())))
                            .map(msg -> new DataTranslation(msg.getId(), null, TranslationStatus.NATIVE)).toList();
                    return new Object[]{msgs, attachments, translations};
                })
                .subscribe(data -> {
                    messageDAO.save((List<DataMessage>) data[0]);
                    attachmentDAO.save((List<DataAttachment>) data[1]);
                    translationsDAO.save((List<DataTranslation>) data[2]);
                }));

        messagePagePagination.setOnEntityLoadedListener(new OnLoadedListener<Message>() {
            @Override
            public void onLoaded(List<Message> entities) {
                if (loadedListener == null) return;
                loadedListener.onPageLoaded(page, entities);
            }

            @Override
            public void onError(Exception e) {
                if (errorListener == null) return;
                errorListener.onPageError();
            }
        });
        messagePagePagination.loadPage(page, before);
    }

    private List<DataAttachment> getDataAttachment(List<Message> messages) {
        List<DataAttachment> result = new LinkedList<>();
        for (Message m : messages) {
            result.addAll(DataAttachment.fromMessage(m));
        }
        return result;
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
