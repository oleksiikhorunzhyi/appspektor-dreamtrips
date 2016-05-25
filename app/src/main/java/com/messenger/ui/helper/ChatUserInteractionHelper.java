package com.messenger.ui.helper;

import android.content.Context;

import com.messenger.delegate.ProfileCrosser;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.constant.TranslationStatus;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.storage.dao.TranslationsDAO;
import com.messenger.storage.helper.PhotoAttachmentHelper;
import com.messenger.util.Utils;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.Observable;

public class ChatUserInteractionHelper {

    private final PhotoAttachmentHelper photoAttachmentHelper;
    private final Router router;
    private final ProfileCrosser profileCrosser;
    private final MessageDAO messageDAO;
    private final TranslationsDAO translationsDAO;

    @Inject
    public ChatUserInteractionHelper(TranslationsDAO translationsDAO, MessageDAO messageDAO,
                                     ProfileCrosser profileCrosser, Router router,
                                     PhotoAttachmentHelper photoAttachmentHelper) {
        this.translationsDAO = translationsDAO;
        this.messageDAO = messageDAO;
        this.profileCrosser = profileCrosser;
        this.router = router;
        this.photoAttachmentHelper = photoAttachmentHelper;
    }

    public void openUserProfile(DataUser user) {
        profileCrosser.crossToProfile(user);
    }

    public void openPhotoInFullScreen(String attachmentImageId) {
        photoAttachmentHelper.obtainPhotoAttachment(attachmentImageId)
                .map(photoAttachment -> {
                    ArrayList<IFullScreenObject> items = new ArrayList<>();
                    items.add(photoAttachment);
                    return items;
                })
                .compose(new IoToMainComposer<>())
                .subscribe(items -> {
                    router.moveTo(Route.FULLSCREEN_PHOTO_LIST,
                            NavigationConfigBuilder.forActivity()
                                    .data(new FullScreenImagesBundle.Builder()
                                            .position(0)
                                            .type(TripImagesType.FIXED)
                                            .route(Route.MESSAGE_IMAGE_FULLSCREEN)
                                            .fixedList(items)
                                            .build())
                                    .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                                    .build());

                });
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
