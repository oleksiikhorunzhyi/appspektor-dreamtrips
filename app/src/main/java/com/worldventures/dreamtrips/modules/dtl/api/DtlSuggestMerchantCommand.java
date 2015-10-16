package com.worldventures.dreamtrips.modules.dtl.api;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.modules.dtl.model.ContactTime;
import com.worldventures.dreamtrips.modules.dtl.model.RateContainer;
import com.worldventures.dreamtrips.modules.dtl.model.SuggestMerchantPostData;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class DtlSuggestMerchantCommand extends DtlRequest<Void> {

    private int id;
    private String contactName;
    private String phone;
    private ContactTime contactTime;
    private RateContainer rateContainer;
    private String description;

    public DtlSuggestMerchantCommand(int id, String contactName, String phone,
                                     ContactTime contactTime, RateContainer rateContainer, String description) {
        super(Void.class);
        this.id = id;
        this.contactName = contactName;
        this.phone = phone;
        this.contactTime = contactTime;
        this.rateContainer = rateContainer;
        this.description = description;
    }

    @Override
    public Void loadDataFromNetwork() {
        return getService().suggestDining(id,
                new SuggestMerchantPostData(contactName, phone, contactTime, rateContainer, description));
    }
}
