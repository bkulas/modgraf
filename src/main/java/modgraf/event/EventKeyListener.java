package modgraf.event;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Properties;

import modgraf.view.Editor;
import modgraf.memento.Caretaker;
import modgraf.memento.Originator;

import javax.swing.*;

/**
 * Created by Basia on 16.06.2016.
 *
 * @author Barbara Kulas
 */
public class EventKeyListener extends JFrame implements KeyListener {

    private Editor editor;
    private Properties prop;
    private Originator origin;
    private Caretaker care;

    public EventKeyListener(Editor e)
    {
        editor = e;
        prop = e.getProperties();
        origin = e.getOriginator();
    }

    @Override
    public void keyPressed(KeyEvent kevt){
    }

    @Override
    public void keyReleased(KeyEvent kevt) {

        if ((kevt.getKeyCode() == KeyEvent.VK_ADD)) {
            System.out.println("+");
            editor.getGraphComponent().zoomAndCenter();
            editor.getGraphComponent().zoomIn();
            System.out.println("UP");
        }

        if ((kevt.getKeyCode() == KeyEvent.VK_SUBTRACT)) {
            System.out.println("+");
            editor.getGraphComponent().zoomOut();
            System.out.println("UP");
        }

    }

    @Override
    public void keyTyped(KeyEvent kvt) {
    }


}
