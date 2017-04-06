package com.worldventures.dreamtrips.api.tests.smoke;

import com.worldventures.dreamtrips.api.bucketlist.model.BucketListLocation;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketListDining;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketListActivity;
import com.worldventures.dreamtrips.api.bucketlist.GetBucketListActivitiesHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.GetBucketListDiningsHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.GetBucketListLocationsHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.GetBucketListPopularActivitiesHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.GetBucketListPopularDinningsHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.GetBucketListPopularLocationsHttpAction;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;

import org.testng.annotations.Test;

import ru.yandex.qatools.allure.annotations.Features;

import static org.assertj.core.api.Assertions.assertThat;

@Features("Bucket List")
public class BucketListPopularTest extends BaseTestWithSession {

    BucketListLocation bucketListLocation;
    BucketListDining bucketListDining;
    BucketListActivity bucketListActivity;

    @Test
    void testGetBucketListLocations() {
        GetBucketListLocationsHttpAction action = execute(new GetBucketListLocationsHttpAction());
        assertThat(action.response()).isNotEmpty();
        bucketListLocation = action.response().get(0);
    }

    @Test(dependsOnMethods = "testGetBucketListLocations")
    void testSearchBucketListLocations() {
      GetBucketListPopularLocationsHttpAction action = execute(new GetBucketListPopularLocationsHttpAction(bucketListLocation.name()));
      assertThat(action.response()).isNotEmpty();
    }

    @Test
    void testGetBucketListActivities() {
        GetBucketListActivitiesHttpAction action = execute(new GetBucketListActivitiesHttpAction());
        assertThat(action.response()).isNotEmpty();
        bucketListActivity = action.response().get(0);
    }

    @Test(dependsOnMethods = "testGetBucketListActivities")
    void testSearchBucketListActivities() {
      GetBucketListPopularActivitiesHttpAction action = execute(new GetBucketListPopularActivitiesHttpAction(bucketListActivity.name()));
      assertThat(action.response()).isNotEmpty();
    }

    @Test
    void testGetBucketListDinings() {
        GetBucketListDiningsHttpAction action = execute(new GetBucketListDiningsHttpAction());
        assertThat(action.response()).isNotEmpty();
        bucketListDining = action.response().get(0);
      }

    @Test(dependsOnMethods = "testGetBucketListDinings")
    void testSearchBucketListDinings() {
      GetBucketListPopularDinningsHttpAction action = execute(new GetBucketListPopularDinningsHttpAction(bucketListDining.name()));
      assertThat(action.response()).isNotEmpty();
    }

}
