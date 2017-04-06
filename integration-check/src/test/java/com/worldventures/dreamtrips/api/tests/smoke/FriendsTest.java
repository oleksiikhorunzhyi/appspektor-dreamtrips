package com.worldventures.dreamtrips.api.tests.smoke;

import com.worldventures.dreamtrips.api.api_common.model.PaginatedParams;
import com.worldventures.dreamtrips.api.circles.GetCirclesHttpAction;
import com.worldventures.dreamtrips.api.circles.model.Circle;
import com.worldventures.dreamtrips.api.fixtures.UserCredential;
import com.worldventures.dreamtrips.api.friends.AnswerFriendRequestsHttpAction;
import com.worldventures.dreamtrips.api.friends.GetFriendRequestsHttpAction;
import com.worldventures.dreamtrips.api.friends.GetFriendsHttpAction;
import com.worldventures.dreamtrips.api.friends.GetMutualFriendsHttpAction;
import com.worldventures.dreamtrips.api.friends.HideFriendRequestHttpAction;
import com.worldventures.dreamtrips.api.friends.RemoveFromFriendsHttpAction;
import com.worldventures.dreamtrips.api.friends.SearchFriendsHttpAction;
import com.worldventures.dreamtrips.api.friends.SendFriendRequestHttpAction;
import com.worldventures.dreamtrips.api.friends.model.FriendCandidate;
import com.worldventures.dreamtrips.api.friends.model.FriendProfile;
import com.worldventures.dreamtrips.api.friends.model.FriendRequestParams;
import com.worldventures.dreamtrips.api.friends.model.FriendRequestResponse;
import com.worldventures.dreamtrips.api.friends.model.FriendsParams;
import com.worldventures.dreamtrips.api.friends.model.ImmutableFriendsParams;
import com.worldventures.dreamtrips.api.friends.model.ImmutableMutualFriendsParams;
import com.worldventures.dreamtrips.api.friends.model.ImmutableSearchParams;
import com.worldventures.dreamtrips.api.friends.model.MutualFriendsParams;
import com.worldventures.dreamtrips.api.friends.model.SearchParams;
import com.worldventures.dreamtrips.api.http.executor.ActionExecutor;
import com.worldventures.dreamtrips.api.http.executor.AuthorizedActionExecutor;
import com.worldventures.dreamtrips.api.http.executor.SafeExecutor;
import com.worldventures.dreamtrips.api.profile.GetPublicUserProfileHttpAction;
import com.worldventures.dreamtrips.api.profile.model.PublicUserProfile;
import com.worldventures.dreamtrips.api.session.model.ImmutableMutualFriends;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;
import com.worldventures.dreamtrips.api.tests.util.ServerUtil;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;

import ie.corballis.fixtures.annotation.Fixture;
import ru.yandex.qatools.allure.annotations.Features;

import static com.worldventures.dreamtrips.api.friends.model.FriendRequestResponse.Status.CONFIRM;
import static com.worldventures.dreamtrips.api.friends.model.FriendRequestResponse.Status.REJECT;
import static java.util.Arrays.asList;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Features("Friends")
@Test(priority = 1)
public class FriendsTest extends BaseTestWithSession {

    @Fixture("get_friends_params")
    FriendsParams friendsParams;
    @Fixture("mutual_friends_params")
    MutualFriendsParams mutualfriendsParams;
    @Fixture("search_friends_params")
    SearchParams searchParams;
    @Fixture("user_with_no_rds")
    UserCredential someUser1Credentials;
    @Fixture("user_zh_hk")
    UserCredential someUser2Credentials;

    volatile AuthorizedActionExecutor someUser1;
    volatile AuthorizedActionExecutor someUser2;

    volatile List<Circle> circles;
    volatile List<FriendProfile> friends;
    volatile List<FriendCandidate> searchedUsers;
    volatile int newFriend;
    volatile int friendRequestCandidate;

    ///////////////////////////////////////////////////////////////////////////
    // Preparations
    ///////////////////////////////////////////////////////////////////////////

    @BeforeClass
    void getAccountCircles() {
        circles = execute(new GetCirclesHttpAction()).response();
        assertThat(circles).isNotEmpty();
    }

    @BeforeClass
    void prepareAnotherUsers() {
        someUser1 = as(someUser1Credentials);
        someUser2 = as(someUser2Credentials);
    }

    @BeforeClass(dependsOnMethods = "prepareAnotherUsers")
    void cleanupFriendRequestsBefore() {
        resetFriends();
        ServerUtil.waitForServerLag();
    }


    @AfterClass
    void cleanupFriendRequestsAfter() {
        SafeExecutor safeExecutor = SafeExecutor.from(this);
        safeExecutor.execute(new HideFriendRequestHttpAction(friendCandidate()));
        safeExecutor.execute(new RemoveFromFriendsHttpAction(friendCandidate()));
        //
        resetFriends();
        ServerUtil.waitForServerLag();
    }

    private void resetFriends() {
        ActionExecutor safeSession = SafeExecutor.from(this);
        ActionExecutor safeUser1 = SafeExecutor.from(someUser1);
        ActionExecutor safeUser2 = SafeExecutor.from(someUser2);

        safeSession.execute(new HideFriendRequestHttpAction(someUser1.getSession().user().id()));
        safeSession.execute(new HideFriendRequestHttpAction(someUser2.getSession().user().id()));
        safeSession.execute(new RemoveFromFriendsHttpAction(someUser1.getSession().user().id()));
        safeSession.execute(new RemoveFromFriendsHttpAction(someUser2.getSession().user().id()));
        //
        safeUser1.execute(new HideFriendRequestHttpAction(session().user().id()));
        safeUser1.execute(new RemoveFromFriendsHttpAction(session().user().id()));
        //
        safeUser1.execute(new HideFriendRequestHttpAction(someUser2.getSession().user().id()));
        safeUser1.execute(new RemoveFromFriendsHttpAction(someUser2.getSession().user().id()));
        //
        safeUser2.execute(new HideFriendRequestHttpAction(session().user().id()));
        safeUser2.execute(new RemoveFromFriendsHttpAction(session().user().id()));
        //
        safeUser2.execute(new HideFriendRequestHttpAction(someUser1.getSession().user().id()));
        safeUser2.execute(new RemoveFromFriendsHttpAction(someUser1.getSession().user().id()));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Tests
    ///////////////////////////////////////////////////////////////////////////

    @Test
    void testGetFriends() {
        friends = execute(new GetFriendsHttpAction(friendsParams)).response();
        assertThat(friends).isNotEmpty();
    }

    @Test(dependsOnMethods = {
            "testSearchFriends", "testRemoveFromFriends",
            "testDeclineFriendRequest", "testCancelFriendRequest"}
    )
    void testGetMutualFriends() {
        // create mutual friend
        AuthorizedActionExecutor mutualFriend = someUser2;
        int mutualFriendId = mutualFriend.getSession().user().id();
        String mutualFriendCircleId = mutualFriend.execute(new GetCirclesHttpAction()).response().get(0).id();
        // create friend requests
        sendFriendRequestAs(someUser1, mutualFriendId);
        sendFriendRequestAs(provideActionExecutor(), mutualFriendId);
        // confirm requests
        mutualFriend.execute(new AnswerFriendRequestsHttpAction(asList(
                FriendRequestParams.confirm(someUser1.getSession().user().id(), mutualFriendCircleId),
                FriendRequestParams.confirm(session().user().id(), mutualFriendCircleId)
        )));
        // check we have mutuals
        MutualFriendsParams paramsPageOne = ImmutableMutualFriendsParams.builder().from(mutualfriendsParams)
                .userId(mutualFriendId).build();
        List<FriendCandidate> mutualsPageOne = execute(new GetMutualFriendsHttpAction(paramsPageOne)).response();
        assertThat(mutualsPageOne).isNotEmpty();
        FriendCandidate mutualFriendFromPageOne = mutualsPageOne.get(0);
        // check that we don't have the same mutual on page two
        MutualFriendsParams paramsPageTwo = ImmutableMutualFriendsParams.builder().from(mutualfriendsParams)
                .page(2).build();
        List<FriendCandidate> mutualsPageTwo = execute(new GetMutualFriendsHttpAction(paramsPageTwo)).response();
        assertThat(mutualsPageTwo).doesNotContain(mutualFriendFromPageOne);
    }

    @Test
    void testSearchFriends() {
        searchedUsers = execute(new SearchFriendsHttpAction(searchParams)).response();
        assertThat(searchedUsers).isNotEmpty();
    }

    @Test(dependsOnMethods = "testRemoveFromFriends")
    void testSearchFriendsByUsername() {
        // if user is not your friend than you can find him by his username
        PublicUserProfile newFriendProfile = execute(new GetPublicUserProfileHttpAction(newFriend)).response();

        ImmutableSearchParams params = ImmutableSearchParams.builder().from(searchParams)
                .query(newFriendProfile.username()).build();

        List<FriendCandidate> users = execute(new SearchFriendsHttpAction(params)).response();
        assertThat(users)
                .isNotEmpty()
                .extracting(FriendCandidate::username)
                .containsOnly(params.query());
    }

    @Test(dependsOnMethods = "testSendFriendRequest")
    void testGetFriendRequests() {
        List<FriendCandidate> requests = execute(new GetFriendRequestsHttpAction()).response();
        assertThat(requests).isNotEmpty();
    }

    @Test
    void testAcceptFriendRequest() {
        int requesterId = sendFriendRequestAs(someUser1, session().user().id());
        List<FriendRequestResponse> response = execute(
                new AnswerFriendRequestsHttpAction(FriendRequestParams.confirm(requesterId, firstCircle()))
        ).response();
        //
        assertThat(response)
                .isNotEmpty()
                .extracting(FriendRequestResponse::status).doesNotContain(REJECT);
        newFriend = response.get(0).userId();
        assertThat(newFriend).isEqualTo(requesterId);
    }

    @Test(dependsOnMethods = "testCancelFriendRequest")
    void testDeclineFriendRequest() {
        int requesterId = sendFriendRequestAs(someUser2, session().user().id());
        ServerUtil.waitForServerLag();
        List<FriendRequestResponse> response = execute(
                new AnswerFriendRequestsHttpAction(FriendRequestParams.reject(requesterId))
        ).response();
        //
        assertThat(response)
                .isNotEmpty()
                .extracting(FriendRequestResponse::status).doesNotContain(CONFIRM);
    }

    @Test(dependsOnMethods = {"testGetFriends", "testSearchFriends"})
    void testSendFriendRequest() {
        friendRequestCandidate = friendCandidate();
        SendFriendRequestHttpAction action = execute(new SendFriendRequestHttpAction(friendRequestCandidate, firstCircle()));
        assertThat(action.statusCode()).isEqualTo(204);
    }

    @Test(dependsOnMethods = "testSendFriendRequest")
    void testCancelFriendRequest() {
        HideFriendRequestHttpAction action = execute(
                new HideFriendRequestHttpAction(friendRequestCandidate)
        );
        assertThat(action.statusCode()).isEqualTo(204);
    }

    @Test(dependsOnMethods = "testAcceptFriendRequest")
    void testRemoveFromFriends() {
        RemoveFromFriendsHttpAction action = execute(new RemoveFromFriendsHttpAction(newFriend));
        assertThat(action.statusCode()).isEqualTo(204);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Sugar
    ///////////////////////////////////////////////////////////////////////////

    private int friendCandidate() {
        List<Integer> friends = this.friends.stream()
                .map(FriendProfile::id)
                .collect(Collectors.toList());
        friends.addAll(java.util.Arrays.asList(
                someUser1.getSession().user().id(),
                someUser2.getSession().user().id()
        ));
        return searchedUsers.stream().sequential()
                .map(FriendCandidate::id)
                .filter(id -> !friends.contains(id))
                .findFirst().get();
    }

    private String firstCircle() {
        return circles.get(0).id();
    }

    private int sendFriendRequestAs(AuthorizedActionExecutor someUser, int newFriendId) {
        ServerUtil.waitForServerLag();
        String someCircleId = someUser.execute(new GetCirclesHttpAction()).response().get(0).id();
        SendFriendRequestHttpAction action = someUser.execute(
                new SendFriendRequestHttpAction(newFriendId, someCircleId)
        );
        assertThat(action.statusCode()).isEqualTo(204);
        //
        return someUser.getSession().user().id();

    }

}
