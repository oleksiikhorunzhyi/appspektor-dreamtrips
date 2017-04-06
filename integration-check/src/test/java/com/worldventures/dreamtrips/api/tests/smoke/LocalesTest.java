package com.worldventures.dreamtrips.api.tests.smoke;

import com.worldventures.dreamtrips.api.locales.GetLocalesHttpAction;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;

import org.testng.annotations.Test;

import ru.yandex.qatools.allure.annotations.Features;

import static org.assertj.core.api.Assertions.assertThat;

@Features("Locale")
public class LocalesTest extends BaseTestWithSession {

    @Test
    public void testGetLocales() {
        GetLocalesHttpAction action = execute(new GetLocalesHttpAction());
        assertThat(action.response())
                .isNotNull()
                .isNotEmpty()
                .extracting("localeName")
                .doesNotHaveDuplicates()
                .doesNotContain("");
    }
}
