package io.recheck.uuidprotocol.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ListUtils {

    public static List<List<? extends Object>> batches(List<? extends Object> source, int length) {
        List<List<? extends Object>> chunks = new ArrayList<>();

        int size = source.size();
        for (int i = 0; i < size; i += length) {
            int end = Math.min(size, i + length);
            chunks.add(new ArrayList<>(source.subList(i, end)));
        }

        return chunks;
    }

    public static <T> List<T> concat(List<T> listOne, List<T> listTwo) {
        return Stream.concat(listOne.stream(), listTwo.stream()).toList();
    }

}
