package modgraf.action;

import modgraf.view.Editor;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

/**
 * Klasa odpowiada za wywołanie akcji undo - cofnięcia wykonanej czynności.
 *
 * @author Barbara Kulas
 *
 * @see ActionListener
 */
public class ActionUndo implements ActionListener {

    private Editor editor;
    private Properties lang;
    private Properties prop;

    public ActionUndo(Editor e)
    {
        editor = e;
        lang = editor.getLanguage();
        prop = e.getProperties();
    }

    @Override
    public void actionPerformed(ActionEvent event)
    {
        editor.undo();
    }

}
