package com.worldventures.dreamtrips.api.tests.smoke.smart_card;

import com.worldventures.dreamtrips.api.fixtures.FirmwareData;
import com.worldventures.dreamtrips.api.smart_card.firmware.GetFirmwareHttpAction;
import com.worldventures.dreamtrips.api.smart_card.firmware.model.FirmwareResponse;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;

import org.testng.annotations.Test;

import ie.corballis.fixtures.annotation.Fixture;
import ru.yandex.qatools.allure.annotations.Features;

import static org.assertj.core.api.Assertions.assertThat;


@Features("SmartCard")
public class FirmwareUpdateTest extends BaseTestWithSession {

    @Fixture("firmware_data_old")
    FirmwareData firmwareDataOld;

    @Fixture("firmware_data_new")
    FirmwareData firmwareDataNew;

    @Test
    void testFirmwareUpdateCheckAvailable() {
        FirmwareResponse response = execute(new GetFirmwareHttpAction(firmwareDataOld.firmware(), firmwareDataOld.sdk())).response();
        assertThat(response).isNotNull();
        assertThat(response.updateAvailable()).isTrue();
        assertThat(response.firmwareInfo()).isNotNull();
        assertThat(response.firmwareInfo().firmwareVersions()).isNotNull();
    }

    @Test
    void testFirmwareUpdateCheckNotAvailable() {
        FirmwareResponse response = execute(new GetFirmwareHttpAction(firmwareDataNew.firmware(), firmwareDataNew.sdk())).response();
        assertThat(response).isNotNull();
        assertThat(response.updateAvailable()).isFalse();
    }

}
