package com.dezxxx.hometasks.crud.util;

import java.util.List;

public class Pager<T> {

    public static final int PAGE_SIZE = 5;

    private final List<T> items;
    private int page = 0;

    public Pager(List<T> items) {
        this.items = items;
    }

    public List<T> currentItems() {
        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, items.size());
        return items.subList(start, end);
    }

    public T getByPageIndex(int index) {
        return currentItems().get(index);
    }

    public boolean hasNext() { return (page + 1) * PAGE_SIZE < items.size(); }
    public boolean hasPrev() { return page > 0; }
    public void next()       { if (hasNext()) page++; }
    public void prev()       { if (hasPrev()) page--; }

    public int pageNumber()  { return page + 1; }
    public int totalPages()  { return Math.max(1, (int) Math.ceil((double) items.size() / PAGE_SIZE)); }
    public int total()       { return items.size(); }
    public boolean isEmpty() { return items.isEmpty(); }
    public boolean isSinglePage() { return totalPages() == 1; }
}
