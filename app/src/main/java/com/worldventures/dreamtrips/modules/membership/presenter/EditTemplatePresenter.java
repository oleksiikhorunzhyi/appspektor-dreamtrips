package com.worldventures.dreamtrips.modules.membership.presenter;

import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.Share;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketAddPhotoClickEvent;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhotoUploadTask;
import com.worldventures.dreamtrips.modules.bucketlist.view.custom.BucketPhotosView;
import com.worldventures.dreamtrips.modules.bucketlist.view.custom.IBucketPhotoView;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.membership.api.CreateFilledInvitationsTemplateQuery;
import com.worldventures.dreamtrips.modules.membership.api.InviteBody;
import com.worldventures.dreamtrips.modules.membership.api.SendInvitationsQuery;
import com.worldventures.dreamtrips.modules.membership.api.UploadTemplatePhotoCommand;
import com.worldventures.dreamtrips.modules.membership.event.InvitesSentEvent;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;
import com.worldventures.dreamtrips.modules.membership.model.Member;
import com.worldventures.dreamtrips.modules.membership.model.TemplatePhoto;
import com.worldventures.dreamtrips.modules.tripsimages.view.dialog.ImagePickCallback;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class EditTemplatePresenter extends Presenter<EditTemplatePresenter.View> {

    private InviteTemplate template;
    private boolean preview = false;

    private Uri selectedImageUri;

    @Inject
    Injector injector;

    protected ImagePickCallback selectImageCallback = (fragment, image, error) -> {
        if (error != null) {
            view.informUser(error);
        } else {
            Uri uri = Uri.fromFile(new File(image.getFileThumbnail()));
            handlePhotoPick(uri);
        }
    };

    protected ImagePickCallback fbCallback = (fragment, image, error) -> {
        if (error != null) {
            view.informUser(error);
        } else {
            Uri uri = Uri.parse(image.getFilePathOriginal());
            handlePhotoPick(uri);
        }
    };

    protected BucketPhotosView.DeleteButtonCallback deleteButtonCallback = () -> {
        delete();
    };

    public EditTemplatePresenter(View view, InviteTemplate template) {
        super(view);
        this.template = template;
    }

    @Override
    public void resume() {
        super.resume();
        view.setFrom(template.getFrom());
        view.setSubject(template.getTitle());
        List<String> to = getMembersAddress();
        view.setTo(TextUtils.join(", ", to));
        view.setWebViewContent(template.getContent());
        if (!TextUtils.isEmpty(template.getVideo())) {
            view.hidePhotoUpload();
        }

    }

    public List<String> getMembersAddress() {
        List<String> to = new ArrayList<>();
        for (Member member : template.getTo()) {
            to.add(member.getSubtitle());
        }
        return to;
    }

    public void onEvent(BucketAddPhotoClickEvent event) {
        eventBus.cancelEventDelivery(event);
        view.getBucketPhotosView().showAddPhotoDialog(selectedImageUri != null);
    }

    public ImagePickCallback getPhotoChooseCallback() {
        return selectImageCallback;
    }

    public ImagePickCallback getFbCallback() {
        return fbCallback;
    }

    public BucketPhotosView.DeleteButtonCallback getDeleteCallback() {
        return deleteButtonCallback;
    }

    public Intent getShareIntent() {
        InviteTemplate.Type type = template.getType();
        List<String> membersAddress = getMembersAddress();
        String[] addresses = membersAddress.toArray(new String[membersAddress.size()]);
        Intent intent;
        if (type == InviteTemplate.Type.EMAIL) {
            intent = Share.newEmailIntent(getSubject(), getBody(), addresses);
        } else {
            intent = Share.newSmsIntent(context, getSmsBody(), addresses);
        }
        return intent;
    }

    public void previewAction() {
        preview = true;
        updatePreview();
    }

    private void getFilledInvitationsTemplateFailed(SpiceException spiceException) {
        view.finishLoading();
        handleError(spiceException);
    }

    private void getFilledInvitationsTemplateSuccess(InviteTemplate inviteTemplate) {
        view.finishLoading();
        if (inviteTemplate != null) {
            view.setWebViewContent(inviteTemplate.getContent());
            template.setContent(inviteTemplate.getContent());
            template.setLink(inviteTemplate.getLink());
            if (preview) {
                preview = false;
                activityRouter.openPreviewActivity(inviteTemplate.getLink());
            }
        } else {
            handleError(new SpiceException(""));
        }
    }

    private void delete() {
        selectedImageUri = null;
        template.setCoverImage(null);
        view.getBucketPhotosView().deleteAtPosition(0);
        view.getBucketPhotosView().addFirstItem();
    }

    private void sentInvitesFailed(SpiceException spiceException) {
        handleError(spiceException);
    }

    private void createInviteSuccess(InviteTemplate template) {
        Timber.i("createInviteSuccess");
        getFilledInvitationsTemplateSuccess(template);
        activityRouter.openDefaultShareIntent(getShareIntent());
        notifyServer();
    }

    private void createInviteFailed(SpiceException spiceException) {
        Timber.e(spiceException, "");
    }

    private void sentInviteSuccess(JSONObject aVoid) {
        Timber.i("sentInviteSuccess");
        eventBus.post(new InvitesSentEvent());
    }

    private String getSubject() {
        return template.getTitle();
    }

    private String getBody() {
        return String.format(context.getString(R.string.invitation_text_template),
                getUsername(),
                getMessage(),
                template.getLink());
    }

    private String getSmsBody() {
        return template.getTitle() + " " + template.getLink();
    }

    private String getMessage() {
        return TextUtils.isEmpty(view.getMessage()) ? "" :
                "\n\n" + view.getMessage() + ".";
    }

    private String getUsername() {
        return getMembersAddress().size() > 1 ? "" : " " + template.getName();
    }

    private void notifyServer() {
        InviteBody body = new InviteBody();
        body.setContacts(getContactAddress());
        body.setTemplateId(template.getId());
        body.setType(template.getType());
        dreamSpiceManager.execute(
                new SendInvitationsQuery(body),
                this::sentInviteSuccess,
                this::sentInvitesFailed
        );
    }

    private List<String> getContactAddress() {
        return Queryable.from(template.getTo()).map(Member::getSubtitle).toList();
    }

    private void updatePreview() {
        view.startLoading();
        doRequest(new CreateFilledInvitationsTemplateQuery(
                        template.getId(),
                        view.getMessage(),
                        null),
                this::getFilledInvitationsTemplateSuccess,
                this::getFilledInvitationsTemplateFailed);
    }

    public void shareRequest() {
        dreamSpiceManager.execute(new CreateFilledInvitationsTemplateQuery(template.getId(),
                        view.getMessage(), null),
                this::createInviteSuccess,
                this::createInviteFailed);
    }

    private void handlePhotoPick(Uri uri) {
        selectedImageUri = uri;
        BucketPhotoUploadTask task = new BucketPhotoUploadTask();
        task.setTaskId((int) System.currentTimeMillis());
        task.setBucketId(template.getId());
        task.setFilePath(uri.toString());
        startUpload(task);
    }

    private void startUpload(final BucketPhotoUploadTask task) {
        view.startLoading();
        UploadTemplatePhotoCommand uploadBucketPhotoCommand = new UploadTemplatePhotoCommand(task,
                getMessage(), injector);
        doRequest(uploadBucketPhotoCommand,
                this::photoUploaded,
                this::getFilledInvitationsTemplateFailed);
    }

    private void photoUploaded(InviteTemplate inviteTemplate) {
        view.finishLoading();
        template.setCoverImage(inviteTemplate.getCoverImage());
        view.getBucketPhotosView().deleteAtPosition(0);
        view.getBucketPhotosView().addTemplatePhoto(new TemplatePhoto(selectedImageUri));
    }

    public Parcelable getTemplate() {
        return template;
    }

    public interface View extends Presenter.View {

        void setFrom(String from);

        void setSubject(String title);

        void setTo(String s);

        void setWebViewContent(String content);

        String getMessage();

        void startLoading();

        void finishLoading();

        IBucketPhotoView getBucketPhotosView();

        void hidePhotoUpload();
    }
}
