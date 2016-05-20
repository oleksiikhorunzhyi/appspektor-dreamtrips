package com.worldventures.dreamtrips.modules.dtl.action;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import io.techery.janet.CommandActionBase;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DtlUpdateAmenitiesAction extends CommandActionBase<List<DtlMerchantAttribute>> implements InjectableAction {

    @Inject
    SnappyRepository db;

    private final List<DtlMerchant> dtlMerchants;

    public DtlUpdateAmenitiesAction(List<DtlMerchant> dtlMerchants) {
        this.dtlMerchants = dtlMerchants;
    }

    @Override
    protected void run(CommandCallback<List<DtlMerchantAttribute>> callback) throws Throwable {
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
        callback.onSuccess(result);
    }
}
