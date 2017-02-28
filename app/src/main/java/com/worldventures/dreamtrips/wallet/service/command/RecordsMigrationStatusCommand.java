package com.worldventures.dreamtrips.wallet.service.command;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.storage.disk.CardListStorage;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

/**
 * Checks whether app has non-tokenized bank cards stored, thus migration
 * {@link TokenizeRecordsMigrationCommand} may be needed.
 */
@CommandAction
public class RecordsMigrationStatusCommand extends Command<Boolean> implements InjectableAction {

   @Inject CardListStorage oldCardListStorage;

   @Override
   protected void run(CommandCallback<Boolean> callback) throws Throwable {
      final List<BankCard> cardsToMigrate = Queryable.from(oldCardListStorage.readWalletCardsList())
            .cast(BankCard.class)
            .toList();
      callback.onSuccess(!cardsToMigrate.isEmpty());
   }

}