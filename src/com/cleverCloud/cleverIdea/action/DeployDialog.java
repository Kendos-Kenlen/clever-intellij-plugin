package com.cleverCloud.cleverIdea.action;

import com.cleverCloud.cleverIdea.api.json.Application;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class DeployDialog extends DialogWrapper {
  private JPanel contentPane;
  private JComboBox<Application> myComboBox;

  public DeployDialog(Project project, @NotNull List<Application> apps, Application lastApplication) {
    super(project);
    init();
    setTitle("Choose Application to Deploy");
    setComboBoxContent(apps);
    myComboBox.setSelectedItem(lastApplication);
  }

  @Nullable
  @Override
  protected JComponent createCenterPanel() {
    return contentPane;
  }

  private void setComboBoxContent(@NotNull List<Application> applications) {

    if (!applications.isEmpty()) {
      applications.forEach(myComboBox::addItem);
    }
  }

  @Nullable
  @Override
  public JComponent getPreferredFocusedComponent() {
    return myComboBox;
  }

  @NotNull
  public Application getSelectedItem() {
    return (Application)myComboBox.getSelectedItem();
  }
}
