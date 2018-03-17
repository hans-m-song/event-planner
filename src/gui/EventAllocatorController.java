package gui;

import java.io.IOException;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import planner.*;

/**
 * The controller for the event allocator program.
 */
public class EventAllocatorController {

    // the model of the event allocator
    private EventAllocatorModel model;
    // the view of the event allocator
    private EventAllocatorView view;

    private Event event;
    private Venue venue;

    /**
     * Initialises the controller for the event allocator program.
     * 
     * @param model
     *            the model of the event allocator
     * @param view
     *            the view of the event allocator
     */
    public EventAllocatorController(EventAllocatorModel model,
            EventAllocatorView view) {
        this.model = model;
        this.view = view;
        event = null;
        venue = null;
        view.saveButtonHandler(new saveBtnActionHandler());
        view.removeButtonHandler(new removeBtnActionHandler());
        
    }

    /**
     * EventHandler class for the save button.
     * retrieves the new event and selected venue and adds it to the set 
     * of allocations
     */
    private class saveBtnActionHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent onMouseClick) {
            try {
                event = view.getNewEvent();
                venue = view.getSelectedVenue();
            } catch (IllegalArgumentException | NullPointerException e) {
                view.throwError(e);
            }
            
            try {
                if (event.getSize() < venue.getCapacity()) {
                    model.addAllocation(event, venue);
                    model.addEvent(event);
                } else {
                    view.throwError(new IllegalArgumentException(
                            "event size too big for venue"));
                }
            } catch (NullPointerException | IllegalArgumentException e) {
                view.throwError(e);
            }
           
            view.clear();
            try {
                view.updateContent();
            } catch (IOException | FormatException | NullPointerException e) {
                view.throwError(e);
            }
            view.clear();
        }
    }
    
    /**
     * EventHandler class for the remove button
     */
    private class removeBtnActionHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent onMouseClick) {
            event = view.getSelectedEvent();
            model.removeEvent(event);
            model.removeAllocation(event);
            try {
                view.updateContent();
            } catch (NullPointerException | IllegalArgumentException | 
                     IOException | FormatException e) {
                view.throwError(e);
            }
        }
    }

}
