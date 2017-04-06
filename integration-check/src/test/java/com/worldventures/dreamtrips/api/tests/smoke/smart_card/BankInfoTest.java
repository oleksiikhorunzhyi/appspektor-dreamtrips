package com.worldventures.dreamtrips.api.tests.smoke.smart_card;

import com.worldventures.dreamtrips.api.smart_card.bank_info.GetBankInfoHttpAction;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;

import org.testng.annotations.Test;

import ru.yandex.qatools.allure.annotations.Features;

import static org.assertj.core.api.Assertions.assertThat;

@Features("SmartCard")
public class BankInfoTest extends BaseTestWithSession {

    @Test
    void testBankInfo() {
        GetBankInfoHttpAction action = new GetBankInfoHttpAction(431940);

        execute(action);

        assertThat(action.response()).isNotNull();
        assertThat(action.statusCode()).isEqualTo(200);
    }

}
