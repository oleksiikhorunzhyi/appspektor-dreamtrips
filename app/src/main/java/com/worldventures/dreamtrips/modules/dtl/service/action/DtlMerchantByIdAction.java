package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.dreamtrips.core.janet.JanetPlainActionComposer;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.service.DtlMerchantService;

import javax.inject.Inject;

import io.techery.janet.CommandActionBase;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class DtlMerchantByIdAction extends CommandActionBase<DtlMerchant> implements InjectableAction {

    @Inject
    DtlMerchantService merchantService;

    private final String merchantId;

    public DtlMerchantByIdAction(String merchantId) {
        this.merchantId = merchantId;
    }

    @Override
    protected void run(CommandCallback<DtlMerchant> callback) throws Throwable {
        merchantService.merchantsActionPipe().observeWithReplay()
                .first()
                .compose(JanetPlainActionComposer.instance())
                .map(DtlMerchantsAction::getResult)
                .flatMap(Observable::from)
                .filter(merchant -> merchant.getId().equals(merchantId))
                .subscribe(callback::onSuccess, callback::onFail);
    }

    public String getMerchantId() {
        return merchantId;
    }
}
