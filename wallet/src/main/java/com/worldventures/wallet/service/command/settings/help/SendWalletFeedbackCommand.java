package com.worldventures.wallet.service.command.settings.help;

import android.os.Build;

import com.worldventures.core.modules.infopages.model.FeedbackImageAttachment;
import com.worldventures.core.service.DeviceInfoProvider;
import com.worldventures.core.utils.AppVersionNameBuilder;
import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.feedback.model.BaseFeedback;
import com.worldventures.dreamtrips.api.feedback.model.Feedback;
import com.worldventures.dreamtrips.api.feedback.model.FeedbackAttachment;
import com.worldventures.dreamtrips.api.feedback.model.ImmutableMetadata;
import com.worldventures.dreamtrips.api.smart_card.feedback.model.ImmutableSmartCardMetadata;
import com.worldventures.dreamtrips.api.smart_card.feedback.model.SmartCardMetadata;
import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.wallet.domain.entity.SmartCard;
import com.worldventures.wallet.domain.entity.SmartCardDetails;
import com.worldventures.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.wallet.domain.storage.WalletStorage;
import com.worldventures.wallet.util.SCFirmwareUtils;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.smartcard.util.SmartCardSDK;
import io.techery.mappery.MapperyContext;
import rx.Observable;

public abstract class SendWalletFeedbackCommand<F extends BaseFeedback> extends Command<Void> implements InjectableAction {

   @Inject Janet janet;
   @Inject AppVersionNameBuilder appVersionNameBuilder;
   @Inject DeviceInfoProvider deviceInfoProvider;
   @Inject WalletStorage walletStorage;
   @Inject MapperyContext mappery;

   protected final String description;
   private final List<FeedbackImageAttachment> imageAttachments;

   SendWalletFeedbackCommand(String description, List<FeedbackImageAttachment> imageAttachments) {
      this.description = description;
      this.imageAttachments = imageAttachments;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      provideHttpCommand(provideFeedbackBody()).subscribe(action -> callback.onSuccess(null), callback::onFail);
   }

   abstract Observable<? extends AuthorizedHttpAction> provideHttpCommand(F feedback);

   abstract F provideFeedbackBody();

   List<FeedbackAttachment> provideAttachments() {
      return mappery.convert(imageAttachments, FeedbackAttachment.class);
   }

   SmartCardMetadata provideSmartCardMetadata() {
      SmartCard smartCard = walletStorage.getSmartCard();
      SmartCardFirmware firmware = walletStorage.getSmartCardFirmware();
      if (smartCard == null) {
         return null;
      }

      SmartCardDetails details = smartCard.getDetails();
      return ImmutableSmartCardMetadata.builder()
            .smartCardId(Integer.parseInt(smartCard.getSmartCardId()))
            .smartCardSerialNumber(details.getSerialNumber())
            .bleId(details.getBleAddress())
            .firmwareVersion(SCFirmwareUtils.smartCardFirmwareVersion(firmware))
            .sdkVersion(SmartCardSDK.getSDKVersion())
            .build();
   }

   BaseFeedback.Metadata provideMetadata() {
      String osVersion = String.format(Locale.US, "android-%d", Build.VERSION.SDK_INT);
      String appVersion = appVersionNameBuilder.getSemanticVersionName();
      String deviceModel = String.format("%s:%s", Build.MANUFACTURER, Build.MODEL);
      Feedback.DeviceType deviceType = deviceInfoProvider.isTablet()
            ? Feedback.DeviceType.TABLET : Feedback.DeviceType.PHONE;

      return ImmutableMetadata.builder()
            .appVersion(appVersion)
            .deviceModel(deviceModel)
            .osVersion(osVersion)
            .deviceType(deviceType)
            .build();
   }
}
