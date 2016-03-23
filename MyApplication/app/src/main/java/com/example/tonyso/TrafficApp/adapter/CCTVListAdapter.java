package com.example.tonyso.TrafficApp.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tonyso.TrafficApp.R;
import com.example.tonyso.TrafficApp.model.RouteCCTV;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by soman on 2016/2/14.
 */
public class CCTVListAdapter extends RecyclerView.Adapter<CCTVListAdapter.ViewHolder> {

    private Context context;
    List<RouteCCTV> routeCCTVList;
    private static final String TRAFFIC_URL = "http://tdcctv.data.one.gov.hk/";
    private ImageLoader imageLoader;
    private DisplayImageOptions displayImageOptions;
    private SortedList<RouteCCTV> sortedList;

    public void setImageLoader(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    public void setDisplayImageOptions(DisplayImageOptions displayImageOptions) {
        this.displayImageOptions = displayImageOptions;
    }

    public CCTVListAdapter(List<RouteCCTV> routeCCTVList, Context context) {
        this.routeCCTVList = routeCCTVList;
        this.context = context;
        sortedList = new SortedList<RouteCCTV>(RouteCCTV.class, new SortedList.Callback<RouteCCTV>() {
            @Override
            public int compare(RouteCCTV o1, RouteCCTV o2) {
                double p1 = Double.parseDouble(o1.getDistance());
                double p2 = Double.parseDouble(o2.getDistance());
                return (p1 < p2) ? 1 : 0;
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(RouteCCTV oldItem, RouteCCTV newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areItemsTheSame(RouteCCTV item1, RouteCCTV item2) {
                return item1 == item2;
            }
        });

        sortedList.addAll(routeCCTVList);
    }


    @Override
    public CCTVListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sugggest_cctv_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CCTVListAdapter.ViewHolder holder, int position) {
        String url = TRAFFIC_URL + sortedList.get(position).getRef_key() + ".JPG";
        imageLoader.displayImage(url, holder.cctvImage, displayImageOptions);
        holder.title.setText(sortedList.get(position).getDescription()[1]);
        holder.distance.setText(String.format("%s %s %s",
                getResources().getString(R.string.distance),
                sortedList.get(position).getDistance(),
                getResources().getString(R.string.km)));
    }

    public Resources getResources() {
        return context.getResources();
    }

    @Override
    public int getItemCount() {
        return routeCCTVList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView cctvImage;
        TextView title, distance;

        public ViewHolder(View itemView) {
            super(itemView);
            cctvImage = (ImageView) itemView.findViewById(R.id.cctv);
            title = (TextView) itemView.findViewById(R.id.txtCCTVName);
            distance = (TextView) itemView.findViewById(R.id.txtCCTVDistance);
        }
    }
}
