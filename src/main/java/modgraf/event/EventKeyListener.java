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
        care = e.getCaretaker();
    }

    @Override
    public void keyPressed(KeyEvent kevt){
    }

    @Override
    public void keyReleased(KeyEvent kevt) {
        //System.out.println("keyReleased");

        if ((kevt.getKeyCode() == KeyEvent.VK_Z) && ((kevt.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            System.out.println("woot! Z");
            origin.undo();
            System.out.println("UNDO");
        }

        if ((kevt.getKeyCode() == KeyEvent.VK_Y) && ((kevt.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            System.out.println("woot! Y");
            origin.redo();
            System.out.println("REDO");
        }
    }

    @Override
    public void keyTyped(KeyEvent kvt) {
    }


}
