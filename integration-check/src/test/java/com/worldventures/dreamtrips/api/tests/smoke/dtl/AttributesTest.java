package com.worldventures.dreamtrips.api.tests.smoke.dtl;

import com.worldventures.dreamtrips.api.dtl.attributes.AttributesHttpAction;
import com.worldventures.dreamtrips.api.dtl.attributes.model.Attribute;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;

import org.fest.assertions.core.Condition;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import ru.yandex.qatools.allure.annotations.Features;

import static com.worldventures.dreamtrips.api.dtl.attributes.model.AttributeType.AMENITY;
import static com.worldventures.dreamtrips.api.dtl.attributes.model.AttributeType.CATEGORY;
import static org.fest.assertions.api.Assertions.assertThat;

@Features("DT Local")
public class AttributesTest extends BaseTestWithSession {

    @Test
    void testGetAllAttributes() {
        AttributesHttpAction action = execute(new AttributesHttpAction("51.50,0", 80.4d, null));

        assertThat(action.statusCode()).isEqualTo(200);
        assertThat(action.attributes()).isNotEmpty();
    }

    @Test
    void testGetCategories() {
        AttributesHttpAction action = execute(new AttributesHttpAction("51.50,0", 80.4d,
                new ArrayList<String>(Arrays.asList(CATEGORY.toString().toLowerCase(Locale.US)))));

        assertThat(action.statusCode()).isEqualTo(200);
        assertThat(action.attributes()).isNotEmpty()
                .have(new Condition<Attribute>() {
                    @Override
                    public boolean matches(Attribute value) {
                        return value.type() == CATEGORY;
                    }
                });
    }

    @Test
    void testGetAmenitiesCategories() {
        AttributesHttpAction action = execute(new AttributesHttpAction("51.50,0", 80.4d, new ArrayList<String>(
                Arrays.asList(AMENITY.toString().toLowerCase(Locale.US), CATEGORY.toString().toLowerCase(Locale.US)))));

        assertThat(action.statusCode()).isEqualTo(200);
        assertThat(action.attributes()).isNotEmpty()
                .have(new Condition<Attribute>() {
                    @Override
                    public boolean matches(Attribute value) {
                        return value.type() == CATEGORY || value.type() == AMENITY;
                    }
                });
    }
}
