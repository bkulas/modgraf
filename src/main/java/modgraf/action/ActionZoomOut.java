package modgraf.action;

import modgraf.view.Editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Klasa odpowiada za oddalenie widoku grafu.
 *
 * @author Barbara Kulas
 *
 * @see ActionListener
 */
public class ActionZoomOut implements ActionListener {

    private Editor editor;

    public ActionZoomOut(Editor e)
    {
        editor = e;
    }

    @Override
    public void actionPerformed(ActionEvent event)
    {
        editor.getGraphComponent().zoomOut();
    }
}
