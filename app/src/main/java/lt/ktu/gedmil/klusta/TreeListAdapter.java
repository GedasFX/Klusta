package lt.ktu.gedmil.klusta;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

        TextView textName = convertView.findViewById(R.id.textTreeName);
        TextView textLastAccessed = convertView.findViewById(R.id.textDateLastAccessed);

        textName.setText(tree.getName());
        textLastAccessed.setText(new Date(tree.getLastOpened()).toString());

        return convertView;
    }
}
