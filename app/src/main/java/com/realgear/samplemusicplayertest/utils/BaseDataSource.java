package com.realgear.samplemusicplayertest.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.paging.PagedList;
import androidx.paging.PositionalDataSource;

import com.realgear.samplemusicplayertest.ui.adapters.models.BaseRecyclerViewItem;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class BaseDataSource extends PositionalDataSource<BaseRecyclerViewItem> {

    private Context m_vContext;

    private PagedList<?> m_vItems;

    private Class<?> m_vSubClass;
    private Class<?> m_vParameter;

    public BaseDataSource(Context context, PagedList<?> items, Class<?> subClass, Class<?> parameter) {
        this.m_vItems = items;
        this.m_vContext = context;

        this.m_vSubClass = subClass;
        this.m_vParameter = parameter;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<BaseRecyclerViewItem> callback) {
        List<BaseRecyclerViewItem> items = new ArrayList<>();

        for (Object item : m_vItems.subList(params.requestedStartPosition, params.requestedLoadSize)) {
            BaseRecyclerViewItem temp_item = getItem(item);

            if (item != null) {
                temp_item.onCache(this.m_vContext);
                items.add(temp_item);
            }
        }

        int requestSize = params.requestedLoadSize;
        if ((params.requestedStartPosition + requestSize) > items.size()) {
            requestSize = items.size() - params.requestedStartPosition;
        }

        callback.onResult(items, params.requestedStartPosition, requestSize);
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<BaseRecyclerViewItem> callback) {
        List<BaseRecyclerViewItem> items = new ArrayList<>();

        int fromIndex = params.startPosition;
        int toIndex = fromIndex + params.loadSize;

        if (toIndex >= this.m_vItems.size()) {
            int tempIndex = toIndex - (toIndex - this.m_vItems.size());
            toIndex = tempIndex;
        }

        for (Object item : m_vItems.subList(fromIndex, toIndex)) {
            BaseRecyclerViewItem temp_item = getItem(item);

            if (item != null) {
                temp_item.onCache(this.m_vContext);
                items.add(temp_item);
            }
        }

        if (m_vItems.size() > toIndex)
            m_vItems.loadAround(toIndex);

        callback.onResult(items);
    }

    public <T extends BaseRecyclerViewItem> BaseRecyclerViewItem getItem(Object db_item) {
        Constructor<?> constructor;
        try {
            constructor = m_vSubClass.getDeclaredConstructor(m_vParameter);
            return (T)constructor.newInstance(db_item);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }

    }
}
