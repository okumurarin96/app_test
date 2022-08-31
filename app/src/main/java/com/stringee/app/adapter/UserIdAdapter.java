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
import com.stringee.app.adapter.UserIdAdapter.UserIdHolder;
import com.stringee.app.listener.ItemClickListener;
import com.stringee.app.model.UserId;

import java.util.List;

public class UserIdAdapter extends Adapter<UserIdHolder> {
    private final List<UserId> data;
    private final LayoutInflater inflater;
    private ItemClickListener listener;

    public UserIdAdapter(Context context, List<UserId> data) {
        this.data = data;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserIdHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(layout.item_user_id, parent, false);
        return new UserIdHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserIdHolder holder, int position) {
        UserId userId = data.get(position);

        holder.tvId.setText(userId.getId());
        holder.ivCheck.setVisibility(userId.isSelected() ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class UserIdHolder extends ViewHolder implements OnClickListener, OnLongClickListener {
        ImageView ivCheck;
        TextView tvId;

        public UserIdHolder(View itemView) {
            super(itemView);
            ivCheck = itemView.findViewById(id.iv_check);
            tvId = itemView.findViewById(id.tv_id);

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
