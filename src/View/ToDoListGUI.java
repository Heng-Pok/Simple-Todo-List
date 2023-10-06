package View;
/**
 * Create a GUI that allows users to create and manage their todo lists.
 * Implementing serialization which allows users to save their todo lists regardless of
 * how many times the application is run.
 * 
 * @Author Hengsocheat Pok
 */

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.*;
import java.util.ArrayList;
import java.util.Optional;

import Model.SerializableArrayList;

public class ToDoListGUI extends Application {
    private VBox mainPane;
    private TextField taskNameEnteringField;
    private Label taskEnteringPrompt;
    private HBox mainPanesButtonArea;
    private Button buttonMovingToTop;
    private Button buttonMovingToBottom;
    private Button buttonForRaise;
    private Button buttonForLowering;
    private Button buttonForRemoving;
    private ListView<String> listView;
    private ObservableList<String> observableListOfTasks;
    private SerializableArrayList serializableArrayList;
    private Button buttonForSavingList = new Button("Save current list");

    @Override
    public void start(Stage stage) throws IOException {
        // set up the main scene
        mainSceneLayoutGUI();
        registerMainSceneHandlers();
        serializableArrayList = new SerializableArrayList();

        // start pop-up
        checkItOutAtStart();

        // add the scene to the stage
        Scene mainScene = new Scene(mainPane, 555, 400);
        stage.setScene(mainScene);
        stage.setTitle("ToDo List");
        stage.show();

        stage.setOnCloseRequest( e -> {
            checkItOutAtEnd();
        });
    }

    private void checkItOutAtEnd() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Do you wish to save your current todo list?");
        if (observableListOfTasks.equals(serializableArrayList.getList()) || serializableArrayList.getList().isEmpty())
            alert.setContentText("Click 'OK' to save before exiting or 'Cancel' to exit without saving");
        else
            alert.setContentText("Click 'OK' to save before exiting (will overwrite your previously saved todo list) or 'Cancel' to exit without saving");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            System.out.println("OK Clicked: your list is saved!");
            // load the state
            serializableArrayList.getList().clear();
            serializableArrayList.getList().addAll(observableListOfTasks);
            saveState(serializableArrayList, "ToDoList.ser");
        }
        else{
            System.out.println("Cancel Clicked: your latest change made to the list was not saved!");
            // do not load any state, clear everything and save it as a new state.
            //serializableArrayList.getList().clear();}
            observableListOfTasks.clear();}
        Platform.exit();
        System.exit(0);
    }

    private void checkItOutAtStart() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("Do you wish to start with a new list or load a previously saved one?");
        alert.setContentText("Click 'Yes' to start with your last saved persistent todo list or 'No' to start with zero todos");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.YES){
            System.out.println("Yes Clicked: starting with a persistent list...");
            // load the state
            loadState(serializableArrayList, "ToDoList.ser");
            observableListOfTasks.addAll(serializableArrayList.getList());
        }
        else{
            System.out.println("No Clicked: starting with a new list...");
            observableListOfTasks.clear();}
    }

    private void mainSceneLayoutGUI() {
        // init the main pane
        mainPane = new VBox(10);
        mainPanesButtonArea = new HBox(20);

        // init the field for entering a task name and its label and add them to the main pane
        taskNameEnteringField = new TextField();
        taskEnteringPrompt = new Label("Enter a new ToDo");
        mainPane.getChildren().addAll(taskEnteringPrompt, taskNameEnteringField);

        // init the buttons for interacting with the list.
        buttonMovingToTop = new Button("Top");
        buttonMovingToTop.setPrefWidth(100);
        buttonMovingToBottom = new Button("Bottom");
        buttonMovingToBottom.setPrefWidth(100);
        buttonForRaise = new Button("Raise");
        buttonForRaise.setPrefWidth(100);
        buttonForLowering = new Button("Lower");
        buttonForLowering.setPrefWidth(100);
        buttonForRemoving = new Button("Remove");
        buttonForRemoving.setPrefWidth(100);

        // add the buttons to the Hbox that will be at the bottom side.
        mainPanesButtonArea.getChildren().addAll(buttonMovingToTop, buttonMovingToBottom, buttonForRaise, buttonForLowering, buttonForRemoving);

        mainPane.getChildren().add(buttonForSavingList);                      // adding the save button to the top area of the main pane.
        observableListOfTasks = FXCollections.observableArrayList();
        listView = new ListView<>(observableListOfTasks);
        mainPane.getChildren().addAll(listView);
        mainPane.setPadding(new Insets(10, 10, 10, 10));
        mainPane.getChildren().addAll(mainPanesButtonArea);                   // adding the button area of the main pane to the main pane
    }

    private void registerMainSceneHandlers() {
        // set an event handler for the button used to save the list.
        buttonForSavingList.setOnAction(pressEvent -> {
            String clickedItem = listView.getSelectionModel().getSelectedItem();
            System.out.println(clickedItem);
        });

        // set an event handler for the button used to 'top' an item.
        buttonMovingToTop.setOnAction(pressEvent -> {
            String clickedItem = listView.getSelectionModel().getSelectedItem();              // get the clicked item
            if (clickedItem != null){
                observableListOfTasks.remove(clickedItem);
                observableListOfTasks.add(0, clickedItem);
                listView.getSelectionModel().selectIndices(0);
            }
        });

        // set an event handler for the button used to 'bottom' an item.
        buttonMovingToBottom.setOnAction(pressEvent -> {
            String clickedItem = listView.getSelectionModel().getSelectedItem();              // get the clicked item
            if (clickedItem != null){
                observableListOfTasks.remove(clickedItem);
                observableListOfTasks.add(observableListOfTasks.size(), clickedItem);
                listView.getSelectionModel().selectLast();
            }
        });

        // set an event handler for the button used to raise an item.
        buttonForRaise.setOnAction(pressEvent -> {
            String clickedItem = listView.getSelectionModel().getSelectedItem();              // get the clicked item
            if (clickedItem != null){
                int selectedIndex = observableListOfTasks.indexOf(clickedItem);
                if (selectedIndex != 0){
                    observableListOfTasks.remove(clickedItem);
                    observableListOfTasks.add(selectedIndex - 1, clickedItem);
                    listView.getSelectionModel().select(selectedIndex - 1);}
            }
        });

        // set an event handler for the button used to lower an item.
        buttonForLowering.setOnAction(pressEvent -> {
            String clickedItem = listView.getSelectionModel().getSelectedItem();              // get the clicked item
            if (clickedItem != null){
                int selectedIndex = observableListOfTasks.indexOf(clickedItem);
                if (selectedIndex != observableListOfTasks.size() - 1){
                    observableListOfTasks.remove(clickedItem);
                    observableListOfTasks.add(selectedIndex + 1, clickedItem);
                    listView.getSelectionModel().select(selectedIndex + 1);}
            }
        });

        // set an event handler for the button used to remove an item.
        buttonForRemoving.setOnAction(pressEvent -> {
            String clickedItem = listView.getSelectionModel().getSelectedItem();              // get the clicked item
            if (clickedItem != null){
                int selectedIndex = observableListOfTasks.indexOf(clickedItem);
                observableListOfTasks.remove(clickedItem);}
        });

        // set an event handler for the text field.
        taskNameEnteringField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER){
                String whatIsEntered = taskNameEnteringField.getText();
                if (whatIsEntered != null){
                    observableListOfTasks.add(whatIsEntered);
                    taskNameEnteringField.clear();
                }
            }
        });

        // set an event handler for the save button
        buttonForSavingList.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            if (!observableListOfTasks.equals(serializableArrayList.getList()) && !serializableArrayList.getList().isEmpty()) {
            	alert.setHeaderText("Do you wish to overwrite your last saved todo list?");
	            Optional<ButtonType> result = alert.showAndWait();
	            if (result.get() == ButtonType.OK){
		            serializableArrayList.getList().clear();
		            serializableArrayList.getList().addAll(observableListOfTasks);
		            saveState(serializableArrayList, "ToDoList.ser");}}
            else {
	            serializableArrayList.getList().clear();
	            serializableArrayList.getList().addAll(observableListOfTasks);
	            saveState(serializableArrayList, "ToDoList.ser");           	
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
    
    // serialize the items in a todo list
    private void saveState(SerializableArrayList serializableArrayList, String filename){
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(serializableArrayList);
            System.out.println("serialized!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // deserialize the items in a todo list
    private void loadState(SerializableArrayList serializableArrayList, String filename){
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            SerializableArrayList loaded = (SerializableArrayList) in.readObject();
            serializableArrayList.getList().addAll(loaded.getList());
            System.out.println("deserialized !");

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}