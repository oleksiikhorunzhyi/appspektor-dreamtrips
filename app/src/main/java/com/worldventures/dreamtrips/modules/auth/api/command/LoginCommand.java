package com.worldventures.dreamtrips.modules.auth.api.command;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.AuthRetryPolicy;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.api.action.LoginAction;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.auth.service.AuthInteractor;
import com.worldventures.dreamtrips.modules.auth.util.SessionUtil;
import com.worldventures.dreamtrips.modules.common.model.Session;
import com.worldventures.dreamtrips.modules.settings.model.Setting;
import com.worldventures.dreamtrips.modules.settings.util.SettingsFactory;
import com.worldventures.dreamtrips.modules.settings.util.SettingsManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.schedulers.Schedulers;

@CommandAction
public class LoginCommand extends CommandWithError<UserSession> implements InjectableAction {

   @Inject Janet janet;
   @Inject SessionHolder<UserSession> appSessionHolder;
   @Inject AuthInteractor authInteractor;
   @Inject SnappyRepository db;

   private String userName;
   private String userPassword;

   public LoginCommand() {
   }

   public LoginCommand(String userName, String userPassword) {
      this.userName = userName;
      this.userPassword = userPassword;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_login;
   }

   @Override
   protected void run(CommandCallback<UserSession> callback) throws Throwable {
      if (userName == null || userPassword == null) {
         if (AuthRetryPolicy.isCredentialExist(appSessionHolder)) {
            UserSession userSession = appSessionHolder.get().get();
            this.userName = userSession.getUsername();
            this.userPassword = userSession.getUserPassword();
         } else {
            throw new Exception("You have to set username and password");
         }
      }

      janet.createPipe(LoginAction.class, Schedulers.io())
            .createObservableResult(new LoginAction(userName, userPassword))
            .map(LoginAction::getLoginResponse)
            .doOnNext(this::saveSettings)
            .map(session -> SessionUtil.createUserSession(session, userName, userPassword))
            .doOnNext(this::saveSession)
            .doOnNext(this::notifyUserUpdated)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private void saveSession(UserSession userSession) {
      if (userSession.getUser() != null & userSession.getApiToken() != null) {
         appSessionHolder.put(userSession);
      }
   }

   private void saveSettings(Session session) {
      List<Setting> settings = session.getSettings();
      if (settings == null) settings = new ArrayList<>();
      db.saveSettings(SettingsManager.merge(settings, SettingsFactory.createSettings()), true);
   }

   private void notifyUserUpdated(UserSession userSession) {
      authInteractor.updateUserPipe().send(new UpdateUserCommand(userSession.getUser()));
   }
}