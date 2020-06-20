package org.jzl.android.library_no1.v4;

import android.util.SparseArray;

import org.jzl.lang.util.ObjectUtils;

public class Observable<O> {

    private SparseArray<O> observers = new SparseArray<>();

    public void register(int viewType, O observer) {
        if (ObjectUtils.nonNull(observer)) {
            observers.put(viewType, observer);
        }
    }

    public O getObserver(int viewType) {
        return observers.get(viewType);
    }

}
