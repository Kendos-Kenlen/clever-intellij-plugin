package com.cleverCloud.cleverIdea.toolWindow;

import com.cleverCloud.cleverIdea.ProjectSettings;
import com.cleverCloud.cleverIdea.api.CcApi;
import com.cleverCloud.cleverIdea.api.CleverCloudApi;
import com.cleverCloud.cleverIdea.api.json.Application;
import com.cleverCloud.cleverIdea.utils.WebSocketCore;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class CcLogsToolWindowFactory implements ToolWindowFactory, Condition<Project> {

  @Override
  public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
    ContentManager contentManager = toolWindow.getContentManager();
    ProjectSettings projectSettings = ServiceManager.getService(project, ProjectSettings.class);
    ArrayList<Application> applications = projectSettings.applications;

    for (Application application : applications) {
      TextConsoleBuilder builder = TextConsoleBuilderFactory.getInstance().createBuilder(project);
      ConsoleView console = builder.getConsole();

      Content logs = contentManager.getFactory().createContent(console.getComponent(), application.name, false);
      contentManager.addContent(logs);

      writeLogs(project, application, console);
      String oldLogs = CcApi.getInstance(project).logRequest(application);

      if (oldLogs != null && !oldLogs.isEmpty()) {
        WebSocketCore.printSocket(console, oldLogs);
      }
      else if (oldLogs != null && oldLogs.isEmpty()) {
        WebSocketCore.printSocket(console, "No logs available.\n");
      }
    }
  }

  private void writeLogs(@NotNull Project project, @NotNull Application lastUsedApplication, @NotNull ConsoleView consoleView) {
    TimeZone tz = TimeZone.getTimeZone("UTC");
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:s.S'Z'");
    df.setTimeZone(tz);
    String timestamp = df.format(new Date());

    try {
      URI logUri = new URI(String.format(CleverCloudApi.LOGS_SOKCET_URL, lastUsedApplication.id, timestamp));
      WebSocketCore webSocketCore = new WebSocketCore(logUri, project, consoleView);
      webSocketCore.connect();
    }
    catch (@NotNull URISyntaxException | NoSuchAlgorithmException | KeyManagementException e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean value(@NotNull Project project) {
    ProjectSettings projectSettings = ServiceManager.getService(project, ProjectSettings.class);
    return !projectSettings.applications.isEmpty();
  }
}
