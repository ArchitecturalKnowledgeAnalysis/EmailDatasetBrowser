package nl.andrewl.emaildatasetbrowser.view;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Implements generalized listener for JTextField and JTextArea components.
 * Allowing you to only listen to the events you're interested in.
 */
public class ConcreteKeyEventListener implements KeyListener {
    private record Listener(int key, int keyMask, Consumer<KeyEvent> event) {
    }

    public enum KeyEventType {
        KEY_TYPED,
        KEY_PRESSED,
        KEY_RELEASED
    }

    private final HashMap<KeyEventType, ArrayList<Listener>> listeners = new HashMap<>();

    public ConcreteKeyEventListener() {
        for (KeyEventType key : KeyEventType.values()) {
            listeners.put(key, new ArrayList<>());
        }
    }

    /**
     * Adds listener event when the key is pressed.
     * 
     * @param eventType type of event that is listened to.
     * @param key       key that is listened for.
     * @param event     event that is invoked when the event is triggered.
     */
    public ConcreteKeyEventListener addKeyListener(KeyEventType eventType, int key, Consumer<KeyEvent> event) {
        return addKeyListener(eventType, key, 0, event);
    }

    /**
     * Adds listener event when the key + mask is pressed.
     * 
     * @param eventType type of event that is listened to.
     * @param key       key that is listened for.
     * @param keyMask   key mask that must be active when listening.
     * @param event     event that is invoked when the event is triggered.
     */
    public ConcreteKeyEventListener addKeyListener(KeyEventType eventType, int key, int keyMask,
            Consumer<KeyEvent> event) {
        listeners.get(eventType).add(new Listener(key, keyMask, event));
        return this;
    }

    private void notify(KeyEvent event, KeyEventType eventType) {
        for (Listener listenerEvent : listeners.get(eventType)) {
            if (event.getKeyCode() == listenerEvent.key
                    && (listenerEvent.keyMask == 0 || event.getModifiersEx() == listenerEvent.keyMask)) {
                listenerEvent.event.accept(event);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        notify(e, KeyEventType.KEY_TYPED);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        notify(e, KeyEventType.KEY_PRESSED);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        notify(e, KeyEventType.KEY_RELEASED);
    }
}
