package modgraf.action;

import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
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
import java.util.Map.Entry;

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

        JPanel p = new JPanel();
        Map<String, Vertex> vertices = editor.getVertices();
        Map<String, ModgrafEdge> edges = editor.getEdges();

        if (cell.isVertex())
        {
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
            ModgrafEdge e = edges.get(cell.getId());

            JLabel id = new JLabel("ID: "+cell.getId());
            p.add(id);

            JLabel source = new JLabel("Źródło: ");
            p.add(source);
            JTextField source_field = new JTextField(10);
            source_field.setText(e.getSource().getName());
            source.setLabelFor(source_field);
            p.add(source_field);

            JLabel target = new JLabel("Cel: ");
            p.add(target);
            JTextField target_field = new JTextField(10);
            target_field.setText(e.getTarget().getName());
            target.setLabelFor(target_field);
            p.add(target_field);

            /* zmienimy na pojemność i wagę... z przepływem nie da rady tutaj
            JLabel flow = new JLabel("Przepływ: "+cell.getValue());
            p.add(flow);
            */

            JOptionPane.showMessageDialog(editor.getGraphComponent(), p,
                    lang.getProperty("properties"), JOptionPane.PLAIN_MESSAGE);
            String new_source = source_field.getText();
            String new_target = target_field.getText();

            System.out.println("EdgeId: "+editor.getEdgeId(e.getSource().getId(),e.getTarget().getId()));
            String old_source_id = e.getSource().getId();
            String old_target_id = e.getTarget().getId();
            String new_source_id = old_source_id;
            String new_target_id = old_target_id;

            if(!new_source.equals(e.getSource().getName())) {
                System.out.println("Nie równe " + new_source);
                String key = "-1";
                for (Entry<String, Vertex> entry : vertices.entrySet()) {
                    if (new_source.equals(entry.getValue().getName())) {
                        key = entry.getKey();
                        System.out.println("Znaleziono klucz " + key);
                        new_source_id = key;
                    }
                }

                if (!key.equals("-1")){
                    System.out.println("Zmieniono źródło" + new_source);
                    e.setSource(vertices.get(key));
                    mxGraphModel model = new mxGraphModel(editor.getGraphComponent().getGraph().getModel().getRoot());
                    mxCell ce = (mxCell)model.getCell(cell.getId());
                    System.out.println("Znaleziono celke krawedz " + ce.getId());
                    mxICell cv = (mxICell)model.getCell(key);
                    System.out.println("Znaleziono celke wierzcholek " + cv.getId());
                    ce.setSource(cv);
                }

                else JOptionPane.showMessageDialog(editor.getGraphComponent(), "Nie ma takiego wierzchołka.",
                        lang.getProperty("properties"), JOptionPane.PLAIN_MESSAGE);
            }

            System.out.println("Wpisano source: "+new_source);

            if(!new_target.equals(e.getTarget().getName())) {
                System.out.println("Nie równe " + new_target);
                String key = "-1";
                for (Entry<String, Vertex> entry : vertices.entrySet()) {
                    if (new_target.equals(entry.getValue().getName())) {
                        key = entry.getKey();
                        System.out.println("Znaleziono klucz " + key);
                        new_target_id = key;
                    }
                }

                if (!key.equals("-1")) {
                    System.out.println("Zmieniono cel " + new_target);
                    e.setTarget(vertices.get(key));
                    mxGraphModel model = new mxGraphModel(editor.getGraphComponent().getGraph().getModel().getRoot());
                    mxCell ce = (mxCell)model.getCell(cell.getId());
                    System.out.println("Znaleziono celke krawedz " + ce.getId());
                    mxICell cv = (mxICell)model.getCell(key);
                    System.out.println("Znaleziono celke wierzcholek " + cv.getId());
                    ce.setTarget(cv);
                }
                else JOptionPane.showMessageDialog(editor.getGraphComponent(), "Nie ma takiego wierzchołka.",
                        lang.getProperty("properties"), JOptionPane.PLAIN_MESSAGE);
            }

            System.out.println("Wpisano target: "+new_target);

            editor.removeEdgeId(old_source_id, old_target_id);
            editor.setEdgeId(new_source_id, new_target_id, e.getId());

            //Graph<Vertex, ModgrafEdge> gT = editor.getGraphT();

            editor.getGraphComponent().refresh();
        }
        else {

        }
    }
}
