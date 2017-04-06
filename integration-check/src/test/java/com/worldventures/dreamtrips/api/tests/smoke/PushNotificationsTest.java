package com.worldventures.dreamtrips.api.tests.smoke;

import com.worldventures.dreamtrips.api.push_notifications.SubscribeToPushNotificationsHttpAction;
import com.worldventures.dreamtrips.api.push_notifications.UnsubscribeFromPushNotificationsHttpAction;
import com.worldventures.dreamtrips.api.push_notifications.model.ImmutablePushSubscriptionParams;
import com.worldventures.dreamtrips.api.push_notifications.model.PushSubscriptionParams;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;

import org.assertj.core.api.Assertions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import ie.corballis.fixtures.annotation.Fixture;
import ru.yandex.qatools.allure.annotations.Features;

@Features("Push Notifications")
public class PushNotificationsTest extends BaseTestWithSession {

    @Fixture("push_notification_subscription_params")
    PushSubscriptionParams params;

    @BeforeClass
    void prepareParams() {
        params = ImmutablePushSubscriptionParams.builder()
                .from(params)
                .token(params.token() + System.currentTimeMillis())
                .build();
    }

    @Test
    void testSubscribeToPushNotifications() {
        SubscribeToPushNotificationsHttpAction action = execute(new SubscribeToPushNotificationsHttpAction(params));
        Assertions.assertThat(action.statusCode()).isEqualTo(204);
    }

    @Test(dependsOnMethods = "testSubscribeToPushNotifications")
    void testUnsubscribeFromPushNotifications() {
        UnsubscribeFromPushNotificationsHttpAction action = execute(
                new UnsubscribeFromPushNotificationsHttpAction(params.token())
        );
        Assertions.assertThat(action.statusCode()).isEqualTo(204);
    }
}
