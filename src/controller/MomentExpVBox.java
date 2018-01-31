/*****************************************************************************
 * MomentExpVBox.java
 *****************************************************************************
 * Copyright � 2017 uPMT
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

package controller;

import java.awt.ScrollPane;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import controller.command.RemoveMomentCommand;
import controller.command.RenameMomentCommand;
import controller.controller.AddPropertySchemeController;
import controller.controller.MomentAddTypeController;
import controller.controller.MomentColorController;
import controller.controller.MomentExtractController;
import controller.controller.MomentNameController;
import controller.controller.MomentRemoveTypeController;
import controller.controller.Observer;
import controller.controller.PropertyExtractController;
import controller.typeTreeView.TypeTreeView;
import controller.typeTreeView.TypeTreeViewControllerClass;
import controller.controller.Observable;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;
import model.Classe;
import model.MomentExperience;
import model.Propriete;
import model.SerializedMomentVBox;
import model.Serializer;
import model.Type;
import utils.MainViewTransformations;
import utils.UndoCollector;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Button;

public class MomentExpVBox extends VBox implements Initializable, Observer, Serializable{
	
	private MomentExperience moment;
	private BorderPane momentPane;
	private @FXML Label label;
	private @FXML FlowPane typeSpace;
	private GridPane sousMomentPane;
	private @FXML BorderPane borderPaneLabel;
	private @FXML BorderPane momentCardPane;
	private Main main;
	private @FXML ImageView hasExtractImage;
	private Tooltip extractTooltip;
	
	//Controllers
	private MomentNameController nameController;
	private MomentColorController colorController;
	private MomentExtractController extractController;
	private MomentAddTypeController addTypeController;
	private MomentRemoveTypeController momentRemoveTypeController;
	
	public static DataFormat df = new DataFormat("controller.MomentExpVBox");
	public static DataFormat realCol = new DataFormat("controller.MomentExpVBox.realCol");
	public static DataFormat rootCol = new DataFormat("controller.MomentExpVBox.rootCol");
	private static final long serialVersionUID = 1420672609912364060L;
	
	private MomentExpVBox momentParent=null;

	
	
	
	// Stack of redoable Classes
	private Deque<TypeClassRepresentationController> stack = new ArrayDeque<TypeClassRepresentationController>();

	public MomentExpVBox(MomentExperience mexp, Main main) {
		this.main = main;
		moment = mexp;
        this.setPrefWidth(USE_COMPUTED_SIZE);
        this.setMaxWidth(USE_COMPUTED_SIZE);
        this.setMinHeight(200);
        //System.out.println("HFJFDJEJER "+moment.getNom());
        loadMomentPane();
        extractTooltip = new Tooltip();
        BorderPane.setMargin(this.momentPane,(new Insets(10,10,10,10)));
        VBox.setMargin(this,(new Insets(10,10,10,10)));
        //implementation du tooltip
        hasExtractImage.setOnMouseEntered(new EventHandler<MouseEvent>() {
		    @Override
		    public void handle(MouseEvent event) {
		        javafx.geometry.Point2D p = hasExtractImage.localToScreen(hasExtractImage.getLayoutBounds().getMaxX(), hasExtractImage.getLayoutBounds().getMaxY()); 
		        extractTooltip.show(hasExtractImage, p.getX(), p.getY());
		    }
		});
		hasExtractImage.setOnMouseExited(new EventHandler<MouseEvent>() {
		    @Override
		    public void handle(MouseEvent event) {
		    	extractTooltip.hide();
		    }
		});
		extractTooltip.setOpacity(0);
		
		// creation of the name observer
		nameController = new MomentNameController(this.moment);
		nameController.addObserver(this);
		nameController.addObserver(main.getMainViewController());
		// creation creation of the color observer
		colorController = new MomentColorController(this.moment);
		colorController.addObserver(this);
		colorController.addObserver(main.getMainViewController());
		// creation creation of the extract observer
		extractController = new MomentExtractController(this.moment);
		extractController.addObserver(this);
		extractController.addObserver(main.getMainViewController());
		// creation creation of the adding class observer
		addTypeController = new MomentAddTypeController(this.moment);
		addTypeController.addObserver(this);
		addTypeController.addObserver(main.getMainViewController());
		// creation creation of the deleting class observer
		this.momentRemoveTypeController = new MomentRemoveTypeController(this.moment);
        this.momentRemoveTypeController.addObserver(this);
        addTypeController.addObserver(main.getMainViewController());
        
        
        borderPaneLabel.setOnDragDetected(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
            	Dragboard db = borderPaneLabel.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                // Store node ID in order to know what is dragged.
                content.putString("moveMoment");
                content.put(MomentExpVBox.rootCol, MomentExpVBox.this.getColOfRootMoment());
				content.put(MomentExpVBox.realCol, MomentExpVBox.this.getCol());
				try {
					content.put(MomentExpVBox.df, Serializer.serialize(MomentExpVBox.this.getMoment()));
				}
				catch(IOException e) {
					e.printStackTrace();
				}
                db.setContent(content);
                event.consume();
            }
        });
        
	}
	
	public MomentExpVBox(Main main){
		this(new MomentExperience("------",-1,-1), main);
	}
	

	private void loadMomentPane(){
		sousMomentPane = new GridPane();
        try {
        	FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/MomentExperience.fxml"));
            loader.setController(this);
			momentPane = (BorderPane) loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.setLabelChangeName(main,this);
		// adding the deletion of the moment by suppr / del
		this.setOnKeyPressed(new EventHandler<KeyEvent>()
	    {
	        @Override
	        public void handle(KeyEvent ke)
	        {
	            if ((ke.getCode().equals(KeyCode.DELETE) || ke.getCode().equals(KeyCode.BACK_SPACE)) && isFocused())
	            {
	                deleteMoment();
	            }
	        }
	    });
		
	}
	
	public void setCurrentProperty(Propriete n) {
		moment.setCurrentProperty(n);
	}
	
	public Propriete getCurrentProperty() {
		return moment.getCurrentProperty();
	}
	
	
	
	public void LoadMomentData(){
		label.setText(moment.getNom());
		if (this.moment.getCouleur() != null) {
			setColor(this.moment.getCouleur());
		}
		if(this.moment.getMorceauDescripteme() != null){
			showExtractIcon(this.moment.getMorceauDescripteme());
		}
	}
	
	public void setColor(String col){
		String styleLabel = "-fx-background-color: "+col+"; ";
		if(Main.activateBetaDesign)
			styleLabel+= 		"-fx-border-color: transparent;";
		else
			styleLabel+= 		"-fx-border-color: black;";
		this.borderPaneLabel.setStyle(styleLabel);
	}
	
	public void showExtractIcon(String tooltip){
		File image = new File("./img/hasExtractIcon.gif");
		Image icon = new Image(image.toURI().toString());
		this.hasExtractImage.setImage(icon);
		extractTooltip.setText(tooltip);
		extractTooltip.setOpacity(1);
	}

	
	public void hideExtractIcon(){
		this.hasExtractImage.setImage(null);
		extractTooltip.setOpacity(0);
	}
	

	public void deleteMoment(){
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
    	alert.setTitle("Avertissement suppression");
    	alert.setHeaderText("Vous allez supprimer le moment et tous ses sous-moments");
    	alert.setContentText("Voulez-vous continuer ?");
    	alert.initStyle(StageStyle.UTILITY);

    	Optional<ButtonType> result = alert.showAndWait();
    	if (result.get() == ButtonType.OK){
    		RemoveMomentCommand cmd = new RemoveMomentCommand(this, main);
    		cmd.execute();
    		UndoCollector.INSTANCE.add(cmd);
    	}
	}
	
	private void setLabelChangeName(Main main,MomentExpVBox thiss){
		label.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				if(arg0.getClickCount() == 2){
					//System.out.println("DoubleClick");
					editNameMode();
				}
			}
		});
	}
	
	private void editNameMode() {
		TextField t = new TextField();
		t.setMaxWidth(180);
		t.setText(moment.getNom());
		t.requestFocus();
		
		ChangeListener<Boolean>	 listener = new ChangeListener<Boolean>() {
			 @Override
			    public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
			    {
			        if (!newPropertyValue)
			        {
			        	RenameMomentCommand cmd = new RenameMomentCommand(
			        			nameController,
			        			moment.getNom(),
			        			t.getText(),
			        			main);
						cmd.execute();
						UndoCollector.INSTANCE.add(cmd);
						borderPaneLabel.setCenter(label);
						t.focusedProperty().removeListener(this);
			        }
			    }
		};
		t.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if(event.getCode() == KeyCode.ENTER){
					t.setText(t.getText());
					borderPaneLabel.setCenter(label);
				}
				if(event.getCode() == KeyCode.ESCAPE){
					borderPaneLabel.setCenter(label);
				}
			}
		});
		t.focusedProperty().addListener(listener);
		Platform.runLater(()->t.requestFocus());
		Platform.runLater(()->t.selectAll());
		borderPaneLabel.setCenter(t);
	}
	
	public TypeClassRepresentationController getTypeClassRep(Classe item) {
		for(Node n : typeSpace.getChildren()){
			TypeClassRepresentationController tcr = (TypeClassRepresentationController) n;
			if(tcr.getClasse().equals(item)){
				return tcr;
			}
		}
		return null;
	}
	
	public void removeTypeClassRep(TypeClassRepresentationController tcrc) {
		if(this.typeSpace.getChildren().contains(tcrc)) {
			this.typeSpace.getChildren().remove(tcrc);
			this.moment.getType().remove(tcrc.getClasse());
			stack.push(tcrc);
			if(main.getMainViewController().isInspectorOpen()) {
				main.getMainViewController().renderInspector();
			}
		}
	}
	
	public void putPreviousClassRep() {
		if(!stack.isEmpty()) {
			TypeClassRepresentationController tcrc = stack.getFirst();
			if(!this.typeSpace.getChildren().contains(tcrc)) {
				this.typeSpace.getChildren().add(tcrc);
				this.moment.getType().add(tcrc.getClasse());
				stack.pop();
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(main.getMainViewController().isInspectorOpen()) {
							main.getMainViewController().renderInspector();
						}
					}
				});
				
			}
		}
		
	}
	
	public void showMoment(){
		this.getChildren().add(0, momentPane);
		this.setPadding(new Insets(10,0,0,0));
	}
	
	public void hideMoment() {
		this.getChildren().remove(momentPane);
		this.setPadding(new Insets(0));
	}
	
	public MomentExperience getMoment() {
		return moment;
	}

	public void setMoment(MomentExperience moment) {
		this.moment = moment;
		nameController.updateModel(moment);
		colorController.updateModel(moment);
		extractController.updateModel(moment);
		addTypeController.updateModel(moment);
		momentRemoveTypeController.updateModel(moment);
	}
	
	@Override
	public void updateVue(Observable obs, Object value) {

		if(obs.getClass().equals(MomentNameController.class)) {
			label.setText((String) value);
		}
		if(obs.getClass().equals(MomentColorController.class)) {
			this.setColor((String) value);
		}
		if(obs.getClass().equals(MomentExtractController.class)) {
			if(value != null) {
				this.showExtractIcon((String) value);

			}else {
				this.hideExtractIcon();
			}
		}
		if(obs.getClass().equals(MomentAddTypeController.class)) {
			if(value != null) {
				TypeClassRepresentationController elementPane = new TypeClassRepresentationController((Classe) value,this,main);
				
					MainViewTransformations.addTypeListener(elementPane, this, (Type) value, main);
					//((TypeTreeViewControllerClass)((TypeTreeView)Main.tempDragReference).getController()).getNameController().addObserver(elementPane);
					this.typeSpace.getChildren().add(elementPane);
			}
			else {
				this.typeSpace.getChildren().remove(this.typeSpace.getChildren().size()-1);
			}
		}
		if(obs.getClass().equals(MomentRemoveTypeController.class)) {

			TypeClassRepresentationController t = (TypeClassRepresentationController) value;
			boolean contains = TypeClassRepresentationController.ListcontainsTypeClassRep(this.typeSpace.getChildren(), t);
			if(contains){
				TypeClassRepresentationController.RemoveTypeClassRepFromList(this.typeSpace.getChildren(), t);
			}else {
				this.typeSpace.getChildren().add((TypeClassRepresentationController)value);
			}
		}
	}
	

	
	public void setBorderColor(String couleur) {
		if(Main.activateBetaDesign && couleur == "black") couleur = "transparent";
		momentPane.setStyle("-fx-border-color : "+couleur);
		String styleLabel = "-fx-background-color: "+moment.getCouleur()+"; -fx-border-color:"+couleur +";";
		this.borderPaneLabel.setStyle(styleLabel);
	}

	public int getCol() {
		return this.moment.getGridCol();
	}

	public void setCol(int col) {
		this.moment.setGridCol(col);
	}

	public Label getLabel() {
		return label;
	}

	public void setLabelText(String label) {
		this.label.setText(label);
	}

	public FlowPane getTypeSpace() {
		return typeSpace;
	}

	public void setTypeSpace(FlowPane typeSpace) {
		this.typeSpace = typeSpace;
	}

	public BorderPane getMomentPane() {
		return momentPane;
	}

	public GridPane getSousMomentPane() {
		return sousMomentPane;
	}

	public void setSousMomentPane(GridPane sousMomentPane) {
		this.sousMomentPane = sousMomentPane;
	}
	
	public BorderPane getborderPaneLabel(){
		return this.borderPaneLabel;
	}
	
	public MomentNameController getMomentNameController() {
		return this.nameController;
	}
	
	public MomentColorController getMomentColorController() {
		return this.colorController;
	}
	
	public MomentExtractController getMomentExtractController() {
		return this.extractController;
	}
	
	public MomentAddTypeController getMomentAddTypeController() {
		return this.addTypeController;
	}
	
	public MomentRemoveTypeController getMomentRemoveTypeController() {
		return this.momentRemoveTypeController;
	}
	
	public MomentExpVBox getVBoxParent() {
		return this.momentParent;
	}
	
	public void setVBoxParent(MomentExpVBox parent) {
		this.momentParent = parent;
	}
	
	public int getColOfRootMoment() {
		if(hasParent()) return this.momentParent.getColOfRootMoment();
		else return this.getCol();
	}
	
	public boolean isAChildOf(MomentExperience p) {
		if(hasParent()) {
			if(p.equals(this.getVBoxParent().getMoment()))
				return true;
			else 
				return this.getVBoxParent().isAChildOf(p);
		}
		else return false;
	}
	
	public boolean isAParentOf(MomentExperience p) {
		boolean ret = false;
		for(Node n : this.getSousMomentPane().getChildren()) {
			if(ret)break;
			MomentExpVBox m = (MomentExpVBox)n;
			if(m.getMoment().equals(p)) ret = true;
			else {
				ret = m.isAParentOf(p);
			}
		}
		return ret;
	}
	
	public boolean isDirectParentOf(MomentExperience p) {
		boolean ret = false;
		for(Node n : this.getSousMomentPane().getChildren()) {
			MomentExpVBox m = (MomentExpVBox)n;
			//System.out.print(m.getMoment().getNom()+" est le pere de "+p.getNom()+" ?");
			if(m.getMoment().equals(p)) {
				//System.out.println(" -> Oui");
				ret = true;
				break;
			}
			else {
				//System.out.println(" -> Non");
			}
		}
		return ret;
	}
	
	
	public boolean hasParent() {
		return this.momentParent!=null;
	}
	
	
	public String toString() {
		String ret="";
		ret="{Nom:"+this.moment.getNom()+"; Sous-Moments:[";
		for(Node n : this.getSousMomentPane().getChildren()) {
			MomentExpVBox m = (MomentExpVBox)n;
			ret+=m.toString();
		}
		ret+="]}\n";
		return ret;
	}
}
