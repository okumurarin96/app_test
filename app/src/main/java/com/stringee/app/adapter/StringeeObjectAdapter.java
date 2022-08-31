package com.stringee.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.stringee.app.R.id;
import com.stringee.app.R.layout;
import com.stringee.app.adapter.StringeeObjectAdapter.StringeeHolder;
import com.stringee.app.listener.ItemClickListener;
import com.stringee.messaging.ChatRequest;
import com.stringee.messaging.Conversation;
import com.stringee.messaging.Message;

import java.util.ArrayList;
import java.util.List;

public class StringeeObjectAdapter<T> extends Adapter<StringeeHolder> {
    private List<T> data = new ArrayList<>();
    private LayoutInflater inflater;
    private ItemClickListener listener;

    public StringeeObjectAdapter(Context context, List<T> data) {
        this.data = data;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public StringeeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(layout.item_stringee_object, parent, false);
        return new StringeeHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull com.stringee.app.adapter.StringeeObjectAdapter.StringeeHolder holder, int position) {
        T object = data.get(position);
        if (object instanceof Message) {
            Message message = (Message) object;
            holder.tvId.setText(message.getId());
        } else {
            if (object instanceof Conversation) {
                Conversation conversation = (Conversation) object;
                holder.tvId.setText(conversation.getId());
            } else {
                ChatRequest chatRequest = (ChatRequest) object;
                holder.tvId.setText(chatRequest.getConvId());
            }
        }
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class StringeeHolder extends ViewHolder implements OnClickListener, OnLongClickListener {
        TextView tvId;

        public StringeeHolder(View itemView) {
            super(itemView);
            tvId = itemView.findViewById(id.tv_id);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.onClick(view, getLayoutPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (listener != null) {
                listener.onLongClick(view, getLayoutPosition());
            }
            return false;
        }
    }
}
