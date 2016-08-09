package modgraf.action;

import modgraf.view.Editor;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

/**
 * Created by barbara.kulas on 2016-08-09.
 *
 * @author Barbara Kulas
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
