package modgraf.memento;

/**
 * Created by Basia on 15.06.2016.
 *
 * @author Barbara Kulas
 */
public class Memento {
    private final int number;
    private final String state;

    public Memento(int number, String state) {
        super();
        this.number = number;
        this.state = state;
    }

    public int getNumber() {
        return number;
    }

    public String getState() {
        return state;
    }
}

