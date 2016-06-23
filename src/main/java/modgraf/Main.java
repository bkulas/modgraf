package modgraf;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import modgraf.action.ActionNewGraph;
import modgraf.view.Editor;
import modgraf.memento.Originator;
import modgraf.memento.Caretaker;

/**
 * Klasa startowa. Tworzy instancję klasy Editor i główne okno programu.
 * 
 * @author Daniel Pogrebniak
 *
 */
public class Main 
{
	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e1)
		{
			JOptionPane.showMessageDialog(null,
					"Nie udało się ustawić systemowego stylu okien!\n" +
					"Został użyty styl domyślny",
					"Ostrzeżenie", JOptionPane.WARNING_MESSAGE);
		}
		Editor editor = new Editor();
		editor.createFrame();
		if (editor.getProperties().getProperty("show-new-graph-window-on-startup").equals("true"))
			new ActionNewGraph(editor).actionPerformed(null);

		/*Caretaker caretaker = editor.getCaretaker();
		Originator originator = editor.getOriginator();

		//Test of undo/redo mechanism
		originator.setState("State1");
		originator.setState("State2");
		caretaker.addMemento(originator.createMemento());
		originator.setState("State3");
		caretaker.addMemento(originator.createMemento());
		originator.setState("State4");

		for(int i=0; i<caretaker.getSize(); i++) {
			System.out.println(caretaker.getMemento(i).getState());
		}

		System.out.println("koniec");

		caretaker.addMemento(originator.createMemento());
		originator.undo(caretaker);

		for(int i=0; i<caretaker.getSize(); i++) {
			System.out.println(caretaker.getMemento(i).getState());
		}

		System.out.println("stan "+originator.getState());

		originator.setState("State5");
		caretaker.addMemento(originator.createMemento());

		for(int i=0; i<caretaker.getSize(); i++) {
			System.out.println(caretaker.getMemento(i).getState());
		}

		System.out.println("stan "+originator.getState());

		originator.undo(caretaker);
		originator.undo(caretaker);
		System.out.println("undo undo stan "+originator.getState());
		originator.undo(caretaker);
		System.out.println("undo undo undo stan "+originator.getState());
		originator.redo(caretaker);
		System.out.println("redo stan "+originator.getState());
		originator.redo(caretaker);
		System.out.println("redo stan "+originator.getState());
		originator.redo(caretaker);
		System.out.println("redo stan "+originator.getState());
		originator.setState("State6");
		caretaker.addMemento(originator.createMemento());
		System.out.println("stan "+originator.getState());
		originator.redo(caretaker);
		System.out.println("redo stan "+originator.getState());
		originator.undo(caretaker);
		System.out.println("undo stan "+originator.getState());
		originator.redo(caretaker);
		System.out.println("redo stan "+originator.getState());

		for(int i=0; i<caretaker.getSize(); i++) {
			System.out.println(caretaker.getMemento(i).getState());
		}*/


	}
}
