package com.worldventures.dreamtrips.modules.dtl.api;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.modules.dtl.model.SuggestPlacePostData;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class SuggestPlaceCommand extends DtlRequest<Void> {

    private final SuggestPlacePostData postData;

    public SuggestPlaceCommand(SuggestPlacePostData postData) {
        super(Void.class);
        this.postData = postData;
    }

    @Override
    public Void loadDataFromNetwork() {
        if (postData.id == null) { // looks like we're suggesting new place from RepTools
            return getService().suggestPlace(postData);
        } else { // suggest dining from Dtl
            return getService().suggestDining(postData.id, postData);
        }
    }
}
