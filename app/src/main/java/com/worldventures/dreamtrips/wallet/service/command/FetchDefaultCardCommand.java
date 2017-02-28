package com.worldventures.dreamtrips.wallet.service.command;

import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.Card;
import com.worldventures.dreamtrips.wallet.domain.storage.disk.PersistentCardListStorage;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class FetchDefaultCardCommand extends Command<BankCard> implements InjectableAction {

   @Inject SnappyRepository snappyRepository;
   @Inject PersistentCardListStorage cardListStorage;

   @Override
   protected void run(CommandCallback<BankCard> callback) throws Throwable {
      List<Card> cards = cardListStorage.readWalletCardsList();
      String defaultId = snappyRepository.readWalletDefaultCardId();
      Card defaultCard = Queryable.from(cards).firstOrDefault(c -> TextUtils.equals(c.id(), defaultId));
      callback.onSuccess((BankCard) defaultCard);
   }
}
