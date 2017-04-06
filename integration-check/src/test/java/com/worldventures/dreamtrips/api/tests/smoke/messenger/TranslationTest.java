package com.worldventures.dreamtrips.api.tests.smoke.messenger;

import com.worldventures.dreamtrips.api.messenger.TranslateTextHttpAction;
import com.worldventures.dreamtrips.api.messenger.model.request.TranslateTextBody;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;

import org.testng.annotations.Test;

import ie.corballis.fixtures.annotation.Fixture;
import ru.yandex.qatools.allure.annotations.Features;

import static org.assertj.core.api.Assertions.assertThat;

@Features("Translation")
public class TranslationTest extends BaseTestWithSession {

    @Fixture("translate_text_body")
    TranslateTextBody translateTextParams;

    @Fixture("translate_text_result")
    String translatedText;

    @Test
    void testTranslateMessageWithLanguageParameter() {
        TranslateTextHttpAction action = execute(new TranslateTextHttpAction(translateTextParams));

        assertThat(action.statusCode()).isEqualTo(200);
        assertThat(action.getTranslatedText())
                .isNotEmpty()
                .isEqualTo(translatedText);
    }

}
