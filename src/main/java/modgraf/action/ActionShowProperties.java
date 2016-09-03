package modgraf.action;

import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxUtils;
import layout.TableLayout;
import modgraf.jgrapht.edge.ModgrafEdge;
import modgraf.view.Editor;
import modgraf.jgrapht.Vertex;
import modgraf.view.properties.ChangeColorListener;
import modgraf.view.properties.PreferencesTab;
import modgraf.jgrapht.DoubleWeightedGraph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.jgrapht.Graph;
import org.jgrapht.WeightedGraph;

import layout.TableLayout;

import static com.mxgraph.util.mxConstants.*;

/**
 * Created by Basia on 14.07.2016.
 *
 * @author Barbara Kulas
 */
public class ActionShowProperties implements ActionListener
{
    private Editor editor;
    private Properties lang;
    private mxCell cell;
    private PreferencesTab p;
    private Map<String, Vertex> vertices;
    private Map<String, ModgrafEdge> edges;
    private Graph<Vertex, ModgrafEdge> gt;
    private boolean changed = false;

    //private static final long serialVersionUID = -1343713811059005780L;
    private static final double PARAMS_COLUMN_WIDTH = 0.37;
    private static final double SPACE_COLUMN_WIDTH = 0.03;
    private static final double LABEL_COLUMN_WIDTH = 0.6;
    private static final int ROW_COUNT = 13;
    private static final double ROW_HEIGHT = 30;
    public static final int VERTEX_MINIMUM_SIZE = 1;
    public static final int VERTEX_MAXIMUM_SIZE = 1000;
    public static final int BORDER_MINIMUM_WIDTH = 0;
    public static final int BORDER_MAXIMUM_WIDTH = 100;
    private TableLayout tableLayout;
    private JComboBox<String> shape;
    private JFormattedTextField height;
    private JFormattedTextField width;
    private JFormattedTextField borderWidth;
    private ChangeColorListener fillColorListener;
    private ChangeColorListener borderColorListener;
    private JComboBox<Object> fontFamily;
    private JFormattedTextField fontSize;
    private ChangeColorListener fontColorListener;

    public ActionShowProperties(Editor e)
    {
        editor = e;
        lang = editor.getLanguage();
        createLayout();
    }

    protected void createLayout()
    {
        double[] columns = { LABEL_COLUMN_WIDTH, SPACE_COLUMN_WIDTH,
                PARAMS_COLUMN_WIDTH};
        double[] rows = new double[ROW_COUNT];
        for (int i = 0; i < ROW_COUNT; ++i)
            rows[i] = ROW_HEIGHT;
        tableLayout = new TableLayout();
        tableLayout.setColumn(columns);
        tableLayout.setRow(rows);
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
            showProperties();
        }
    }

    private void showProperties()
    {
        cell = (mxCell)editor.getGraphComponent().getGraph().getSelectionCell();

        double size[][] =
                {{0.55, 0.05, 0.4},
                        {30, 30, 30, 30}};
        p = new PreferencesTab(editor, size);
        p.setLayout(tableLayout);
        vertices = editor.getVertices();
        edges = editor.getEdges();
        gt = editor.getGraphT();

        createLabelsColumn();
        createParamsColumn();

        if (cell.isVertex())
        {
            Vertex v = vertices.get(cell.getId());

            //SpringLayout layout = new SpringLayout();
            //p.setLayout(layout);

            JLabel id = new JLabel("ID: "+cell.getId());
            p.add(id, "0 10 r c");

            JLabel name = new JLabel("Nazwa: ");
            p.add(name, "0 11 r c");
            JTextField name_field = new JTextField(10);
            name_field.setText(v.getName());
            name.setLabelFor(name_field);
            p.add(name_field, "2 11 l c");



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

            JLabel id = new JLabel("ID: "+e.getId());
            p.add(id, "0 10 r c");

            JLabel source = new JLabel("Źródło: ");
            p.add(source, "0 11 r c");
            JTextField source_field = new JTextField(10);
            source_field.setText(e.getSource().getName());
            source.setLabelFor(source_field);
            p.add(source_field, "2 11 l c");

            JLabel target = new JLabel("Cel: ");
            p.add(target, "0 12 r c");
            JTextField target_field = new JTextField(10);
            target_field.setText(e.getTarget().getName());
            target.setLabelFor(target_field);
            p.add(target_field, "2 12 l c");

            JOptionPane.showMessageDialog(editor.getGraphComponent(), p,
                    lang.getProperty("properties"), JOptionPane.PLAIN_MESSAGE);
            String new_source = source_field.getText();
            String new_target = target_field.getText();

            System.out.println("EdgeId: "+editor.getEdgeId(e.getSource().getId(),e.getTarget().getId()));
            String old_source_id = e.getSource().getId();
            String old_target_id = e.getTarget().getId();
            String new_source_id = old_source_id;
            String new_target_id = old_target_id;
            String e_id = e.getId();

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
                    gt.removeEdge(e);
                    editor.removeEdgeId(old_source_id, old_target_id);

                    System.out.println("Zmieniono źródło" + new_source);
                    e.setSource(vertices.get(key));
                    mxGraphModel model = new mxGraphModel(editor.getGraphComponent().getGraph().getModel().getRoot());
                    mxCell ce = (mxCell)model.getCell(cell.getId());
                    System.out.println("Znaleziono celke krawedz " + ce.getId());
                    mxICell cv = (mxICell)model.getCell(key);
                    System.out.println("Znaleziono celke wierzcholek " + cv.getId());
                    cell.setSource(cv);

                    Vertex v_source = vertices.get(ce.getSource().getId());
                    Vertex v_target = vertices.get(ce.getTarget().getId());

                    e.setId(e_id);
                    editor.setEdgeId(new_source_id, new_target_id, e_id);
                    ModgrafEdge ed = gt.addEdge(v_source, v_target);
                    ed.setId(e_id);
                    gt.getEdgeFactory().createEdge(v_source, v_target);
                    editor.getEdges().put(e_id, ed);

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
                    gt.removeEdge(e);
                    editor.removeEdgeId(old_source_id, old_target_id);

                    System.out.println("Zmieniono cel " + new_target);
                    e.setTarget(vertices.get(key));
                    mxGraphModel model = new mxGraphModel(editor.getGraphComponent().getGraph().getModel().getRoot());
                    mxCell ce = (mxCell)model.getCell(cell.getId());
                    System.out.println("Znaleziono celke krawedz " + ce.getId());
                    mxICell cv = (mxICell)model.getCell(key);
                    System.out.println("Znaleziono celke wierzcholek " + cv.getId());
                    cell.setTarget(cv);

                    Vertex v_source = vertices.get(ce.getSource().getId());
                    Vertex v_target = vertices.get(ce.getTarget().getId());

                    e.setId(e_id);
                    editor.setEdgeId(new_source_id, new_target_id, e_id);
                    ModgrafEdge ed = gt.addEdge(v_source, v_target);
                    ed.setId(e_id);
                    gt.getEdgeFactory().createEdge(v_source, v_target);
                    editor.getEdges().put(e_id, ed);
                }
                else JOptionPane.showMessageDialog(editor.getGraphComponent(), "Nie ma takiego wierzchołka.",
                        lang.getProperty("properties"), JOptionPane.PLAIN_MESSAGE);
            }

            System.out.println("Wpisano target: "+new_target);

            editor.removeEdgeId(old_source_id, old_target_id);
            editor.setEdgeId(new_source_id, new_target_id, e.getId());



        }
        else {

        }

        saveChanges();
        if(changed) editor.saveState(lang.getProperty("memento-change"));
    }

    private void createLabelsColumn(){

        p.add(new JLabel(lang.getProperty("prop-id")), "0 0 r c"); //id
        p.add(new JLabel(lang.getProperty("prop-border-color")), "0 1 r c"); //Kolor obramowania
        p.add(new JLabel(lang.getProperty("prop-border-width")), "0 2 r c"); //grubosc obramowania
        p.add(new JLabel(lang.getProperty("prop-font-family")), "0 3 r c"); //czcionka
        p.add(new JLabel(lang.getProperty("prop-font-size")), "0 4 r c"); //rozmiar czcionki
        p.add(new JLabel(lang.getProperty("prop-font-color")), "0 5 r c"); //kolor czcionki

        if(cell.isVertex()){
            p.add(new JLabel(lang.getProperty("prop-vertex-shape")), "0 6 r c"); //kształt
            p.add(new JLabel(lang.getProperty("prop-vertex-height")), "0 7 r c"); //wysokosc
            p.add(new JLabel(lang.getProperty("prop-vertex-width")), "0 8 r c"); //szerokosc
            p.add(new JLabel(lang.getProperty("prop-vertex-fill-color")), "0 9 r c"); //Kolor wypełnienia

        }
        else if(cell.isEdge()){
            p.add(new JLabel(lang.getProperty("prop-edge-source")), "0 6 r c"); //źródło
            p.add(new JLabel(lang.getProperty("prop-edge-target")), "0 7 r c"); //cel
            if(gt instanceof WeightedGraph){
                p.add(new JLabel(lang.getProperty("prop-edge-weight")), "0 8 r c"); //waga

            } else if(gt instanceof DoubleWeightedGraph) {
                p.add(new JLabel(lang.getProperty("prop-edge-capacity")), "0 8 r c"); //przepustowosc
                p.add(new JLabel(lang.getProperty("prop-edge-cost")), "0 9 r c"); //koszt
            }
        }
    }

    private void createParamsColumn(){

        p.add(new JLabel(cell.getId()), "2 0 l c"); //id
        p.add(createBorderColorChooser(), "2 1 l c"); //Kolor obramowania
//        p.add(createBorderWidthField(), "2 2 l c"); //grubosc obramowania
//        p.add(createFontFamilyField(), "2 3 l c"); //czcionka
//        p.add(createFontSizeField(), "2 4 l c"); //rozmiar czcionki
        p.add(createFontColorChooser(), "2 5 l c"); //kolor czcionki

        if(cell.isVertex()){
            p.add(createShapeComboBox(), "2 6 l c"); //kształt
            p.add(createHeightField(), "2 7 l c"); //wysokosc
            p.add(createWidthField(), "2 8 l c"); //szerokosc
            p.add(createFillColorChooser(), "2 9 l c"); //Kolor wypełnienia

        }
        else if(cell.isEdge()){
//            p.add(new JLabel(lang.getProperty("prop-edge-source")), "2 6 l c"); //źródło
//            p.add(new JLabel(lang.getProperty("prop-edge-target")), "2 7 l c"); //cel
            if(gt instanceof WeightedGraph){
//                p.add(new JLabel(lang.getProperty("prop-edge-weight")), "2 8 l c"); //waga

            } else if(gt instanceof DoubleWeightedGraph) {
//                p.add(new JLabel(lang.getProperty("prop-edge-capacity")), "2 8 l c"); //przepustowosc
//                p.add(new JLabel(lang.getProperty("prop-edge-cost")), "2 9 l c"); //koszt
            }
        }

    }

    private JComponent createBorderColorChooser(){
        Color borderColor = mxUtils.parseColor((String)editor.getGraphComponent().getGraph().getCellStyle(cell).get(STYLE_STROKECOLOR));
        borderColorListener = new ChangeColorListener(editor, borderColor);
        return p.createColorChooser(borderColor, borderColorListener);
    }

    private JComponent createFontColorChooser(){
        Color fontColor = mxUtils.parseColor((String)editor.getGraphComponent().getGraph().getCellStyle(cell).get(STYLE_FONTCOLOR));
        fontColorListener = new ChangeColorListener(editor, fontColor);
        return p.createColorChooser(fontColor, fontColorListener);
    }

    private JComponent createShapeComboBox()
    {
        Vector<String> vector = new Vector<>(8);
        vector.add(lang.getProperty("menu-vertex-shape-circle"));
        vector.add(lang.getProperty("menu-vertex-shape-square"));
        vector.add(lang.getProperty("menu-vertex-shape-rhombus"));
        vector.add(lang.getProperty("menu-vertex-shape-cloud"));
        vector.add(lang.getProperty("menu-vertex-shape-hexagon"));
        shape = new JComboBox<>(vector);
        shape.setSelectedIndex(shapeIndex((String)editor.getGraphComponent().getGraph().getCellStyle(cell).get(STYLE_SHAPE)));
        return shape;
    }

    private int shapeIndex(String name)
    {
        switch (name)
        {
            case "ellipse": return 0;
            case "rectangle": return 1;
            case "rhombus": return 2;
            case "cloud": return 3;
            case "hexagon": return 4;
            case "triangle": return 5;
            case "actor": return 6;
            case "cylinder": return 7;
            default: return 0;
        }
    }

    private String getSelectedShape()
    {
        switch (shape.getSelectedIndex())
        {
            case 0: return SHAPE_ELLIPSE;
            case 1: return SHAPE_RECTANGLE;
            case 2: return SHAPE_RHOMBUS;
            case 3: return SHAPE_CLOUD;
            case 4: return SHAPE_HEXAGON;
            case 5: return SHAPE_TRIANGLE;
            case 6: return SHAPE_ACTOR;
            case 7: return SHAPE_CYLINDER;
            default: return SHAPE_ELLIPSE;
        }
    }

    private JComponent createHeightField(){
        height = new JFormattedTextField(
                p.createNumberFormatter(VERTEX_MINIMUM_SIZE, VERTEX_MAXIMUM_SIZE));
        height.setColumns(p.FIELD_SIZE);
        height.setToolTipText(p.createHint(VERTEX_MINIMUM_SIZE, VERTEX_MAXIMUM_SIZE));
        height.setValue(cell.getGeometry().getHeight());
        return height;
    }

    private JComponent createWidthField(){
        width = new JFormattedTextField(
                p.createNumberFormatter(VERTEX_MINIMUM_SIZE, VERTEX_MAXIMUM_SIZE));
        width.setColumns(p.FIELD_SIZE);
        width.setToolTipText(p.createHint(VERTEX_MINIMUM_SIZE, VERTEX_MAXIMUM_SIZE));
        width.setValue(cell.getGeometry().getWidth());
        return width;
    }

    private JComponent createFillColorChooser()
    {
        Color fillColor = mxUtils.parseColor((String)editor.getGraphComponent().getGraph().getCellStyle(cell).get(STYLE_FILLCOLOR));
        System.out.println(editor.getGraphComponent().getGraph().getCellStyle(cell).get(STYLE_FILLCOLOR));
        fillColorListener = new ChangeColorListener(editor, fillColor);
        return p.createColorChooser(fillColor, fillColorListener);
    }

    private void saveChanges(){

        //bordercolor
        System.out.println(editor.getGraphComponent().getGraph().getCellStyle(cell).get(STYLE_STROKECOLOR));
        System.out.println(mxUtils.hexString(borderColorListener.getColor()));
        if (!mxUtils.hexString(borderColorListener.getColor()).equals(editor.getGraphComponent().getGraph().getCellStyle(cell).get(STYLE_STROKECOLOR))) {
            editor.getGraphComponent().getGraph().setCellStyles(STYLE_STROKECOLOR,mxUtils.hexString(borderColorListener.getColor()));
            changed = true;
        }

        //fontcolor
        if (!mxUtils.hexString(fontColorListener.getColor()).equals(editor.getGraphComponent().getGraph().getCellStyle(cell).get(STYLE_FONTCOLOR))) {
            editor.getGraphComponent().getGraph().setCellStyles(STYLE_FONTCOLOR,mxUtils.hexString(fontColorListener.getColor()));
            changed = true;
        }

        if(cell.isVertex()) {



            //shape
            if (!getSelectedShape().equals(editor.getGraphComponent().getGraph().getCellStyle(cell).get(STYLE_SHAPE))) {
                System.out.println(getSelectedShape());
                ActionSetShape sh = new ActionSetShape(editor, getSelectedShape());
                sh.setStyle(getSelectedShape());
                changed = true;
            }

            //height
            if(!height.getValue().equals(cell.getGeometry().getHeight())){
                System.out.println(height.getValue());
                int h = (int)height.getValue();
                if(h!=cell.getGeometry().getHeight()){
                    cell.getGeometry().setHeight((double) h);
                    changed = true;
                }
            }

            //width
            if(!width.getValue().equals(cell.getGeometry().getWidth())){
                System.out.println(width.getValue());
                int v = (int)width.getValue();
                if(v!=cell.getGeometry().getWidth()){
                    cell.getGeometry().setWidth((double) v);
                    changed = true;
                }
            }

            //fillcolor
            if (!mxUtils.hexString(fillColorListener.getColor()).equals(editor.getGraphComponent().getGraph().getCellStyle(cell).get(STYLE_FILLCOLOR))) {
                editor.getGraphComponent().getGraph().setCellStyles(STYLE_FILLCOLOR,mxUtils.hexString(fillColorListener.getColor()));
                changed = true;
            }
        }

        editor.getGraphComponent().refresh();

    }
}
