package modgraf.algorithm;

import java.awt.event.ActionEvent;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

import modgraf.jgrapht.DoubleWeightedGraph;
import modgraf.jgrapht.Vertex;
import modgraf.jgrapht.edge.ModgrafEdge;
import modgraf.view.Editor;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.EdmondsKarpMaximumFlow;

/**
 * Klasa rozwiązuje problem maksymalnego przepływu.
 * 
 * @author Daniel Pogrebniak
 *
 * @see ModgrafAbstractAlgorithm
 * @see EdmondsKarpMaximumFlow
 * 
 */
public class ModgrafEdmondsKarpMaximumFlow extends ModgrafAbstractAlgorithm
{

	public ModgrafEdmondsKarpMaximumFlow(Editor e)
	{
		super(e);
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		if (editor.getGraphT() instanceof DirectedGraph && (editor.getGraphT() instanceof WeightedGraph || editor.getGraphT() instanceof DoubleWeightedGraph))
			openParamsWindow();
		else
			JOptionPane.showMessageDialog(editor.getGraphComponent(),
					lang.getProperty("warning-wrong-graph-type")+
				    lang.getProperty("alg-mf-graph-type"),
				    lang.getProperty("warning"), JOptionPane.WARNING_MESSAGE);
	}

	@Override
	public String getName()
	{
		return lang.getProperty("menu-algorithm-maximum-flow");
	}

	private void createTextResult(EdmondsKarpMaximumFlow<Vertex,ModgrafEdge> ekmf)
	{
		StringBuilder sb = new StringBuilder();
		String newLine = "\n";
		sb.append(lang.getProperty("alg-mf-message-1"));
		sb.append(ekmf.getMaximumFlowValue());
		sb.append(newLine);
		sb.append(lang.getProperty("alg-mf-message-2"));
		sb.append(newLine);
		Graph<Vertex, ModgrafEdge> graphT = editor.getGraphT();
		Map<ModgrafEdge, Double> resultMap = ekmf.getMaximumFlow();
		for (Entry<ModgrafEdge, Double> entry : resultMap.entrySet())
		{
			double flow = entry.getValue();
			if (flow > 0)
			{
				ModgrafEdge edge = entry.getKey();
				double capacity =  graphT.getEdgeWeight(edge);
				sb.append(edge.getId());
				sb.append(" - ");
				sb.append(edge.getSource().getName());
				sb.append(" : ");
				sb.append(edge.getTarget().getName());
				sb.append(" --> ");
				sb.append(flow);
				sb.append("/");
				sb.append(capacity);
				sb.append(newLine);
			}
		}
		editor.setText(sb.toString());
	}

	private void createGraphicalResult(EdmondsKarpMaximumFlow<Vertex,ModgrafEdge> ekmf)
	{
		int width = 4;
		int halfWidth = 2;
		Graph<Vertex, ModgrafEdge> graphT = editor.getGraphT();
		changeVertexStrokeWidth(startVertex, width);
		changeVertexStrokeWidth(endVertex, width);
		Map<ModgrafEdge, Double> resultMap = ekmf.getMaximumFlow();
		for (Entry<ModgrafEdge, Double> entry : resultMap.entrySet())
		{
			double flow = entry.getValue();
			if (flow > 0)
			{
				ModgrafEdge edge = entry.getKey();
				Vertex source = edge.getSource();
				Vertex target = edge.getTarget();
				String idEdge = editor.getEdgeId(source.getId(), target.getId());
				ModgrafEdge edge2 = editor.getEdges().get(idEdge);
				double capacity =  graphT.getEdgeWeight(edge2);
				if (flow == capacity)
					changeEdgeStrokeWidth(edge2, width);
				if (flow < capacity)
					changeEdgeStrokeWidth(edge2, halfWidth);
			}
		}
		editor.getGraphComponent().refresh();
	}

	@Override
	protected void findAndShowResult()
	{
		if (editor.getGraphT() instanceof DoubleWeightedGraph){
			EdmondsKarpMaximumFlow<Vertex, ModgrafEdge> ekmf =
					new EdmondsKarpMaximumFlow<>((DirectedGraph<Vertex, ModgrafEdge>) editor.getGraphT());
		}
		EdmondsKarpMaximumFlow<Vertex, ModgrafEdge> ekmf = 
				new EdmondsKarpMaximumFlow<>((DirectedGraph<Vertex, ModgrafEdge>) editor.getGraphT());
		ekmf.calculateMaximumFlow(startVertex, endVertex);
		if (ekmf.getMaximumFlowValue() > 0)
		{
			createTextResult(ekmf);
			createGraphicalResult(ekmf);
		}
		else
			JOptionPane.showMessageDialog(editor.getGraphComponent(),
					lang.getProperty("message-no-solution"),
					lang.getProperty("information"), JOptionPane.INFORMATION_MESSAGE);
	}

}
