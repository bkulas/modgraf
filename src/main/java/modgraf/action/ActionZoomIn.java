package modgraf.action;

import modgraf.view.Editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Klasa odpowiada za przybliżenie widoku grafu.
 *
 * @author Barbara Kulas
 *
 * @see ActionListener
 */
public class ActionZoomIn implements ActionListener {

    private Editor editor;

    public ActionZoomIn(Editor e)
    {
        editor = e;
    }

    @Override
    public void actionPerformed(ActionEvent event)
    {
        editor.getGraphComponent().zoomIn();
    }
}
