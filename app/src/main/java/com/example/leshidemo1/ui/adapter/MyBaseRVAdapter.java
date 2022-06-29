package com.example.leshidemo1.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 */

public abstract class MyBaseRVAdapter<T, H extends RecyclerView.ViewHolder> extends RecyclerView.Adapter {
    protected Context mContext;
    protected LayoutInflater mInflater;
    protected List<T> mList;
    protected boolean mView;
    protected OnItemClickListener mOnItemClickListener;
    protected OnItemLongClickListener mOnItemLongClickListener;
    protected OnItemRemoveClickLinstener mOnItemRemoveClickLinstener;
    protected final static String INDEX = "index";
    public static final int TYPE_ITEM = 0;

    public MyBaseRVAdapter(Context context, List<T> list) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        updateList(list);
    }
    public MyBaseRVAdapter(Context context, List<T> list, boolean mView) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        updateList(list);
    }
    /**
     * 刷新列表
     */
    public void updateList(List<T> list) {
        if (list == null)
            list = new ArrayList<>();
        mList = list;
        notifyDataSetChanged();
    }
    /**
     * 更新某个item
     */
    public void updateList(T list,int position) {
        mList.set(position,list);
        notifyDataSetChanged();
    }
    /**
     * 刷新列表
     */
    public void updateList(List<T> list,boolean view) {
        mView = view;
        mList = list;
        notifyDataSetChanged();
    }
    /**
     * 刷新列表
     */
    public void updateList() {
        notifyDataSetChanged();
    }
    /**
     * 指定位置添加数据
     */
    public void add(int index,T data) {
        if (mList == null)
            mList = new ArrayList<>();
        mList.add(index,data);
        notifyDataSetChanged();
    }
    /**
     * 添加数据
     */
    public void add(T data) {
        if (mList == null)
            mList = new ArrayList<>();
        mList.add(data);
        notifyDataSetChanged();
    }
    /**
     * 删除数据
     */
    public void remove(int position) {
        if (mList == null)
            mList = new ArrayList<>();
        mList.remove(position);
        notifyDataSetChanged();
    }
    /**
     * 删除数据
     * item上移动画
     */
    public void removeAnimator(int position) {
        if (mList == null)
            mList = new ArrayList<>();
        mList.remove(position);
        notifyItemRemoved(position);
    }
    public List<T> getList() {
        if (mList == null)
            mList = new ArrayList<>();
        return mList;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }


    /**
     * 创建
     */
    protected abstract H ICreateItemHolder(ViewGroup parent, int viewType);

    /**
     * 加载
     */
    protected abstract void IBindItemHolder(H holder, int position);

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return ICreateItemHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        IBindItemHolder((H) holder, position);
    }


    /**
     * @param onItemClickListener 设置列表点击监听
     */
    public void setOnItemClickListeners(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    /**
     * @param mOnItemLongClickListener 列表长按监听
     */
    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    /**
     * 点击监听
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    /**
     * 长按监听
     */
    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    /**
     * 点击删除监听
     */
    public interface OnItemRemoveClickLinstener {
        void onItemRemoveClick(View view, int position);
    }
    /**
     * @param onItemRemoveClickLinstener 设置列表点击删除监听
     */
    public void setOnItemRemoveClickLinstener(OnItemRemoveClickLinstener onItemRemoveClickLinstener) {
        mOnItemRemoveClickLinstener = onItemRemoveClickLinstener;
    }

}
