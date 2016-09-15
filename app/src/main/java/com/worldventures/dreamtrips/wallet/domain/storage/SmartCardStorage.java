package com.worldventures.dreamtrips.wallet.domain.storage;


import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.MultipleActionStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.command.ActivateSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.ConnectSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.CreateAndConnectToCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetAutoClearSmartCardDelayCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetDisableDefaultCardDelayCommand;
import com.worldventures.dreamtrips.wallet.service.command.UpdateSmartCardConnectionStatus;
import com.worldventures.dreamtrips.wallet.service.command.FetchSmartCardLockState;
import com.worldventures.dreamtrips.wallet.service.command.SetLockStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetStealthModeCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetupUserDataCommand;

import java.util.Arrays;
import java.util.List;

public class SmartCardStorage implements MultipleActionStorage<SmartCard> {

   public static final String CARD_ID_PARAM = "card_id";

   private final SnappyRepository snappyRepository;

   public SmartCardStorage(SnappyRepository snappyRepository) {
      this.snappyRepository = snappyRepository;
   }

   @Override
   public void save(@Nullable CacheBundle params, SmartCard data) {
      snappyRepository.saveSmartCard(data);
   }

   @Override
   public SmartCard get(@Nullable CacheBundle bundle) {
      return snappyRepository.getSmartCard(obtainCardId(bundle));
   }

   private String obtainCardId(@Nullable CacheBundle bundle) {
      if (bundle == null || !bundle.contains(CARD_ID_PARAM))
         throw new IllegalArgumentException("you should provide CARD_ID_PARAM throw bundle");

      return bundle.get(CARD_ID_PARAM);
   }

   @Override
   public List<Class<? extends CachedAction>> getActionClasses() {
      return Arrays.asList(
            ConnectSmartCardCommand.class,
            UpdateSmartCardConnectionStatus.class,
            ActivateSmartCardCommand.class,
            CreateAndConnectToCardCommand.class,
            FetchSmartCardLockState.class,
            SetStealthModeCommand.class,
            SetAutoClearSmartCardDelayCommand.class,
            SetDisableDefaultCardDelayCommand.class,
            SetLockStateCommand.class,
            SetupUserDataCommand.class);
   }
}
