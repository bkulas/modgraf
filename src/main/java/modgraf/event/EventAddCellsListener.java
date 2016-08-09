package modgraf.event;

import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Properties;

import javax.swing.JOptionPane;

import modgraf.jgrapht.DoubleWeightedGraph;
import modgraf.jgrapht.Vertex;
import modgraf.jgrapht.edge.DoubleWeightedEdge;
import modgraf.jgrapht.edge.ModgrafEdge;
import modgraf.view.Editor;
import modgraf.action.*;

import org.jgrapht.Graph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.WeightedGraph;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxGraph;

/**
 * Zdarzenie <code>mxEvent.CELLS_ADDED</code> na obiekcie {@link mxGraph} 
 * pojawia się w momencie dodania nowego wierzchołka lub krawędzi. <br>
 * Jeżeli w warstwie wizualnej dodany został wierzchołek, to w warstwie 
 * matematycznej także zostanie utworzony wierzchołek i zostanie 
 * zaktualizowana mapa z nazwami wierzchołków. <br>
 * Jeżeli w warstwie wizualnej dodana została krawędź, to zostanie wykonane 
 * sprawdzenie czy taka krawędź nie istniała wcześniej. Jeśli istniała, 
 * to zdublowana krawędź zostanie usunięta i użytkownik zobaczy komunikat 
 * "Nie można dodać krawędzi! Prawdopodobnie istniała już wcześniej". <br>
 * Jeśli nie istniała, to zostanie dodana i zostanie zaktualizowana mapa 
 * krawędzi. Ponadto w grafach ważonych za pomocą metod z klasy 
 * {@link LabelChangedUtils} zostaną wpisane domyślne wagi zarówno w warstwie 
 * matematycznej jak i wizualnej.
 * 
 * @author Daniel Pogrebniak
 * 
 * @see mxIEventListener
 *
 */
public class EventAddCellsListener implements mxIEventListener
{
	private Editor editor;
	private Properties prop;

	public EventAddCellsListener(Editor e)
	{
		editor = e;
		prop = e.getProperties();
	}
	
	@Override
	public void invoke(Object sender, mxEventObject evt)
	{
		Object[] cells = (Object[])evt.getProperties().get("cells");
		if (cells != null)
		{
			for (int i = 0; i < cells.length; ++i)
			{
				if (cells[i] instanceof mxCell)
				{
					mxCell cell = (mxCell)cells[i];
					addCellToGraphT(cell);
//					addCellToGraphT(cell, evt);
					if(cell.isVertex())
						editor.saveState(editor.getLanguage().getProperty("memento-add-vertex"));
					else editor.saveState(editor.getLanguage().getProperty("memento-add-edge"));
				}
			}
		}
	}

	private void addCellToGraphT(mxCell cell)
//	private void addCellToGraphT(mxCell cell, mxEventObject event)
	{
		Graph<Vertex, ModgrafEdge> graphT = editor.getGraphT();
		Map<String, Vertex> vertices = editor.getVertices();
		if (cell.isVertex())
		{
			Vertex vertex = new Vertex(cell);
			graphT.addVertex(vertex);
			editor.setVertexId(cell.getValue().toString(), cell.getId());
			vertices.put(vertex.getId(), vertex);
		}
		if (cell.isEdge())
		{
			Vertex source = vertices.get(cell.getSource().getId());

			//każde dodanie krawedzi dodaje nowy wierzcholek w położeniu początkowym
			//sprawdzić czy rowniez w warstwie logicznej - chyba tak (można dodac krawedz do tego wierzcholka)
			//nalezy użyc funkcji z ActionAddVertexWithPosition
			if (cell.getTarget() == null){
				System.out.print("target ");
//				System.out.print(cell.getTarget().getId());
				System.out.print(" source ");
				System.out.print(cell.getSource().getId());
				System.out.println(".");

//				MouseEvent

				mxGraph graph = editor.getGraphComponent().getGraph();
				Properties prop = editor.getProperties();
				int width = Integer.parseInt(prop.getProperty("default-vertex-width"));
				int height = Integer.parseInt(prop.getProperty("default-vertex-height"));
				Object parent = graph.getDefaultParent();
				int vertexPosition = editor.getVertexCounter() * 5;
//				graph.insertVertex(parent, null, editor.incrementAndGetNewVertexCounter(),
//						vertexPosition, vertexPosition, width, height, "vertexStyle");
				cell.setTarget((mxCell)graph.insertVertex(parent, null, editor.incrementAndGetNewVertexCounter(),
						vertexPosition, vertexPosition, width, height, "vertexStyle"));

			}

			System.out.print("target ");
				System.out.print(cell.getTarget().getId());
			System.out.print(" source ");
			System.out.print(cell.getSource().getId());
			System.out.println(".");

			Vertex target = vertices.get(cell.getTarget().getId());
			ModgrafEdge e = graphT.addEdge(source, target);
			if (e != null)
			{
				e.setId(cell.getId());
				setEdgeValue(cell, graphT, e);
				editor.getEdges().put(cell.getId(), e);
			}
			else
			{
				editor.getGraphComponent().getGraph().getModel().remove(cell);
				Properties lang = editor.getLanguage();
				JOptionPane.showMessageDialog(editor.getGraphComponent(),
						lang.getProperty("warning-not-add-edge"),
					    lang.getProperty("warning"), JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	private void setEdgeValue(mxCell cell, Graph<Vertex, ModgrafEdge> graphT, ModgrafEdge e)
	{
		editor.setEdgeId(cell.getSource().getId(), cell.getTarget().getId(), cell.getId());
		LabelChangedUtils lcu = new LabelChangedUtils(editor);
		if (graphT instanceof WeightedGraph)
		{
			if (cell.getValue() != null && !cell.getValue().equals(""))
				lcu.changeValueForWeightedEdge(cell);
			else
				cell.setValue(prop.getProperty("default-edge-weight"));
		}
		if (graphT instanceof DoubleWeightedGraph)
		{
			if (cell.getValue() != null && !cell.getValue().equals(""))
				setDoubleWeightedEdgeValue(cell, e);
			else
				cell.setValue(prop.getProperty("default-edge-capacity")+"/"+prop.getProperty("default-edge-cost"));
		}
		if (graphT instanceof UndirectedGraph)
			editor.setEdgeId(cell.getTarget().getId(), cell.getSource().getId(), cell.getId());
	}

	private void setDoubleWeightedEdgeValue(mxCell cell, ModgrafEdge e)
	{
		String value = (String) cell.getValue();
		String[] values = value.split("/");
		double capacity = Double.parseDouble(values[0]);
		double cost = Double.parseDouble(values[1]);
		DoubleWeightedEdge dwe = (DoubleWeightedEdge)e;
		dwe.setCapacity(capacity);
		dwe.setCost(cost);
	}
}
