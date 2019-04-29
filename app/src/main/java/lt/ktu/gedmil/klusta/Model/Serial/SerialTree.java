package lt.ktu.gedmil.klusta.Model.Serial;

import com.google.gson.Gson;

public class SerialTree {
    private String name;
    private SerialTreeElement e; // Element tree

    public SerialTree(String name, SerialTreeElement elementTree) {
        this.name = name;
        this.e = elementTree;
    }

    public static SerialTree deserialize(String json) {
        return new Gson().fromJson(json, SerialTree.class);
    }

    public String serialize() {
        return new Gson().toJson(this);
    }

    public SerialTreeElement getElementTree() {
        return e;
    }

    public String getName() {
        return name;
    }
}
