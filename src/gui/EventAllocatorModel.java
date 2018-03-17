package gui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import planner.*;

/**
 * The model for the event allocator program.
 */
public class EventAllocatorModel {

    // TODO scan folder for other files?
    private ObservableList<Venue> venues;
    private ObservableList<Event> events;
    private Map<Event, Venue> allocations;
    private ObservableList<EventAllocatorModel> allocationTableData;
    private ObservableList<Traffic> trafficTableData;
    private String fileName;
    private String event;
    private String venue;
    //private int trafficGenerated;
    

    /**
     * Initialises the model for the event allocator program.
     * 
     */
    public EventAllocatorModel(){         
        this.fileName = "venues.txt";
        this.events = FXCollections.observableArrayList();
        this.allocations = new HashMap<Event, Venue>();
    }
    
    /**
     * Constructor to allow propertyvaluefactory to retrieve relevant
     * information
     * 
     * @param event
     * @param string
     */
    public EventAllocatorModel(Event event, String string) {
        this.event = event.toString();
        this.venue = string.toString();
    }
    
    /**
     * Returns the current list of events
     * 
     * @return events list
     */
    public ObservableList<Event> getEvents() {
        return events;
    }
    
    /**
     * Builds a list of venues read from the specified file
     * 
     * @throws IOException if there is an error reading the file
     * @throws FormatException if there is an error with formatting found
     * during VenueReader.read runtime
     */
    public ObservableList<Venue> getVenues() 
            throws IOException, FormatException {
        try {
            venues = FXCollections.observableList(VenueReader.read(fileName));
        } catch (IOException | FormatException e) {
            throw e;
        }
        
        return venues;
    }
    
    /**
     * Returns the current instance of allocations with corridors stripped
     * from venues
     * 
     * @return allocationTableData
     */
    public ObservableList<EventAllocatorModel> getAllocations() {
        allocationTableData = FXCollections.observableArrayList();
        for (Event e : allocations.keySet()) {
            EventAllocatorModel map = new EventAllocatorModel(e, 
                    allocations.get(e).getName() + " (" + 
                    allocations.get(e).getCapacity() + ")");
            allocationTableData.add(map);
        }
        //System.out.println(allocationTableData.toString());
        return allocationTableData;
    }

    /**
     * Returns the list of corridors with traffic in them
     * 
     * @return traffic
     */
    public ObservableList<Traffic> getTrafficList() {
        Traffic newTraffic = new Traffic();
        for (Event e : allocations.keySet()) {
            Venue v = allocations.get(e);
            newTraffic.addTraffic(v.getTraffic(e));
        }

        trafficTableData = FXCollections.observableArrayList(newTraffic);
        //System.out.println(trafficTableData);
        return trafficTableData;
    }

    /**
     * @return the event
     */
    public String getEvent() {
        return event;
    }

    /**
     * @return the venue
     */
    public String getVenue() {
        //return venue.getName() + " (" + venue.getCapacity() + ")";
        return venue;
    }

    /**
     * @param event the event to set
     */
    public void setEvent(String event) {
        this.event = event;
    }

    /**
     * @param venue the venue to set
     */
    public void setVenue(String venue) {
        this.venue = venue;
    }

    /**
     *  Adds given event to the events list
     *  
     * @param event
     * @throws IllegalArgumentException 
     */
    public void addEvent(Event event) 
            throws IllegalArgumentException {
        if (!events.contains(event) && !event.equals(events.indexOf(event))) {
            try {
                events.add(event);
            } catch(NullPointerException | IllegalArgumentException e) {
                throw e;
            }
        } else {
            throw new IllegalArgumentException("event already exists");
        }
        
    }
    
    /**
     * Maps the given event and venue together and adds to allocations
     * 
     * @param event
     * @param venue
     * @throws IllegalArgumentException on duplicate venues
     */
    public void addAllocation(Event event, Venue venue) 
            throws IllegalArgumentException {
        for (Event e : allocations.keySet()) {
            if (allocations.get(e).equals(venue)) {
                throw new IllegalArgumentException(
                        "cannot use the same venue twice");
            }
        }
        allocations.put(event, venue);
    }
    
    /**
     * Removes chosen event from events list
     * 
     * @param event
     */
    public void removeEvent(Event event) {
        events.remove(events.indexOf(event));
    }
    
    public void removeAllocation(Event event) {
        Map<Event, Venue> tempMap = new HashMap<Event, Venue>();
        for (Event e : allocations.keySet()) {
            tempMap.put(e, allocations.get(e));
        }
        for (Event e : tempMap.keySet()) {
            if(event.equals(e)) {
                allocations.remove(event);
            }
        }
    }
}
