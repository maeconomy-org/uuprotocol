package io.recheck.uuidprotocol.common.utils;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ListUtils {

    public static Stream<List<? extends Object>> batches(List<? extends Object> source, int length) {
        if (length <= 0)
            throw new IllegalArgumentException("length = " + length);

        int size = source.size();
        if (size <= 0)
            return Stream.empty();

        int fullChunks = (size - 1) / length;

        return IntStream.range(0, fullChunks + 1).mapToObj(n -> source.subList(n * length, n == fullChunks ? size : (n + 1) * length));
    }

    public static <T> List<T> concat(List<T> listOne, List<T> listTwo) {
        return Stream.concat(listOne.stream(), listTwo.stream()).toList();
    }

}
