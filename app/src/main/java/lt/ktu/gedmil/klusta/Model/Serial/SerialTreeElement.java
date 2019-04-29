package lt.ktu.gedmil.klusta.Model.Serial;

import com.google.gson.Gson;

public class SerialTreeElement {
    private SerialTreeElement l; // Left
    private SerialTreeElement r; // Right

    private String b;   // Big Text
    private String s;   // Small Text

    public SerialTreeElement(SerialTreeElement left, SerialTreeElement right, String bigText, String smallText) {
        this.l = left;
        this.r = right;
        this.b = bigText;
        this.s = smallText;
    }

    public SerialTreeElement(String b, String s) {
        this.b = b;
        this.s = s;
    }

    public SerialTreeElement getLeft() {
        return l;
    }

    public SerialTreeElement getRight() {
        return r;
    }

    public String getBigText() {
        return b;
    }

    public String getSmallText() {
        return s;
    }

    public void setLeft(SerialTreeElement l) {
        this.l = l;
    }

    public void setRight(SerialTreeElement r) {
        this.r = r;
    }

    public static SerialTreeElement deserialize(String json) {
        return new Gson().fromJson(json, SerialTreeElement.class);
    }

    public String serialize() {
        return new Gson().toJson(this);
    }

}
