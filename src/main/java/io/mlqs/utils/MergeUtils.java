package io.mlqs.utils;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MergeUtils {
    /**
     * 合并两个集合
     * @param sets 集合
     * @param <T> 集合元素类型
     * @return 合并后的集合，HashSet保证顺序
     */
    public static <T> Set<T> mergeToSet(Collection<T>... sets) {
        Set<T> set = new LinkedHashSet<>();
        for (Collection<T> s : sets)
            set.addAll(s);
        return set;
    }

    public static <T> List<T> mergeToList(Collection<T>... sets) {
        Set<T> set = new LinkedHashSet<>();
        for (Collection<T> s : sets)
            set.addAll(s);
        List<T> list = new ArrayList<>(set);
        return list;
    }
}
