package com.worldventures.dreamtrips.api.tests.smoke;

import com.worldventures.dreamtrips.api.bucketlist.AddPhotoToBucketItemHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.CreateBucketItemHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.DeleteBucketItemHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.DeletePhotoFromBucketItemHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.GetBucketItemHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.GetBucketItemsForUserHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.GetBucketItemsHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.GetBucketListActivitiesHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.GetBucketListCategoriesHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.ImmutableGetBucketItemsForUserHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.UpdateBucketItemHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.UpdateBucketItemPositionHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketCreationBody;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketItemSimple;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketItemSocialized;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketListActivity;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketPhotoBody;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketType;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketUpdateBody;
import com.worldventures.dreamtrips.api.bucketlist.model.ImmutableBucketCreationBody;
import com.worldventures.dreamtrips.api.bucketlist.model.ImmutableBucketItemSocialized;
import com.worldventures.dreamtrips.api.fixtures.UserCredential;
import com.worldventures.dreamtrips.api.http.executor.AuthorizedActionExecutor;
import com.worldventures.dreamtrips.api.http.executor.SafeExecutor;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import ie.corballis.fixtures.annotation.Fixture;
import ru.yandex.qatools.allure.annotations.Features;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * BucketListTests checks CRUD.
 * It starts with unique item creation, followed by getters/updaters methods.
 * Tests are finished with deletion of created item.
 */
@Features("Bucket List")
public class BucketListTest extends BaseTestWithSession {

    public static final String GROUP_CREATION = "blt.creation";
    public static final String GROUP_READING = "blt.reading";
    public static final String GROUP_MANIPULATION = "blt.manipulation";
    public static final String GROUP_CLEANUP = "blt.cleanup";

    @Fixture("user_with_no_rds")
    UserCredential anotherUser;
    @Fixture("bucket_body_for_creation")
    BucketCreationBody creationBody;
    @Fixture("bucket_body_for_update")
    BucketUpdateBody updateBody;
    @Fixture("bucket_photo_to_attach")
    BucketPhotoBody photoBody;

    volatile BucketItemSocialized createdItem;
    volatile BucketItemSocialized createdActivityItem;
    volatile List<BucketItemSimple> allBucketItems;
    volatile BucketPhoto attachedPhoto;

    ///////////////////////////////////////////////////////////////////////////
    // Preparations/Tearing Down Rules
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Created bucket item should not intersect with the same instance possibly created from another test runner.
     * So we put a unique suffix to it's name;
     */
    @BeforeClass
    public void prepareFixtures() {
        creationBody = ImmutableBucketCreationBody.builder().from(creationBody)
                .name(creationBody.name() + "_" + System.currentTimeMillis())
                .build();
    }

    @AfterClass
    public void cleanup() {
        SafeExecutor safeExecutor = SafeExecutor.from(this);
        // in case something went wrong and deletion didn't worked out
        if (createdItem != null) safeExecutor.execute(new DeleteBucketItemHttpAction(createdItem.uid()));
        if (createdActivityItem != null) safeExecutor. execute(new DeleteBucketItemHttpAction(createdActivityItem.uid()));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Tests
    ///////////////////////////////////////////////////////////////////////////

    @Test
    void testGetBucketListCategories() {
        GetBucketListCategoriesHttpAction action = execute(new GetBucketListCategoriesHttpAction());
        assertThat(action.response()).isNotEmpty();
    }

    @Test(groups = GROUP_CREATION)
    void testCreateBucketListItem() {
        createdItem = execute(new CreateBucketItemHttpAction(creationBody)).response();
        assertThat(createdItem).isNotNull();
        assertThat(createdItem.name()).isEqualTo(creationBody.name());
        assertThat(createdItem.tags()).hasSize(2);
        assertThat(createdItem.friends()).hasSize(2);
    }

    @Test(groups = GROUP_CREATION)
    void testCreateBucketListItemFromReference() {
        List<BucketListActivity> activities = execute(new GetBucketListActivitiesHttpAction()).response();
        assertThat(activities).isNotEmpty();
        BucketListActivity activity = activities.get(activities.size()-1);

        BucketCreationBody body = ImmutableBucketCreationBody.builder()
                .from(creationBody)
                .name(null) // name is not required when you create bucket item from reference
                .type(BucketType.ACTIVITY.toString())
                .id(activity.id().toString())
                .build();

        createdActivityItem = execute(new CreateBucketItemHttpAction(body)).response();
        assertThat(createdActivityItem).isNotNull();
        assertThat(createdActivityItem.type()).isEqualTo(BucketType.ACTIVITY);
        // Get the last item because otherwise it can affect testSearchBucketListActivities
        assertThat(createdActivityItem.name()).isEqualTo(activity.name());
    }

    @Test(groups = GROUP_CREATION, dependsOnMethods = "testCreateBucketListItem")
    void testCreateBucketListItemWithDuplicateFail() {
        CreateBucketItemHttpAction action = execute(new CreateBucketItemHttpAction(creationBody));
        assertThat(action.statusCode()).isEqualTo(422);
    }

    @Test(groups = {GROUP_MANIPULATION, GROUP_READING}, dependsOnGroups = GROUP_CREATION)
    void testGetBucketList() {
        allBucketItems = execute(new GetBucketItemsHttpAction()).response();
        assertThat(allBucketItems)
            .isNotEmpty()
            .extracting(BucketItemSimple::uid)
            .contains(createdItem.uid());
        // find the item and compare it with created one
        BucketItemSimple item = allBucketItems.stream().filter(bucketItemSimple -> bucketItemSimple.uid()
                .equals(createdItem.uid())).findFirst().get();
        assertThat(item.equals(createdItem));
    }

    @Test
    void testGetBucketListForSomeUser() {
        AuthorizedActionExecutor executor = as(anotherUser);
        int anotherUserId = executor.getSession().user().id();
        GetBucketItemsForUserHttpAction.Params params = ImmutableGetBucketItemsForUserHttpAction.Params.of(anotherUserId);
        GetBucketItemsForUserHttpAction action = executor.execute(new GetBucketItemsForUserHttpAction(params));
        //
        assertThat(action.response()).isNotEmpty();
    }

    @Test(groups = GROUP_MANIPULATION, dependsOnGroups = GROUP_CREATION)
    void testGetBucketListItem() {
        BucketItemSocialized item = execute(new GetBucketItemHttpAction(createdItem.uid())).response();
        assertThat(createdItem).isEqualTo(ImmutableBucketItemSocialized.builder().from(item).id(item.id()).build());
        assertThat(item.tags()).hasSize(2);
        assertThat(item.friends()).hasSize(2);
    }

    @Test(groups = GROUP_MANIPULATION, dependsOnGroups = {GROUP_CREATION, GROUP_READING})
    void testUpdateBucketListItem() {
        BucketItem item = execute(new UpdateBucketItemHttpAction(createdItem.uid(), updateBody)).response();
        assertThat(item).isNotNull();
        assertThat(item.description()).isEqualTo(updateBody.description());
        assertThat(item.uid()).isEqualTo(createdItem.uid());
    }

    @Test(groups = GROUP_MANIPULATION, dependsOnGroups = {GROUP_CREATION, GROUP_READING})
    void testUpdateBucketListItemPosition() {
        // find needed bucket item in the list
        assertThat(allBucketItems).extracting(BucketItemSimple::uid).contains(createdItem.uid());
        BucketItemSimple item = allBucketItems.stream()
                .filter(bucketItemSimple -> bucketItemSimple.uid().equals(createdItem.uid()))
                .findFirst().get();
        // update its position
        int curPos = allBucketItems.indexOf(item);
        int newPos = curPos != 0 ? 0 : allBucketItems.size() - 1;
        UpdateBucketItemPositionHttpAction action = execute(new UpdateBucketItemPositionHttpAction(createdItem.uid(), newPos));
        assertThat(action.statusCode()).isEqualTo(204);
    }

    @Test(groups = GROUP_MANIPULATION, dependsOnGroups = {GROUP_CREATION, GROUP_READING})
    void testUploadPhotoToBucketListItem() {
        attachedPhoto = execute(new AddPhotoToBucketItemHttpAction(createdItem.uid(), photoBody)).response();
        assertThat(attachedPhoto).isNotNull();
    }

    @Test(groups = GROUP_MANIPULATION, dependsOnGroups = GROUP_CREATION, dependsOnMethods = "testUploadPhotoToBucketListItem")
    void testDetachPhotoFromBucketList() {
        DeletePhotoFromBucketItemHttpAction action = execute(
                new DeletePhotoFromBucketItemHttpAction(createdItem.uid(), attachedPhoto.uid())
        );
        assertThat(action.statusCode()).isEqualTo(204);
    }

    @Test(groups = GROUP_CLEANUP, dependsOnGroups = GROUP_MANIPULATION)
    void testDeleteBucketListItem() {
        DeleteBucketItemHttpAction action = execute(new DeleteBucketItemHttpAction(createdItem.uid()));
        assertThat(action.statusCode()).isEqualTo(204);
        createdItem = null;
    }

}
