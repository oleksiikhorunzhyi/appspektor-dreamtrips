package com.worldventures.dreamtrips.modules.dtl.action;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.api.action.CallableCommandAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DtlUpdateAmenitiesAction extends CallableCommandAction<List<DtlMerchantAttribute>> {


    public DtlUpdateAmenitiesAction(SnappyRepository db, List<DtlMerchant> dtlMerchants) {
        super(() -> {
            Set<DtlMerchantAttribute> amenitiesSet = new HashSet<>();
            Queryable.from(dtlMerchants).forEachR(dtlMerchant -> {
                        if (dtlMerchant.getAmenities() != null)
                            amenitiesSet.addAll(dtlMerchant.getAmenities());
                    }
            );
            List<DtlMerchantAttribute> result = Queryable.from(amenitiesSet)
                    .sort(DtlMerchantAttribute.NAME_ALPHABETIC_COMPARATOR)
                    .toList();
            db.saveAmenities(result);
            return result;
        });
    }

}
