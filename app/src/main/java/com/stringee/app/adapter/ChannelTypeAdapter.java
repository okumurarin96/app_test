package com.stringee.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.stringee.app.R.id;
import com.stringee.app.R.layout;
import com.stringee.app.adapter.ChannelTypeAdapter.ChannelTypeHolder;
import com.stringee.app.listener.ItemClickListener;
import com.stringee.app.model.ChannelType;

import java.util.List;

public class ChannelTypeAdapter extends Adapter<ChannelTypeHolder> {
    private final List<ChannelType> data;
    private final LayoutInflater inflater;
    private ItemClickListener listener;

    public ChannelTypeAdapter(Context context, List<ChannelType> data) {
        this.data = data;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChannelTypeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(layout.item_channel_type, parent, false);
        return new ChannelTypeHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChannelTypeHolder holder, int position) {
        holder.tvName.setText(data.get(position).getChannelType().name());
        holder.ivCheck.setVisibility(data.get(position).isSelected() ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ChannelTypeHolder extends ViewHolder implements OnClickListener {
        TextView tvName;
        ImageView ivCheck;

        public ChannelTypeHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(id.tv_name);
            ivCheck = itemView.findViewById(id.iv_check);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.onClick(view, this.getLayoutPosition());
            }
        }
    }
}
