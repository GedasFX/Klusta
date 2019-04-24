package lt.ktu.gedmil.klusta.Model;

public class TreeElement {
    private int id;
    private int treeId;

    private String bigText;
    private String smallText;

    private Integer redirect;

    public TreeElement(int id, int treeId, String bigText, String smallText, Integer redirect) {
        this.id = id;
        this.treeId = treeId;
        this.bigText = bigText;
        this.smallText = smallText;
        this.redirect = redirect;
    }

    public TreeElement(int id, int treeId) {
        this.id = id;
        this.treeId = treeId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBigText() {
        return bigText;
    }

    public void setBigText(String bigText) {
        this.bigText = bigText;
    }

    public String getSmallText() {
        return smallText;
    }

    public void setSmallText(String smallText) {
        this.smallText = smallText;
    }

    public int getTreeId() {
        return treeId;
    }

    public void setTreeId(int treeId) {
        this.treeId = treeId;
    }

    public Integer getRedirect() {
        return redirect;
    }

    public void setRedirect(Integer redirect) {
        this.redirect = redirect;
    }
}
