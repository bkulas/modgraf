package modgraf.memento;

import modgraf.action.ActionSaveAs;
import modgraf.view.Editor;
import modgraf.action.ActionOpen;

import com.mxgraph.util.mxXmlUtils;
import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;

/**
 * Created by Basia on 15.06.2016.
 *
 * @author Barbara Kulas
 */
public class Originator {
    private String state;
    private String name;
    private Caretaker caretaker;
    private int number = 0;
    private Editor editor;

    public Originator(Editor e, String name) {
        super();
        this.editor = e;
        this.name = name;
        ActionSaveAs a = new ActionSaveAs(editor);
        this.state = a.buildXml(editor.getGraphComponent().getGraph(), editor.getGraphT());
        this.caretaker = new Caretaker();
        caretaker.addMemento(createMemento());
        System.out.println(number);
    }

    public void setState(String name) {
        this.name = name;
        ActionSaveAs a = new ActionSaveAs(editor);
        this.state = a.buildXml(editor.getGraphComponent().getGraph(), editor.getGraphT());
        caretaker.addMemento(createMemento());
        System.out.println("Stworzono pamiatke: "+name);
    }

    public String getState() {
        return state;
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public Caretaker getCaretaker() {
        return caretaker;
    }

    public Memento createMemento(){
        return new Memento(++number, this.state, this.name);
    }

    public void restoreMemento(Memento memento){
        this.state = memento.getState();
        this.number = memento.getNumber();
        this.name = memento.getName();
        ActionOpen a = new ActionOpen(editor, true);
        Document doc = a.buildXmlDocument(state);
        a.createGraphTFromXmlDocument(doc);
        a.setmxGraph(doc);
        System.out.println("Memento restored: "+name);
    }

    public void undo(){
        restoreMemento(caretaker.undo());
        //this.state = ct.undo().getState();
        System.out.println(number);
    }

    public void redo(){
        restoreMemento(caretaker.redo());
        //this.state = ct.redo().getState();
        System.out.println(number);
    }

    public String checkUndoName(){
        return caretaker.getActualName();
    }

    public String checkRedoName(){
        return caretaker.getPreviousName();
    }

}
