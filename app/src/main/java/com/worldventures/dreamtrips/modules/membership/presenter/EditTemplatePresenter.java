package com.worldventures.dreamtrips.modules.membership.presenter;

import android.content.Intent;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.IntentUtils;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.membership.api.CreateFilledInvitationsTemplateQuery;
import com.worldventures.dreamtrips.modules.membership.api.InviteBody;
import com.worldventures.dreamtrips.modules.membership.api.SendInvitationsQuery;
import com.worldventures.dreamtrips.modules.membership.bundle.TemplateBundle;
import com.worldventures.dreamtrips.modules.membership.bundle.UrlBundle;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;
import com.worldventures.dreamtrips.modules.membership.model.Member;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class EditTemplatePresenter extends Presenter<EditTemplatePresenter.View> {

   private InviteTemplate template;
   private boolean preview = false;

   private String uploadedPhotoUrl;

   @Inject @ForApplication protected Injector injector;

   public EditTemplatePresenter(TemplateBundle templateBundle) {
      super();
      this.template = templateBundle.getInviteTemplate();
   }

   @Override
   public void onResume() {
      super.onResume();
      view.setFrom(template.getFrom());
      view.setSubject(template.getTitle());
      List<String> to = getMembersAddress();
      view.setTo(TextUtils.join(", ", to));
      view.setWebViewContent(template.getContent());
      if (!TextUtils.isEmpty(template.getVideo())) {
         view.hidePhotoUpload();
      }

   }

   public List<String> getMembersAddress() {
      List<String> to = new ArrayList<>();
      for (Member member : template.getTo()) {
         to.add(member.getSubtitle());
      }
      return to;
   }

   private Intent getShareIntent() {
      InviteTemplate.Type type = template.getType();
      List<String> membersAddress = getMembersAddress();
      String[] addresses = membersAddress.toArray(new String[membersAddress.size()]);
      Intent intent;
      if (type == InviteTemplate.Type.EMAIL) {
         intent = IntentUtils.newEmailIntent(getSubject(), getBody(), addresses);
      } else {
         intent = IntentUtils.newSmsIntent(context, getSmsBody(), addresses);
      }
      trackSharing();
      return intent;
   }

   private void trackSharing() {
      InviteTemplate.Type type = template.getType();
      if (type == InviteTemplate.Type.EMAIL) {
         TrackingHelper.inviteShareAction(TrackingHelper.ACTION_SEND_EMAIL, template.getId(), template.getTo().size());
      } else {
         TrackingHelper.inviteShareAction(TrackingHelper.ACTION_SEND_SMS, template.getId(), template.getTo().size());
      }

   }

   public void previewAction() {
      preview = true;
      updatePreview();
   }

   private void getFilledInvitationsTemplateFailed(SpiceException spiceException) {
      view.finishLoading();
      handleError(spiceException);
   }

   private void getFilledInvitationsTemplateSuccess(InviteTemplate inviteTemplate) {
      view.finishLoading();
      if (inviteTemplate != null) {
         view.setWebViewContent(inviteTemplate.getContent());
         template.setContent(inviteTemplate.getContent());
         template.setLink(inviteTemplate.getLink());
         if (preview) {
            preview = false;
            view.openPreviewTemplate(new UrlBundle(inviteTemplate.getLink()));
         }
      } else {
         handleError(new SpiceException(""));
      }
   }

   private void createInviteSuccess(InviteTemplate template) {
      getFilledInvitationsTemplateSuccess(template);

      view.openShare(getShareIntent());
      notifyServer();
   }

   private String getSubject() {
      return template.getTitle();
   }

   private String getBody() {
      return String.format(context.getString(R.string.invitation_text_template), getUsername(), getMessage(), template.getLink());
   }

   private String getSmsBody() {
      return template.getTitle() + " " + template.getLink();
   }

   private String getMessage() {
      return TextUtils.isEmpty(view.getMessage()) ? "" : "\n\n" + view.getMessage() + ".";
   }

   private String getUsername() {
      return getMembersAddress().size() > 1 ? "" : " " + template.getName();
   }

   private void notifyServer() {
      InviteBody body = new InviteBody();
      body.setContacts(getContactAddress());
      body.setTemplateId(template.getId());
      body.setType(template.getType());
      doRequest(new SendInvitationsQuery(body), jsonObject -> {});
   }

   private List<String> getContactAddress() {
      return Queryable.from(template.getTo()).map(Member::getSubtitle).toList();
   }

   private void updatePreview() {
      view.startLoading();
      doRequest(new CreateFilledInvitationsTemplateQuery(template.getId(), view.getMessage(), uploadedPhotoUrl), this::getFilledInvitationsTemplateSuccess, this::getFilledInvitationsTemplateFailed);
   }

   public void shareRequest() {
      doRequest(new CreateFilledInvitationsTemplateQuery(template.getId(), view.getMessage(), uploadedPhotoUrl), this::createInviteSuccess);
   }

   public interface View extends Presenter.View {

      void setFrom(String from);

      void setSubject(String title);

      void setTo(String s);

      void setWebViewContent(String content);

      String getMessage();

      void startLoading();

      void finishLoading();

      void hidePhotoUpload();

      void openPreviewTemplate(UrlBundle bundle);

      void openShare(Intent intent);
   }
}
