package com.worldventures.dreamtrips.modules.membership.presenter;

import android.text.TextUtils;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;
import com.worldventures.dreamtrips.modules.membership.model.Member;

import java.util.ArrayList;
import java.util.List;

public class EditTemplatePresenter extends Presenter<EditTemplatePresenter.View> {
    private InviteTemplate template;

    public EditTemplatePresenter(View view, InviteTemplate template) {
        super(view);
        this.template = template;
    }


    @Override
    public void resume() {
        super.resume();
        view.setFrom(template.getFrom());
        view.setSubject(template.getTitle());
        List<String> to = new ArrayList<>();
        for (Member member : template.getTo()) {
            to.add(member.getSubtitle());
        }
        view.setTo(TextUtils.join(", ", to));
        view.setWebViewContent(template.getContent());
    }

    public interface View extends Presenter.View {

        void setFrom(String from);

        void setSubject(String title);

        void setTo(String s);

        void setWebViewContent(String content);
    }
}
