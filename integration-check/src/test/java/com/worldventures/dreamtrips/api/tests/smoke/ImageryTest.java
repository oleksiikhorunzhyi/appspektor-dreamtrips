package com.worldventures.dreamtrips.api.tests.smoke;

import com.worldventures.dreamtrips.api.facility.ImageUrlHelper;
import com.worldventures.dreamtrips.api.facility.QRCodeHelper;
import com.worldventures.dreamtrips.api.http.executor.SafeExecutor;
import com.worldventures.dreamtrips.api.http.provider.SystemEnvProvider;
import com.worldventures.dreamtrips.api.photos.CreatePhotoHttpAction;
import com.worldventures.dreamtrips.api.photos.DeletePhotoHttpAction;
import com.worldventures.dreamtrips.api.photos.model.ImmutablePhotoCreationParams;
import com.worldventures.dreamtrips.api.photos.model.PhotoCreationParams;
import com.worldventures.dreamtrips.api.photos.model.PhotoSimple;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;
import com.worldventures.dreamtrips.api.tests.util.ServerUtil;
import com.worldventures.dreamtrips.api.uploadery.UploadImageHttpAction;
import com.worldventures.dreamtrips.api.uploadery.model.UploaderyImage;

import net.glxn.qrgen.core.image.ImageType;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import ru.yandex.qatools.allure.annotations.Features;

import static org.assertj.core.api.Assertions.assertThat;

@Features({"Photos", "Imagery"})
public class ImageryTest extends BaseTestWithSession {

    String qrCode;
    final int originalSize = 500;
    final ImageType imageType = ImageType.PNG;

    UploaderyImage uploaderyImage;
    PhotoSimple photo;

    @BeforeClass
    void initializingImage() throws IOException {
        qrCode = UUID.randomUUID().toString();
        File qrCodeImage = QRCodeHelper.createSizedQRCode(qrCode, originalSize, originalSize, imageType);

        String uploaderyUrl = new SystemEnvProvider().provide().apiUploaderyUrl();
        UploadImageHttpAction action = execute(new UploadImageHttpAction(uploaderyUrl, qrCodeImage));

        assertThat(action.statusCode()).isEqualTo(200);
        uploaderyImage = action.response().uploaderyPhoto();

        PhotoCreationParams params = ImmutablePhotoCreationParams.builder()
                .title("")
                .width(originalSize)
                .height(originalSize)
                .originURL(uploaderyImage.location())
                .shotAt(new Date())
                .build();

        CreatePhotoHttpAction createPhotoAction = execute(new CreatePhotoHttpAction(params));
        assertThat(createPhotoAction.statusCode()).isEqualTo(200);
        assertThat(createPhotoAction.response().id()).isNotNull();

        photo = createPhotoAction.response();
        ServerUtil.waitForServerLag();
    }

    @AfterClass
    void cleanUp() {
        if (photo == null) return;

        SafeExecutor safeExecutor = SafeExecutor.from(this);
        safeExecutor.execute(new DeletePhotoHttpAction(photo.uid()));
    }

    @Test
    void testCheckTheSameSize() {
        String resizedImageUrl = ImageUrlHelper.obtainSizedImageUrl(photo.images().url(), originalSize, originalSize);
        checkSizeInfo(resizedImageUrl, originalSize, originalSize);
    }

    @Test
    void testImageCropping() {
        String resizedImageUrl = ImageUrlHelper.obtainSizedImageUrl(photo.images().url(), 200, 500);
        checkSameQrCode(resizedImageUrl);
        checkSizeInfo(resizedImageUrl, 200, 200);
    }

    @Test
    void testCheckVarySmallSize() {
        String resizedImageUrl = ImageUrlHelper.obtainSizedImageUrl(photo.images().url(), 10, 10);
        checkSizeInfo(resizedImageUrl, 10, 10);
    }

    @Test
    void testCheckBigSize() throws Exception {
        String resizedImageUrl = ImageUrlHelper.obtainSizedImageUrl(photo.images().url(), 1000, 1000);
        checkSameQrCode(resizedImageUrl);
        checkSizeInfo(resizedImageUrl, Math.min(originalSize, 1000), Math.min(originalSize, 1000));
    }

    private void checkSizeInfo(String resizedImageUrl, int width, int height) {
        ServerUtil.waitForServerLag(3000L);
        ImageUrlHelper.Size size = ImageUrlHelper.obtainSizeOfImageFromUrlWithAssertion(resizedImageUrl);
        assertThat(size.width).isEqualTo(width);
        assertThat(size.height).isEqualTo(height);
    }

    private void checkSameQrCode(String imageUrl) {
        ServerUtil.waitForServerLag(3000L);
        String receivedKey = QRCodeHelper.readQRCodeFromURLWithAssertion(imageUrl);
        assertThat(receivedKey).isEqualTo(qrCode);
    }
}
