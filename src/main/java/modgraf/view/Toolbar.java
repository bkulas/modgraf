package modgraf.view;

import java.awt.Component;
import java.awt.Dimension;
//import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

//import com.mxgraph.swing.mxGraphComponent;
//import com.mxgraph.util.mxEvent;
//import com.mxgraph.util.mxEventObject;
//import com.mxgraph.util.mxEventSource;
//import com.mxgraph.util.mxResources;
import modgraf.action.ActionAddVertex;
import modgraf.action.ActionNewGraph;
import modgraf.action.ActionOpen;
import modgraf.action.ActionRemoveSelected;
import modgraf.action.ActionSave;
import modgraf.action.ActionUndo;
import modgraf.action.ActionRedo;

//import com.mxgraph.view.mxGraphView;


/**
 * Klasa zawiera pasek narzędzi.
 * 
 * @author Daniel Pogrebniak
 *
 */
public class Toolbar extends JToolBar 
{
	private static final long serialVersionUID = 3207006628680937574L;

	private Editor editor;
	private JTextField graphType;
	private JTextField edges;
	boolean useClassLoader;
//	private boolean ignoreZoomChange = false;

	public Toolbar (Editor e)
	{
		editor = e;
		String useClassLoaderString = e.getProperties().getProperty("use-class-loader");
		useClassLoader = Boolean.parseBoolean(useClassLoaderString);
		
		addButton(new ActionNewGraph(editor), "icons/new.gif", true);
		addButton(new ActionOpen(editor), "icons/open.png", true);
		addButton(new ActionSave(editor), "icons/save.gif", false);
		addSeparator();
		addButton(new ActionAddVertex(editor), "icons/add.png", false);
		addButton(new ActionRemoveSelected(editor), "icons/minus.png", false);
		addSeparator();
		addButton(new ActionUndo(editor), "icons/undo.png", false);
		addButton(new ActionRedo(editor), "icons/redo.png", false);
		addSeparator();
		
		add(new JLabel(editor.getLanguage().getProperty("label-graph-type")+" "));
		graphType = new JTextField();
		edges = new JTextField();
		graphType.setMaximumSize(new Dimension(100, 20));
		edges.setMaximumSize(new Dimension(120, 20));
		graphType.setEditable(false);
		edges.setEditable(false);
		add(graphType);
		add(edges);
	}
	
	private void addButton(ActionListener action, String icon, boolean enabled)
	{
		JButton button = new JButton();
		button.setEnabled(enabled);
		button.addActionListener(action);
		if (useClassLoader)
			button.setIcon(new ImageIcon(getClass().getClassLoader().getResource(icon)));
		else
			button.setIcon(new ImageIcon(icon));
		add(button);
	}
	
	protected void setEdgeTypeText (String text)
	{
		edges.setText(text);
	}
	
	protected void setGraphTypeText (String text)
	{
		graphType.setText(text);
	}
	
	public void enableAllElements()
	{
		Component[] components = this.getComponents();
		for(Component component:components)
			component.setEnabled(true);
	}
}
