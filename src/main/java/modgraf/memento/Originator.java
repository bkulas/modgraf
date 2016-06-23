package modgraf.memento;

/**
 * Created by Basia on 15.06.2016.
 *
 * @author Barbara Kulas
 */
public class Originator {
    private String state;

    public Originator(String state) {
        super();
        this.state = state;
        System.out.println(state);
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public Memento createMemento(){
        return new Memento(this.state);
    }

    public void restoreMemento(Memento memento){
        this.state = memento.getState();
    }

    public void undo(Caretaker ct){
        restoreMemento(ct.undo());
        //this.state = ct.undo().getState();
        System.out.println(state);
    }

    public void redo(Caretaker ct){
        restoreMemento(ct.redo());
        //this.state = ct.redo().getState();
        System.out.println(state);
    }
}
