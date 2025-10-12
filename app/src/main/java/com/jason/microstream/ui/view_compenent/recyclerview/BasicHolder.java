package com.jason.microstream.ui.view_compenent.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * itemClick itemChildClick and its longClick
 */
public abstract class BasicHolder<D extends BasicHolder.Item> extends RecyclerView.ViewHolder
        implements View.OnClickListener, View.OnLongClickListener {
    /**
     * block the construct func
     * @param itemView
     */
    private BasicHolder(@NonNull View itemView) {
        super(itemView);
        bindView();
    }
    public BasicHolder(ViewGroup parent, @LayoutRes int itemViewId) {
        super(LayoutInflater.from(parent.getContext()).inflate(itemViewId,parent,false));
        bindView();
    }
    private BasicAdapter.ItemClickListener<D> itemClickListener;
    private BasicAdapter.ItemChildClickListener<D> itemChildClickListener;
//    public BasicHolder(Context context, ViewGroup parent,@LayoutRes int itemViewId
//            ,BasicAdapter.ItemClickListener<D> itemClickListener) {
//        super(LayoutInflater.from(context).inflate(itemViewId, parent, false));
//        createInit(itemClickListener,null);
//    }
    public BasicHolder(ViewGroup parent,@LayoutRes int itemViewId
            ,BasicAdapter.ItemClickListener<D> itemClickListener
            ,BasicAdapter.ItemChildClickListener<D> itemChildClickListener) {
        super(LayoutInflater.from(parent.getContext()).inflate(itemViewId, parent, false));
        createInit(itemClickListener,itemChildClickListener);
    }
    private void createInit(BasicAdapter.ItemClickListener<D> itemClickListener
            ,BasicAdapter.ItemChildClickListener<D> itemChildClickListener) {
        this.itemClickListener = itemClickListener;
        this.itemChildClickListener = itemChildClickListener;
        if (itemClickListener != null) {
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }
        bindView();
    }



    /**
     * include click & findView etc.
     */
    protected abstract void bindView();
//    public abstract void bindData(D item);

    ArrayList<D> items;
    D item;
    int position;
    void bindDataBase(ArrayList<D> items, D item,int position) {
        this.items = items;
        this.item = item;
        this.position = position;
        bindData(item, position);
    }

    public abstract void bindData(D item, int position);


    public ArrayList<D> getItems() {
        return items;
    }

    public void addChildClick(View view) {
        view.setOnClickListener(this);
    }




    @Override
    public void onClick(View view) {
        if (itemView == view) {
            itemClickListener.onItemClick(item, position);
        } else {
            if (itemChildClickListener != null) {
                itemChildClickListener.onItemChildClick(view, item, position);
            }
        }
    }
    @Override
    public boolean onLongClick(View view) {
        if (itemView == view) {
            itemClickListener.onItemLongClick(item, position);
        } else {
            if (itemChildClickListener != null) {
                itemChildClickListener.onItemChildLongClick(view, item, position);
            }
        }
        return true;
    }


    public static class Item{

    }

}
