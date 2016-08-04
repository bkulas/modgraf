package modgraf.memento;

/**
 * Created by Basia on 15.06.2016.
 *
 * @author Barbara Kulas
 */
public class Memento {
    private final int number;
    private final String state;
    private final String name;

    public Memento(int number, String state, String name) {
        super();
        this.number = number;
        this.state = state;
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public String getState() {
        return state;
    }

    public String getName() {
        return name;
    }
}

