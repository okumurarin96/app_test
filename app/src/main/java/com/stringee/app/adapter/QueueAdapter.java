package com.stringee.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.stringee.app.R.id;
import com.stringee.app.R.layout;
import com.stringee.app.adapter.QueueAdapter.QueueHolder;
import com.stringee.app.listener.ItemClickListener;
import com.stringee.app.model.QueueObject;

import java.util.List;

public class QueueAdapter extends Adapter<QueueHolder> {
    private final List<QueueObject> data;
    private final android.view.LayoutInflater inflater;
    private ItemClickListener listener;

    public QueueAdapter(Context context, List<QueueObject> data) {
        this.data = data;
        this.inflater = (android.view.LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public QueueHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(layout.item_queue, parent, false);
        return new QueueHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull QueueHolder holder, int position) {
        QueueObject queue = data.get(position);
        holder.tvValue.setText(queue.getQueue().getName());
        holder.ivCheck.setVisibility(queue.isSelected() ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class QueueHolder extends ViewHolder implements View.OnClickListener {
        TextView tvValue;
        ImageView ivCheck;

        public QueueHolder(View itemView) {
            super(itemView);
            tvValue = itemView.findViewById(id.tv_value);
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
