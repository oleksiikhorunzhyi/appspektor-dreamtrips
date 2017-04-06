package com.worldventures.dreamtrips.api.tests.smoke;

import com.worldventures.dreamtrips.api.documents.GetDocumentsHttpAction;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;

import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Features("Documents")
public class DocumentsTest extends BaseTestWithSession {

    @Test
    void testGetListOfDocuments() {
        GetDocumentsHttpAction action = execute(new GetDocumentsHttpAction());
        assertThat(action.response()).isNotEmpty();
    }

}
