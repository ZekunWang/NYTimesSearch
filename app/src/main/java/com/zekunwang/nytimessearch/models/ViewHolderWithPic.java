package com.zekunwang.nytimessearch.models;

import com.zekunwang.nytimessearch.R;
import com.zekunwang.nytimessearch.adapters.ContactsAdapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

// Provide a direct reference to each of the views within a data item
// Used to cache the views within the item layout for fast access
public class ViewHolderWithPic extends RecyclerView.ViewHolder implements View.OnClickListener {
    @BindView(R.id.ivImage) ImageView ivImage;
    @BindView(R.id.tvTitle) TextView tvTitle;

    public ViewHolderWithPic(View view) {
        // Stores the itemView in a public final member variable that can be used
        // to access the context from any ViewHolder instance.
        super(view);
        ButterKnife.bind(this, view);
        view.setOnClickListener(this);
    }

    public ImageView getIvImage() {
        return ivImage;
    }

    public TextView getTvTitle() {
        return tvTitle;
    }

    @Override
    public void onClick(View v) {
        ContactsAdapter.getListener().onItemClick(v, this.getAdapterPosition());
    }
}
