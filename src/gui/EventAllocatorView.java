package gui;

import planner.*;

import java.io.IOException;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * The view for the event allocator program.
 */
public class EventAllocatorView {

    // the model of the event allocator
    private EventAllocatorModel model;
    
    private VBox root;
    private HBox parent;
    private VBox addRemovePane;
    
    // content panes
    private BorderPane currentAllocationPane;
    private BorderPane currentTrafficPane;
    private BorderPane removeEventPane;
    
    // components with information to retrieve
    private TextField nameField;
    private TextField sizeField;
    private ComboBox<Venue> venueList;
    
    // components to populate with data
    private TableView<EventAllocatorModel> allocationTable;
    //private ListView<Map<Event, String>> allocationTable;
    //private ListView<Traffic> trafficList;
    private ListView<Traffic> trafficList;
    private ListView<Event> eventList;
    
    // warning labels
    private Label warning;
    
    // components that require actions
    private Button saveButton;
    private Button removeButton;
    
    private Venue venue;

    /**
     * Initialises the view for the event allocator program.
     * 
     * @param model
     *            the model of the event allocator
     * @throws IOException 
     */
    public EventAllocatorView(EventAllocatorModel model) 
            throws IOException, FormatException {
        venue = null;
        this.model = model;
        
        root = new VBox();
        parent = new HBox();
        parent.setPadding(new Insets(10, 10, 10, 10));
        
        currentAllocationView();
        currentTrafficView();
        
        addRemovePane = new VBox();
        addEventView();
        removeEventView();
        parent.getChildren().add(addRemovePane);
        
        warning = new Label("");
        warning.setTextFill(Color.RED);
        VBox.setMargin(warning, new Insets(0, 10, 10, 10));
        root.getChildren().addAll(parent, warning);
        
        try {
            updateContent();
        } catch (IOException | FormatException e) {
            throwError(new IOException(
                    "file i/o error or incorrect formatting"));
        }
    }

    /**
     * Returns the scene for the event allocator application.
     * 
     * @return returns the scene for the application
     */
    public Scene getScene() {
        Scene scene = new Scene(root, 1000.0, 600.0);
        return scene;
    }
    
    /**
     * Returns the new event created by user and clears form for next event
     * 
     * @return new event
     * @throws NumberFormatException
     * @throws IllegalArgumentException
     */
    public Event getNewEvent() 
            throws NumberFormatException, IllegalArgumentException {
        String name = "";
        int size = 0;
        
        if (!nameField.getText().equals("")) {
            name = nameField.getText();
            warning.setText("");
        } else {
            throw new IllegalArgumentException("name cannot be empty");
        }
        
        try {
            size = Integer.parseInt(sizeField.getText());
            warning.setText("");
        } catch (NumberFormatException e) {
            throw new NumberFormatException("size not a number");
        }
        
        return new Event(name, size);       
    }
    
    /**
     * Return selected event from event list
     * 
     * @return event
     */
    public Event getSelectedEvent() {
        return eventList.getSelectionModel().getSelectedItem();
    }
    
    /**
     * Returns the venue selected from the combobox
     * 
     * @return venue
     */
    public Venue getSelectedVenue() throws IllegalArgumentException {
        try {
            venue = venueList.getValue();
        } catch (NullPointerException e) {
            throw new NullPointerException("please select a venue");
        }
        return venue;
    }
    
    /**
     * Builds the current allocation view pane and adds it to parent
     */
    @SuppressWarnings("unchecked") // suppress compiler varargs complaint
    private void currentAllocationView() {
        currentAllocationPane = new BorderPane();
        currentAllocationPane.setPadding(new Insets(0, 10, 10, 10));           

        Label allocationTitle = new Label(
                "Current mappings of events to venues");
        currentAllocationPane.setTop(allocationTitle);
        BorderPane.setAlignment(allocationTitle, Pos.CENTER);
        BorderPane.setMargin(
                allocationTitle, new Insets(0, 10, 10, 10));
        
        //allocationTable = new ListView<Map<Event, String>>();
        /**/allocationTable = new TableView<EventAllocatorModel>();
        allocationTable.setEditable(false);
        
        TableColumn<EventAllocatorModel, String> eventCol = 
                new TableColumn<EventAllocatorModel, String>("Events");
        eventCol.setCellValueFactory(new PropertyValueFactory
                <EventAllocatorModel, String>("event"));
        
        TableColumn<EventAllocatorModel, String> venueCol = 
                new TableColumn<EventAllocatorModel, String>("Venues");
        venueCol.setCellValueFactory(new PropertyValueFactory
                <EventAllocatorModel, String>("venue"));
        
        allocationTable.getColumns().addAll(eventCol, venueCol);
        allocationTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY);
        
        currentAllocationPane.setCenter(allocationTable);
        
        parent.getChildren().add(currentAllocationPane);
        HBox.setHgrow(currentAllocationPane, Priority.ALWAYS);
    }
    
    /**
     * Builds current traffic view pane and adds it to parent
     */
    private void currentTrafficView() {
        currentTrafficPane = new BorderPane();
        currentTrafficPane.setPadding(new Insets(0, 10, 10, 10));
        
        Label title = new Label("Corridors with traffic");
        currentTrafficPane.setTop(title);
        BorderPane.setAlignment(title, Pos.CENTER);
        BorderPane.setMargin(title, new Insets(0, 10, 10, 10));

        trafficList = new ListView<Traffic>();
        trafficList.setEditable(false);
        
        currentTrafficPane.setCenter(trafficList);
        
        parent.getChildren().add(currentTrafficPane);
        HBox.setHgrow(currentTrafficPane, Priority.ALWAYS);
    }
    
    /**
     * Builds add event pane and adds it to add/remove event pane
     */
    private void addEventView() {
        GridPane addEventPane = new GridPane();
        addEventPane.setHgap(10);
        addEventPane.setVgap(10);
        ColumnConstraints col0 = new ColumnConstraints(130);
        ColumnConstraints col1 = new ColumnConstraints(200);
        addEventPane.getColumnConstraints().addAll(col0, col1);
        
        // column 1
        Label nameLabel = new Label("Event name:");
        addEventPane.add(nameLabel, 0, 0);
            
        Label sizeLabel = new Label("Event size:");
        addEventPane.add(sizeLabel, 0, 1);
            
        Label venueListLabel = new Label("Allocate to venue:");
        addEventPane.add(venueListLabel, 0, 2);
        
        // column 2
        nameField = new TextField();
        addEventPane.add(nameField, 1, 0);
        
        sizeField = new TextField();
        addEventPane.add(sizeField, 1, 1);
        
        venueList = new ComboBox<Venue>();
        addEventPane.add(venueList, 1, 2);
        
        saveButton = new Button("Save");
        addEventPane.add(saveButton, 1, 3);
        GridPane.setHalignment(saveButton, HPos.RIGHT);
        
        
        addRemovePane.getChildren().add(addEventPane);
    }
    
    /**
     * Builds remove event pane and adds it to add/remove event pane
     */
    private void removeEventView() {
        removeEventPane = new BorderPane();
        removeEventPane.setPadding(new Insets(10, 10, 10, 10));
        
        Label title = new Label("Existing events");
        removeEventPane.setTop(title);
        BorderPane.setAlignment(title, Pos.CENTER);
        BorderPane.setMargin(title, new Insets(10, 10, 10, 10));

        eventList = new ListView<Event>();
        //corridorList.setItems(corridors);
        removeEventPane.setCenter(eventList);
        
        removeButton = new Button("Remove");
        removeEventPane.setBottom(removeButton);
        BorderPane.setMargin(removeButton, new Insets(10, 10, 10, 10));
        BorderPane.setAlignment(removeButton, Pos.BOTTOM_RIGHT);
        //removeEventPaneWarning.setRight(removeButton);
        removeEventPane.setBottom(removeButton);

        addRemovePane.getChildren().add(removeEventPane);
    }

    /**
     * Retrieves data from model and displays it in the corresponding panels
     * 
     * @throws IOException on bad file
     * @throws FormatException on bad file formatting
     * @throws NullPointerException on empty data sets
     * @throws IllegalArgumentException on bad inputs from model
     */
    public void updateContent() throws IOException, FormatException, 
        NullPointerException, IllegalArgumentException {
        try {
            venueList.setItems(model.getVenues());
        } catch (IOException ioe) {
            throwError(new IOException("BAD FILE"));
            disable();
        } catch (FormatException fe) {
            throwError(new FormatException("bad file formatting"));
            disable();
        }
        
        try {
            eventList.setItems(model.getEvents());
        } catch (NullPointerException | IllegalArgumentException e) {
            e.printStackTrace();
            throwError(new IllegalArgumentException("no events"));
        }
        
        try {
            allocationTable.setItems(model.getAllocations());
        } catch(NullPointerException e) {
            throw new NullPointerException("empty allocations");
        }
        try {
            trafficList.setItems(model.getTrafficList());
        } catch(NullPointerException e) {
            throw new NullPointerException("no traffic");
        }
        
    }
    
    /**
     * Takes an exception and sets the warning label
     * @param e
     */
    public void throwError(Exception e) {
        warning.setText(warning.getText() + e + ". ");
        //System.out.println(e);
    }
    
    /**
     * Clears fields and combobox
     */
    public void clear() {
        nameField.setText("");
        sizeField.setText("");
        venueList.getSelectionModel().clearSelection();
        venueList.setValue(null);
    }
    
    /**
     * Disables input if file not found
     */
    public void disable() {
        addRemovePane.setDisable(true);
        currentTrafficPane.setDisable(true);
        currentAllocationPane.setDisable(true);
        warning.setText(warning.getText() + " Please exit and try again" );
    }
    
    /**
     * Add handler to the save button.
     * 
     * @param handler
     *            the handler to be added
     */
    public void saveButtonHandler(EventHandler<MouseEvent> handler) {
        saveButton.setOnMouseClicked(handler);
    }
    
    /**
     * Add handler to the remove button.
     * 
     * @param handler
     *            the handler to be added
     */
    public void removeButtonHandler(EventHandler<MouseEvent> handler) {
        removeButton.setOnMouseClicked(handler);
    }
}

