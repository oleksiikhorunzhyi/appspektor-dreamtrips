package com.worldventures.dreamtrips.api.tests.smoke.smart_card;

import com.worldventures.dreamtrips.api.smart_card.nxt.CreateNxtSessionHttpAction;
import com.worldventures.dreamtrips.api.smart_card.nxt.model.NxtSession;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;

import org.testng.annotations.Test;

import ru.yandex.qatools.allure.annotations.Features;

import static org.assertj.core.api.Assertions.assertThat;

@Features("SmartCard")
public class CreateNxtSessionTest extends BaseTestWithSession {

    @Test
    void testCreateNxtSession() {
        CreateNxtSessionHttpAction action = execute(new CreateNxtSessionHttpAction());
        assertThat(action.statusCode()).isEqualTo(200);
        NxtSession response = action.response();
        assertThat(response).isNotNull();
        assertThat(response.token()).isNotNull();
        assertThat(response.token()).isNotEmpty();
    }
}
