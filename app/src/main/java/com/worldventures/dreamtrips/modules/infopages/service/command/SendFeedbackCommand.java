package com.worldventures.dreamtrips.modules.infopages.service.command;

import android.content.Context;
import android.os.Build;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.feedback.SendFeedbackHttpAction;
import com.worldventures.dreamtrips.api.feedback.model.Feedback;
import com.worldventures.dreamtrips.api.feedback.model.FeedbackAttachment;
import com.worldventures.dreamtrips.api.feedback.model.ImmutableFeedback;
import com.worldventures.dreamtrips.api.feedback.model.ImmutableMetadata;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.utils.AppVersionNameBuilder;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackImageAttachment;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class SendFeedbackCommand extends CommandWithError implements InjectableAction {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;
   @Inject MapperyContext mappery;
   @Inject AppVersionNameBuilder appVersionNameBuilder;
   @Inject Context context;

   private int reasonId;
   private String description;
   private List<FeedbackImageAttachment> imageAttachments;

   public SendFeedbackCommand(int reasonId, String description, List<FeedbackImageAttachment> imageAttachments) {
      this.reasonId = reasonId;
      this.description = description;
      this.imageAttachments = imageAttachments;
   }

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      janet.createPipe(SendFeedbackHttpAction.class)
            .createObservableResult(new SendFeedbackHttpAction(provideFeedbackBody()))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Feedback provideFeedbackBody() {
      ImmutableFeedback.Builder builder = ImmutableFeedback.builder()
            .reasonId(reasonId)
            .text(description)
            .metadata(provideMetadata());

      builder.attachments(mappery.convert(imageAttachments, FeedbackAttachment.class));

      return builder.build();
   }

   private Feedback.Metadata provideMetadata() {
      String osVersion = String.format("android-%d", Build.VERSION.SDK_INT);
      String appVersion = appVersionNameBuilder.getSemanticVersionName();
      String deviceModel = String.format("%s:%s", Build.MANUFACTURER, Build.MODEL);
      Feedback.DeviceType deviceType = ViewUtils.isTablet(context) ? Feedback.DeviceType.TABLET : Feedback.DeviceType.PHONE;
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
