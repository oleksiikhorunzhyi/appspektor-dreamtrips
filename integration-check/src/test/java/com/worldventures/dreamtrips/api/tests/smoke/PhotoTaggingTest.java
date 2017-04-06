package com.worldventures.dreamtrips.api.tests.smoke;

import com.worldventures.dreamtrips.api.friends.GetFriendsHttpAction;
import com.worldventures.dreamtrips.api.friends.model.FriendProfile;
import com.worldventures.dreamtrips.api.friends.model.ImmutableFriendsParams;
import com.worldventures.dreamtrips.api.http.executor.SafeExecutor;
import com.worldventures.dreamtrips.api.photos.AddUserTagsToPhotoHttpAction;
import com.worldventures.dreamtrips.api.photos.CreatePhotoHttpAction;
import com.worldventures.dreamtrips.api.photos.DeletePhotoHttpAction;
import com.worldventures.dreamtrips.api.photos.RemoveUserTagsFromPhotoHttpAction;
import com.worldventures.dreamtrips.api.photos.model.ImmutablePhotoTagParams;
import com.worldventures.dreamtrips.api.photos.model.PhotoCreationParams;
import com.worldventures.dreamtrips.api.photos.model.PhotoSimple;
import com.worldventures.dreamtrips.api.photos.model.PhotoTagParams;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import ie.corballis.fixtures.annotation.Fixture;
import ru.yandex.qatools.allure.annotations.Features;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Features({"Photos", "Tagging"})
public class PhotoTaggingTest extends BaseTestWithSession {

    @Fixture("new_photo")
    PhotoCreationParams photoCreationParams;

    @Fixture("photo_tag")
    PhotoTagParams photoTagParams;

    PhotoSimple photo;

    FriendProfile friend;


    @BeforeClass
    void createPhoto() {
        CreatePhotoHttpAction action = execute(new CreatePhotoHttpAction(photoCreationParams));
        photo = action.response();
        assertThat(action.statusCode()).isEqualTo(200);
        assertThat(photo.id()).isNotNull();

        GetFriendsHttpAction getFriendsAction = execute(new GetFriendsHttpAction(
                ImmutableFriendsParams.builder()
                        .page(1)
                        .perPage(10)
                        .build())
        );
        assertThat(getFriendsAction.response()).isNotEmpty();

        friend = getFriendsAction.response().get(0);
        photoTagParams = ImmutablePhotoTagParams
                .builder()
                .from(photoTagParams)
                .userId(friend.id())
                .build();
    }

    @AfterClass
    void cleanUp() {
        if (photo == null) return;

        SafeExecutor safeExecutor = SafeExecutor.from(this);
        safeExecutor.execute(new DeletePhotoHttpAction(photo.uid()));
    }

    @Test()
    void testAddTagsToPhoto() {
        List<PhotoTagParams> tags = singletonList(photoTagParams);
        AddUserTagsToPhotoHttpAction action = execute(new AddUserTagsToPhotoHttpAction(photo.uid(), tags));

        assertThat(action.statusCode()).isEqualTo(200);
        assertThat(action.response()).isNotEmpty();
    }

    @Test(dependsOnMethods = "testAddTagsToPhoto")
    void testRemoveTagsFromPhoto() {
        RemoveUserTagsFromPhotoHttpAction action = execute(
                new RemoveUserTagsFromPhotoHttpAction(photo.uid(), singletonList(friend.id()))
        );

        assertThat(action.statusCode()).isEqualTo(204);
    }
}
