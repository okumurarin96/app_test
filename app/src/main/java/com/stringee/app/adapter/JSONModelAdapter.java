package com.stringee.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.stringee.app.R.id;
import com.stringee.app.R.layout;
import com.stringee.app.adapter.JSONModelAdapter.JSONModelHolder;
import com.stringee.app.listener.ItemClickListener;
import com.stringee.app.model.JSONModel;

import java.util.List;

public class JSONModelAdapter extends Adapter<JSONModelHolder> {
    private final List<JSONModel> data;
    private final LayoutInflater inflater;
    private ItemClickListener listener;

    public JSONModelAdapter(Context context, List<JSONModel> data) {
        this.data = data;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public JSONModelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(layout.item_json_model, parent, false);
        return new JSONModelHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull JSONModelHolder holder, int position) {
        JSONModel customMessage = data.get(position);

        holder.tvKey.setText(customMessage.getKey());
        holder.tvValue.setText(customMessage.getValue());
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class JSONModelHolder extends ViewHolder implements OnLongClickListener {
        TextView tvKey;
        TextView tvValue;

        public JSONModelHolder(View itemView) {
            super(itemView);
            tvKey = itemView.findViewById(id.tv_key);
            tvValue = itemView.findViewById(id.tv_value);

            itemView.setOnLongClickListener(this);
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
