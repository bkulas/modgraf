package modgraf.memento;

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
    private Caretaker caretaker;
    private int number = 0;
    private Editor editor;

    public Originator(Editor e, String state) {
        super();
        this.editor = e;
        this.state = state;
        this.caretaker = new Caretaker();
        caretaker.addMemento(createMemento());
        System.out.println(number);
    }

    public void setState(String state) {
        this.state = state;
        caretaker.addMemento(createMemento());
    }

    public String getState() {
        return state;
    }

    public int getNumber() {
        return number;
    }

    public Caretaker getCaretaker() {
        return caretaker;
    }

    public Memento createMemento(){
        return new Memento(++number, this.state);
    }

    public void restoreMemento(Memento memento){
        this.state = memento.getState();
        this.number = memento.getNumber();
        ActionOpen a = new ActionOpen(editor);
        Document doc = a.buildXmlDocument(state);
        a.createGraphTFromXmlDocument(doc);
        a.setmxGraph(doc);
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
}
