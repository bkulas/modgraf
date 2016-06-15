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

    public void undo(Memento memento){
        this.state = memento.getState();
    }

    public void redo(Memento memento){
        this.state = memento.getState();
    }
}
