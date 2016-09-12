package modgraf.action;

import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;
import layout.TableLayout;
import modgraf.jgrapht.edge.ModgrafEdge;
import modgraf.view.Editor;
import modgraf.jgrapht.Vertex;
import modgraf.view.properties.ChangeColorListener;
import modgraf.view.properties.PreferencesTab;
import modgraf.jgrapht.*;
import modgraf.jgrapht.edge.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.Map.Entry;

import org.jgrapht.Graph;
import org.jgrapht.WeightedGraph;

import static com.mxgraph.util.mxConstants.*;

/**
 * Klasa wyświetla okno z parametrami zaznaczonych elementów grafu.
 *
 * @author Barbara Kulas
 *
 * @see ActionListener
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
    private boolean different_cells = false;
    private boolean edge_cells = false;
    private boolean vertex_cells = false;

    private static final double PARAMS_COLUMN_WIDTH = 0.37;
    private static final double SPACE_COLUMN_WIDTH = 0.03;
    private static final double LABEL_COLUMN_WIDTH = 0.6;
    private static final int ROW_COUNT = 11;
    private static final double ROW_HEIGHT = 30;
    public static final int VERTEX_MINIMUM_SIZE = 1;
    public static final int VERTEX_MAXIMUM_SIZE = 1000;
    public static final int BORDER_MINIMUM_WIDTH = 0;
    public static final int BORDER_MAXIMUM_WIDTH = 100;
    private static final int FRAME_HEIGHT = 400;
    private static final int FRAME_WIDTH = 400;
    private JFrame frame;
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
    private JTextField name;
    private JTextField source;
    private JTextField target;
    private JLabel name_label;
    private JFormattedTextField edgeWeight;
    private JFormattedTextField edgeCapacity;
    private JFormattedTextField edgeCost;
    private boolean multi=false;

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
     * Metoda sprawdza ile i jakie obiekty są zaznaczone i wyświetla okno z odpowiednimi parametrami.
     */
    @Override
    public void actionPerformed(ActionEvent arg0)
    {
        mxGraph graph = editor.getGraphComponent().getGraph();

        int selectionCount = editor.getGraphComponent().getGraph().getSelectionCount();
        if (selectionCount != 1)
        {
            mxCell first = (mxCell)graph.getSelectionCell();
            if(first.isVertex()) {
                for (Object object : graph.getSelectionCells()) {
                    if (object instanceof mxCell) {
                        mxCell cell = (mxCell) object;
                        if (cell.isEdge()) {
                            different_cells = true;
                            break;
                        }
                    }
                }
                vertex_cells = true;
            }
            else {
                for (Object object : graph.getSelectionCells()) {
                    if (object instanceof mxCell) {
                        mxCell cell = (mxCell) object;
                        if (cell.isVertex()) {
                            different_cells = true;
                            break;
                        }
                    }
                }
                edge_cells = true;
            }
  //          System.out.println("Selection cells: "+different_cells+" "+vertex_cells+" "+edge_cells);
            multi = true;
            showProperties();
        }
        else
        {
            multi = false;
            showProperties();
        }

    }

    /**
     * Metoda ustawia parametry okna.
     */
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

        JPanel buttonPanel = createButtonPanel();
        frame = new JFrame(lang.getProperty("properties"));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        frame.add(p, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationRelativeTo(editor.getGraphComponent());
        frame.setVisible(true);

    }

    private JPanel createButtonPanel()
    {
        JPanel buttonPanel = new JPanel();
        JButton save = new JButton(lang.getProperty("button-save"));
        save.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                saveChanges();
                frame.dispose();
            }
        });
        JButton cancel = new JButton(lang.getProperty("button-cancel"));
        cancel.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                frame.dispose();
            }
        });
        buttonPanel.add(save);
        buttonPanel.add(cancel);
        return buttonPanel;
    }

    /**
     * Metoda tworzy etykiety pól właściwości.
     */
    private void createLabelsColumn(){

        if(!multi) p.add(new JLabel(lang.getProperty("prop-id")), "0 0 r c"); //id
        p.add(new JLabel(lang.getProperty("prop-border-color")), "0 1 r c"); //Kolor obramowania
        p.add(new JLabel(lang.getProperty("prop-border-width")), "0 2 r c"); //grubosc obramowania
        p.add(new JLabel(lang.getProperty("prop-font-family")), "0 3 r c"); //czcionka
        p.add(new JLabel(lang.getProperty("prop-font-size")), "0 4 r c"); //rozmiar czcionki
        p.add(new JLabel(lang.getProperty("prop-font-color")), "0 5 r c"); //kolor czcionki

        if((!multi && cell.isVertex()) || (!different_cells && vertex_cells)){
            p.add(new JLabel(lang.getProperty("prop-vertex-shape")), "0 6 r c"); //kształt
            p.add(new JLabel(lang.getProperty("prop-vertex-height")), "0 7 r c"); //wysokosc
            p.add(new JLabel(lang.getProperty("prop-vertex-width")), "0 8 r c"); //szerokosc
            p.add(new JLabel(lang.getProperty("prop-vertex-fill-color")), "0 9 r c"); //Kolor wypełnienia
            if(!multi) p.add(new JLabel(lang.getProperty("prop-vertex-name")), "0 10 r c"); //Nazwa

        }
        else if((!multi && cell.isEdge()) || (!different_cells && edge_cells)){
            if(!multi) p.add(new JLabel(lang.getProperty("prop-edge-source")), "0 6 r c"); //źródło
            if(!multi) p.add(new JLabel(lang.getProperty("prop-edge-target")), "0 7 r c"); //cel
            if(gt instanceof WeightedGraph){
                p.add(new JLabel(lang.getProperty("prop-edge-weight")), "0 8 r c"); //waga

            } else if(gt instanceof DoubleWeightedGraph) {
                p.add(new JLabel(lang.getProperty("prop-edge-capacity")), "0 8 r c"); //przepustowosc
                p.add(new JLabel(lang.getProperty("prop-edge-cost")), "0 9 r c"); //koszt
            }
        }
    }

    /**
     * Metoda tworzy pola do zmiany wartości właściwości.
     */
    private void createParamsColumn(){

        if(!multi) p.add(new JLabel(cell.getId()), "2 0 l c"); //id
        p.add(createBorderColorChooser(), "2 1 l c"); //Kolor obramowania
        p.add(createBorderWidthField(), "2 2 l c"); //grubosc obramowania
        p.add(createFontFamilyField(), "2 3 l c"); //czcionka
        p.add(createFontSizeField(), "2 4 l c"); //rozmiar czcionki
        p.add(createFontColorChooser(), "2 5 l c"); //kolor czcionki

        if((!multi && cell.isVertex()) || (!different_cells && vertex_cells)){
            p.add(createShapeComboBox(), "2 6 l c"); //kształt
            p.add(createHeightField(), "2 7 l c"); //wysokosc
            p.add(createWidthField(), "2 8 l c"); //szerokosc
            p.add(createFillColorChooser(), "2 9 l c"); //Kolor wypełnienia
            if(!multi) p.add(createNameField(), "2 10 l c"); //Nazwa

        }
        else if((!multi && cell.isEdge()) || (!different_cells && edge_cells)){
            if(!multi) p.add(createSourceField(), "2 6 l c"); //źródło
            if(!multi) p.add(createTargetField(), "2 7 l c"); //cel
            if(gt instanceof WeightedGraph){
                p.add(createEdgeWeightField(), "2 8 l c"); //waga

            } else if(gt instanceof DoubleWeightedGraph) {
                p.add(createCapacityField(), "2 8 l c"); //przepustowosc
                p.add(createCostField(), "2 9 l c"); //koszt
            }
        }

    }

    private boolean checkDifferent(String name){
        if(multi){
            mxGraph graph = editor.getGraphComponent().getGraph();
            mxCell first = (mxCell)graph.getSelectionCell();
            String w = (String)graph.getCellStyle(first).get(name);
            if(w != null) {
                for (Object object : graph.getSelectionCells()) {
                    if (object instanceof mxCell) {
                        String w2 = (String) graph.getCellStyle(object).get(name);
                        if (!w.equals(w2)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private JComponent createBorderColorChooser(){
        Color borderColor;
        if(checkDifferent(STYLE_STROKECOLOR)) {
            borderColor = mxUtils.parseColor(null);
        }
        else {
            borderColor = mxUtils.parseColor((String) editor.getGraphComponent().getGraph().getCellStyle(cell).get(STYLE_STROKECOLOR));
        }
        borderColorListener = new ChangeColorListener(editor, borderColor);
        return p.createColorChooser(borderColor, borderColorListener);
    }

    private JComponent createBorderWidthField()
    {
        borderWidth = new JFormattedTextField(
                p.createNumberFormatter(BORDER_MINIMUM_WIDTH, BORDER_MAXIMUM_WIDTH));
        borderWidth.setColumns(p.FIELD_SIZE);
        borderWidth.setToolTipText(p.createHint(BORDER_MINIMUM_WIDTH, BORDER_MAXIMUM_WIDTH));
        if(!checkDifferent(STYLE_STROKEWIDTH)) {
            borderWidth.setValue(new Integer((String)editor.getGraphComponent().getGraph().getCellStyle(cell).get(STYLE_STROKEWIDTH)));
        }
        return borderWidth;
    }

    private JComponent createFontFamilyField()
    {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        java.util.List<String> fonts = new ArrayList<>();
        fonts.addAll(Arrays.asList(" ","Helvetica", "Verdana",
                "Times New Roman", "Garamond", "Courier New", "Arial", "---"));
        fonts.addAll(Arrays.asList(env.getAvailableFontFamilyNames()));
        fontFamily = new JComboBox<>(fonts.toArray());
        if(!checkDifferent(STYLE_FONTFAMILY)) {
            fontFamily.setSelectedItem(editor.getGraphComponent().getGraph().getCellStyle(cell).get(STYLE_FONTFAMILY));
        }
        return fontFamily;
    }

    private JComponent createFontSizeField()
    {
        fontSize = new JFormattedTextField(
                p.createNumberFormatter(p.FONT_MINIMUM_SIZE, p.FONT_MAXIMUM_SIZE));
        fontSize.setColumns(p.FIELD_SIZE);
        fontSize.setToolTipText(p.createHint(p.FONT_MINIMUM_SIZE, p.FONT_MAXIMUM_SIZE));
        if(!checkDifferent(STYLE_FONTSIZE)) {
            fontSize.setValue(new Integer((String) editor.getGraphComponent().getGraph().getCellStyle(cell).get(STYLE_FONTSIZE)));
        }
        return fontSize;
    }

    private JComponent createFontColorChooser(){
        Color fontColor;
        if(checkDifferent(STYLE_FONTCOLOR)){
            fontColor = mxUtils.parseColor(null);
        }
        else {
            fontColor = mxUtils.parseColor((String) editor.getGraphComponent().getGraph().getCellStyle(cell).get(STYLE_FONTCOLOR));
        }
        fontColorListener = new ChangeColorListener(editor, fontColor);
        return p.createColorChooser(fontColor, fontColorListener);
    }

    private JComponent createShapeComboBox()
    {
        Vector<String> vector = new Vector<>(8);
        vector.add(" ");
        vector.add(lang.getProperty("menu-vertex-shape-circle"));
        vector.add(lang.getProperty("menu-vertex-shape-square"));
        vector.add(lang.getProperty("menu-vertex-shape-rhombus"));
        vector.add(lang.getProperty("menu-vertex-shape-cloud"));
        vector.add(lang.getProperty("menu-vertex-shape-hexagon"));
        shape = new JComboBox<>(vector);
        if(!checkDifferent(STYLE_SHAPE)) {
            shape.setSelectedIndex(shapeIndex((String) editor.getGraphComponent().getGraph().getCellStyle(cell).get(STYLE_SHAPE)));
        }
        return shape;
    }

    private int shapeIndex(String name)
    {
        switch (name)
        {
            case "ellipse": return 1;
            case "rectangle": return 2;
            case "rhombus": return 3;
            case "cloud": return 4;
            case "hexagon": return 5;
            case "triangle": return 6;
            case "actor": return 7;
            case "cylinder": return 8;
            default: return 0;
        }
    }

    private String getSelectedShape()
    {
        switch (shape.getSelectedIndex())
        {
            case 1: return SHAPE_ELLIPSE;
            case 2: return SHAPE_RECTANGLE;
            case 3: return SHAPE_RHOMBUS;
            case 4: return SHAPE_CLOUD;
            case 5: return SHAPE_HEXAGON;
            case 6: return SHAPE_TRIANGLE;
            case 7: return SHAPE_ACTOR;
            case 8: return SHAPE_CYLINDER;
            default: return SHAPE_ELLIPSE;
        }
    }

    private JComponent createHeightField(){
        boolean different = false;
        if(multi){
            mxGraph graph = editor.getGraphComponent().getGraph();
            mxCell first = (mxCell)graph.getSelectionCell();
            double w = first.getGeometry().getHeight();
            for (Object object : graph.getSelectionCells()) {
                if (object instanceof mxCell) {
                    double w2 = ((mxCell)object).getGeometry().getHeight();
                    if (w != w2) {
                        different = true;
                    }
                }
            }
        }
        height = new JFormattedTextField(
                p.createNumberFormatter(VERTEX_MINIMUM_SIZE, VERTEX_MAXIMUM_SIZE));
        height.setColumns(p.FIELD_SIZE);
        height.setToolTipText(p.createHint(VERTEX_MINIMUM_SIZE, VERTEX_MAXIMUM_SIZE));
        if(!different) {
            height.setValue(cell.getGeometry().getHeight());
        }
        return height;
    }

    private JComponent createWidthField(){
        boolean different = false;
        if(multi){
            mxGraph graph = editor.getGraphComponent().getGraph();
            mxCell first = (mxCell)graph.getSelectionCell();
            double w = first.getGeometry().getWidth();
            for (Object object : graph.getSelectionCells()) {
                if (object instanceof mxCell) {
                    double w2 = ((mxCell)object).getGeometry().getWidth();
                    if (w != w2) {
                        different = true;
                    }
                }
            }
        }
        width = new JFormattedTextField(
                p.createNumberFormatter(VERTEX_MINIMUM_SIZE, VERTEX_MAXIMUM_SIZE));
        width.setColumns(p.FIELD_SIZE);
        width.setToolTipText(p.createHint(VERTEX_MINIMUM_SIZE, VERTEX_MAXIMUM_SIZE));
        if(!different) {
            width.setValue(cell.getGeometry().getWidth());
        }
        return width;
    }

    private JComponent createFillColorChooser()
    {
        Color fillColor;
        if(checkDifferent(STYLE_FILLCOLOR)){
            fillColor = mxUtils.parseColor(null);
        }
        else {
            fillColor = mxUtils.parseColor((String) editor.getGraphComponent().getGraph().getCellStyle(cell).get(STYLE_FILLCOLOR));
        }
        fillColorListener = new ChangeColorListener(editor, fillColor);
        return p.createColorChooser(fillColor, fillColorListener);
    }

    private JComponent createNameField()
    {
        name = new JTextField(8);
        name.setText(vertices.get(cell.getId()).getName());
        return name;
    }

    private JComponent createSourceField(){
        source = new JTextField(8);
        source.setText(edges.get(cell.getId()).getSource().getName());
        return source;
    }

    private JComponent createTargetField(){
        target = new JTextField(8);
        target.setText(edges.get(cell.getId()).getTarget().getName());
        return target;
    }

    private JComponent createEdgeWeightField(){
        boolean different = false;
        if(multi){
            mxGraph graph = editor.getGraphComponent().getGraph();
            mxCell first = (mxCell)graph.getSelectionCell();
            String w = first.getValue().toString();
            for (Object object : graph.getSelectionCells()) {
                if (object instanceof mxCell) {
                    String w2 = ((mxCell)object).getValue().toString();
                    if (!w.equals(w2)) {
                        different = true;
                    }
                }
            }
        }
        edgeWeight = new JFormattedTextField(
                p.createNumberFormatter(VERTEX_MINIMUM_SIZE, VERTEX_MAXIMUM_SIZE));
        edgeWeight.setColumns(p.FIELD_SIZE);
        edgeWeight.setToolTipText(p.createHint(VERTEX_MINIMUM_SIZE, VERTEX_MAXIMUM_SIZE));
        if(!different) {
            edgeWeight.setValue(new Double(cell.getValue().toString()));
        }
        return edgeWeight;
    }

    private JComponent createCapacityField(){
        boolean different = false;
        if(multi){
            mxGraph graph = editor.getGraphComponent().getGraph();
            mxCell first = (mxCell)graph.getSelectionCell();
            if(gt instanceof DirectedDoubleWeightedGraph) {
                double w = ((DirectedDoubleWeightedEdge)(edges.get(first.getId()))).getCapacity();
                for (Object object : graph.getSelectionCells()) {
                    if (object instanceof mxCell) {
                        double w2 = ((DirectedDoubleWeightedEdge)(edges.get(((mxCell)object).getId()))).getCapacity();
                        if (w != w2) {
                            different = true;
                        }
                    }
                }
            }
            else if(gt instanceof DoubleWeightedGraph){
                double w = ((DoubleWeightedEdgeImpl)(edges.get(first.getId()))).getCapacity();
                for (Object object : graph.getSelectionCells()) {
                    if (object instanceof mxCell) {
                        double w2 = ((DoubleWeightedEdgeImpl)(edges.get(((mxCell)object).getId()))).getCapacity();
                        if (w != w2) {
                            different = true;
                        }
                    }
                }
            }

        }
        edgeCapacity = new JFormattedTextField(
                p.createNumberFormatter(VERTEX_MINIMUM_SIZE, VERTEX_MAXIMUM_SIZE));
        edgeCapacity.setColumns(p.FIELD_SIZE);
        edgeCapacity.setToolTipText(p.createHint(VERTEX_MINIMUM_SIZE, VERTEX_MAXIMUM_SIZE));
        if(!different) {
            if (gt instanceof DirectedDoubleWeightedGraph) {
                DirectedDoubleWeightedEdge e = (DirectedDoubleWeightedEdge) edges.get(cell.getId());
                edgeCapacity.setValue(e.getCapacity());
            } else if (gt instanceof DoubleWeightedGraph) {
                DoubleWeightedEdgeImpl e = (DoubleWeightedEdgeImpl) edges.get(cell.getId());
                edgeCapacity.setValue(e.getCapacity());
            }
        }
        return edgeCapacity;
    }

    private JComponent createCostField(){
        boolean different = false;
        if(multi){
            mxGraph graph = editor.getGraphComponent().getGraph();
            mxCell first = (mxCell)graph.getSelectionCell();
            if(gt instanceof DirectedDoubleWeightedGraph) {
                double w = ((DirectedDoubleWeightedEdge)(edges.get(first.getId()))).getCost();
                for (Object object : graph.getSelectionCells()) {
                    if (object instanceof mxCell) {
                        double w2 = ((DirectedDoubleWeightedEdge)(edges.get(((mxCell)object).getId()))).getCost();
                        if (w != w2) {
                            different = true;
                        }
                    }
                }
            }
            else if(gt instanceof DoubleWeightedGraph){
                double w = ((DoubleWeightedEdgeImpl)(edges.get(first.getId()))).getCost();
                for (Object object : graph.getSelectionCells()) {
                    if (object instanceof mxCell) {
                        double w2 = ((DoubleWeightedEdgeImpl)(edges.get(((mxCell)object).getId()))).getCost();
                        if (w != w2) {
                            different = true;
                        }
                    }
                }
            }

        }
        edgeCost = new JFormattedTextField(
                p.createNumberFormatter(VERTEX_MINIMUM_SIZE, VERTEX_MAXIMUM_SIZE));
        edgeCost.setColumns(p.FIELD_SIZE);
        edgeCost.setToolTipText(p.createHint(VERTEX_MINIMUM_SIZE, VERTEX_MAXIMUM_SIZE));
        if(!different) {
            if (gt instanceof DirectedDoubleWeightedGraph) {
                DirectedDoubleWeightedEdge e = (DirectedDoubleWeightedEdge) edges.get(cell.getId());
                edgeCost.setValue(e.getCost());
            } else if (gt instanceof DoubleWeightedGraph) {
                DoubleWeightedEdgeImpl e = (DoubleWeightedEdgeImpl) edges.get(cell.getId());
                edgeCost.setValue(e.getCost());
            }
        }
        return edgeCost;
    }

    /**
     * Metoda sprawdza czy wprowadzone dane zostały zmienione.
     * W razie potrzeby zmienia wartości w grafie wartswy wizualnej lub matematycznej.
     */
    private void saveChanges(){

        //bordercolor
        if(borderColorListener.getColor() != null) {
            if (!mxUtils.hexString(borderColorListener.getColor()).equals(editor.getGraphComponent().getGraph().getCellStyle(cell).get(STYLE_STROKECOLOR))) {
                editor.getGraphComponent().getGraph().setCellStyles(STYLE_STROKECOLOR, mxUtils.hexString(borderColorListener.getColor()));
                if(!changed) changed = true;
            }
        }

        //borderwidth
        if(borderWidth.getValue() != null) {
            if (!borderWidth.getValue().toString().equals(editor.getGraphComponent().getGraph().getCellStyle(cell).get(STYLE_STROKEWIDTH))) {
                editor.getGraphComponent().getGraph().setCellStyles(STYLE_STROKEWIDTH, borderWidth.getValue().toString());
                if(!changed) changed = true;
            }
        }

        //font
        if(fontFamily.getSelectedItem() != null) {
            if (!fontFamily.getSelectedItem().equals(editor.getGraphComponent().getGraph().getCellStyle(cell).get(STYLE_FONTFAMILY))) {
                editor.getGraphComponent().getGraph().setCellStyles(STYLE_FONTFAMILY, fontFamily.getSelectedItem().toString());
                if(!changed) changed = true;
            }
        }

        //fontsize
        if(fontSize.getValue() != null) {
            if (!fontSize.getValue().toString().equals(editor.getGraphComponent().getGraph().getCellStyle(cell).get(STYLE_FONTSIZE))) {
                editor.getGraphComponent().getGraph().setCellStyles(STYLE_FONTSIZE, fontSize.getValue().toString());
                if(!changed) changed = true;
            }
        }

        //fontcolor
        if(fontColorListener.getColor() != null) {
            if (!mxUtils.hexString(fontColorListener.getColor()).equals(editor.getGraphComponent().getGraph().getCellStyle(cell).get(STYLE_FONTCOLOR))) {
                editor.getGraphComponent().getGraph().setCellStyles(STYLE_FONTCOLOR, mxUtils.hexString(fontColorListener.getColor()));
                if(!changed) changed = true;
            }
        }

        if(cell.isVertex()) {
            Vertex v = vertices.get(cell.getId());

            //shape
            if(!multi || !different_cells && vertex_cells) {
                if (shape.getSelectedIndex() != 0) {
                    if (!getSelectedShape().equals(editor.getGraphComponent().getGraph().getCellStyle(cell).get(STYLE_SHAPE))) {
                        ActionSetShape sh = new ActionSetShape(editor, getSelectedShape());
                        sh.setStyle(getSelectedShape());
                        if(!changed) changed = true;
                    }
                }
            }

            //height
            if(!multi || !different_cells && vertex_cells) {
                if (height.getValue() != null) {
                    if (!height.getValue().equals(cell.getGeometry().getHeight())) {
                        int h = (int) height.getValue();
                        for (Object object : editor.getGraphComponent().getGraph().getSelectionCells()) {
                            if (h != ((mxCell) object).getGeometry().getHeight()) {
                                ((mxCell) object).getGeometry().setHeight((double) h);
                                if(!changed) changed = true;
                            }
                        }
                    }
                }
            }

            //width
            if(!multi || !different_cells && vertex_cells) {
                if (width.getValue() != null) {
                    if (!width.getValue().equals(cell.getGeometry().getWidth())) {
                        int w = (int) width.getValue();
                        for (Object object : editor.getGraphComponent().getGraph().getSelectionCells()) {
                            if (w != ((mxCell) object).getGeometry().getWidth()) {
                                ((mxCell) object).getGeometry().setWidth((double) w);
                                if(!changed) changed = true;
                            }
                        }
                    }
                }
            }

            //fillcolor
            if(!multi || !different_cells && vertex_cells) {
                if (fillColorListener.getColor() != null) {
                    if (!mxUtils.hexString(fillColorListener.getColor()).equals(editor.getGraphComponent().getGraph().getCellStyle(cell).get(STYLE_FILLCOLOR))) {
                        editor.getGraphComponent().getGraph().setCellStyles(STYLE_FILLCOLOR, mxUtils.hexString(fillColorListener.getColor()));
                        if(!changed) changed = true;
                    }
                }
            }

            //name
            if(!multi) {
                if (name.getText() != null) {
                    if (!name.getText().equals(cell.getValue().toString())) {
                        cell.setValue(name.getText());
                        v.setName(name.getText());
                        if(!changed) changed = true;
                    }
                }
            }
        }
        else if(cell.isEdge())
        {
            ModgrafEdge e = edges.get(cell.getId());
            String new_source_id = e.getSource().getId();
            String new_target_id = e.getTarget().getId();
            boolean new_connect = false;

            //source
            if(!multi) {
                if (source.getText() != null && (!source.getText().equals(""))) {
                    if (!source.getText().equals(e.getSource().getName())) {

                        String key = "-1";
                        for (Entry<String, Vertex> entry : vertices.entrySet()) {
                            if (source.getText().equals(entry.getValue().getName())) {
                                key = entry.getKey();
                                new_source_id = key;
                            }
                        }

                        if (!key.equals("-1")) {
                            gt.removeEdge(e);
                            editor.removeEdgeId(e.getSource().getId(), e.getTarget().getId());

                            e.setSource(vertices.get(key));
                            mxGraphModel model = new mxGraphModel(editor.getGraphComponent().getGraph().getModel().getRoot());
                            mxICell cv = (mxICell) model.getCell(key);
                            cell.setSource(cv);
                            e.setId(e.getId());

                            new_connect = true;
                        }
                        if(!changed) changed = true;
                    }
                }
            }

            //target
            if(!multi) {
                if (target.getText() != null && (!target.getText().equals(""))) {
                    if (!target.getText().equals(e.getTarget().getName())) {

                        String key = "-1";
                        for (Entry<String, Vertex> entry : vertices.entrySet()) {
                            if (target.getText().equals(entry.getValue().getName())) {
                                key = entry.getKey();
                                new_target_id = key;
                            }
                        }

                        if (!key.equals("-1")) {
                            gt.removeEdge(e);
                            editor.removeEdgeId(e.getSource().getId(), e.getTarget().getId());

                            e.setTarget(vertices.get(key));
                            mxGraphModel model = new mxGraphModel(editor.getGraphComponent().getGraph().getModel().getRoot());
                            mxICell cv = (mxICell) model.getCell(key);
                            cell.setTarget(cv);
                            e.setId(e.getId());

                            new_connect = true;
                        }
                        if(!changed) changed = true;
                    }
                }
            }

            if(new_connect) {
                editor.setEdgeId(new_source_id, new_target_id, e.getId());
                ModgrafEdge ed = gt.addEdge(e.getSource(), e.getTarget());
                ed.setId(e.getId());
                gt.getEdgeFactory().createEdge(e.getSource(), e.getTarget());
                editor.getEdges().put(e.getId(), ed);
            }

            //weight
            if(!multi) {
                if (gt instanceof ModgrafUndirectedWeightedGraph) {
                    if (edgeWeight.getText() != null) {
                        if (!edgeWeight.getText().equals(cell.getValue().toString())) {
                            cell.setValue(edgeWeight.getValue());
                            int w = (int) edgeWeight.getValue();
                            ((WeightedEdgeImpl) e).setWeight((double) w);
                            if(!changed) changed = true;
                        }
                    }
                } else if (gt instanceof ModgrafDirectedWeightedGraph) {
                    if (edgeWeight.getText() != null) {
                        if (!edgeWeight.getText().equals(cell.getValue().toString())) {
                            cell.setValue(edgeWeight.getValue());
                            int w = (int) edgeWeight.getValue();
                            ((DirectedWeightedEdge) e).setWeight((double) w);
                            if(!changed) changed = true;
                        }
                    }
                }
            }
            else {
                if (gt instanceof ModgrafUndirectedWeightedGraph) {
                    if (edgeWeight.getText() != null) {
                        for (Object object : editor.getGraphComponent().getGraph().getSelectionCells()) {
                            if (!edgeWeight.getText().equals(((mxCell) object).getValue().toString())) {
                                ((mxCell) object).setValue(edgeWeight.getValue());
                                int w = (int) edgeWeight.getValue();
                                ((WeightedEdgeImpl) edges.get(((mxCell) object).getId())).setWeight((double) w);
                                if(!changed) changed = true;
                            }
                        }
                    }
                } else if (gt instanceof ModgrafDirectedWeightedGraph) {
                    if (edgeWeight.getText() != null) {
                        for (Object object : editor.getGraphComponent().getGraph().getSelectionCells()) {
                            if (!edgeWeight.getText().equals(((mxCell) object).getValue().toString())) {
                                ((mxCell) object).setValue(edgeWeight.getValue());
                                int w = (int) edgeWeight.getValue();
                                ((DirectedWeightedEdge) edges.get(((mxCell) object).getId())).setWeight((double) w);
                                if(!changed) changed = true;
                            }
                        }
                    }
                }
            }

            //capacity & cost
            if(!multi) {
                if (gt instanceof UndirectedDoubleWeightedGraph) {
                    if (edgeCapacity.getText() != null) {
                        if (!edgeCapacity.getValue().equals(((DoubleWeightedEdgeImpl) e).getCapacity())) {
                            int w = (int) edgeCapacity.getValue();
                            ((DoubleWeightedEdge) e).setCapacity((double) w);
                            if(!changed) changed = true;
                        }
                    }
                    if (edgeCost.getText() != null) {
                        if (!edgeCost.getValue().equals(((DoubleWeightedEdgeImpl) e).getCost())) {
                            int w = (int) edgeCost.getValue();
                            ((DoubleWeightedEdge) e).setCost((double) w);
                            if(!changed) changed = true;
                        }
                    }
                    cell.setValue(((DoubleWeightedEdge) e).getCapacity() + "/" + ((DoubleWeightedEdge) e).getCost());
                } else if (gt instanceof DirectedDoubleWeightedGraph) {
                    if (edgeCapacity.getText() != null) {
                        if (!edgeCapacity.getValue().equals(((DirectedDoubleWeightedEdge) e).getCapacity())) {
                            int w = (int) edgeCapacity.getValue();
                            ((DirectedDoubleWeightedEdge) e).setCapacity((double) w);
                            if(!changed) changed = true;
                        }
                    }
                    if (edgeCost.getText() != null) {
                        if (!edgeCost.getValue().equals(((DirectedDoubleWeightedEdge) e).getCost())) {
                            int w = (int) edgeCost.getValue();
                            ((DirectedDoubleWeightedEdge) e).setCost((double) w);
                            if(!changed) changed = true;
                        }
                    }
                    cell.setValue(((DoubleWeightedEdge) e).getCapacity() + "/" + ((DoubleWeightedEdge) e).getCost());
                }
            }
            else{
                if (gt instanceof UndirectedDoubleWeightedGraph) {
                    for (Object object : editor.getGraphComponent().getGraph().getSelectionCells()) {
                        if (edgeCapacity.getText() != null) {
                            if (!edgeCapacity.getValue().equals(((DoubleWeightedEdgeImpl)edges.get(((mxCell)object).getId())).getCapacity())) {
                                int w = (int) edgeCapacity.getValue();
                                ((DoubleWeightedEdge)edges.get(((mxCell)object).getId())).setCapacity((double) w);
                                if(!changed) changed = true;
                            }
                        }
                        if (edgeCost.getText() != null) {
                            if (!edgeCost.getValue().equals(((DoubleWeightedEdgeImpl)edges.get(((mxCell)object).getId())).getCost())) {
                                int w = (int) edgeCost.getValue();
                                ((DoubleWeightedEdge)edges.get(((mxCell)object).getId())).setCost((double) w);
                                if(!changed) changed = true;
                            }
                        }
                        ((mxCell)object).setValue(((DoubleWeightedEdge) e).getCapacity() + "/" + ((DoubleWeightedEdge) e).getCost());
                    }
                } else if (gt instanceof DirectedDoubleWeightedGraph) {
                    for (Object object : editor.getGraphComponent().getGraph().getSelectionCells()) {
                        if (edgeCapacity.getText() != null) {
                            if (!edgeCapacity.getValue().equals(((DirectedDoubleWeightedEdge)edges.get(((mxCell)object).getId())).getCapacity())) {
                                int w = (int) edgeCapacity.getValue();
                                ((DirectedDoubleWeightedEdge)edges.get(((mxCell)object).getId())).setCapacity((double) w);
                                if(!changed) changed = true;
                            }
                        }
                        if (edgeCost.getText() != null) {
                            if (!edgeCost.getValue().equals(((DirectedDoubleWeightedEdge)edges.get(((mxCell)object).getId())).getCost())) {
                                int w = (int) edgeCost.getValue();
                                ((DirectedDoubleWeightedEdge)edges.get(((mxCell)object).getId())).setCost((double) w);
                                if(!changed) changed = true;
                            }
                        }
                        ((mxCell)object).setValue(((DoubleWeightedEdge) e).getCapacity() + "/" + ((DoubleWeightedEdge) e).getCost());
                    }
                }
            }
        }

        if(changed) editor.saveState(lang.getProperty("memento-change"));
        editor.getGraphComponent().refresh();

    }
}
