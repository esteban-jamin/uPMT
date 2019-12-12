/*****************************************************************************
 * RootLayoutController.java
 *****************************************************************************
 * Copyright 锟 2017 uPMT
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/


package Components.RootLayout.Controllers;

import application.History.HistoryManager;
import application.Project.Models.Project;
import application.Commands.ApplicationCommandFactory;
import application.Configuration.Configuration;
import Components.MainView.Controller.MainViewController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.*;
import utils.OS;

import java.awt.im.InputContext;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class RootLayoutController implements Initializable {

	public @FXML BorderPane rootLayout;
	public @FXML Menu menu;

	public @FXML Menu openRecentProject;
	public @FXML MenuItem saveProject;
	public @FXML MenuItem saveProjectAs;
	public @FXML MenuItem exportProject;
	public @FXML MenuItem newInterview;

	public @FXML MenuItem undo;
	public @FXML MenuItem redo;

	public @FXML MenuItem userGuide;

	private ApplicationCommandFactory appCommandFactory;


	public static BorderPane createRootLayout(RootLayoutController controller) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(controller.getClass().getResource("/views/RootLayout/RootLayout.fxml"));
			loader.setResources(Configuration.langBundle);
			loader.setController(controller);
			return loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public RootLayoutController(ApplicationCommandFactory appCommandFactory) {
		this.appCommandFactory = appCommandFactory;
	}

	public void setProject(Project project) {
		Platform.runLater(() -> {
			bindUndoRedoButtons(false);
			MainViewController controller = new MainViewController(project);
			rootLayout.setCenter(MainViewController.createMainView(controller));
			appCommandFactory.changeApplicationTitle("uPMT - "+ project.getName()).execute();
			setProjectRelatedControlsDisable(false);
			bindUndoRedoButtons(true);
		});

	}

	@FXML
	public void newProject() {
		appCommandFactory.newProject().execute();
	}

	@FXML
	public void openProject(){
		appCommandFactory.openProject().execute();
	}

	@FXML
	public void saveProject() {
		appCommandFactory.saveProject().execute();
	}
	
	@FXML
	public void saveProjectAs() {
		appCommandFactory.saveProjectAs().execute();
	}
	
	@FXML
	public void exportProject(){
		//TODO
		System.out.println("Exporting project functionnality not availaible yet !");
	}
	
	@FXML
	public void undo(){
		HistoryManager.goBack();
	}
	
	@FXML
	public void redo(){
		HistoryManager.goForward();
	}
	
	@FXML
	public void aboutUs(){
		//TODO
		/*Stage helpWindow = new Stage(StageStyle.UTILITY);
		helpWindow.setTitle(main._langBundle.getString("about_us"));
		helpWindow.setResizable(false);
		//helpWindow.setAlwaysOnTop(true);
		helpWindow.initModality(Modality.APPLICATION_MODAL);
		//helpWindow.setWidth(610);  
		//helpWindow.setHeight(350);
 
		try {
			FXMLLoader loader = new FXMLLoader();
			
            loader.setLocation(getClass().getResource("/view/HelpView.fxml"));
            loader.setController(new AboutController(main, helpWindow));
            
//          loader.setController(new HelpController(main, helpWindow, this.document));
            loader.setResources(main._langBundle);
            AnchorPane layout = (AnchorPane) loader.load();
            Scene sc = new Scene(layout);
			helpWindow.setScene(sc);
			helpWindow.showAndWait();
			
		} catch (IOException e) {
			// TODO Exit Program
			e.printStackTrace();
		}*/
	}
	// ----------------------------modifier: open a document

	@FXML
	public void openALink() {
		try {
			java.awt.Desktop.getDesktop().browse(new URI("https://github.com/coco35700/uPMT/wiki"));
		} catch (IOException | URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	public void newInterview() {
		//TODO -- maybe move this control somewhere else ?
	}

	private void saveRequest(WindowEvent event) throws IOException{
		//TODO alert window on closing !
		/*if(!main.isNeedToBeSaved()) {
			try {
				event.consume();
			} catch (NullPointerException e) {
			//System.out.println("no event, exit normally");
			}
			Platform.exit();
	        System.exit(0);
		} else {
			Alert alert = new Alert(AlertType.CONFIRMATION);
	    	alert.setTitle(main._langBundle.getString("quit"));
	    	alert.setHeaderText(main._langBundle.getString("quit_alarm"));
	    	ButtonType buttonTypeOne = new ButtonType(main._langBundle.getString("ok"));
	    	ButtonType buttonTypeTwo = new ButtonType(main._langBundle.getString("no"));
	    	ButtonType buttonTypeCancel = new ButtonType(main._langBundle.getString("cancel"));
	    	
	    	alert.getButtonTypes().setAll(buttonTypeOne,buttonTypeTwo,buttonTypeCancel);
	
	    	Optional<ButtonType> result = alert.showAndWait();
	    	if (result.get() == buttonTypeOne){
	    		saveProject();
	    		alert.close();
	    		Platform.exit();
		        System.exit(0);
	    	} else if (result.get() == buttonTypeTwo) {
	    		Utils.deleteRecovery();
	    		alert.close();
	    		Platform.exit();
		        System.exit(0);
	    	} else {
	    		
	    		try {
	   	    	 	event.consume();
				} catch (NullPointerException e) {
				//System.out.println("no event, exit normally");
				}
	    	}
		}*/
	}
	
	@FXML
	public void statsCategory(){
		//TODO reimplement !
		/*Stage statsWindow = new Stage(StageStyle.UTILITY);
		statsWindow.setTitle(main._langBundle.getString("stats_category"));
		statsWindow.setResizable(false);
		statsWindow.initModality(Modality.APPLICATION_MODAL);
 
		try {
			FXMLLoader loader = new FXMLLoader();
			
            loader.setLocation(getClass().getResource("/view/StatsView.fxml"));
            loader.setController(new StatsCategoryController(main, statsWindow));
           
            loader.setResources(main._langBundle);
    		
            //////////
            AnchorPane layout = (AnchorPane) loader.load();
            Scene sc = new Scene(layout);
            statsWindow.setScene(sc);
			statsWindow.showAndWait();
			
		} catch (IOException e) {
			// TODO Exit Program
			e.printStackTrace();
		}*/
	}
	
	/**
	 * Launch window for moment comparison view
	 */
	public void momentsComparaison(){
		//TODO reimplement !
		/*Stage comparisonWindow = new Stage(StageStyle.UTILITY);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		comparisonWindow.setTitle(main._langBundle.getString("moments_comparaison"));
		comparisonWindow.setResizable(true);
		comparisonWindow.initModality(Modality.APPLICATION_MODAL);

		try {
			FXMLLoader loader = new FXMLLoader();
			
            loader.setLocation(getClass().getResource("/view/MomentComparaisonView.fxml"));
            loader.setController(new MomentComparaisonController(main, comparisonWindow));
           
            loader.setResources(main._langBundle);
    		
            //////////
            HBox layout = (HBox) loader.load();
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setPrefSize(screenSize.getWidth()-10, screenSize.getHeight()-150);
            System.out.println("width " + screenSize.getWidth());
            System.out.println("height " + screenSize.getHeight());
            scrollPane.setContent(layout);

            
            Scene sc = new Scene(scrollPane);
            comparisonWindow.setScene(sc);
            comparisonWindow.showAndWait();
			
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}

	@FXML
	public void close() {
		appCommandFactory.closeApplication().execute();
	} 

	@FXML
	public void openEnglishVersion(){
		appCommandFactory.changeLanguage(Locale.ENGLISH).execute();
	}

	@FXML
	public void openFrenchVersion(){
		appCommandFactory.changeLanguage(Locale.FRANCE).execute();
	}

	@FXML
	public void openSpanishVersion(){
		appCommandFactory.changeLanguage(new Locale("es")).execute();
	}
	@FXML
	public void openItalienVersion(){
		appCommandFactory.changeLanguage(Locale.ITALIAN).execute();
	}

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		final KeyCodeCombination keyCombREDO=new KeyCodeCombination(KeyCode.Y, KeyCombination.SHORTCUT_DOWN);
		final KeyCodeCombination keyCombSAVE=new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN);
		final KeyCodeCombination keyCombNEW=new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN);
		InputContext context = InputContext.getInstance(); 
		String loc = context.getLocale().toString();
		System.out.println(loc);  
		// javafx keyboard layout bug management 
		if(OS.current == OS.mac) {
			if (loc.equals("fr")){
				Locale.setDefault(Locale.FRANCE);
			    KeyCodeCombination keyCombUNDO=new KeyCodeCombination(KeyCode.W, KeyCombination.SHORTCUT_DOWN);
				undo.setAccelerator(keyCombUNDO);
				redo.setAccelerator(keyCombREDO);
				saveProject.setAccelerator(keyCombSAVE);
				newInterview.setAccelerator(keyCombNEW);
			} else {
				Locale.setDefault(Locale.US);
				KeyCodeCombination keyCombUNDO=new KeyCodeCombination(KeyCode.W, KeyCombination.SHORTCUT_DOWN);
				undo.setAccelerator(keyCombUNDO);
				redo.setAccelerator(keyCombREDO);
				saveProject.setAccelerator(keyCombSAVE);
				newInterview.setAccelerator(keyCombNEW);
			}
		}else {
		    KeyCodeCombination keyCombUNDO=new KeyCodeCombination(KeyCode.W, KeyCombination.SHORTCUT_DOWN);
			undo.setAccelerator(keyCombUNDO);
			redo.setAccelerator(keyCombREDO);
			saveProject.setAccelerator(keyCombSAVE);
			newInterview.setAccelerator(keyCombNEW);
		}

		setupRecentProjectUpdate();
		setProjectRelatedControlsDisable(true);
	}

	private void setupRecentProjectUpdate() {
		menu.setOnShowing(event -> {
			String[] recentsProjects = Configuration.getProjectsPath();
			openRecentProject.getItems().removeAll(openRecentProject.getItems());
			for(int i = 0; i < Math.min(recentsProjects.length, 5); i++){
				String text = recentsProjects[i];
				MenuItem item = new MenuItem(text);
				item.setOnAction(e -> { appCommandFactory.openRecentProject(text).execute(); });
				openRecentProject.getItems().add(item);
			}
		});
	}

	private void setProjectRelatedControlsDisable(boolean YoN) {
		saveProject.setDisable(YoN);
		saveProjectAs.setDisable(YoN);
		exportProject.setDisable(YoN);
		newInterview.setDisable(YoN);
		undo.setDisable(YoN);
		redo.setDisable(YoN);
	}

	private void bindUndoRedoButtons(boolean YoN) {
		if(YoN){
			undo.disableProperty().bind(HistoryManager.canGoBackProperty().not());
			redo.disableProperty().bind(HistoryManager.canGoForwardProperty().not());
		}
		else {
			undo.disableProperty().unbind();
			redo.disableProperty().unbind();
		}
	}
}
