package org.jzl.android.library_no1.v6;

import org.jzl.lang.fun.IntConsumer;
import org.jzl.lang.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

public class Observable<T> {

    private MatchPolicy matchPolicy;
    private List<ObserverHolder<T>> holders;

    public Observable(MatchPolicy matchPolicy) {
        this.matchPolicy = ObjectUtils.requireNonNull(matchPolicy, "matchPolicy");
        this.holders = new ArrayList<>();
    }

    public void register(int viewType, T observer) {
        if (ObjectUtils.nonNull(observer)) {
            this.holders.add(ObserverHolder.of(viewType, observer));
        }
    }

    public void match(int targetViewType, IntConsumer<T> consumer) {
        for (ObserverHolder<T> holder : holders) {
            int viewType = holder.getViewType();
            if (matchPolicy.match(targetViewType, viewType)) {
                consumer.accept(viewType, holder.getObserver());
            }
        }
    }

    public T getSingleObserver(int targetViewType) {
        for (ObserverHolder<T> holder : holders) {
            if (matchPolicy.match(targetViewType, holder.getViewType())) {
                return holder.getObserver();
            }
        }
        return null;
    }

    private static class ObserverHolder<T> {

        private int viewType;
        private T observer;

        private ObserverHolder(int viewType, T observer) {
            this.viewType = viewType;
            this.observer = observer;
        }

        public int getViewType() {
            return viewType;
        }

        public T getObserver() {
            return observer;
        }

        public static <T> ObserverHolder<T> of(int viewType, T observer) {
            return new ObserverHolder<>(viewType, ObjectUtils.requireNonNull(observer, "observer"));
        }
    }
}
