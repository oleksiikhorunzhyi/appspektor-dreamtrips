package com.worldventures.core.modules.auth.api.command;

public interface LogoutAction {

   String PRIORITY_HIGH = "PRIORITY_HIGH";

   void call() throws Exception;
}
