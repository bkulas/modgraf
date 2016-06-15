package modgraf.memento;

/**
 * Created by Basia on 15.06.2016.
 *
 * @author Barbara Kulas
 */
public class Memento {
    private final String state;

    public Memento(String state) {
        super();
        this.state = state;
    }

    public String getState() {
        return state;
    }
}

