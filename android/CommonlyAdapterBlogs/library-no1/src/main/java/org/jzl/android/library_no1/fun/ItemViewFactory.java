package org.jzl.android.library_no1.fun;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

@FunctionalInterface
public interface ItemViewFactory {
    View createItemView(LayoutInflater layoutInflater, ViewGroup parent);
}
