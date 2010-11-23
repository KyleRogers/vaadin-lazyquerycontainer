/**
 * Copyright 2010 Tommi S.E. Laukkanen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vaadin.addons.lazyquerycontainer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.vaadin.data.Buffered;
import com.vaadin.data.Container;
import com.vaadin.data.Container.Indexed;
import com.vaadin.data.Container.ItemSetChangeNotifier;
import com.vaadin.data.Container.PropertySetChangeNotifier;
import com.vaadin.data.Container.Sortable;
import com.vaadin.data.Item;
import com.vaadin.data.Property;

/**
 * LazyQueryContainer provides lazy loading of items from business services. See
 * package level documentation for detailed description. This implements event
 * notification functionality and delegates other methods to QueryView
 * aggregate.
 * 
 * @author Tommi S.E. Laukkanen
 */
public final class LazyQueryContainer implements Indexed, Sortable, ItemSetChangeNotifier, PropertySetChangeNotifier,
        Buffered {
    /** Java serialization UID. */
    private static final long serialVersionUID = 1L;
    /** QueryView where LazyQueryContainer delegates method calls to. */
    private QueryView queryView;
    /** List of registered ItemSetChangeListener. */
    private List<ItemSetChangeListener> itemSetChangeListeners = new ArrayList<ItemSetChangeListener>();
    /** List of registered PropertySetChangeListeners. */
    private List<PropertySetChangeListener> propertySetChangeListeners = new ArrayList<PropertySetChangeListener>();

    /**
     * Constructs LazyQueryContainer with LazyQueryView and given queryFactory.
     * @param queryFactory The query factory to be used.
     * @param batchSize The batch size to be used when loading data.
     */
    public LazyQueryContainer(final QueryFactory queryFactory, final int batchSize) {
        this.queryView = new LazyQueryView(queryFactory, batchSize);
    }

    /**
     * Constructs LazyQueryContainer with LazyQueryView and given queryFactory
     * and queryDefinition.
     * @param queryFactory The query factory to be used.
     * @param queryDefinition The query definition to be used.
     */
    public LazyQueryContainer(final QueryDefinition queryDefinition, final QueryFactory queryFactory) {
        this.queryView = new LazyQueryView(queryDefinition, queryFactory);
    }

    /**
     * Constructs LazyQueryContainer with the given QueryView. This constructor
     * role is to enable use of custom view implementations.
     * @param queryView The query view to be used.
     */
    public LazyQueryContainer(final QueryView queryView) {
        this.queryView = queryView;
    }

    /**
     * Sets new sort state and refreshes view.
     * @param sortPropertyIds The IDs of the properties participating in sort.
     * @param ascendingStates The sort state of the properties participating in sort. True means ascending.
     */
    public void sort(final Object[] sortPropertyIds, final boolean[] ascendingStates) {
        queryView.sort(sortPropertyIds, ascendingStates);
    }

    /**
     * Lists of the property IDs queried.
     * @return A list of property IDs queried.
     */
    public Collection<?> getContainerPropertyIds() {
        return queryView.getDefinition().getPropertyIds();
    }

    /**
     * List of the property IDs which can be sorted.
     * @return A list of the property IDs which can be sorted.
     */
    public Collection<?> getSortableContainerPropertyIds() {
        return queryView.getDefinition().getSortablePropertyIds();
    }

    /**
     * Gets the property value class of the given property.
     * @param propertyId If of the property of interest.
     * @return The value class of the given property.
     */
    public Class<?> getType(final Object propertyId) {
        return queryView.getDefinition().getPropertyType(propertyId);
    }

    /**
     * Adds a new property to the definition.
     * @param propertyId Id of the property.
     * @param type Value class of the property.
     * @param defaultValue Default value of the property.
     * @return always true.
     */
    public boolean addContainerProperty(final Object propertyId, final Class<?> type, final Object defaultValue) {
        queryView.getDefinition().addProperty(propertyId, type, defaultValue, true, false);
        notifyPropertySetChanged();
        return true;
    }

    /**
     * Adds a new property to the definition.
     * @param propertyId Id of the property.
     * @param type Value class of the property.
     * @param defaultValue Default value of the property.
     * @param readOnly Read only state of the property.
     * @param sortable Sortable state of the property.
     * @return always true.
     */
    public boolean addContainerProperty(final Object propertyId, final Class<?> type, final Object defaultValue,
            final boolean readOnly, final boolean sortable) {
        queryView.getDefinition().addProperty(propertyId, type, defaultValue, readOnly, sortable);
        notifyPropertySetChanged();
        return true;
    }

    /**
     * Removes the given property from the definition.
     * @param propertyId If of the property to be removed.
     * @return always true.
     */
    public boolean removeContainerProperty(final Object propertyId) {
        queryView.getDefinition().removeProperty(propertyId);
        notifyPropertySetChanged();
        return true;
    }

    /**
     * Number of items in the container.
     * @return number of items.
     */
    public int size() {
        return queryView.size();
    }

    /**
     * Gets list of item indexes in the container.
     * @return Collection of Integers.
     */
    public Collection<?> getItemIds() {
        ArrayList<Integer> ids = new ArrayList<Integer>(size());
        for (int i = 0; i < size(); i++) {
            ids.add(i);
        }
        return ids;
    }

    /**
     * Gets item at given index.
     * @param itemId index of the item.
     * @return Item at the given index.
     */
    public Item getItem(final Object itemId) {
        return queryView.getItem((Integer) itemId);
    }

    /**
     * Gets property of an item.
     * @param itemId The index of the item.
     * @param propertyId ID of the property.
     * @return the property corresponding to given IDs.
     */
    public Property getContainerProperty(final Object itemId, final Object propertyId) {
        return getItem(itemId).getItemProperty(propertyId);
    }

    /**
     * Gets ID of given index.
     * In other words returns the index itself.
     * @param index The index of the item.
     * @return the object ID i.e. index.
     */
    public Object getIdByIndex(final int index) {
        return index;
    }

    /**
     * Gets index of given ID.
     * In other words returns the index itself.
     * @param itemId the item ID.
     * @return the index.
     */
    public int indexOfId(final Object itemId) {
        return (Integer) itemId;
    }

    /**
     * True if container contains the given index.
     * @param itemId Item index.
     * @return true if container contains the given index.
     */
    public boolean containsId(final Object itemId) {
        if (itemId.getClass() == Integer.class) {
            return size() > (Integer) itemId && (Integer) itemId >= 0;
        } else {
            return false;
        }
    }

    /**
     * Check if given index is first i.e. 0.
     * @param itemId the object index.
     * @return true if index is 0.
     */
    public boolean isFirstId(final Object itemId) {
        if (itemId.getClass() == Integer.class) {
            return (Integer) itemId == 0;
        } else {
            return false;
        }        
    }

    /**
     * Check if given index is first i.e. size() - 1.
     * @param itemId the object index.
     * @return true if index is size() - 1.
     */
    public boolean isLastId(final Object itemId) {
        if (itemId.getClass() == Integer.class) {
            return (Integer) itemId == size() - 1;
        } else {
            return false;
        }        
    }

    /**
     * @return first item ID i.e. 0.
     */
    public Object firstItemId() {
        return 0;
    }

    /**
     * @return last item ID i.e. size() - 1
     */
    public Object lastItemId() {
        return size() - 1;
    }

    /**
     * @param itemId the item index
     * @return itemId + 1
     */
    public Object nextItemId(final Object itemId) {
        return (Integer) itemId + 1;
    }

    /**
     * @param itemId the item index
     * @return itemId - 1
     */
    public Object prevItemId(final Object itemId) {
        return (Integer) itemId - 1;
    }

    /**
     * Not supported.
     * @param index item index
     * @return new item ID.
     */
    public Object addItemAt(final int index) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported.
     * @param previousItemId ID of previous item.
     * @return new item ID.
     */
    public Object addItemAfter(final Object previousItemId) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported.
     * @param index item index
     * @param newItemId ID of new item
     * @return new Item
     */
    public Item addItemAt(final int index, final Object newItemId) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported.
     * @param previousItemId ID of previous item.
     * @param newItemId ID of new item
     * @return new Item
     */
    public Item addItemAfter(final Object previousItemId, final Object newItemId) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported.
     * @param itemId itemId
     * @return new Item
     */
    public Item addItem(final Object itemId) {
        throw new UnsupportedOperationException();
    }

    /**
     * Constructs and adds new item.
     * @return item index.
     */
    public Object addItem() {
        Object itemId = queryView.addItem();
        notifyItemSetChanged();
        return itemId;
    }

    /**
     * Removes Item at given index.
     * @param itemId Item index.
     * @return always true.
     */
    public boolean removeItem(final Object itemId) {
        queryView.removeItem((Integer) itemId);
        notifyItemSetChanged();
        return true;
    }

    /**
     * Removes all items.
     * @return always true.
     */
    public boolean removeAllItems() {
        queryView.removeAllItems();
        refresh();
        return true;
    }

    /**
     * Adds ItemSetChangeListener.
     * @param listener ItemSetChangeListener to be added.
     */
    public void addListener(final ItemSetChangeListener listener) {
        itemSetChangeListeners.add(listener);
    }

    /**
     * Removes ItemSetChangeListener.
     * @param listener ItemSetChangeListener to be removed.
     */
    public void removeListener(final ItemSetChangeListener listener) {
        itemSetChangeListeners.remove(listener);
    }

    /**
     * Refreshes container.
     */
    public void refresh() {
        queryView.refresh();
        notifyItemSetChanged();
    }

    /**
     * Notifies that item set has been changed.
     */
    private void notifyItemSetChanged() {
        QueryItemSetChangeEvent event = new QueryItemSetChangeEvent(this);
        for (ItemSetChangeListener listener : itemSetChangeListeners) {
            listener.containerItemSetChange(event);
        }
    }

    /**
     * Adds PropertySetChangeListener.
     * @param listener PropertySetChangeListener to be added.
     */    
    public void addListener(final PropertySetChangeListener listener) {
        propertySetChangeListeners.add(listener);
    }

    /**
     * Removes PropertySetChangeListener.
     * @param listener PropertySetChangeListener to be removed.
     */    
    public void removeListener(final PropertySetChangeListener listener) {
        propertySetChangeListeners.remove(listener);
    }

    /**
     * Notifies that property set has been changed.
     */
    private void notifyPropertySetChanged() {
        QueryPropertySetChangeEvent event = new QueryPropertySetChangeEvent(this);
        for (PropertySetChangeListener listener : propertySetChangeListeners) {
            listener.containerPropertySetChange(event);
        }
    }

    /**
     * Private ItemSetChangeEvent implementation.
     * @author Tommi Laukkanen
     */
    private class QueryItemSetChangeEvent implements ItemSetChangeEvent {
        /** Java serialization version UID. */
        private static final long serialVersionUID = 1L;
        /** The container where event occurred. */
        private LazyQueryContainer container;

        /**
         * Constructor for setting the container.
         * @param container the Contianer.
         */
        public QueryItemSetChangeEvent(final LazyQueryContainer container) {
            this.container = container;
        }

        /**
         * Gets the container where event occurred.
         * @return the Contianer.
         */
        public Container getContainer() {
            return container;
        }

    }

    /**
     * Private PropertySetChangeEvent implementation.
     * @author Tommi Laukkanen
     */
    private class QueryPropertySetChangeEvent implements PropertySetChangeEvent {
        /** Java serialization version UID. */
        private static final long serialVersionUID = 1L;
        /** The container where event occurred. */
        private LazyQueryContainer container;

        /**
         * Constructor for setting the container.
         * @param container the Contianer.
         */
        public QueryPropertySetChangeEvent(final LazyQueryContainer container) {
            this.container = container;
        }

        /**
         * Gets the container where event occurred.
         * @return the Contianer.
         */
        public Container getContainer() {
            return container;
        }

    }

    /**
     * Commits changed.
     */
    public void commit() {
        queryView.commit();
        refresh();
    }

    /**
     * Discards changes.
     */
    public void discard() {
        queryView.discard();
        refresh();
    }

    /**
     * @return true if container contains uncommited modifications.
     */
    public boolean isModified() {
        return queryView.isModified();
    }

    /**
     * Is container in read through mode.
     * @return always false.
     */
    public boolean isReadThrough() {
        return false;
    }

    /**
     * Is container in write through mode.
     * @return always false.
     */
    public boolean isWriteThrough() {
        return false;
    }

    /**
     * Not supported.
     * @param readThrough is container in read through mode.
     */
    public void setReadThrough(final boolean readThrough) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported.
     * @param writeThrough is container in write through mode.
     */
    public void setWriteThrough(final boolean writeThrough) {
        throw new UnsupportedOperationException();
    }
}
