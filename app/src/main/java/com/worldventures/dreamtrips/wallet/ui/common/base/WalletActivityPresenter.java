package com.worldventures.dreamtrips.wallet.ui.common.base;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;

import java.util.List;

import javax.inject.Inject;

public class WalletActivityPresenter extends ActivityPresenter<ActivityPresenter.View> {

    @Inject SnappyRepository snappyRepository;

    public boolean hasSmartCard() {
        List<SmartCard> smartCards = snappyRepository.getSmartCards();
        for (SmartCard card : smartCards) {
            if (card.getStatus() == SmartCard.CardStatus.ACTIVE) {
                return true;
            }
        }
        return false;
    }
}
