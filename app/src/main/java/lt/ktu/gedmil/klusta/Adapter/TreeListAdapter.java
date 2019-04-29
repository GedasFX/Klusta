package lt.ktu.gedmil.klusta.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import lt.ktu.gedmil.klusta.R;
import lt.ktu.gedmil.klusta.Model.Tree;

public class TreeListAdapter extends ArrayAdapter<Tree> {
    private final int resource;

    public TreeListAdapter(Context context, int resource, List<Tree> objects) {
        super(context, resource, objects);
        this.resource = resource;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Tree tree = getItem(position);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(resource, parent, false);

        TextView textName = convertView.findViewById(R.id.textTreeListAdapterTreeName);
        TextView textLastAccessed = convertView.findViewById(R.id.textTreeListAdapterDateLastAccessed);

        convertView.findViewById(R.id.btnTreeListAdapterEditTree).setTag(tree);
        convertView.findViewById(R.id.btnTreeListAdapterDeleteTree).setTag(tree);
        convertView.findViewById(R.id.btnTreeListAdapterShareTree).setTag(tree);
        convertView.setTag(tree);

        assert tree != null;
        textName.setText(tree.getName());
        textLastAccessed.setText(new Date(tree.getLastOpened()).toString());

        return convertView;
    }
}
