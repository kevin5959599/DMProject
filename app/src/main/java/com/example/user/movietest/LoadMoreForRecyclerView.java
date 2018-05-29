package com.example.user.movietest;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by jt3282 on 2018/1/2.
 */

public class LoadMoreForRecyclerView {
    //最後一個顯示的item的position
    private int lastVisibleItemPosition;
    //觸摸記錄之前的觸摸位置
    private float oldY;
    //滑動狀態
    private int state;
    //滑動時Y軸偏移（大於0表示向上滑動）
    private int offsetY = 0;
    //touch事件中移動的距離（當前觸摸的位置-oldY）
    private float moveY;
    /**
     * 構造方法
     * @param recyclerView
     * @param loadMoreListener
     */
    public LoadMoreForRecyclerView(RecyclerView recyclerView, LoadMoreListener loadMoreListener) {
        width(recyclerView, loadMoreListener);
    }
    /**
     *
     * @param recyclerView recyclerview
     * @param loadMoreListener 回調監聽
     */
    public void width(final RecyclerView recyclerView, final LoadMoreListener loadMoreListener) {
        mLoadMoreListener = loadMoreListener;
        //線性布局管理
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        //recyclerview設置滑動監聽
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //滑動狀態
                state = newState;
                //獲得最後一個顯示的item位置
                lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //記錄Y軸偏移量
                offsetY = dy;
            }
        });
        //設置touch監聽
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        //計算出移動的距離（小於0表示向上拖動）
                        moveY = motionEvent.getY() - oldY;
                        //記錄當前觸摸的位置
                        oldY = motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_UP:

                        //如果是拖動或者飛快滑動並且顯示的是適配器中最後一條數據
                        if ((state == 1 || state == 2)&& lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1) {
                            if (offsetY > 0) {//如果偏移量大於0
                                if (mLoadMoreListener != null) {
                                    //回調加載更多監聽
                                    Log.i("if1", "herher");
                                    mLoadMoreListener.loadListener();
                                }
                            } else if (offsetY == 0) {//如果偏移量一直等於0
                                Log.i("if2", "herher");
                                if ((moveY) < 0) {//手指移動的距離小於0（向上拖動recyclerView）
                                    if (mLoadMoreListener != null) {
                                        //回調加載更多
                                        mLoadMoreListener.loadListener();
                                    }
                                }
                            }
                        }
                        break;
                }
                //返回值要為false，不會攔截滑動事件
                return false;
            }
        });
    }
    private LoadMoreListener mLoadMoreListener;
    /**
     * 回調接口
     */
    public interface LoadMoreListener {
        void loadListener();
    }
}
