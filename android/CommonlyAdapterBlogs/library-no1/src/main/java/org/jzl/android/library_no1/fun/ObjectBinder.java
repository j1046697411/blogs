package org.jzl.android.library_no1.fun;

@FunctionalInterface
public interface ObjectBinder<T> {

    void bind(T target);

}