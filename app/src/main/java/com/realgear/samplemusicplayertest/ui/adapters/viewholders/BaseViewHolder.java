package com.realgear.samplemusicplayertest.ui.adapters.viewholders;

import android.os.Handler;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.realgear.samplemusicplayertest.ui.adapters.BaseRecyclerViewAdapter;
import com.realgear.samplemusicplayertest.ui.adapters.async.AsyncItemLoader;
import com.realgear.samplemusicplayertest.ui.adapters.models.BaseRecyclerViewItem;

public abstract class BaseViewHolder extends RecyclerView.ViewHolder implements AsyncItemLoader {

    BaseRecyclerViewAdapter.ViewType m_vViewType;

    Handler m_vUIHandler;

    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);

        this.m_vUIHandler = new Handler();
    }

    public abstract void onInitializeView(BaseRecyclerViewAdapter.ViewType viewType);
    public abstract void onBindViewHolder(BaseRecyclerViewItem viewItem);

    public abstract void onBindData();

    public <T extends android.view.View> T findViewById(@IdRes int id) {
        return this.itemView.findViewById(id);
    }
}
