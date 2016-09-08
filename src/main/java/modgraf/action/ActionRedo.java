package modgraf.action;

import modgraf.view.Editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

/**
 * Klasa odpowiada za wywołanie akcji redo - powtórzenia cofniętej czynności.
 *
 * @author Barbara Kulas
 *
 * @see ActionListener
 */
public class ActionRedo implements ActionListener {

    private Editor editor;
    private Properties lang;
    private Properties prop;

    public ActionRedo(Editor e)
    {
        editor = e;
        lang = editor.getLanguage();
        prop = e.getProperties();
    }

    @Override
    public void actionPerformed(ActionEvent event)
    {
        editor.redo();
    }
}
