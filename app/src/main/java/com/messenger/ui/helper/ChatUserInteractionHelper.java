package com.messenger.ui.helper;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import com.messenger.delegate.ProfileCrosser;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.constant.TranslationStatus;
import com.messenger.storage.dao.MediaDAO;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.storage.dao.TranslationsDAO;
import com.messenger.util.Utils;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.Observable;

public class ChatUserInteractionHelper {

    private final Router router;
    private final ProfileCrosser profileCrosser;
    private final MessageDAO messageDAO;
    private final TranslationsDAO translationsDAO;
    private final MediaDAO mediaDAO;

    private final String currentUserId;

    @Inject
    public ChatUserInteractionHelper(TranslationsDAO translationsDAO, MessageDAO messageDAO, MediaDAO mediaDAO,
                                     ProfileCrosser profileCrosser, Router router,
                                     SessionHolder<UserSession> sessionHolder) {
        this.translationsDAO = translationsDAO;
        this.messageDAO = messageDAO;
        this.mediaDAO = mediaDAO;
        this.profileCrosser = profileCrosser;
        this.router = router;
        this.currentUserId = sessionHolder.get().get().getUsername();
    }

    public void openUserProfile(DataUser user) {
        profileCrosser.crossToProfile(user);
    }

    public void openPhotoInFullScreen(String attachmentImageId, String conversationId, long sinceTime) {
        mediaDAO.getPhotoAttachmentsSinceTime(conversationId, currentUserId, sinceTime).take(1)
                .filter(photoAttachments -> !photoAttachments.isEmpty())
                .map(photoAttachments -> {
                    for (int i = 0; i < photoAttachments.size(); i++) {
                        if (TextUtils.equals(photoAttachments.get(i).getPhotoAttachmentId(), attachmentImageId)) {
                            return new Pair<>(photoAttachments, i);
                        }
                    }
                    return new Pair<>(photoAttachments, 0);
                }).map(listIntegerPair -> new FullScreenImagesBundle.Builder()
                                            .position(listIntegerPair.second)
                                            .type(TripImagesType.FIXED)
                                            .route(Route.MESSAGE_IMAGE_FULLSCREEN)
                                            .fixedList(new ArrayList<>(listIntegerPair.first))
                                            .build())
                .map(bundle -> NavigationConfigBuilder.forActivity()
                                    .data(bundle)
                                    .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                                    .build())
                .compose(new IoToMainComposer<>())
                .subscribe(navigationConfig -> router.moveTo(Route.FULLSCREEN_PHOTO_LIST, navigationConfig));
    }

    public void copyToClipboard(Context context, String messageId) {
        Observable.combineLatest(translationsDAO.getTranslation(messageId), messageDAO.getMessage(messageId),
                (dataTranslation, dataMessage) -> {
                    if (dataTranslation != null && dataTranslation.getTranslateStatus() == TranslationStatus.TRANSLATED) {
                        return dataTranslation.getTranslation();
                    } else {
                        return dataMessage.getText();
                    }
                })
                .take(1)
                .subscribe(text -> Utils.copyToClipboard(context, text));
    }

}
