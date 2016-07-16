package modgraf.action;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import modgraf.event.EventLabelChangedListener;
import modgraf.jgrapht.edge.ModgrafEdge;
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

        JPanel p = new JPanel();

        //kombinowania TO CHANGE
        if (cell.isVertex())
        {
            Map<String, Vertex> vertices = editor.getVertices();

            Vertex v = vertices.get(cell.getId());

            //SpringLayout layout = new SpringLayout();

            //p.setLayout(layout);

            JLabel id = new JLabel("ID: "+cell.getId());
            p.add(id);

            JLabel name = new JLabel("Nazwa: ");
            p.add(name);
            JTextField name_field = new JTextField(10);
            name_field.setText(v.getName());
            name.setLabelFor(name_field);
            p.add(name_field);



            JOptionPane.showMessageDialog(editor.getGraphComponent(), p,
                    lang.getProperty("properties"), JOptionPane.PLAIN_MESSAGE);
            String new_name = name_field.getText();

            if(!new_name.equals(v.getName())) {
                System.out.println("Zmieniono nazwę");
                cell.setValue(new_name);
                v.setName(new_name);
            }

            System.out.println("new: "+new_name);

            editor.getGraphComponent().refresh();

        }
        if (cell.isEdge())
        {
            Map<String, ModgrafEdge> edges = editor.getEdges();

            ModgrafEdge e = edges.get(cell.getId());

            JLabel id = new JLabel("ID: "+cell.getId());
            p.add(id);

            JLabel source = new JLabel("Źródło: ");
            p.add(source);
            JTextField source_field = new JTextField(10);
            source_field.setText(cell.getSource().getId());
            source.setLabelFor(source_field);
            p.add(source_field);

            JLabel target = new JLabel("Cel: ");
            p.add(target);
            JTextField target_field = new JTextField(10);
            target_field.setText(cell.getTarget().getId());
            target.setLabelFor(target_field);
            p.add(target_field);

            JLabel flow = new JLabel("Przepływ: "+cell.getValue());
            p.add(flow);

            JOptionPane.showMessageDialog(editor.getGraphComponent(), p,
                    lang.getProperty("properties"), JOptionPane.PLAIN_MESSAGE);
            String new_source = source_field.getText();
            String new_target = target_field.getText();

            //nie działa jeszcze
            if(!new_source.equals(e.getSource().getName())) {
                System.out.println("Nie równe " + new_source);
                String key = "-1";
                for (Map.Entry<String, ModgrafEdge> entry : edges.entrySet()) {
                    if (new_source.equals(entry.getValue())) {
                        key = entry.getKey();
                        System.out.println("Znaleziono klucz " + key);

                    }
                }

                if (edges.get(key) != null)
                    System.out.println("Zmieniono źródło" + new_source);
                //cell.setValue(new_name);
                //e.setName(new_name);
            }

            System.out.println("source: "+new_source);

            editor.getGraphComponent().refresh();
        }
        else {
            message = lang.getProperty("message-new-edge-name");
            title = lang.getProperty("frame-change-weight");
        }
    }
}
