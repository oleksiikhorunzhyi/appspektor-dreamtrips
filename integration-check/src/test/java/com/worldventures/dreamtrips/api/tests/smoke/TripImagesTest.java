package com.worldventures.dreamtrips.api.tests.smoke;

import com.worldventures.dreamtrips.api.http.executor.SafeExecutor;
import com.worldventures.dreamtrips.api.photos.CreatePhotoHttpAction;
import com.worldventures.dreamtrips.api.photos.CreatePhotosHttpAction;
import com.worldventures.dreamtrips.api.photos.DeletePhotoHttpAction;
import com.worldventures.dreamtrips.api.photos.GetPhotoHttpAction;
import com.worldventures.dreamtrips.api.photos.GetPhotosHttpAction;
import com.worldventures.dreamtrips.api.photos.GetPhotosOfUserHttpAction;
import com.worldventures.dreamtrips.api.photos.UpdatePhotoHttpAction;
import com.worldventures.dreamtrips.api.photos.model.PhotoCreationParams;
import com.worldventures.dreamtrips.api.photos.model.PhotoSimple;
import com.worldventures.dreamtrips.api.photos.model.PhotoUpdateParams;
import com.worldventures.dreamtrips.api.photos.model.PhotosCreationParams;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.util.List;

import ie.corballis.fixtures.annotation.Fixture;
import ru.yandex.qatools.allure.annotations.Features;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Features("Trip Images")
@Test(singleThreaded = true)
public class TripImagesTest extends BaseTestWithSession {

    @Fixture("new_photo")
    PhotoCreationParams photoCreationParams;

    @Fixture("new_photos")
    PhotosCreationParams photosCreationParams;

    @Fixture("updated_photo")
    PhotoUpdateParams photoUpdateParams;

    PhotoSimple createdPhoto;
    List<PhotoSimple> createdPhotos;

    @Test()
    void testGetListOfTripImages() {
        GetPhotosHttpAction action = execute(new GetPhotosHttpAction(1, 20));

        assertThat(action.response()).isNotEmpty();
    }

    @Test()
    void testGetListOfUserPhotos() {
        int userId = session().user().id();
        GetPhotosOfUserHttpAction action = execute(new GetPhotosOfUserHttpAction(userId, 1, 20));

        assertThat(action.response()).isNotEmpty();
    }

    @Test()
    void testPostPhoto() {
        CreatePhotoHttpAction action = execute(new CreatePhotoHttpAction(photoCreationParams));

        assertThat(action.statusCode()).isEqualTo(200);

        createdPhoto = action.response();
        assertThat(createdPhoto).isNotNull();
        assertThat(createdPhoto.id()).isNotNull();
    }

    @Test()
    void testPostPhotos() {
        CreatePhotosHttpAction action = execute(new CreatePhotosHttpAction(photosCreationParams));

        assertThat(action.statusCode()).isEqualTo(200);

        createdPhotos = action.response();

        assertThat(createdPhoto).isNotNull();
        assertThat(createdPhoto.id()).isNotNull();
    }

    @Test(dependsOnMethods = "testPostPhoto")
    void testGetTripImageInfo() {
        GetPhotoHttpAction action = execute(new GetPhotoHttpAction(createdPhoto.uid()));

        assertThat(action.response().uid()).isEqualTo(createdPhoto.uid());
        assertThat(action.response().images()).isEqualTo(createdPhoto.images());
    }

    @Test(dependsOnMethods = "testUpdateTripImage")
    void testDeleteTripImage() {
        DeletePhotoHttpAction action = execute(new DeletePhotoHttpAction(createdPhoto.uid()));
        assertThat(action.statusCode()).isEqualTo(204);
    }

    @Test(dependsOnMethods = "testGetTripImageInfo")
    void testUpdateTripImage() {
        UpdatePhotoHttpAction action = execute(new UpdatePhotoHttpAction(createdPhoto.uid(), photoUpdateParams));

        assertThat(action.statusCode()).isEqualTo(200);
        assertThat(action.response().title()).isEqualTo(photoUpdateParams.title());
    }

    @AfterClass
    public void cleanup() {
        SafeExecutor safeExecutor = SafeExecutor.from(this);

        if (createdPhoto != null) safeExecutor.execute(new DeletePhotoHttpAction(createdPhoto.uid()));
        if (createdPhotos != null) {
            for (PhotoSimple photoSimple : createdPhotos) {
                safeExecutor.execute(new DeletePhotoHttpAction(photoSimple.uid()));
            }
        }
    }
}
