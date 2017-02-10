/*
 * Copyright (C) 2011 Nameless Production Committee.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package consolio.bebop.ui;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import kiss.Events;
import kiss.Observer;

/**
 * @version 2017/02/10 11:34:15
 */
public abstract class Selectable<T> implements Iterable<T> {

    /** The index of current selected item. */
    private int index = -1;

    /** The item manager. */
    private List<T> items = new ArrayList();

    /** The addtion event manager. */
    private List<Observer<? super T>> adds = new CopyOnWriteArrayList<>();

    /** The addtion event manager. */
    public transient final Events<T> add = new Events<>(adds);

    /** The remove event manager. */
    private List<Observer<? super T>> removes = new CopyOnWriteArrayList<>();

    /** The remove event manager. */
    public transient final Events<T> remove = new Events<>(removes);

    /** The selection event manager. */
    private List<Observer<? super T>> selects = new CopyOnWriteArrayList<>();

    /** The selection event manager. */
    public transient final Events<T> select = new Events<>(selects);

    /** The deselection event manager. */
    private List<Observer<? super T>> deselects = new CopyOnWriteArrayList<>();

    /** The deselection event manager. */
    public transient final Events<T> deselect = new Events<>(deselects);

    /**
     * Get the index property of this {@link Selectable}.
     * 
     * @return The index property.
     */
    public int getSelectionIndex() {
        return index;
    }

    /**
     * Set the index property of this {@link Selectable}.
     * 
     * @param index The index value to set.
     */
    public void setSelectionIndex(int index) {
        int old = this.index;
        int size = items.size();

        if (size == 0) {
            index = -1;
        } else if (index < 0) {
            index = 0;
        } else if (size <= index) {
            index = size - 1;
        }
        this.index = index;

        // notify
        if (old != -1) {
            T item = items.get(old);
            deselects.forEach(o -> o.accept(item));
        }

        if (index != -1) {
            T item = items.get(index);
            selects.forEach(o -> o.accept(item));
        }
    }

    /**
     * Get the items property of this {@link Selectable}.
     * 
     * @return The items property.
     */
    protected List<T> getItems() {
        return items;
    }

    /**
     * Set the items property of this {@link Selectable}.
     * 
     * @param items The items value to set.
     */
    protected void setItems(List<T> items) {
        this.items = items;
    }

    /**
     * <p>
     * Retrieve the selected item. If no item is selected, <code>null</code> will be returned.
     * </p>
     * 
     * @return
     */
    @Transient
    public T getSelection() {
        return index == -1 || items.size() == 0 ? null : items.get(index);
    }

    /**
     * <p>
     * Select the specified item.
     * </p>
     */
    @Transient
    public void setSelection(T item) {
        if (item == null) {
            deselect();
        } else {
            setSelectionIndex(add(item));
        }
    }

    /**
     * <p>
     * Deselect item.
     * </p>
     */
    public final void deselect() {
        setSelectionIndex(-1);
    }

    /**
     * <p>
     * Select next item.
     * </p>
     * 
     * @return
     */
    public final T selectNext() {
        setSelectionIndex(index + 1);

        return getSelection();
    }

    /**
     * <p>
     * Select previous item.
     * </p>
     * 
     * @return
     */
    public final T selectPrevious() {
        setSelectionIndex(index - 1);

        return getSelection();
    }

    /**
     * <p>
     * Select first item.
     * </p>
     * 
     * @return
     */
    public final T selectFirst() {
        setSelectionIndex(0);

        return getSelection();
    }

    /**
     * <p>
     * Select last item.
     * </p>
     * 
     * @return
     */
    public final T selectLast() {
        setSelectionIndex(items.size() - 1);

        return getSelection();
    }

    /**
     * <p>
     * Appends the specified item to the end of this model.
     * </p>
     * 
     * @param item An item to be appended to this model.
     * @return The index of the added item in this model.
     */
    public final int add(T item) {
        int index = items.indexOf(item);

        if (index == -1) {
            index = items.size();
            items.add(item);
            adds.forEach(o -> o.accept(item));
        }
        return index;
    }

    /**
     * <p>
     * Removes the first occurrence of the specified item from this model, if it is present . If
     * this model does not contain the item, it is unchanged.
     * </p>
     * 
     * @param item An item to be removed from this model, if present.
     */
    public final void remove(T item) {
        int index = items.indexOf(item);

        if (index != -1) {
            T removed = items.remove(index);

            // synchronize actual model item position and the selected item position
            if (index <= this.index) {
                this.index--;
            }

            // At first, notify item removing.
            removes.forEach(o -> o.accept(item));

            // Then notify selection changing.
            setSelectionIndex(index);
        }
    }

    /**
     * <p>
     * Returns the item at the specified position in this model.
     * </p>
     * 
     * @param index A index of the item to return
     * @return The item at the specified position in this model.
     */
    public final T get(int index) {
        return items.get(index);
    }

    /**
     * <p>
     * Returns the number of items in this model.
     * </p>
     * 
     * @return The number of items in this model.
     */
    public final int size() {
        return items.size();
    }

    /**
     * <p>
     * Returns the index of the first occurrence of the specified item in this list, or -1 if this
     * model does not contain the item.
     * </p>
     * 
     * @param item An item to search for.
     * @return The index of the first occurrence of the specified item in this model, or -1 if this
     *         list does not contain the item.
     */
    public final int indexOf(T item) {
        return items.indexOf(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Iterator<T> iterator() {
        return items.iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void forEach(Consumer<? super T> action) {
        items.forEach(action);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Spliterator<T> spliterator() {
        return items.spliterator();
    }
}
