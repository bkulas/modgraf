package modgraf.memento;

/**
 * Created by Basia on 15.06.2016.
 *
 * @author Barbara Kulas
 */

import java.util.ArrayList;
import java.util.List;

public class Caretaker {
    private List<Memento> mementos;
    private int actual = -1;
    private int previous;

    public List<Memento> getMementos() {
        if(mementos == null){
            mementos = new ArrayList();
        }
        return mementos;
    }

    public void addMemento(Memento memento){
        getMementos();
        if(actual != mementos.size()-1){
            for(int i=(mementos.size()-1); i>actual; i--){
                mementos.remove(i);
            }
        }
        getMementos().add(memento);
        actual = mementos.size()-1;
        previous = actual-1;
    }

    public Memento getMemento(int index){
        return getMementos().get(index);
    }

    public int getSize(){
        return mementos.size();
    }

    public Memento undo(){
        if(actual == 0) return getMemento(actual);
        previous = actual;
        actual = actual-1;
        return getMemento(actual);
    }

    public Memento redo(){
        if(previous<=actual) return getMemento(actual);
        if(previous == mementos.size()-1){
            actual = previous;
            previous = actual-1;
            return getMemento(actual);
        }
        actual = previous;
        previous = actual+1;
        return getMemento(actual);
    }

    public String getActualName(){
        return getMemento(actual).getName();
    }

    public String getPreviousName(){
        return getMemento(previous).getName();
    }
}
