package lt.ktu.gedmil.klusta.Model;

public class TreeElement {
    private Integer id;
    private int treeId;

    private Integer parentId;
    private Integer leftId;
    private Integer rightId;

    private String bigText;
    private String smallText;

    private Integer redirect;

    public TreeElement(Integer id, int treeId, String bigText, String smallText, Integer redirect) {
        this.id = id;
        this.treeId = treeId;
        this.bigText = bigText;
        this.smallText = smallText;
        this.redirect = redirect;
    }

    public TreeElement(Integer id, int treeId, String bigText, String smallText, Integer redirect, Integer parentId) {
        this(id, treeId, bigText, smallText, redirect);
        this.parentId = parentId;
    }

    public TreeElement(Integer id, int treeId, String bigText, String smallText, Integer redirect, Integer parentId, Integer left, Integer right) {
        this(id, treeId, bigText, smallText, redirect, parentId);
        this.leftId = left;
        this.rightId = right;
    }

    public TreeElement(Integer id, int treeId) {
        this(treeId);
        this.id = id;
    }

    public TreeElement(int treeId) {
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

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getLeftId() {
        return leftId;
    }

    public void setLeftId(Integer leftId) {
        this.leftId = leftId;
    }

    public Integer getRightId() {
        return rightId;
    }

    public void setRightId(Integer rightId) {
        this.rightId = rightId;
    }
}
