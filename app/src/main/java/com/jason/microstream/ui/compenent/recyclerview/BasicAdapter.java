package com.jason.microstream.ui.compenent.recyclerview;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public abstract class BasicAdapter<H extends BasicHolder<D>,D extends BasicHolder.Item> extends RecyclerView.Adapter<H>
        implements LoadMoreHolder.LoadMoreListener {

    public static final class ViewType{
        public static final int VIEW_LOAD = -2;
        public static final int VIEW_HEADER = -3;
        public static final int VIEW_FOOTER = -4;
    }

    ArrayList<D> items;
    public BasicAdapter(ArrayList<D> items) {
        this.items = items;
    }

    public BasicAdapter(ArrayList<D> items,ItemClickListener<D> itemClickListener) {
        this.items = items;
        this.itemClickListener = itemClickListener;
    }


    @NonNull
    @Override
    public H onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        if (enableLoadMore) {
            if (viewType == ViewType.VIEW_LOAD) {
                LoadMoreHolder loadMoreHolder = new LoadMoreHolder(parent,this);
                return (H) loadMoreHolder;
            }
        }
        if (viewType == ViewType.VIEW_HEADER) {
            if (headerHolder == null) {
                headerHolder = new HeaderHolder(parent);
                ArrayList<View> views = new ArrayList<>();
                for (View headerView : headerViews) {
                    views.add(headerView);
                }
                headerHolder.addHeaderViews(views);
            }
            return (H) headerHolder;
        }  else if (viewType == ViewType.VIEW_FOOTER) {
            if (footerHolder == null) {
                footerHolder = new FooterHolder(parent);
                ArrayList<View> views = new ArrayList<>();
                for (View footerView : footerViews) {
                    views.add(footerView);
                }
                footerHolder.addHeaderViews(views);
            }
            return (H) footerHolder;
        }

        return onCreateHolder(parent, viewType);
    }

    public abstract H onCreateHolder(@NonNull ViewGroup parent, int viewType);

    boolean loadMoreFlag;
    @Override
    public void onBindViewHolder(@NonNull H holder, int position) {
        if (items == null) return;

        if (holder instanceof LoadMoreHolder) {
            if (loadMoreItem.status == LoadMoreHolder.Item.Status.NO_LOAD && items.size() > 0) {
                loadMoreItem.status = LoadMoreHolder.Item.Status.LOADING;
                if (!loadMoreFlag) {
                    loadMoreFlag = true;
                    if (loadMoreListener != null) {
                        // getRecyclerView()
                        loadMoreListener.onLoadMore();
                    }
                }
            }
            holder.bindDataBase(items, (D) loadMoreItem, position);
        } else if (holder instanceof HeaderHolder) {
            holder.bindDataBase(items, null, position);
        } else if (holder instanceof FooterHolder) {
            holder.bindDataBase(items, null, position);
        } else {
            holder.bindDataBase(items, items.get(getDataItemPosition(position)), getDataItemPosition(position));
        }
    }

    private int getDataItemPosition(int sourcePosition) {
        return sourcePosition - headerCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (enableLoadMore && position == getLoadMoreItemPosition()) {
            return ViewType.VIEW_LOAD;
        }
        if (footerCount != 0 && position == getFooterItemPosition()) {
            return ViewType.VIEW_FOOTER;
        }
        if (headerCount != 0 && position == getHeaderItemPosition()) {
            return ViewType.VIEW_HEADER;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return getActualItemsSize();
    }




    private int getLoadMoreItemPosition() { //loadMore item in last
        return (getActualItemsSize() - 1 - footerCount);
    }
    private int getFooterItemPosition() {
        return (getActualItemsSize() - 1); //footer item in last
    }
    private int getHeaderItemPosition() { //header item in first
        return (headerCount-1);
    }
    private int headerCount = 0;
    private int footerCount = 0;
    private int loadMoreCount = 0;
    private int getActualItemsSize() {
        if (items != null) {
            return items.size() + headerCount + footerCount + loadMoreCount;
        }
        return headerCount + footerCount + loadMoreCount;
    }


    //header view///////////////////////////////////////////////////////////////////////////
    ArrayList<View> headerViews = new ArrayList<>();
    HeaderHolder headerHolder;
    public void addHeader(View headerView) {
        headerCount = 1;
        headerViews.add(headerView);
    }
    //footer view///////////////////////////////////////////////////////////////////////////
    ArrayList<View> footerViews = new ArrayList<>();
    FooterHolder footerHolder;
    public void addFooter(View footerView) {
        footerCount = 1;
        footerViews.add(footerView);
    }


    //load more/////////////////////////////////////////////////////////////////
    private boolean enableLoadMore;
    LoadMoreHolder.Item loadMoreItem;
    public void enableLoadMore(boolean enableLoadMore) {
        this.enableLoadMore = enableLoadMore;
        loadMoreCount = 1;
        loadMoreItem = new LoadMoreHolder.Item();
        loadMoreItem.status = LoadMoreHolder.Item.Status.NO_LOAD;
        loadMoreFlag = false;
    }

    public void loadMoreComplete() {
        if (loadMoreItem != null) {
            loadMoreItem.status = LoadMoreHolder.Item.Status.NO_LOAD;
            if (items != null) {
                notifyItemChanged(items.size());
            }
        }
        loadMoreFlag = false;
    }

    public void loadMoreEnd() {
        if (loadMoreItem != null) {
            loadMoreItem.status = LoadMoreHolder.Item.Status.LOAD_END;
            if (items != null) {
                notifyItemChanged(items.size());
            }
        }
        loadMoreFlag = false;
    }

    public void loadMoreFail() {
        if (loadMoreItem != null) {
            loadMoreItem.status = LoadMoreHolder.Item.Status.LOAD_FAIL;
            if (items != null) {
                notifyItemChanged(items.size());
            }
        }
        loadMoreFlag = false;
    }
    @Override
    public void onClickReload() {
        if (loadMoreListener != null) {
            loadMoreFlag = true;
            loadMoreItem.status = LoadMoreHolder.Item.Status.NO_LOAD;
            notifyItemChanged(items.size());
            loadMoreListener.onClickReload();
        }
    }

    @Override
    public void onLoadMore() {
        if (loadMoreListener != null) {
            loadMoreListener.onLoadMore();
        }
    }

    LoadMoreHolder.LoadMoreListener loadMoreListener;
    public void setLoadMoreListener(LoadMoreHolder.LoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }


    //item click listener/////////////////////////////////////////////////////////////////
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
        void onItemChildLongClick(View view, D item, int position);
    }



}
