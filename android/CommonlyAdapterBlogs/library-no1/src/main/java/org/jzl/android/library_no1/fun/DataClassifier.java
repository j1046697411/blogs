package org.jzl.android.library_no1.fun;

@FunctionalInterface
public interface DataClassifier<T> {

    int getItemType(int position, T data);

}
