package application.appCommands;

import persistency.ProjectSaver;
import application.history.HistoryManager;
import application.project.controllers.SaveAsProjectController;
import application.configuration.Configuration;
import application.UPMTApp;

import java.io.IOException;

public class SaveProjectAsCommand extends ApplicationCommand<Void> {

    public SaveProjectAsCommand(UPMTApp application) {
        super(application);
    }

    @Override
    public Void execute() {
        SaveAsProjectController controller = SaveAsProjectController.createSaveAsProjectController(upmtApp.getPrimaryStage(), upmtApp.getCurrentProject());
        if(controller.getState() == SaveAsProjectController.State.SUCCESS) {
            try {
                ProjectSaver.save(upmtApp.getCurrentProject(), controller.getSavePath());
                upmtApp.setLastSavedCommandId(HistoryManager.getCurrentCommandId());
                new ProjectSavingStatusChangedCommand(upmtApp).execute();
                Configuration.addToProjects(controller.getSavePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
