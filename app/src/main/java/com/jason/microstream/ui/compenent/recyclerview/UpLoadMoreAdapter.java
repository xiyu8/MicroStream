package com.jason.microstream.ui.compenent.recyclerview;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public abstract class UpLoadMoreAdapter<H extends BasicHolder<D>,D extends BasicHolder.Item> extends RecyclerView.Adapter<H> 
        implements UpLoadMoreHolder.UpLoadMoreListener {

    public static final class ViewType{
        public static final int VIEW_LOAD = -2;
        public static final int VIEW_HEADER = -3;
        public static final int VIEW_FOOTER = -4;
        public static final int VIEW_UP_LOAD = -5;
    }

    ArrayList<D> items;
    public UpLoadMoreAdapter(ArrayList<D> items) {
        this.items = items;
    }
    public UpLoadMoreAdapter(ArrayList<D> items, ItemClickListener<D> itemClickListener) {
        this.items = items;
        this.itemClickListener = itemClickListener;
    }


    @NonNull
    @Override
    public H onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        if (enableUpLoadMore) {
            if (viewType == ViewType.VIEW_UP_LOAD) {
                UpLoadMoreHolder upLoadMoreHolder = new UpLoadMoreHolder(parent.getContext(), parent,this);
                return (H) upLoadMoreHolder;
            } else {
                return onCreateHolder(parent, viewType);
            }
        }
        return onCreateHolder(parent, viewType);
    }

    public abstract H onCreateHolder(@NonNull ViewGroup parent, int viewType);

    boolean upLoadMoreFlag;
    @Override
    public void onBindViewHolder(@NonNull H holder, int position) {
        if(items==null) return;

//        if ( enableUpLoadMore && position == items.size() && items.size() > 0) {
        if ( holder instanceof UpLoadMoreHolder) {
            if (upLoadMoreItem.status == UpLoadMoreHolder.Item.Status.NO_LOAD && items.size() > 0) {
                upLoadMoreItem.status = UpLoadMoreHolder.Item.Status.LOADING;
                if (!upLoadMoreFlag) {
                    upLoadMoreFlag = true;
                    if (upLoadMoreListener != null) {
                        // getRecyclerView()
                        upLoadMoreListener.onUpLoadMore();
                    }
                }
            }
            holder.bindDataBase(items, (D) upLoadMoreItem, position);
        } else {
            holder.bindDataBase(items, items.get(getDataItemPosition(position)), position);
        }
    }

    private int getDataItemPosition(int position) {
        if (enableUpLoadMore) {
            position -= 1;
        }
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (enableUpLoadMore && position == getLoadMoreItemPosition()) {
            return ViewType.VIEW_LOAD;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        if (enableUpLoadMore) {
            return items.size() + 1;
        }
        return items.size();
    }




    private int getLoadMoreItemPosition() {
        return 0; //uploadMore item in first
    }
    private int headerSize = 0;
    private int footerSize = 0;
    private int upLoadMoreSize = 1;
    private int getActualItemsSize() {
        if (items != null) {
            return items.size() + headerSize + footerSize + upLoadMoreSize;
        }
        return headerSize + footerSize + upLoadMoreSize;
    }




    private boolean enableUpLoadMore;
    UpLoadMoreHolder.Item upLoadMoreItem;
    public void enableUpLoadMore(boolean enableUpLoadMore) {
        this.enableUpLoadMore = enableUpLoadMore;
        upLoadMoreItem = new UpLoadMoreHolder.Item();
        upLoadMoreItem.status = UpLoadMoreHolder.Item.Status.NO_LOAD;
        upLoadMoreFlag = false;
    }

    public void upLoadMoreComplete() {
        if (upLoadMoreItem != null) {
            upLoadMoreItem.status = UpLoadMoreHolder.Item.Status.NO_LOAD;
            notifyItemChanged(0);
        }
        upLoadMoreFlag = false;
    }

    public void upLoadMoreEnd() {
        if (upLoadMoreItem != null) {
            upLoadMoreItem.status = UpLoadMoreHolder.Item.Status.LOAD_END;
            notifyItemChanged(0);
        }
        upLoadMoreFlag = false;
    }

    public void upLoadMoreFail() {
        if (upLoadMoreItem != null) {
            upLoadMoreItem.status = UpLoadMoreHolder.Item.Status.LOAD_FAIL;
            notifyItemChanged(0);
        }
        upLoadMoreFlag = false;
    }

    @Override
    public void onClickUpReload() {
        if (upLoadMoreListener != null) {
            upLoadMoreFlag = true;
            upLoadMoreItem.status = UpLoadMoreHolder.Item.Status.NO_LOAD;
            notifyItemChanged(items.size());
            upLoadMoreListener.onClickUpReload();
        }
    }

    @Override
    public void onUpLoadMore() {
        if (upLoadMoreListener != null) {
            upLoadMoreListener.onUpLoadMore();
        }
    }

    UpLoadMoreHolder.UpLoadMoreListener upLoadMoreListener;
    public void setUpLoadMoreListener(UpLoadMoreHolder.UpLoadMoreListener upLoadMoreListener) {
        this.upLoadMoreListener = upLoadMoreListener;
    }




    protected ItemClickListener<D> itemClickListener;
    protected ItemChildClickListener<D> itemChildClickListener;
    public void setItemClickListener(ItemClickListener<D> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
    public void setItemChildClickListener(ItemChildClickListener<D> itemChildClickListener) {
        this.itemChildClickListener = itemChildClickListener;
    }

    public interface ItemClickListener<D>{
        void onItemClick(D item,int position);
        void onItemLongClick(D item,int position);
    }

    public interface ItemChildClickListener<D>{
        void onItemChildClick(View view, D item, int position);
    }



}
