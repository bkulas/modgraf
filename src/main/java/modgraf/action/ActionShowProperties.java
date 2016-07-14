package modgraf.action;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import modgraf.event.EventLabelChangedListener;
import modgraf.view.Editor;
import modgraf.jgrapht.Vertex;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;
import java.util.Map;

/**
 * Created by Basia on 14.07.2016.
 *
 * @author Barbara Kulas
 */
public class ActionShowProperties implements ActionListener
{
    private Editor editor;
    private Properties lang;

    public ActionShowProperties(Editor e)
    {
        editor = e;
        lang = editor.getLanguage();
    }

    /**
     * Metoda zmienia nazwę wierzchołka lub wagę krawędzi.<br>
     * Metoda jest wywoływana z menu kontekstowego <i>Zmień nazwę</i>.
     */
    @Override
    public void actionPerformed(ActionEvent arg0)
    {
        int selectionCount = editor.getGraphComponent().getGraph().getSelectionCount();
        if (selectionCount != 1)
        {
            JOptionPane.showMessageDialog(editor.getGraphComponent(),
                    lang.getProperty("warning-only-one-vertex"),
                    lang.getProperty("warning"), JOptionPane.WARNING_MESSAGE);
        }
        else
        {
           /* mxCell cell = (mxCell)editor.getGraphComponent().getGraph().getSelectionCell();
            String message = null;
            String title = null;
            if (cell.isVertex())
            {
                message = lang.getProperty("message-new-vertex-name");
                title = lang.getProperty("frame-change-name");
            }
            else {
                message = lang.getProperty("message-new-edge-name");
                title = lang.getProperty("frame-change-weight");
            }*/
            showProperties();
        }
    }

    private void showProperties()
    {
        mxCell cell = (mxCell)editor.getGraphComponent().getGraph().getSelectionCell();
        String message = null;
        String title = null;


        //kombinowania TO CHANGE
        if (cell.isVertex())
        {
            Map<String, Vertex> vertices = editor.getVertices();

            Vertex v = vertices.get(cell.getId());
            String[] labels = {"ID: "+cell.getId(), "Nazwa: ", "Email: ", "Address: "};
            int numPairs = labels.length;

            JPanel p = new JPanel();

//        for (int i = 0; i < numPairs; i++) {
            JLabel l = new JLabel(labels[0]);
            p.add(l);
            JTextField textField = new JTextField(10);
            l.setLabelFor(textField);
            //textField.setText(cell.getId());
            p.add(textField);

            JLabel l2 = new JLabel(labels[1]);
            p.add(l2);
            JTextField textField2 = new JTextField(10);
            textField2.setText(v.getName());
            l.setLabelFor(textField2);

            p.add(textField2);
            //       }



            String value = (String) JOptionPane.showInputDialog(editor.getGraphComponent(), p,
                    lang.getProperty("properties"), JOptionPane.PLAIN_MESSAGE, null, null, cell.getValue());
            String value1 = textField.getText();
            String value2 = textField2.getText();
            cell.setValue(value2);
            v.setName(value2);

            System.out.println(value+" "+value1+" "+value2);

            editor.getGraphComponent().refresh();

            //cell.setId(value1);
        }
        else {
            message = lang.getProperty("message-new-edge-name");
            title = lang.getProperty("frame-change-weight");
        }
    }
}
