package com.worldventures.dreamtrips.api.tests.smoke;

import com.worldventures.dreamtrips.api.facility.QRCodeHelper;
import com.worldventures.dreamtrips.api.fixtures.UserCredential;
import com.worldventures.dreamtrips.api.flagging.FlagItemHttpAction;
import com.worldventures.dreamtrips.api.flagging.GetFlagReasonsHttpAction;
import com.worldventures.dreamtrips.api.flagging.model.FlagReason;
import com.worldventures.dreamtrips.api.http.executor.AuthorizedActionExecutor;
import com.worldventures.dreamtrips.api.http.provider.SystemEnvProvider;
import com.worldventures.dreamtrips.api.photos.CreatePhotoHttpAction;
import com.worldventures.dreamtrips.api.photos.DeletePhotoHttpAction;
import com.worldventures.dreamtrips.api.photos.model.ImmutablePhotoCreationParams;
import com.worldventures.dreamtrips.api.photos.model.PhotoCreationParams;
import com.worldventures.dreamtrips.api.photos.model.PhotoSimple;
import com.worldventures.dreamtrips.api.post.CreatePostHttpAction;
import com.worldventures.dreamtrips.api.post.DeletePostHttpAction;
import com.worldventures.dreamtrips.api.post.model.request.PostData;
import com.worldventures.dreamtrips.api.post.model.response.PostSimple;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;
import com.worldventures.dreamtrips.api.uploadery.UploadImageHttpAction;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import ie.corballis.fixtures.annotation.Fixture;
import ru.yandex.qatools.allure.annotations.Features;

import static org.assertj.core.api.Assertions.assertThat;

@Features("Flagging")
public class FlaggingTest extends BaseTestWithSession {

    static final String CLASS_NAME = "FlaggingTests";
    static final String GROUP_PREPARATION = CLASS_NAME + "_preparation";

    @Fixture("user_zh_hk")
    UserCredential secondUserCredential;

    @Fixture("post_without_location")
    PostData data;

    AuthorizedActionExecutor secondUserExecutor;
    List<FlagReason> flagReasonList;
    PhotoSimple photo;
    PostSimple post;

    @BeforeClass
    void setUp() throws IOException {
        secondUserExecutor = as(secondUserCredential);
        photo = createPhoto();
        post = createPost();
    }

    @AfterClass
    void cleanUp() {
        if (photo != null) {
            deletePhoto(photo.uid());
        }

        if (post != null) {
            deletePost(post.uid());
        }
    }

    @Test(groups = GROUP_PREPARATION)
    void testObtainFlagReasons() {
        GetFlagReasonsHttpAction action = execute(new GetFlagReasonsHttpAction());
        assertThat(action.statusCode()).isEqualTo(200);

        flagReasonList = action.getFlagReasons();
        assertThat(flagReasonList).as("Check size of flag list")
                .isNotEmpty();

        assertThat(flagReasonList).as("Check flag's names").extracting("name")
                .doesNotHaveDuplicates()
                .doesNotContain("")
                .doesNotContainNull();

        assertThat(flagReasonList).as("Check different flag's ids").extracting("id")
                .doesNotHaveDuplicates();

    }

    @Test(dependsOnGroups = GROUP_PREPARATION)
    void testPostFlagging() {
        flagItem(post.uid());
    }

    @Test(dependsOnGroups = GROUP_PREPARATION)
    void testFlagAlreadyFlaggedPost() {
        flagItem(post.uid());
    }

    @Test(dependsOnGroups = GROUP_PREPARATION)
    void testPhotoFlagging() throws IOException {
        flagItem(photo.uid());
    }

    private void flagItem(String itemUid) {
        FlagReason flagReason = flagReasonList.get(0);
        String reason = flagReason.requireDescription() ? "test description" : flagReason.name();
        FlagItemHttpAction flagItemAction = secondUserExecutor.execute(new FlagItemHttpAction(itemUid, flagReason.id(), reason));
        assertThat(flagItemAction.statusCode()).isEqualTo(204);
    }

    private PostSimple createPost() {
        CreatePostHttpAction createPostAction = execute(new CreatePostHttpAction(data));
        assertThat(createPostAction.statusCode()).isEqualTo(200);
        return createPostAction.response();
    }

    private void deletePost(String postUid) {
        DeletePostHttpAction action = execute(new DeletePostHttpAction(postUid));
        assertThat(action.statusCode()).isEqualTo(204);
    }

    private PhotoSimple createPhoto() throws IOException {
        File qrCodeImage = QRCodeHelper.createQRCode(UUID.randomUUID().toString());
        String uploaderyUrl = new SystemEnvProvider().provide().apiUploaderyUrl();
        UploadImageHttpAction action = execute(new UploadImageHttpAction(uploaderyUrl, qrCodeImage));
        assertThat(action.statusCode()).isEqualTo(200);

        PhotoCreationParams params = ImmutablePhotoCreationParams.builder()
                .title("")
                .width(500)
                .height(500)
                .originURL(action.response().uploaderyPhoto().location())
                .shotAt(new Date()).build();
        CreatePhotoHttpAction createPhotoAction = execute(new CreatePhotoHttpAction(params));
        assertThat(createPhotoAction.statusCode()).isEqualTo(200);
        assertThat(createPhotoAction.response().id()).isNotNull();

        return createPhotoAction.response();
    }

    private void deletePhoto(String photoUid) {
        DeletePhotoHttpAction action = execute(new DeletePhotoHttpAction(photoUid));
        assertThat(action.statusCode()).isEqualTo(204);
    }
}
