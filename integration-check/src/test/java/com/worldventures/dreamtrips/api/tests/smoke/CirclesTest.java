package com.worldventures.dreamtrips.api.tests.smoke;

import com.google.common.collect.Lists;
import com.worldventures.dreamtrips.api.circles.AddFriendsToCircleHttpAction;
import com.worldventures.dreamtrips.api.circles.GetCirclesHttpAction;
import com.worldventures.dreamtrips.api.circles.RemoveFriendsFromCircleHttpAction;
import com.worldventures.dreamtrips.api.circles.model.Circle;
import com.worldventures.dreamtrips.api.friends.GetFriendsHttpAction;
import com.worldventures.dreamtrips.api.friends.model.FriendProfile;
import com.worldventures.dreamtrips.api.friends.model.ImmutableFriendsParams;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;
import com.worldventures.dreamtrips.api.tests.util.ServerUtil;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import ru.yandex.qatools.allure.annotations.Features;

import static org.assertj.core.api.Assertions.assertThat;

@Features("Friends")
public class CirclesTest extends BaseTestWithSession {

    Circle circle;
    FriendProfile candidate;

    @BeforeClass
    void getCandidate() {
        candidate = getFriend();
    }

    @Test()
    void testGetCircles() {
        GetCirclesHttpAction action = execute(new GetCirclesHttpAction());
        List<Circle> circles = action.response();

        assertThat(circles).isNotEmpty().doesNotContainNull();

        circle = circles.get(0);
        removeFriendFromCircleIfRequired();
    }

    @Test(dependsOnMethods = "testGetCircles")
    void testAddFriendsToCircle() {
        addFriendToCircle(circle.id(), candidate.id());
        ServerUtil.waitForServerLag();
        assertThat(getFriend(candidate).circles()).contains(circle);
    }

    @Test(dependsOnMethods = "testAddFriendsToCircle")
    void testRemoveFriendsFromCircle() {
        removeFriendFromCircle(circle.id(), candidate.id());
        ServerUtil.waitForServerLag();
        List<Circle> circles = getFriend(candidate).circles();
        if (circles != null) assertThat(circles).doesNotContain(circle);
    }

    @AfterClass
    void removeFriendFromCircleIfRequired() {
        List<Circle> circles = getFriend(candidate, false).circles();
        if (circles != null && circles.contains(circle)) {
            removeFriendFromCircle(circle.id(), candidate.id());
        }
    }

    private FriendProfile getFriend() {
        return getFriend(null, true);
    }

    private FriendProfile getFriend(FriendProfile candidate) {
        return getFriend(candidate, true);
    }

    private FriendProfile getFriend(FriendProfile candidate, boolean withAssertion) {
        ImmutableFriendsParams.Builder params = ImmutableFriendsParams.builder().page(1).perPage(10);
        if (candidate != null) params.query(candidate.username());
        //
        List<FriendProfile> friendProfiles = execute(new GetFriendsHttpAction(params.build())).response();
        FriendProfile profile = null;
        if (withAssertion) {
            assertThat(friendProfiles).isNotEmpty();
            profile = friendProfiles.get(0);
        } else if (!(friendProfiles == null || friendProfiles.isEmpty())) {
            profile = friendProfiles.get(0);
        }
        return profile;
    }

    AddFriendsToCircleHttpAction addFriendToCircle(String circleId, int friendId) {
        List<Integer> friendsIds = Lists.newArrayList(friendId);
        AddFriendsToCircleHttpAction addAction = execute(new AddFriendsToCircleHttpAction(circleId, friendsIds));
        assertThat(addAction.statusCode()).isEqualTo(204);
        return addAction;
    }

    RemoveFriendsFromCircleHttpAction removeFriendFromCircle(String circleId, int friendId) {
        List<Integer> friendsIds = Lists.newArrayList(friendId);
        RemoveFriendsFromCircleHttpAction removeAction = execute(new RemoveFriendsFromCircleHttpAction(circleId, friendsIds));
        assertThat(removeAction.statusCode()).isEqualTo(204);
        return removeAction;
    }

}
