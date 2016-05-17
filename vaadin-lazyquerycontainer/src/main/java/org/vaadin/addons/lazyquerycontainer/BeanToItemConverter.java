package org.vaadin.addons.lazyquerycontainer;

import com.vaadin.data.Item;

/**
 * @author Stefan Penndorf
 */
public interface BeanToItemConverter<T> {


    Item toItem(T bean, QueryDefinition queryDefinition);

    T fromItem(Item item, QueryDefinition queryDefinition);
}
