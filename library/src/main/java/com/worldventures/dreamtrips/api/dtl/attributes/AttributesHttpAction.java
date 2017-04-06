package com.worldventures.dreamtrips.api.dtl.attributes;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.dtl.attributes.model.Attribute;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "api/dtl/v2/attributes")
public class AttributesHttpAction extends AuthorizedHttpAction {

    final @Query("ll") String coordinates;
    final @Query("radius") Double radius;
    final @Query("attribute_type") List<String> attributeTypes;

    @Response List<Attribute> attributes;

    public AttributesHttpAction(String coordinates, Double radius, List<String> attributeTypes) {
        this.coordinates = coordinates;
        this.radius = radius;
        this.attributeTypes = attributeTypes;
    }

    public List<Attribute> attributes() {
        return attributes;
    }
}
