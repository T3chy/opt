/**
 * Copyright (c) 2019, Regents of the University of California
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *   Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 **/
package opt.config;

import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.NumberStringConverter;
import opt.AppMainController;
import opt.UserSettings;
import opt.data.AbstractLink;
import opt.data.FreewayScenario;
import opt.data.LinkOfframp;
import opt.data.LinkOnramp;
import opt.utils.Misc;
import opt.utils.ModifiedDoubleStringConverter;
import opt.utils.ModifiedIntegerStringConverter;


/**
 * Link Editor UI control.
 * Link Editor is located in Configuration tab of the Action Pane when a link
 * is selected in the navigation tree.
 * 
 * @author Alex Kurzhanskiy
 */
public class LinkEditorController {
    private Stage primaryStage = null;
    private AppMainController appMainController = null;
    private NewLinkController newLinkController = null;
    private Scene newLinkScene = null;
    private NewRampController newRampController = null;
    private Scene newRampScene = null;
    private ConnectController connectController = null;
    private Scene connectScene = null;
    private AbstractLink myLink = null;
    private boolean ignoreChange = true;
    //TitledPane focusTitledPane = null;
    
    private List<AbstractLink> onramps = new ArrayList<AbstractLink>();
    private List<AbstractLink> offramps = new ArrayList<AbstractLink>();
    
    private List<AbstractLink> upConnectCandidates = new ArrayList<AbstractLink>();
    private List<AbstractLink> dnConnectCandidates = new ArrayList<AbstractLink>();
    
    private SpinnerValueFactory<Double> mergePrioritySpinnerValueFactory = null;
    private SpinnerValueFactory<Double> lengthSpinnerValueFactory = null;
    
    private SpinnerValueFactory<Integer> numLanesGPSpinnerValueFactory = null;
    private SpinnerValueFactory<Integer> numLanesAuxSpinnerValueFactory = null;
    private SpinnerValueFactory<Integer> numLanesManagedSpinnerValueFactory = null;
    
    private SpinnerValueFactory<Double> capacityGPSpinnerValueFactory = null;
    private SpinnerValueFactory<Double> capacityAuxSpinnerValueFactory = null;
    private SpinnerValueFactory<Double> capacityManagedSpinnerValueFactory = null;
    
    private SpinnerValueFactory<Double> ffSpeedGPSpinnerValueFactory = null;
    private SpinnerValueFactory<Double> ffSpeedAuxSpinnerValueFactory = null;
    private SpinnerValueFactory<Double> ffSpeedManagedSpinnerValueFactory = null;
    
    private SpinnerValueFactory<Double> jamDensityGPSpinnerValueFactory = null;
    private SpinnerValueFactory<Double> jamDensityAuxSpinnerValueFactory = null;
    private SpinnerValueFactory<Double> jamDensityManagedSpinnerValueFactory = null;
    
    
    
    
    
    @FXML // fx:id="linkEditorMainPane"
    private SplitPane linkEditorMainPane; // Value injected by FXMLLoader
    
     @FXML // fx:id="canvasParent"
    private AnchorPane canvasParent; // Value injected by FXMLLoader
    
    @FXML // fx:id="linkEditorCanvas"
    private Canvas linkEditorCanvas; // Value injected by FXMLLoader
    
    @FXML // fx:id="addSectionUpstream"
    private Button addSectionUpstream; // Value injected by FXMLLoader

    @FXML // fx:id="connectSectionUpstream"
    private Button connectSectionUpstream; // Value injected by FXMLLoader

    @FXML // fx:id="connectSectionDownstream"
    private Button connectSectionDownstream; // Value injected by FXMLLoader

    @FXML // fx:id="addSectionDownstream"
    private Button addSectionDownstream; // Value injected by FXMLLoader

    @FXML // fx:id="deleteSection"
    private Button deleteSection; // Value injected by FXMLLoader

    @FXML // fx:id="labelFromName"
    private Label labelFromName; // Value injected by FXMLLoader

    @FXML // fx:id="labelToName"
    private Label labelToName; // Value injected by FXMLLoader
    
    @FXML // fx:id="labelLength"
    private Label labelLength; // Value injected by FXMLLoader

    @FXML // fx:id="linkFromName"
    private TextField linkFromName; // Value injected by FXMLLoader

    @FXML // fx:id="linkToName"
    private TextField linkToName; // Value injected by FXMLLoader

    @FXML // fx:id="mergePriority"
    private Spinner<Double> mergePriority; // Value injected by FXMLLoader
    
    @FXML // fx:id="linkLength"
    private Spinner<Double> linkLength; // Value injected by FXMLLoader
    
    @FXML // fx:id="linkEditorAccordionParent"
    private AnchorPane linkEditorAccordionParent; // Value injected by FXMLLoader

    @FXML // fx:id="linkEditorAccordion"
    private Accordion linkEditorAccordion; // Value injected by FXMLLoader

    @FXML // fx:id="laneProperties"
    private TitledPane laneProperties; // Value injected by FXMLLoader

    @FXML // fx:id="numGPLanes"
    private Spinner<Integer> numGPLanes; // Value injected by FXMLLoader

    @FXML // fx:id="numAuxLanes"
    private Spinner<Integer> numAuxLanes; // Value injected by FXMLLoader

    @FXML // fx:id="numManagedLanes"
    private Spinner<Integer> numManagedLanes; // Value injected by FXMLLoader

    @FXML // fx:id="capacityGPLane"
    private Spinner<Double> capacityGPLane; // Value injected by FXMLLoader

    @FXML // fx:id="capacityAuxLane"
    private Spinner<Double> capacityAuxLane; // Value injected by FXMLLoader

    @FXML // fx:id="capacityManagedLane"
    private Spinner<Double> capacityManagedLane; // Value injected by FXMLLoader

    @FXML // fx:id="ffSpeedGP"
    private Spinner<Double> ffSpeedGP; // Value injected by FXMLLoader

    @FXML // fx:id="ffSpeedAux"
    private Spinner<Double> ffSpeedAux; // Value injected by FXMLLoader

    @FXML // fx:id="ffSpeedManaged"
    private Spinner<Double> ffSpeedManaged; // Value injected by FXMLLoader

    @FXML // fx:id="jamDensityGPLane"
    private Spinner<Double> jamDensityGPLane; // Value injected by FXMLLoader

    @FXML // fx:id="jamDensityAuxLane"
    private Spinner<Double> jamDensityAuxLane; // Value injected by FXMLLoader

    @FXML // fx:id="jamDensityManagedLane"
    private Spinner<Double> jamDensityManagedLane; // Value injected by FXMLLoader
    
    @FXML // fx:id="cbBarrier"
    private CheckBox cbBarrier; // Value injected by FXMLLoader

    @FXML // fx:id="cbSeparated"
    private CheckBox cbSeparated; // Value injected by FXMLLoader
    
     @FXML // fx:id="labelManagedLaneCapacity"
    private Label labelManagedLaneCapacity; // Value injected by FXMLLoader

    @FXML // fx:id="labelGPLaneCapacity"
    private Label labelGPLaneCapacity; // Value injected by FXMLLoader

    @FXML // fx:id="labelAuxLaneCapacity"
    private Label labelAuxLaneCapacity; // Value injected by FXMLLoader

    @FXML // fx:id="labelFreeFlowSpeedManaged"
    private Label labelFreeFlowSpeedManaged; // Value injected by FXMLLoader

    @FXML // fx:id="labelFreeFlowSpeedGP"
    private Label labelFreeFlowSpeedGP; // Value injected by FXMLLoader

    @FXML // fx:id="labelFreeFlowSpeedAux"
    private Label labelFreeFlowSpeedAux; // Value injected by FXMLLoader

    @FXML // fx:id="labelJamDensityManaged"
    private Label labelJamDensityManaged; // Value injected by FXMLLoader

    @FXML // fx:id="labelJamDensityGP"
    private Label labelJamDensityGP; // Value injected by FXMLLoader

    @FXML // fx:id="labelJamDensityAux"
    private Label labelJamDensityAux; // Value injected by FXMLLoader

    @FXML // fx:id="rampsPane"
    private TitledPane rampsPane; // Value injected by FXMLLoader
    
    @FXML // fx:id="listOnramps"
    private ListView<String> listOnramps; // Value injected by FXMLLoader

    @FXML // fx:id="listOfframps"
    private ListView<String> listOfframps; // Value injected by FXMLLoader

    @FXML // fx:id="addOnRamp"
    private Button addOnRamp; // Value injected by FXMLLoader

    @FXML // fx:id="addOffRamp"
    private Button addOffRamp; // Value injected by FXMLLoader

    @FXML // fx:id="deleteOnRamps"
    private Button deleteOnRamps; // Value injected by FXMLLoader

    @FXML // fx:id="deleteOffRamps"
    private Button deleteOffRamps; // Value injected by FXMLLoader

    @FXML // fx:id="trafficDemand1"
    private TitledPane trafficDemand1; // Value injected by FXMLLoader

    @FXML // fx:id="trafficSplitDownstream"
    private TitledPane trafficSplitDownstream; // Value injected by FXMLLoader
    
    @FXML // fx:id="linkControllerPane"
    private TitledPane linkControllerPane; // Value injected by FXMLLoader
    
    @FXML // fx:id="linkEventPane"
    private TitledPane linkEventPane; // Value injected by FXMLLoader
    
    @FXML // fx:id="ttAddSectionUpstream"
    private Tooltip ttAddSectionUpstream; // Value injected by FXMLLoader
    
    @FXML // fx:id="ttAddSectionDownstream"
    private Tooltip ttAddSectionDownstream; // Value injected by FXMLLoader
    
     @FXML // fx:id="ttConnectUpstream"
    private Tooltip ttConnectUpstream; // Value injected by FXMLLoader
    
    @FXML // fx:id="ttConnectDownstream"
    private Tooltip ttConnectDownstream; // Value injected by FXMLLoader
    
     @FXML // fx:id="ttDeleteSection"
    private Tooltip ttDeleteSection; // Value injected by FXMLLoader



    public void setPrimaryStage(Stage s) {
        primaryStage = s;
    }
    
    /**
     * This function should be called once: during the initialization.
     * @param ctrl - pointer to the main app controller that is used to sync up
     *               all sub-windows.
     */
    public void setAppMainController(AppMainController ctrl) {
        appMainController = ctrl;
    }
    
    /**
     * This function should be called once: during the initialization.
     * @param ctrl - pointer to the new link controller that is used to set up
     *               new road sections.
     */
    public void setNewLinkControllerAndScene(NewLinkController ctrl, Scene scn) {
        newLinkController = ctrl;
        newLinkScene = scn;
        newLinkScene.getStylesheets().add(getClass().getResource("/opt.css").toExternalForm());
    }
    
    /**
     * This function should be called once: during the initialization.
     * @param ctrl - pointer to the new ramp controller that is used to set up
     *               new on- and off-ramps.
     */
    public void setNewRampControllerAndScene(NewRampController ctrl, Scene scn) {
        newRampController = ctrl;
        newRampScene = scn;
        newRampScene.getStylesheets().add(getClass().getResource("/opt.css").toExternalForm());
    }
    
    /**
     * This function should be called once: during the initialization.
     * @param ctrl - pointer to the connect controller that is used to connect
     *               two open-ended links.
     */
    public void setConnectControllerAndScene(ConnectController ctrl, Scene scn) {
        connectController = ctrl;
        connectScene = scn;
        connectScene.getStylesheets().add(getClass().getResource("/opt.css").toExternalForm());
    }
    
    

    @FXML
    void onAddOnRamp(ActionEvent event) {
        int num_in_ors = myLink.get_segment().num_in_ors();
        int num_out_ors = myLink.get_segment().num_out_ors();
        if ((num_in_ors >= 3) && (num_out_ors >= 3)) {
            opt.utils.Dialogs.ErrorDialog("Cannot add an on-ramp to this freeway section...",
                                          "The on-ramp limit is reached!");
            return;
        }
        Stage inputStage = new Stage();
        inputStage.initOwner(primaryStage);
        inputStage.setScene(newRampScene);
        newRampController.initWithLinkAndType(myLink, AbstractLink.Type.onramp);
        inputStage.setTitle("Adding New On-Ramp");
        inputStage.getIcons().add(new Image(getClass().getResourceAsStream("/OPT_icon.png")));
        inputStage.initModality(Modality.APPLICATION_MODAL);
        inputStage.setResizable(false);
        inputStage.showAndWait();
    }
    
    @FXML
    void onAddOffRamp(ActionEvent event) {
        int num_in_frs = myLink.get_segment().num_in_frs();
        int num_out_frs = myLink.get_segment().num_out_frs();
        if ((num_in_frs >= 3) && (num_out_frs >= 3)) {
            opt.utils.Dialogs.ErrorDialog("Cannot add an off-ramp to this freeway section...",
                                          "The off-ramp limit is reached!");
            return;
        }
        Stage inputStage = new Stage();
        inputStage.initOwner(primaryStage);
        inputStage.setScene(newRampScene);
        newRampController.initWithLinkAndType(myLink, AbstractLink.Type.offramp);
        inputStage.setTitle("Adding New Off-Ramp");
        inputStage.getIcons().add(new Image(getClass().getResourceAsStream("/OPT_icon.png")));
        inputStage.initModality(Modality.APPLICATION_MODAL);
        inputStage.setResizable(false);
        inputStage.showAndWait();
    }

    
    
    @FXML
    void onDeleteOnRamp(ActionEvent event) {
        if (ignoreChange)
            return;
        
        int idx = listOnramps.getSelectionModel().getSelectedIndex();
            if ((idx < 0) || (idx >= onramps.size()))
                return;
        
        String header = "You are deleting on-ramp '" + onramps.get(idx).get_name() + "'...";
                
        if (!opt.utils.Dialogs.ConfirmationYesNoDialog(header, "Are you sure?")) 
            return;
     
        if (idx < myLink.get_segment().num_out_ors())
            myLink.get_segment().delete_out_or((LinkOnramp)onramps.get(idx));
        else
            myLink.get_segment().delete_in_or((LinkOnramp)onramps.get(idx));
        
        appMainController.objectNameUpdate(myLink);
    }
    
    @FXML
    void onDeleteOffRamp(ActionEvent event) {
        if (ignoreChange)
            return;
        
        int idx = listOfframps.getSelectionModel().getSelectedIndex();
            if ((idx < 0) || (idx >= offramps.size()))
                return;
            
        String header = "You are deleting off-ramp '" + offramps.get(idx).get_name() + "'...";
                
        if (!opt.utils.Dialogs.ConfirmationYesNoDialog(header, "Are you sure?")) 
            return;
        
        if (idx < myLink.get_segment().num_out_frs())
            myLink.get_segment().delete_out_fr((LinkOfframp)offramps.get(idx));
        else
            myLink.get_segment().delete_in_fr((LinkOfframp)offramps.get(idx));
        
        appMainController.objectNameUpdate(myLink);
    }

    
    
    
    @FXML
    void onrampsOnClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            int idx = listOnramps.getSelectionModel().getSelectedIndex();
            if ((idx < 0) || (idx >= onramps.size()))
                return;
            appMainController.selectLink(onramps.get(idx));
        }
    }
    
    @FXML
    void onrampsKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            int idx = listOnramps.getSelectionModel().getSelectedIndex();
            if ((idx < 0) || (idx >= onramps.size()))
                return;
            appMainController.selectLink(onramps.get(idx));
        }
        if ((event.getCode() == KeyCode.DELETE) || (event.getCode() == KeyCode.BACK_SPACE)) {
            onDeleteOnRamp(null);
        }
    }
    
    
    
    @FXML
    void offrampsOnClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            int idx = listOfframps.getSelectionModel().getSelectedIndex();
            if ((idx < 0) || (idx >= offramps.size()))
                return;
            appMainController.selectLink(offramps.get(idx));
        }
    }

    @FXML
    void offrampsKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            int idx = listOfframps.getSelectionModel().getSelectedIndex();
            if ((idx < 0) || (idx >= offramps.size()))
                return;
            appMainController.selectLink(offramps.get(idx));
        }
        if ((event.getCode() == KeyCode.DELETE) || (event.getCode() == KeyCode.BACK_SPACE)) {
            onDeleteOffRamp(null);
        }
    }

    
    
    @FXML
    void onAddSectionDownstreamAction(ActionEvent event) {
        Stage inputStage = new Stage();
        inputStage.initOwner(primaryStage);
        inputStage.setScene(newLinkScene);
        newLinkController.initWithTwoLinks(myLink, null);
        inputStage.setTitle("Adding New Freeway Section");
        if ((myLink.get_type() == AbstractLink.Type.onramp) || (myLink.get_type() == AbstractLink.Type.offramp))
            inputStage.setTitle("Adding New Connector");
        inputStage.getIcons().add(new Image(getClass().getResourceAsStream("/OPT_icon.png")));
        inputStage.initModality(Modality.APPLICATION_MODAL);
        inputStage.setResizable(false);
        inputStage.showAndWait();
    }

    @FXML
    void onAddSectionUpstreamAction(ActionEvent event) {
        Stage inputStage = new Stage();
        inputStage.initOwner(primaryStage);
        inputStage.setScene(newLinkScene);
        newLinkController.initWithTwoLinks(null, myLink);
        inputStage.setTitle("Adding New Freeway Section");
        if ((myLink.get_type() == AbstractLink.Type.onramp) || (myLink.get_type() == AbstractLink.Type.offramp))
            inputStage.setTitle("Adding New Connector");
        inputStage.getIcons().add(new Image(getClass().getResourceAsStream("/OPT_icon.png")));
        inputStage.initModality(Modality.APPLICATION_MODAL);
        inputStage.setResizable(false);
        inputStage.showAndWait();
    }
    
    
    
    @FXML
    void onConnectSectionDownstreamAction(ActionEvent event) {
        if (dnConnectCandidates.isEmpty()) {
            opt.utils.Dialogs.ErrorDialog("There are no sections to connect to...", "No compatibe origin sections are found!");
            return;
        }
        Stage inputStage = new Stage();
        inputStage.initOwner(primaryStage);
        inputStage.setScene(connectScene);
        String title = "Connect to a Freeway Section Downstream";
        String label = "Choose an Origin Freeway Section:";
        if (myLink.get_type() == AbstractLink.Type.offramp) {
            title = "Connect to a Connector Downstream";
            label = "Choose a Connector:";
        } else if (myLink.get_type() == AbstractLink.Type.connector) {
            title = "Connect to an On-Ramp Downstream";
            label = "Choose an On-Ramp:";
        } else if (myLink.get_type() == AbstractLink.Type.onramp) {
            opt.utils.Dialogs.ErrorDialog("On-ramps cannot connect to downstream sections...", "Please, report this problem!");
            return;
        }
        connectController.initWithLinkAndCandidates(myLink, dnConnectCandidates, label, true);
        inputStage.setTitle(title);
        inputStage.getIcons().add(new Image(getClass().getResourceAsStream("/OPT_icon.png")));
        inputStage.initModality(Modality.APPLICATION_MODAL);
        inputStage.setResizable(false);
        inputStage.showAndWait();
    }

    @FXML
    void onConnectSectionUpstreamAction(ActionEvent event) {
        if (upConnectCandidates.isEmpty()) {
            opt.utils.Dialogs.ErrorDialog("There are no sections to connect to...", "No compatibe destination sections are found!");
            return;
        }
        Stage inputStage = new Stage();
        inputStage.initOwner(primaryStage);
        inputStage.setScene(connectScene);
        String title = "Connect to a Freeway Section Upstream";
        String label = "Choose a Destination Freeway Section:";
        if (myLink.get_type() == AbstractLink.Type.onramp) {
            title = "Connect to a Connector Upstream";
            label = "Choose a Connector:";
        } else if (myLink.get_type() == AbstractLink.Type.connector) {
            title = "Connect to an Off-Ramp Upstream";
            label = "Choose an Off-Ramp:";
        } else if (myLink.get_type() == AbstractLink.Type.offramp) {
            opt.utils.Dialogs.ErrorDialog("Off-ramps cannot connect to upstream sections...", "Please, report this problem!");
            return;
        }
        connectController.initWithLinkAndCandidates(myLink, upConnectCandidates, label, false);
        inputStage.setTitle(title);
        inputStage.getIcons().add(new Image(getClass().getResourceAsStream("/OPT_icon.png")));
        inputStage.initModality(Modality.APPLICATION_MODAL);
        inputStage.setResizable(false);
        inputStage.showAndWait();
    }

    

    @FXML
    void onDeleteSection(ActionEvent event) {
        if (ignoreChange)
            return;
        
        String header = "You are deleting " +
                opt.utils.Misc.linkType2String(myLink.get_type()).toLowerCase() +
                " section '" + myLink.get_name() + "'...";
        String content = "Are you sure?";
        String bt1 = "Yes, Disconnect Freeway";
        String bt2 = "Yes, Reconnect Freeway";
        String bt3 = "No";
        
        if ((myLink.get_type() == AbstractLink.Type.connector) ||
            (myLink.get_up_link() == null) || (myLink.get_dn_link() == null)) {
            if (!opt.utils.Dialogs.ConfirmationYesNoDialog(header, content))
                return;
           
            appMainController.deleteLink(myLink, false);
            return;
        }
        
        int res = opt.utils.Dialogs.Confirmation3ButtonDialog(header, content, bt1, bt2, bt3);
        if (res < 0)
            return;
        
        appMainController.deleteLink(myLink, (res > 0));
    }
    
    

    @FXML
    void onLinkNameChanged(ActionEvent event) {
        if (ignoreChange)
            return;
        
        String link_name = linkFromName.getText() + " -> " + linkToName.getText();
        link_name = opt.utils.Misc.validateAndCorrectLinkName(link_name, myLink.get_segment().get_scenario());
        
        myLink.set_name(link_name);
        if ((myLink.get_type() == AbstractLink.Type.freeway) || (myLink.get_type() == AbstractLink.Type.connector))
            myLink.get_segment().name = link_name;
        
        appMainController.objectNameUpdate(myLink);
    }
    
    

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        
        linkFromName.textProperty().addListener((observable, oldValue, newValue) -> {
            onLinkNameChanged(null);
        });
        
        linkToName.textProperty().addListener((observable, oldValue, newValue) -> {
            onLinkNameChanged(null);
        });
        
        
        double merge_priority_step = 0.01;
        mergePrioritySpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 1.0, 0.0, merge_priority_step);
        mergePrioritySpinnerValueFactory.setConverter(new ModifiedDoubleStringConverter());
        mergePriority.setValueFactory(mergePrioritySpinnerValueFactory);
        mergePriority.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (Math.abs(oldValue-newValue) > 0.00001)) {
                onMergePriorityChange();
            }
        });
        
        mergePriority.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                return;
            opt.utils.WidgetFunctionality.commitEditorText(mergePriority, UserSettings.defaultMergePriority);
        });
        
        
        double length_step = 1;
        if (UserSettings.unitsLength.equals("kilometers") || UserSettings.unitsLength.equals("miles"))
            length_step = 0.1;
        lengthSpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, 0.0, length_step);
        lengthSpinnerValueFactory.setConverter(new ModifiedDoubleStringConverter());
        linkLength.setValueFactory(lengthSpinnerValueFactory);
        linkLength.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (Math.abs(oldValue-newValue) > 0.00001)) {
                onLinkLengthChange();
            }
        });
        
        linkLength.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                return;
            Double length = new Double(1);
            if (myLink != null) {
                length = new Double(myLink.get_length_meters());
                length = UserSettings.convertLength(length, "meters", UserSettings.unitsLength);
            }
            opt.utils.WidgetFunctionality.commitEditorText(linkLength, length);
        });
        
        
        numLanesGPSpinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1, 1);
        numLanesGPSpinnerValueFactory.setConverter(new ModifiedIntegerStringConverter());
        numGPLanes.setValueFactory(numLanesGPSpinnerValueFactory);
        numGPLanes.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (oldValue != newValue))
                onNumLanesChange();
        });
        
        numGPLanes.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                return;
            Integer num_lanes = new Integer(1);
            if (myLink != null) {
                num_lanes = new Integer(myLink.get_gp_lanes());
            }
            opt.utils.WidgetFunctionality.commitEditorText(numGPLanes, num_lanes);
        });
        
        numLanesAuxSpinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, 0, 1);
        numLanesAuxSpinnerValueFactory.setConverter(new ModifiedIntegerStringConverter(0));
        numAuxLanes.setValueFactory(numLanesAuxSpinnerValueFactory);
        numAuxLanes.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (oldValue != newValue))
                onNumLanesChange();
        });
        
        numAuxLanes.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                return;
            Integer num_lanes = new Integer(0);
            if (myLink != null) {
                num_lanes = new Integer(myLink.get_aux_lanes());
            }
            opt.utils.WidgetFunctionality.commitEditorText(numAuxLanes, num_lanes);
        });
        
        numLanesManagedSpinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, 0, 1);
        numLanesManagedSpinnerValueFactory.setConverter(new ModifiedIntegerStringConverter(0));
        numManagedLanes.setValueFactory(numLanesManagedSpinnerValueFactory);
        numManagedLanes.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (oldValue != newValue))
                onNumLanesChange();
        });
        
        numManagedLanes.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                return;
            Integer num_lanes = new Integer(0);
            if (myLink != null) {
                num_lanes = new Integer(myLink.get_managed_lanes());
            }
            opt.utils.WidgetFunctionality.commitEditorText(numManagedLanes, num_lanes);
        });
        
        
        double cap_step = 1;
        if (UserSettings.unitsFlow.equals("vps"))
            cap_step = 0.01;
        capacityGPSpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, 0.0, cap_step);
        capacityGPSpinnerValueFactory.setConverter(new ModifiedDoubleStringConverter());
        capacityGPLane.setValueFactory(capacityGPSpinnerValueFactory);
        capacityGPLane.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (oldValue != newValue))
                onParamChange();
        });
        
        capacityAuxSpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, 0.0, cap_step);
        capacityAuxSpinnerValueFactory.setConverter(new ModifiedDoubleStringConverter());
        capacityAuxLane.setValueFactory(capacityAuxSpinnerValueFactory);
        capacityAuxLane.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (oldValue != newValue))
                onParamChange();
        });
        
        capacityManagedSpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, 0.0, cap_step);
        capacityManagedSpinnerValueFactory.setConverter(new ModifiedDoubleStringConverter());
        capacityManagedLane.setValueFactory(capacityManagedSpinnerValueFactory);
        capacityManagedLane.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (oldValue != newValue))
                onParamChange();
        });
        
        ffSpeedGPSpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, 0.0, 1);
        ffSpeedGPSpinnerValueFactory.setConverter(new ModifiedDoubleStringConverter());
        ffSpeedGP.setValueFactory(ffSpeedGPSpinnerValueFactory);
        ffSpeedGP.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (oldValue != newValue))
                onParamChange();
        });
        
        ffSpeedAuxSpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, 0.0, 1);
        ffSpeedAuxSpinnerValueFactory.setConverter(new ModifiedDoubleStringConverter());
        ffSpeedAux.setValueFactory(ffSpeedAuxSpinnerValueFactory);
        ffSpeedAux.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (oldValue != newValue))
                onParamChange();
        });
        
        ffSpeedManagedSpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, 0.0, 1);
        ffSpeedManagedSpinnerValueFactory.setConverter(new ModifiedDoubleStringConverter());
        ffSpeedManaged.setValueFactory(ffSpeedManagedSpinnerValueFactory);
        ffSpeedManaged.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (oldValue != newValue))
                onParamChange();
        });
        
        double density_step = 1;
        String num_format = "#.##";
        if (UserSettings.unitsDensity.equals("vpmtr") || UserSettings.unitsDensity.equals("vpf")) {
            density_step = 0.001;
            num_format = "#.###";
        }
        jamDensityGPSpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, 0.0, density_step);
        jamDensityGPSpinnerValueFactory.setConverter(new ModifiedDoubleStringConverter(num_format));
        jamDensityGPLane.setValueFactory(jamDensityGPSpinnerValueFactory);
        jamDensityGPLane.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (oldValue != newValue))
                onParamChange();
        });
        
        jamDensityAuxSpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, 0.0, density_step);
        jamDensityAuxSpinnerValueFactory.setConverter(new ModifiedDoubleStringConverter(num_format));
        jamDensityAuxLane.setValueFactory(jamDensityAuxSpinnerValueFactory);
        jamDensityAuxLane.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (oldValue != newValue))
                onParamChange();
        });
        
        jamDensityManagedSpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, 0.0, density_step);
        jamDensityManagedSpinnerValueFactory.setConverter(new ModifiedDoubleStringConverter(num_format));
        jamDensityManagedLane.setValueFactory(jamDensityManagedSpinnerValueFactory);
        jamDensityManagedLane.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (oldValue != newValue))
                onParamChange();
        });
        

       
        
        
        linkEditorCanvas.widthProperty().bind(canvasParent.widthProperty());
        linkEditorCanvas.heightProperty().bind(canvasParent.heightProperty());

        
        
        linkEditorCanvas.widthProperty().addListener((observable, oldValue, newValue) -> {
           ignoreChange = true;
           drawRoadSection(); 
           ignoreChange = false;
        });
        
        linkEditorCanvas.heightProperty().addListener((observable, oldValue, newValue) -> {
           ignoreChange = true;
           drawRoadSection();
           ignoreChange = false;
        });
        
        
    }
    
    
    
    
    
    
    /**
     * This function is called every time one opens a link in the
     * configuration module.
     * @param lnk 
     */
    public void initWithLinkData(AbstractLink lnk) {        
        if (lnk == null)
            return;
        
        ignoreChange = true;
                
        myLink = lnk;
        String link_name = myLink.get_name();
        String[] name_subs = link_name.split(" -> ");
        int sz = name_subs.length;
        String from_name = name_subs[0];
        String to_name = "";
        for (int i = 1; i < sz; i++) {
            to_name += name_subs[i];
            if (i < sz - 1)
                to_name += " -> ";
        }
        if (myLink.get_type() == AbstractLink.Type.onramp) {
            if (from_name.equals(""))
                linkFromName.setText(to_name);
            else
                linkFromName.setText(from_name);
            linkToName.setText("");
            linkFromName.setVisible(true);
            linkToName.setVisible(false);
            labelFromName.setVisible(true);
            //labelToName.setVisible(false);
            labelToName.setText("Merge Priority:");
            mergePriority.setVisible(true);
            deleteSection.setVisible(false);
            addSectionDownstream.setVisible(false);
            connectSectionDownstream.setVisible(false);
            if (myLink.get_up_link() == null) {
                addSectionUpstream.setVisible(true);
            }
            else {
                addSectionUpstream.setVisible(false);
            }
        } else if (myLink.get_type() == AbstractLink.Type.offramp) {
            if (to_name.equals(""))
                linkToName.setText(from_name);
            else
                linkToName.setText(to_name);
            linkFromName.setText("");
            linkFromName.setVisible(false);
            linkToName.setVisible(true);
            labelFromName.setVisible(false);
            //labelToName.setVisible(true);
            labelToName.setText("TO Name:");
            mergePriority.setVisible(false);
            deleteSection.setVisible(false);
            addSectionUpstream.setVisible(false);
            connectSectionUpstream.setVisible(false);
            if (myLink.get_dn_link() == null) {
                addSectionDownstream.setVisible(true);
            }
            else {
                addSectionDownstream.setVisible(false);
            }
        } else {
            linkFromName.setText(from_name);
            linkToName.setText(to_name);
            linkFromName.setVisible(true);
            linkToName.setVisible(true);
            labelFromName.setVisible(true);
            //labelToName.setVisible(true);
            labelToName.setText("TO Name:");
            mergePriority.setVisible(false);
            deleteSection.setVisible(true);
            addSectionUpstream.setVisible(true);
            addSectionDownstream.setVisible(true);
            
            if (myLink.get_type() == AbstractLink.Type.connector) {
                if (myLink.get_up_link() != null)
                    addSectionUpstream.setVisible(false);
                if (myLink.get_dn_link() != null)
                    addSectionDownstream.setVisible(false);   
            }
        }
        
        double mergePriority = UserSettings.defaultMergePriority; // FIXME
        mergePrioritySpinnerValueFactory.setValue(mergePriority);
        
        String unitsLength = UserSettings.unitsLength;
        double length = myLink.get_length_meters();
        length = UserSettings.convertLength(length, "meters", unitsLength);
        labelLength.setText("Length (" + unitsLength + "):");
        lengthSpinnerValueFactory.setValue(length);
        ((ModifiedDoubleStringConverter)lengthSpinnerValueFactory.getConverter()).setDefaultValue(length);
        
        if (myLink.get_type() == AbstractLink.Type.freeway) {
            rampsPane.setVisible(true);
        } else {
            rampsPane.setVisible(false);
        }
        
        int managed_lanes = myLink.get_managed_lanes();
        int gp_lanes = myLink.get_gp_lanes();
        int aux_lanes = myLink.params.get_aux_lanes();
        
        numLanesManagedSpinnerValueFactory.setValue(managed_lanes+1);
        numLanesGPSpinnerValueFactory.setValue(gp_lanes+1);
        numLanesAuxSpinnerValueFactory.setValue(aux_lanes+1);
        numLanesManagedSpinnerValueFactory.setValue(managed_lanes);
        numLanesGPSpinnerValueFactory.setValue(gp_lanes);
        numLanesAuxSpinnerValueFactory.setValue(aux_lanes);
        
        if (myLink.get_type() != AbstractLink.Type.freeway) {
            boolean flag = true;
            numAuxLanes.setDisable(flag);
            capacityAuxLane.setDisable(flag);
            ffSpeedAux.setDisable(flag);
            jamDensityAuxLane.setDisable(flag);
        } else {
            boolean flag = false;
            numAuxLanes.setDisable(flag);
            capacityAuxLane.setDisable(flag);
            ffSpeedAux.setDisable(flag);
            jamDensityAuxLane.setDisable(flag);
        }
        
        // Capacity per lane
        String unitsFlow = UserSettings.unitsFlow;
        labelGPLaneCapacity.setText("GP Lane Capacity (" + unitsFlow + "):");
        double cap = myLink.get_capacity_vphpl();
        cap = UserSettings.convertFlow(cap, "vph", unitsFlow);
        capacityGPSpinnerValueFactory.setValue(cap+1);
        capacityGPSpinnerValueFactory.setValue(cap);
        ((ModifiedDoubleStringConverter)capacityGPSpinnerValueFactory.getConverter()).setDefaultValue(cap);
        labelManagedLaneCapacity.setText("M Lane Capacity (" + unitsFlow + "):");
        cap = myLink.get_capacity_vphpl(); // FIXME
        cap = UserSettings.convertFlow(cap, "vph", unitsFlow);
        capacityManagedSpinnerValueFactory.setValue(cap+1);
        capacityManagedSpinnerValueFactory.setValue(cap);
        ((ModifiedDoubleStringConverter)capacityManagedSpinnerValueFactory.getConverter()).setDefaultValue(cap);
        labelAuxLaneCapacity.setText("Aux Lane Capacity (" + unitsFlow + "):");
        cap = myLink.get_capacity_vphpl(); // FIXME
        cap = UserSettings.convertFlow(cap, "vph", unitsFlow);
        capacityAuxSpinnerValueFactory.setValue(cap+1);
        capacityAuxSpinnerValueFactory.setValue(cap);
        ((ModifiedDoubleStringConverter)capacityAuxSpinnerValueFactory.getConverter()).setDefaultValue(cap);

        // Free flow speed
        String unitsSpeed = UserSettings.unitsSpeed;
        labelFreeFlowSpeedGP.setText("GP Lane Free Flow Speed (" + unitsSpeed + "):");
        double ffspeed = myLink.get_freespeed_kph();
        ffspeed = UserSettings.convertSpeed(ffspeed, "kph", unitsSpeed);
        ffSpeedGPSpinnerValueFactory.setValue(ffspeed+1);
        ffSpeedGPSpinnerValueFactory.setValue(ffspeed);
        ((ModifiedDoubleStringConverter)ffSpeedGPSpinnerValueFactory.getConverter()).setDefaultValue(ffspeed);
        labelFreeFlowSpeedManaged.setText("M Lane Free Flow Speed (" + unitsSpeed + "):");
        ffspeed = myLink.get_freespeed_kph(); // FIXME
        ffspeed = UserSettings.convertSpeed(ffspeed, "kph", unitsSpeed);
        ffSpeedManagedSpinnerValueFactory.setValue(ffspeed+1);
        ffSpeedManagedSpinnerValueFactory.setValue(ffspeed);
        ((ModifiedDoubleStringConverter)ffSpeedManagedSpinnerValueFactory.getConverter()).setDefaultValue(ffspeed);
        labelFreeFlowSpeedAux.setText("Aux Lane Free Flow Speed (" + unitsSpeed + "):");
        ffspeed = myLink.get_freespeed_kph(); // FIXME
        ffspeed = UserSettings.convertSpeed(ffspeed, "kph", unitsSpeed);
        ffSpeedAuxSpinnerValueFactory.setValue(ffspeed+1);
        ffSpeedAuxSpinnerValueFactory.setValue(ffspeed);
        ((ModifiedDoubleStringConverter)ffSpeedAuxSpinnerValueFactory.getConverter()).setDefaultValue(ffspeed);
        
        // Jam density per lane
        String unitsDensity = UserSettings.unitsDensity;
        labelJamDensityGP.setText("GP Lane Jam Density (" + unitsDensity + "):");
        double jamDensity = myLink.get_jam_density_vpkpl();
        jamDensity = UserSettings.convertDensity(jamDensity, "vpkm", unitsDensity);
        jamDensityManagedSpinnerValueFactory.setValue(jamDensity+1);
        jamDensityManagedSpinnerValueFactory.setValue(jamDensity);
        ((ModifiedDoubleStringConverter)jamDensityManagedSpinnerValueFactory.getConverter()).setDefaultValue(jamDensity);
        labelJamDensityManaged.setText("M Lane Jam Density (" + unitsDensity + "):");
        jamDensity = myLink.get_jam_density_vpkpl(); // FIXME
        jamDensity = UserSettings.convertDensity(ffspeed, "vpkm", unitsDensity);
        jamDensityGPSpinnerValueFactory.setValue(jamDensity+1);
        jamDensityGPSpinnerValueFactory.setValue(jamDensity);
        ((ModifiedDoubleStringConverter)jamDensityGPSpinnerValueFactory.getConverter()).setDefaultValue(jamDensity);
        labelJamDensityAux.setText("Aux Lane Jam Density (" + unitsDensity + "):");
        jamDensity = myLink.get_jam_density_vpkpl(); // FIXME
        jamDensity = UserSettings.convertDensity(jamDensity, "vpkm", unitsDensity);
        jamDensityAuxSpinnerValueFactory.setValue(jamDensity+1);
        jamDensityAuxSpinnerValueFactory.setValue(jamDensity);
        ((ModifiedDoubleStringConverter)jamDensityAuxSpinnerValueFactory.getConverter()).setDefaultValue(jamDensity);
        
        
        
        
        listOnramps.getItems().clear();
        onramps.clear();
        listOfframps.getItems().clear();
        offramps.clear();
        
        if (myLink.get_type() == AbstractLink.Type.freeway) {
            // Populate onramp list
            int num_ramps = myLink.get_segment().num_out_ors();
            for (int i = 0; i < num_ramps; i++) {
                AbstractLink or = myLink.get_segment().out_ors(i);
                listOnramps.getItems().add(or.get_name() + " (outer)");
                onramps.add(or);
            }
            num_ramps = myLink.get_segment().num_in_ors();
            for (int i = 0; i < num_ramps; i++) {
                AbstractLink or = myLink.get_segment().in_ors(i);
                listOnramps.getItems().add(or.get_name() + " (inner)");
                onramps.add(or);
            }
        
            // Populate offramp list
            num_ramps = myLink.get_segment().num_out_frs();
            for (int i = 0; i < num_ramps; i++) {
                AbstractLink fr = myLink.get_segment().out_frs(i);
                listOfframps.getItems().add(fr.get_name() + " (outer)");
                offramps.add(fr);
            }
            num_ramps = myLink.get_segment().num_in_frs();
            for (int i = 0; i < num_ramps; i++) {
                AbstractLink fr = myLink.get_segment().in_frs(i);
                listOfframps.getItems().add(fr.get_name() + " (inner)");
                offramps.add(fr);
            }
        }
        
        upConnectCandidates.clear();
        dnConnectCandidates.clear();
        FreewayScenario scenario = myLink.get_segment().get_scenario();
        List<AbstractLink> allLinks = scenario.get_links();
        for (AbstractLink l : allLinks) {
            if (myLink.get_type() == AbstractLink.Type.freeway) {
                if (l.get_type() == AbstractLink.Type.freeway) {
                    if ((myLink.get_up_link() == null) && 
                        (l.get_dn_link() == null) &&
                        (!myLink.equals(l)))
                        upConnectCandidates.add(l);
                    if ((myLink.get_dn_link() == null) && 
                        (l.get_up_link() == null) &&
                        (!myLink.equals(l)))
                        dnConnectCandidates.add(l);
                }
            } else if (myLink.get_type() == AbstractLink.Type.connector) {
                if (l.get_type() == AbstractLink.Type.onramp) {
                    if ((myLink.get_dn_link() == null) && 
                        (l.get_up_link() == null))
                        dnConnectCandidates.add(l);
                }
                if (l.get_type() == AbstractLink.Type.offramp) {
                    if ((myLink.get_up_link() == null) && 
                        (l.get_dn_link() == null))
                        upConnectCandidates.add(l);
                }
            } else if (myLink.get_type() == AbstractLink.Type.onramp) {
                if (myLink.get_up_link() != null)
                    break;
                if ((l.get_type() == AbstractLink.Type.connector) && (l.get_dn_link() == null))
                    upConnectCandidates.add(l);
            } else { // offramp
                if (myLink.get_dn_link() != null)
                    break;
                if ((l.get_type() == AbstractLink.Type.connector) && (l.get_up_link() == null))
                    dnConnectCandidates.add(l);
            }
        }
        
        if (upConnectCandidates.isEmpty())
            connectSectionUpstream.setVisible(false);
        else
            connectSectionUpstream.setVisible(true);
            
        if (dnConnectCandidates.isEmpty())
            connectSectionDownstream.setVisible(false);
        else
            connectSectionDownstream.setVisible(true);
        
        cbBarrier.setSelected(myLink.get_barrier());
        cbSeparated.setSelected(myLink.get_separated());
        
        if (myLink.get_type() == AbstractLink.Type.freeway) {
             ttDeleteSection.setText("Delete the entire freeway section with all its ramps (if it has any)");
             ttAddSectionDownstream.setText("Create and attach a new freeway section downstream");
             if (myLink.get_dn_link() != null) 
                 ttAddSectionDownstream.setText("Create and insert a new freeway section downstream");
             ttAddSectionUpstream.setText("Create and attach a new freeway section upstream");
             if (myLink.get_up_link() != null) 
                 ttAddSectionDownstream.setText("Create and insert a new freeway section upstream");
             ttConnectDownstream.setText("Connect to an origin (source) freeway section downstream");
             ttConnectUpstream.setText("Connect to a destination (sink) freeway section upstream");
        } else if (myLink.get_type() == AbstractLink.Type.connector) {
             ttDeleteSection.setText("Delete the connector");
             ttAddSectionDownstream.setText("Create and attach a new freeway section with an on-ramp downstream");
             ttAddSectionUpstream.setText("Create and attach a new freeway with an off-ramp section upstream");
             ttConnectDownstream.setText("Connect to an origin (source) on-ramp downstream");
             ttConnectUpstream.setText("Connect to a destination (sink) off-ramp section upstream");
        } else if (myLink.get_type() == AbstractLink.Type.onramp) {
             ttAddSectionUpstream.setText("Create and attach a new connector upstream");
             ttConnectUpstream.setText("Connect to a destination (sink) connector upstream");
        } else { // off-ramp
             ttAddSectionDownstream.setText("Create and attach a new connector downstream");
             ttConnectDownstream.setText("Connect to an origin (source) connector downstream");
        }
        
        
        drawRoadSection();
        
        ignoreChange = false;
    }
    
    



    
    
    /**
     * Draw the road section according to its type, lane configuration and ramps.
     */
    @FXML
    private void drawRoadSection() {
        int ramp_angle = 45;
        double width = linkEditorCanvas.getWidth();
        double height = linkEditorCanvas.getHeight();
        boolean rightSideRoads = UserSettings.rightSideRoads;
        
        int managed_lanes = numLanesManagedSpinnerValueFactory.getValue();
        int gp_lanes = numLanesGPSpinnerValueFactory.getValue();
        int aux_lanes = numLanesAuxSpinnerValueFactory.getValue();
        int total_lanes = gp_lanes + managed_lanes + aux_lanes;
        
        if (managed_lanes > 0) {
            cbBarrier.setDisable(false);
            if (managed_lanes > 1) {
                cbSeparated.setDisable(false);
            } else {
                cbSeparated.setDisable(true);
            }
        } else {
            cbBarrier.setDisable(true);
        }
        
        boolean barrier = cbBarrier.isSelected();
        boolean separated = cbSeparated.isSelected();
        
        
        // Set lane properties to link
        if (!ignoreChange) {
            try {
                myLink.set_managed_lanes(managed_lanes);
                myLink.set_gp_lanes(gp_lanes);
                if (myLink.get_type() == AbstractLink.Type.freeway)
                    myLink.params.set_aux_lanes(aux_lanes);
                myLink.set_barrier(barrier);
                myLink.set_separated(separated);
                appMainController.setProjectModified(true);
            } catch(Exception e) {
                opt.utils.Dialogs.ExceptionDialog("Could not change lane configuration...", e);
            }
        }
        
        
        GraphicsContext g = linkEditorCanvas.getGraphicsContext2D();
        //g.setFill(Color.WHITE);
        g.clearRect(0, 0, width, height);

        double lane_length = 2*width/3;
        double lane_width = height/16;
        if (total_lanes > 8) { // we want the road segment to take about half of the canvas 
            lane_width = Math.sqrt(2)*(height/(2 * total_lanes));
        }
        
        double x0 = width/6;
        double x1 = x0 + lane_length;
        
        int total_ramp_lanes = 0;
        int num_ramps = myLink.get_segment().num_out_ors();
        for (int i = 0; i < num_ramps; i++) {
            int l_count = myLink.get_segment().out_ors(i).get_gp_lanes() +
                          myLink.get_segment().out_ors(i).get_managed_lanes();
            if (l_count > total_ramp_lanes)
                total_ramp_lanes = l_count;
        }
        num_ramps = myLink.get_segment().num_in_ors();
        for (int i = 0; i < num_ramps; i++) {
            int l_count = myLink.get_segment().in_ors(i).get_gp_lanes() +
                          myLink.get_segment().in_ors(i).get_managed_lanes();
            if (l_count > total_ramp_lanes)
                total_ramp_lanes = l_count;
        }
        num_ramps = myLink.get_segment().num_out_frs();
        for (int i = 0; i < num_ramps; i++) {
            int l_count = myLink.get_segment().out_frs(i).get_gp_lanes() +
                          myLink.get_segment().out_frs(i).get_managed_lanes();
            if (l_count > total_ramp_lanes)
                total_ramp_lanes = l_count;
        }
        num_ramps = myLink.get_segment().num_in_frs();
        for (int i = 0; i < num_ramps; i++) {
            int l_count = myLink.get_segment().in_frs(i).get_gp_lanes() +
                          myLink.get_segment().in_frs(i).get_managed_lanes();
            if (l_count > total_ramp_lanes)
                total_ramp_lanes = l_count;
        }
        double coeff = Math.min(1.0, (double)total_lanes/(double)total_ramp_lanes);
        
        
        if (rightSideRoads) { // right-side driving road
            double base_y = 0.75*height;
            double base_y_1 = base_y - total_lanes*lane_width;
            double ramp_length = height - base_y;
            double r_lane_width = coeff*lane_width;
            double y1 = base_y;
            double y0 = y1;
            
            if (myLink.get_type() == AbstractLink.Type.freeway) {
                // Draw outer on-ramps
                num_ramps = myLink.get_segment().num_out_ors();
                double delta = 0.0;
                for (int i = 0; i < num_ramps; i++) {
                    int or_gp = myLink.get_segment().out_ors(i).get_gp_lanes();
                    int or_managed = myLink.get_segment().out_ors(i).get_managed_lanes();
                    boolean or_barrier = myLink.get_segment().out_ors(i).get_barrier();
                    boolean or_separated = myLink.get_segment().out_ors(i).get_separated();
                    double or_lanes = or_gp + or_managed;
                    double or_width = or_lanes * r_lane_width;
                    double or_gp_width = or_gp * r_lane_width;
                    double or_managed_width = or_managed * r_lane_width;
                    if (i > 0)
                        delta += 0.5*or_width;
                    double rotationCenterX = x0 + delta;
                    y0 = base_y + 0.5*or_width - 0.5*or_gp_width;
                    double rotationCenterY = base_y;
                    g.setFill(Color.DARKGREY);
                    g.save();
                    g.translate(rotationCenterX, rotationCenterY);
                    g.rotate(-ramp_angle);
                    g.translate(-rotationCenterX, -rotationCenterY);
                    g.fillRect(x0+delta-ramp_length/2, y0-or_gp_width/2, ramp_length, or_gp_width);

                    y0 -= 0.5*or_width;
                    g.setFill(Color.BLACK);
                    g.fillRect(x0+delta-ramp_length/2, y0-or_managed_width/2, ramp_length, or_managed_width);
                    
                    g.setStroke(Color.WHITE);
                    for (int j = 1; j < or_lanes; j++) {
                        y0 = base_y+or_width/2 - j*r_lane_width;
                        if (or_barrier && (j == or_gp)) {
                            g.setLineDashes();
                            g.setLineWidth(2);
                        } else if (or_separated && (j > or_gp)) {
                            g.setLineDashes();
                            g.setLineWidth(1);
                        } else {
                            g.setLineDashes(r_lane_width/3, r_lane_width/2);
                            g.setLineWidth(1);
                        }
                        g.strokeLine(x0+delta-ramp_length/2, y0, x0+delta+ramp_length/2, y0);
                    }
                    g.restore();
                    delta += 0.5*ramp_length + 0.5*or_width;
                }
                
                // Draw inner on-ramps
                num_ramps = myLink.get_segment().num_in_ors();
                delta = 0.0;
                for (int i = 0; i < num_ramps; i++) {
                    int or_gp = myLink.get_segment().in_ors(i).get_gp_lanes();
                    int or_managed = myLink.get_segment().in_ors(i).get_managed_lanes();
                    boolean or_barrier = myLink.get_segment().in_ors(i).get_barrier();
                    boolean or_separated = myLink.get_segment().in_ors(i).get_separated();
                    double or_lanes = or_gp + or_managed;
                    double or_width = or_lanes * r_lane_width;
                    double or_gp_width = or_gp * r_lane_width;
                    double or_managed_width = or_managed * r_lane_width;
                    if (i > 0)
                        delta += 0.5*or_width;
                    
                    double rotationCenterX = x0 + delta;
                    y0 = base_y_1 + 0.5*or_width - 0.5*or_gp_width;
                    double rotationCenterY = base_y_1;
                    g.setFill(Color.DARKGREY);
                    g.save();
                    g.translate(rotationCenterX, rotationCenterY);
                    g.rotate(ramp_angle);
                    g.translate(-rotationCenterX, -rotationCenterY);
                    g.fillRect(x0+delta-ramp_length/2, y0-or_gp_width/2, ramp_length, or_gp_width);

                    y0 -= 0.5*or_width;
                    g.setFill(Color.BLACK);
                    g.fillRect(x0+delta-ramp_length/2, y0-or_managed_width/2, ramp_length, or_managed_width);
                    
                    g.setStroke(Color.WHITE);
                    for (int j = 1; j < or_lanes; j++) {
                        y0 = base_y_1 + or_width/2 - j*r_lane_width;
                        if (or_barrier && (j == or_gp)) {
                            g.setLineDashes();
                            g.setLineWidth(2);
                        } else if (or_separated && (j > or_gp)) {
                            g.setLineDashes();
                            g.setLineWidth(1);
                        } else {
                            g.setLineDashes(r_lane_width/3, r_lane_width/2);
                            g.setLineWidth(1);
                        }
                        g.strokeLine(x0+delta-ramp_length/2, y0, x0+delta+ramp_length/2, y0);
                    }
                    g.restore();
                    delta += 0.5*ramp_length + 0.5*or_width;
                }



                // Draw outer off-ramps
                num_ramps = myLink.get_segment().num_out_frs();
                delta = 0.0;
                for (int i = num_ramps-1; i >= 0; i--) {
                    int fr_gp = myLink.get_segment().out_frs(i).get_gp_lanes();
                    int fr_managed = myLink.get_segment().out_frs(i).get_managed_lanes();
                    boolean fr_barrier = myLink.get_segment().out_frs(i).get_barrier();
                    boolean fr_separated = myLink.get_segment().out_frs(i).get_separated();
                    double fr_lanes = fr_gp + fr_managed;
                    double fr_width = fr_lanes * r_lane_width;
                    double fr_gp_width = fr_gp * r_lane_width;
                    double fr_managed_width = fr_managed * r_lane_width;
                    if (i < num_ramps-1)
                        delta += 0.5*fr_width;
                    double rotationCenterX = x1 - delta;
                    y0 = base_y + 0.5*fr_width - 0.5*fr_gp_width;
                    double rotationCenterY = base_y;
                    g.setFill(Color.DARKGREY);
                    g.save();
                    g.translate(rotationCenterX, rotationCenterY);
                    g.rotate(ramp_angle);
                    g.translate(-rotationCenterX, -rotationCenterY);
                    g.fillRect(x1-delta-ramp_length/2, y0-fr_gp_width/2, ramp_length, fr_gp_width);
                    
                    y0 -= 0.5*fr_width;
                    g.setFill(Color.BLACK);
                    g.fillRect(x1-delta-ramp_length/2, y0-fr_managed_width/2, ramp_length, fr_managed_width);
                    
                    g.setStroke(Color.WHITE);
                    for (int j = 1; j < fr_lanes; j++) {
                        y0 = base_y + fr_width/2 - j*r_lane_width;
                        if (fr_barrier && (j == fr_gp)) {
                            g.setLineDashes();
                            g.setLineWidth(2);
                        } else if (fr_separated && (j > fr_gp)) {
                            g.setLineDashes();
                            g.setLineWidth(1);
                        } else {
                            g.setLineDashes(r_lane_width/3, r_lane_width/2);
                            g.setLineWidth(1);
                        }
                        g.strokeLine(x1-delta-ramp_length/2, y0, x1-delta+ramp_length/2, y0);
                    }
                    g.restore();
                    delta += 0.5*ramp_length + 0.5*fr_width;
                }
                
                // Draw inner off-ramps
                num_ramps = myLink.get_segment().num_in_frs();
                delta = 0.0;
                for (int i = num_ramps-1; i >= 0; i--) {
                    int fr_gp = myLink.get_segment().in_frs(i).get_gp_lanes();
                    int fr_managed = myLink.get_segment().in_frs(i).get_managed_lanes();
                    boolean fr_barrier = myLink.get_segment().in_frs(i).get_barrier();
                    boolean fr_separated = myLink.get_segment().in_frs(i).get_separated();
                    double fr_lanes = fr_gp + fr_managed;
                    double fr_width = fr_lanes * r_lane_width;
                    double fr_gp_width = fr_gp * r_lane_width;
                    double fr_managed_width = fr_managed * r_lane_width;
                    if (i < num_ramps-1)
                        delta += 0.5*fr_width;
                    double rotationCenterX = x1 - delta;
                    y0 = base_y_1 + 0.5*fr_width - 0.5*fr_gp_width;
                    double rotationCenterY = base_y_1;
                    g.setFill(Color.DARKGREY);
                    g.save();
                    g.translate(rotationCenterX, rotationCenterY);
                    g.rotate(-ramp_angle);
                    g.translate(-rotationCenterX, -rotationCenterY);
                    g.fillRect(x1-delta-ramp_length/2, y0-fr_gp_width/2, ramp_length, fr_gp_width);
                    
                    y0 -= 0.5*fr_width;
                    g.setFill(Color.BLACK);
                    g.fillRect(x1-delta-ramp_length/2, y0-fr_managed_width/2, ramp_length, fr_managed_width);
                    
                    g.setStroke(Color.WHITE);
                    for (int j = 1; j < fr_lanes; j++) {
                        y0 = base_y_1 + fr_width/2 - j*r_lane_width;
                        if (fr_barrier && (j == fr_gp)) {
                            g.setLineDashes();
                            g.setLineWidth(2);
                        } else if (fr_separated && (j > fr_gp)) {
                            g.setLineDashes();
                            g.setLineWidth(1);
                        } else {
                            g.setLineDashes(r_lane_width/3, r_lane_width/2);
                            g.setLineWidth(1);
                        }
                        g.strokeLine(x1-delta-ramp_length/2, y0, x1-delta+ramp_length/2, y0);
                    }
                    g.restore();
                    delta += 0.5*ramp_length + 0.5*fr_width;
                }

               
                
            }
            
            
            
            
            
            
            
            
            
            y0 = y1;
            if (aux_lanes > 0) {
                g.setFill(Color.LIGHTGREY);
                y0 = y1 - aux_lanes*lane_width;
                g.fillRect(x0, y0, lane_length, aux_lanes*lane_width);
                y1 = y0;
            }
            if (gp_lanes > 0) {
                g.setFill(Color.DARKGREY);
                y0 = y1 - gp_lanes*lane_width;
                g.fillRect(x0, y0, lane_length, gp_lanes*lane_width);
                y1 = y0;
            }
            if (managed_lanes > 0) {
                g.setFill(Color.BLACK);
                y0 = y1 - managed_lanes*lane_width;
                g.fillRect(x0, y0, lane_length, managed_lanes*lane_width);
            }
            
            g.setStroke(Color.WHITE);
            g.setLineWidth(1);
            for (int i = 1; i < total_lanes; i++) {
                if (i == aux_lanes) {
                    g.setLineDashes(lane_width/4, lane_width/3);
                    g.setLineWidth(1);
                } else if (barrier && (i == aux_lanes + gp_lanes)) {
                    g.setLineDashes();
                    g.setLineWidth(2);
                } else if (separated && (i > aux_lanes + gp_lanes)) {
                    g.setLineDashes();
                    g.setLineWidth(1);
                } else {
                    g.setLineDashes(lane_width/3, lane_width/2);
                    g.setLineWidth(1);
                }
                    
                y0 = base_y - lane_width*i;
                g.strokeLine(x0, y0, x1, y0);
                
            }
            
            String label = Misc.linkType2String(myLink.get_type());
            g.setStroke(Color.BLACK);
            g.setFill(Color.BLACK);
            g.setLineDashes();
            g.setFont(new Font("Verdana", 18));
            //g.strokeText(label, 0.46*width, 0.2*height);
            g.fillText(label, 0.46*width, 0.2*height);
            
            
            
            
            
            
        
        } else {
            ;//TODO AK left-side driving road
        }
    }





    /************************************************************
     * CALLBACKS
     ************************************************************/
    
    
    @FXML
    private void onMergePriorityChange() {
        if (ignoreChange)
            return;
        double mergePriority = mergePrioritySpinnerValueFactory.getValue();
        //TODO

    }
    
    
    @FXML
    private void onLinkLengthChange() {
        if (ignoreChange)
            return;
        String unitsLength = UserSettings.unitsLength;
        double length = lengthSpinnerValueFactory.getValue();
        ((ModifiedDoubleStringConverter)lengthSpinnerValueFactory.getConverter()).setDefaultValue(length);
        length = UserSettings.convertLength(length, unitsLength, "meters");
        length = Math.max(length, 0.001);
        try {
            myLink.set_length_meters((float)length);
            appMainController.setProjectModified(true);
        } catch(Exception e) {
            opt.utils.Dialogs.ExceptionDialog("Could not change section length...", e);
        }

    }
    
    
    
    private void onNumLanesChange() { 
        if (ignoreChange)
            return;
        drawRoadSection();
        //appMainController.setProjectModified(true);
    }
    
    
    
    private void onParamChange() {    
        
        
        System.err.println("Param change!");
    }
    
    
    
    
}
