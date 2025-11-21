
package io.mlqs.utils.clazz;

import java.util.Arrays;

/**
 * 多元组
 * 可以存放各种类型的一个元组
 * 例如:[1,"1",'1',0.1,[1],...]
 * 获取：int i = multiGroup.get(0);
 * 获取：String s = multiGroup.get(1);
 * ...
 */
public class MultiGroup {
    private Object[] elements;

    @SafeVarargs
    public <T> MultiGroup(T... elements) {
        this.elements = elements == null ? new Object[0] : Arrays.copyOf(elements, elements.length);
    }
    public static <T> MultiGroup of(T... elements){
        return new MultiGroup(elements);
    }

    public int size() {
        return elements.length;
    }

    public boolean isEmpty() {
        return elements.length == 0;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(int index) {
        if (index < 0 || index >= elements.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + elements.length);
        }
        return (T) elements[index];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MultiGroup that = (MultiGroup) o;
        return Arrays.equals(elements, that.elements);
    }

    @Override
    public String toString() {
        return Arrays.toString(elements);
    }
}