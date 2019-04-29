package lt.ktu.gedmil.klusta.Model;

import android.support.annotation.NonNull;
import android.util.SparseArray;

import java.util.Iterator;
import java.util.List;

public class TreeContainer extends SparseArray<TreeElement> implements Iterable<TreeElement> {
    private final int mTreeId;

    public TreeContainer(int treeId, List<TreeElement> treeList) {
        this(treeId);

        for (TreeElement te : treeList) {
            this.append(te.getId(), te);
        }
    }

    public TreeContainer(int treeId) {
        super();
        this.mTreeId = treeId;
    }

    public TreeElement getHead() {
        for (TreeElement te : this)
            if (te.getParentId() == null)
                return te;
        return null;
    }

    @NonNull
    @Override
    public Iterator<TreeElement> iterator() {
        return new Iterator<TreeElement>() {
            private int current = 0;

            @Override
            public boolean hasNext() {
                return current < TreeContainer.this.size();
            }

            @Override
            public TreeElement next() {
                return TreeContainer.this.get(TreeContainer.this.keyAt(current++));
            }
        };
    }
}
