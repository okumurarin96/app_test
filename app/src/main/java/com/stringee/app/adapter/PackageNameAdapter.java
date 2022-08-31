package com.stringee.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.stringee.app.R.id;
import com.stringee.app.R.layout;
import com.stringee.app.adapter.PackageNameAdapter.PackageNameHolder;
import com.stringee.app.listener.ItemClickListener;
import com.stringee.app.model.PackageName;

import java.util.List;

public class PackageNameAdapter extends Adapter<PackageNameHolder> {
    private final List<PackageName> data;
    private final LayoutInflater inflater;
    private ItemClickListener listener;

    public PackageNameAdapter(Context context, List<PackageName> data) {
        this.data = data;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public PackageNameHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(layout.item_package_name, parent, false);
        return new PackageNameHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PackageNameHolder holder, int position) {
        PackageName packageName = data.get(position);

        holder.tvName.setText(packageName.getName());
        holder.ivCheck.setVisibility(packageName.isSelected() ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class PackageNameHolder extends ViewHolder implements OnClickListener, OnLongClickListener {
        ImageView ivCheck;
        TextView tvName;

        public PackageNameHolder(View itemView) {
            super(itemView);
            ivCheck = itemView.findViewById(id.iv_check);
            tvName = itemView.findViewById(id.tv_name);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.onClick(view, this.getLayoutPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (listener != null) {
                listener.onLongClick(view, this.getLayoutPosition());
            }
            return false;
        }
    }
}
