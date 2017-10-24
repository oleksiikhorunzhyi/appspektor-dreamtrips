package com.worldventures.dreamtrips.wallet.service.command.settings.help;

import android.os.Build;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.feedback.model.BaseFeedback;
import com.worldventures.dreamtrips.api.feedback.model.Feedback;
import com.worldventures.dreamtrips.api.feedback.model.FeedbackAttachment;
import com.worldventures.dreamtrips.api.feedback.model.ImmutableMetadata;
import com.worldventures.dreamtrips.api.smart_card.feedback.model.ImmutableSmartCardMetadata;
import com.worldventures.dreamtrips.api.smart_card.feedback.model.SmartCardMetadata;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.AppVersionNameBuilder;
import com.worldventures.dreamtrips.modules.common.delegate.system.DeviceInfoProvider;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackImageAttachment;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardDetails;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.util.SCFirmwareUtils;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.smartcard.util.SmartCardSDK;
import io.techery.mappery.MapperyContext;
import rx.Observable;

public abstract class SendWalletFeedbackCommand<F extends BaseFeedback> extends CommandWithError<Void> implements InjectableAction {

   @Inject Janet janet;
   @Inject AppVersionNameBuilder appVersionNameBuilder;
   @Inject DeviceInfoProvider deviceInfoProvider;
   @Inject SnappyRepository snappyRepository;
   @Inject MapperyContext mappery;

   protected final String description;
   protected final List<FeedbackImageAttachment> imageAttachments;

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

   protected List<FeedbackAttachment> provideAttachments() {
      return mappery.convert(imageAttachments, FeedbackAttachment.class);
   }

   protected SmartCardMetadata provideSmartCardMetadata() {
      SmartCard smartCard = snappyRepository.getSmartCard();
      SmartCardDetails details = snappyRepository.getSmartCardDetails();
      SmartCardFirmware firmware = snappyRepository.getSmartCardFirmware();
      if (smartCard == null || details == null) return null;

      return ImmutableSmartCardMetadata.builder()
            .smartCardId((int) details.smartCardId())
            .smartCardSerialNumber(details.serialNumber())
            .bleId(details.bleAddress())
            .firmwareVersion(SCFirmwareUtils.smartCardFirmwareVersion(firmware))
            .sdkVersion(SmartCardSDK.getSDKVersion())
            .build();
   }

   protected BaseFeedback.Metadata provideMetadata() {
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

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_send_feedback;
   }
}
