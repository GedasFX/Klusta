package lt.ktu.gedmil.klusta.Model;

public class Tree {
    private int id;
    private String name;
    private long lastOpened; // Timestamp

    public Tree(String name) {
        this.lastOpened = System.currentTimeMillis() / 1000;
        this.name = name;
    }

    public Tree(int id, String name, long lastOpened) {
        this.id = id;
        this.name = name;
        this.lastOpened = lastOpened;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastOpened(long lastOpened) {
        this.lastOpened = lastOpened;
    }

    public String getName() {
        return name;
    }

    public long getLastOpened() {
        return lastOpened;
    }
}
