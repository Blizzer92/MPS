package de.sven.mps_mobilepasswordsafe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sven on 27.10.15.
 */
public class CustomListAdapter extends BaseAdapter {

    private ArrayList<Metadaten> listData;

    private LayoutInflater layoutInflater;

    private Context mContext;

    public CustomListAdapter(Context context, ArrayList<Metadaten> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_row_layout, null);
            holder = new ViewHolder();
            holder.headlineView = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Metadaten newsItem = (Metadaten) listData.get(position);
        holder.headlineView.setText(newsItem.get_site());

        return convertView;
    }

    static class ViewHolder {
        TextView headlineView;

    }
}




