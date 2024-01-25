package com.realgear.samplemusicplayertest.utils;

import com.realgear.samplemusicplayertest.ui.adapters.async.AsyncItemLoader;

import java.util.ArrayDeque;
import java.util.Queue;

public class AsyncDataLoader {

    private static AsyncDataLoader m_vInstance;

    Queue<AsyncItemLoader> m_vItems;

    public boolean isRunning;

    private AsyncDataLoader() {
        this.m_vItems = new ArrayDeque<>();
        this.isRunning = false;
    }

    public void Enqueue(AsyncItemLoader item) {
        if (!this.m_vItems.contains(item))
            this.m_vItems.add(item);

        if (!this.isRunning) {
            this.isRunning = true;
            Dequeue();
        }
    }

    public void onRemove(AsyncItemLoader item) {
        if (this.m_vItems.contains(item))
            this.m_vItems.remove(item);
    }

    public void Dequeue() {
        AsyncItemLoader item = this.m_vItems.peek();

        if (item == null) {
            isRunning = false;
            return;
        }

        item.onLoadData();
    }


    public static AsyncDataLoader getInstance() {
        if (m_vInstance == null) {
            m_vInstance = new AsyncDataLoader();
        }

        return m_vInstance;
    }
}
