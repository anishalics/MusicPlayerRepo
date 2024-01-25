package com.realgear.samplemusicplayertest.ui.adapters.viewholders;

import java.util.HashSet;
import java.util.Set;

public class ViewHolderState {
    private static final ViewHolderState ourInstance = new ViewHolderState();
    private Set<Integer> recycled = new HashSet<>();

    private ViewHolderState() { }

    public static ViewHolderState getInstance() {
        return ourInstance;
    }

    public void addRecycledView(int position) {
        recycled.add(position);
    }

    public void removeRecycledView(int position) {
        recycled.remove(position);
    }

    public boolean isViewRecycled(int position) {
        return recycled.contains(position);
    }

    public void clearAll() {
        recycled.clear();
    }
}
