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
		
		add(new JLabel(editor.getLanguage().getProperty("label-graph-type")+" "));
		graphType = new JTextField();
		edges = new JTextField();
		graphType.setMaximumSize(new Dimension(100, 20));
		edges.setMaximumSize(new Dimension(120, 20));
		graphType.setEditable(false);
		edges.setEditable(false);
		add(graphType);
		add(edges);

		/*
		final mxGraphView view = editor.getGraphComponent().getGraph()
				.getView();

		final JComboBox zoomCombo = new JComboBox(new Object[] { "400%",
				"200%", "150%", "100%", "75%", "50%", mxResources.get("page"),
				mxResources.get("width"), mxResources.get("actualSize") });
		zoomCombo.setEditable(true);
		zoomCombo.setMinimumSize(new Dimension(75, 0));
		zoomCombo.setPreferredSize(new Dimension(75, 0));
		zoomCombo.setMaximumSize(new Dimension(75, 100));
		zoomCombo.setMaximumRowCount(9);
		e.getGraphComponent().add(zoomCombo);

		// Sets the zoom in the zoom combo the current value
		mxEventSource.mxIEventListener scaleTracker = new mxEventSource.mxIEventListener()
		{

			public void invoke(Object sender, mxEventObject evt)
			{
				ignoreZoomChange = true;

				try
				{
					zoomCombo.setSelectedItem((int) Math.round(100 * view.getScale()) + "%");
				}
				finally
				{
					ignoreZoomChange = false;
				}
			}
		};

		// Installs the scale tracker to update the value in the combo box
		// if the zoom is changed from outside the combo box
		e.getGraphComponent().getGraph().getView().addListener(mxEvent.SCALE, scaleTracker);
		e.getGraphComponent().getGraph().getView().addListener(mxEvent.SCALE_AND_TRANSLATE, scaleTracker);

		// Invokes once to sync with the actual zoom value
		scaleTracker.invoke(null, null);

		zoomCombo.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent ev)
			{
				mxGraphComponent graphComponent = e.getGraphComponent();

				// Zoomcombo is changed when the scale is changed in the diagram
				// but the change is ignored here
				if (!ignoreZoomChange)
				{
					String zoom = zoomCombo.getSelectedItem().toString();

					if (zoom.equals(mxResources.get("page")))
					{
						graphComponent.setPageVisible(true);
						graphComponent.setZoomPolicy(mxGraphComponent.ZOOM_POLICY_PAGE);
					}
					else if (zoom.equals(mxResources.get("width")))
					{
						graphComponent.setPageVisible(true);
						graphComponent.setZoomPolicy(mxGraphComponent.ZOOM_POLICY_WIDTH);
					}
					else if (zoom.equals(mxResources.get("actualSize")))
					{
						graphComponent.zoomActual();
					}
					else
					{
						try
						{
							zoom = zoom.replace("%", "");
							double scale = Math.min(16, Math.max(0.01,
									Double.parseDouble(zoom) / 100));
							graphComponent.zoomTo(scale, graphComponent
									.isCenterZoom());
						}
						catch (Exception ex)
						{
							JOptionPane.showMessageDialog(e.getGraphComponent(), ex.getMessage());
						}
					}
				}
			}
		});*/
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
