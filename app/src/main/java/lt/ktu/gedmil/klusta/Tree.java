package lt.ktu.gedmil.klusta;

class Tree {
    private String name;
    private long lastOpened; // Timestamp

    public Tree(String name) {
        this.lastOpened = System.currentTimeMillis() / 1000;
        this.name = name;
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
