package org.vaadin.addons.lazyquerycontainer;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.ObjectProperty;

/**
 * @author Stefan Penndorf
 */
public class BeanToNestedItemConverter<T> implements BeanToItemConverter<T> {

    @Override
    @SuppressWarnings("unchecked")
    public Item toItem(final T bean, final QueryDefinition queryDefinition) {
        final BeanItem<T> beanItem = createBeanItem(bean, queryDefinition);

        if (queryDefinition.isCompositeItems()) {
            final CompositeItem compositeItem = new CompositeItem();
            compositeItem.addItem("bean", beanItem);

            for (final Object propertyId : queryDefinition.getPropertyIds()) {
                if (compositeItem.getItemProperty(propertyId) == null) {
                    compositeItem.addItemProperty(
                            propertyId,
                            new ObjectProperty(
                                    queryDefinition.getPropertyDefaultValue(propertyId),
                                    queryDefinition.getPropertyType(propertyId),
                                    queryDefinition.isPropertyReadOnly(propertyId)));
                }
            }

            return compositeItem;
        } else {
            return beanItem;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T fromItem(final Item item, final QueryDefinition queryDefinition) {

        final Item beanItem = unwrapIfComposite(item, queryDefinition);

        if (beanItem instanceof BeanItem) {
            return ((BeanItem<T>) beanItem).getBean();
        }

        throw new IllegalStateException("Invalid item type: Not a BeanItem: " + beanItem);

    }

    private Item unwrapIfComposite(Item item, QueryDefinition queryDefinition) {
        return queryDefinition.isCompositeItems() ? ((CompositeItem) item).getItem("bean") : item;
    }

    /**
     * Extension point to BeanToNestedItemConverter to be able to replace the implementation of the item.
     *
     * @param bean Bean object, maybe null.
     * @param queryDefinition query definition used.
     * @return a non-null BeanItem.
     */
    protected BeanItem<T> createBeanItem(final T bean, final QueryDefinition queryDefinition) {
        return new NestingBeanItem<>(
                bean, queryDefinition.getMaxNestedPropertyDepth(), queryDefinition.getPropertyIds());
    }

}
