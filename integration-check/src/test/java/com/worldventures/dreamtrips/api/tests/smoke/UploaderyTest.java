package com.worldventures.dreamtrips.api.tests.smoke;

import com.worldventures.dreamtrips.api.facility.QRCodeHelper;
import com.worldventures.dreamtrips.api.http.provider.SystemEnvProvider;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;
import com.worldventures.dreamtrips.api.uploadery.BaseUploadImageHttpAction;
import com.worldventures.dreamtrips.api.uploadery.UploadFeedbackImageHttpAction;
import com.worldventures.dreamtrips.api.uploadery.UploadImageHttpAction;
import com.worldventures.dreamtrips.api.uploadery.model.UploaderyImage;
import com.worldventures.dreamtrips.api.uploadery.model.UploaderyImageResponse;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import ru.yandex.qatools.allure.annotations.Features;

import static org.assertj.core.api.Assertions.assertThat;

@Features({"Feed", "Uploadery"})
public class UploaderyTest extends BaseTestWithSession {

    String uploaderyUrl;
    String qrCode;
    File qrCodeImage;
    File emptyImage;

    @BeforeClass
    void initializeQrCodeImage() throws IOException {
        uploaderyUrl = new SystemEnvProvider().provide().apiUploaderyUrl();
        assertThat(uploaderyUrl).as("Check enviroment parameter").isNotNull().isNotEmpty();

        qrCode = UUID.randomUUID().toString();
        qrCodeImage = QRCodeHelper.createQRCode(qrCode);

        emptyImage = File.createTempFile("empty", ".png");
        emptyImage.deleteOnExit();
    }

    @Test
    void testUploadNormalFile() throws Exception {
        testUploadingNormalFileInternal(new UploadImageHttpAction(uploaderyUrl, qrCodeImage));
    }

    @Test
    void testUploadNormalFileAsAttachment() throws Exception {
        testUploadingNormalFileInternal(new UploadFeedbackImageHttpAction(uploaderyUrl, qrCodeImage));
    }

    private void testUploadingNormalFileInternal(BaseUploadImageHttpAction testableAction) {
        BaseUploadImageHttpAction action = execute(testableAction);

        assertThat(action.statusCode()).isEqualTo(200);
        checkMainUploaderyImageInfo(action.response());

        String receivedKey = QRCodeHelper.readQRCodeFromURLWithAssertion(action.response().uploaderyPhoto().location());
        assertThat(receivedKey).isEqualTo(qrCode);
    }

    @Test
    void testUploadEmptyFile() throws IOException {
        testUploadEmptyFileInternal(new UploadImageHttpAction(uploaderyUrl, emptyImage));
    }

    @Test
    void testUploadEmptyFileAsFeedbackAttachment() throws IOException {
        testUploadEmptyFileInternal(new UploadFeedbackImageHttpAction(uploaderyUrl, emptyImage));
    }

    private void testUploadEmptyFileInternal(BaseUploadImageHttpAction testableAction) {
        BaseUploadImageHttpAction action = execute(testableAction);
        assertThat(action.statusCode()).isEqualTo(200);
        checkMainUploaderyImageInfo(action.response());
    }

    private void checkMainUploaderyImageInfo(UploaderyImageResponse response) {
        assertThat(response).isNotNull();

        UploaderyImage uploaderyImage = response.uploaderyPhoto();
        assertThat(uploaderyImage).isNotNull();
        assertThat(uploaderyImage.location()).isNotEmpty();
    }

}
