package com.justalex.streams.tests;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class StreamApiToolsTests {

    @Test
    public void testConvertTo2dArray() {
        List<String> list = asList("A", "B", "C");
        String[][] strings = list.stream().map(bean -> new String[]{bean}).toArray(String[][]::new);

        assertEquals("A", strings[0][0]);
        assertEquals("B", strings[1][0]);
        assertEquals("C", strings[2][0]);
    }

    @Test
    public void testCollectStepByStep() {
        Stream<String> stream = Stream.of("a", "b", "c");
        // with lambdas
        ArrayList<String> collectDetailed = stream.collect(() -> new ArrayList<String>(), (list, streamElement) -> list.add(streamElement), (list1, list2) -> list1.addAll(list2));

        // with method reference
        stream = Stream.of("a", "b", "c");
        ArrayList<String> collectMethodRefference = stream.collect(ArrayList::new, List::add, List::addAll);

        assertIterableEquals(collectDetailed, collectMethodRefference);
    }

    @Test
    void testReplaceAll() {
        List<Integer> list = asList(1, 2, 3, 4);
        list.replaceAll(x -> x * 2);

        assertIterableEquals(asList(2, 4, 6, 8), list);
    }

    @Test
    void testReduce() {
        Stream<Integer> integerStream = Stream.of(1, 2, 3, 4, 5, 1, 2);
        int initialValue = 0; // it is not the index!
        int sum = integerStream.reduce(initialValue, (x, y) -> x + y);

        assertEquals(18, sum);
    }

    @Test
    void testMapForEach() {
        Map<String, Integer> namedInts = new TreeMap<>();
        namedInts.put("a", 1);
        namedInts.put("b", 2);
        namedInts.put("c", 3);
        Map<Integer, String> intedNames = new TreeMap<>();
        namedInts.forEach((a, x) -> intedNames.put(x, a));

        assertIterableEquals(namedInts.keySet(), intedNames.values());
        assertIterableEquals(namedInts.values(), intedNames.keySet());
    }

    @Test
    void testFlatMap() {
        List<Integer> flatten = Stream.of(asList(1, 2, 3), asList(4, 5, 6))
            .flatMap(List::stream)
            .collect(Collectors.toList());

        assertIterableEquals(asList(1, 2, 3, 4, 5, 6), flatten);
    }

    @Test
    void testFilterBasic() {
        Stream<String> stream = Stream.of("a", "b", "c");
        List<String> res = stream
            .filter(it -> it.equals("b"))
            .collect(Collectors.toList());

        assertIterableEquals(asList("b"), res);
    }

    @Test
    void testFilterAvoidNulls() {
        List<String> res = Stream.of("a", null, "b", "c", null, "d")
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        assertIterableEquals(asList("a", "b", "c", "d"), res);
    }

    @Test
    void testPeek() {
        Stream<Integer> stream = Stream.of(1, 2, 3);
        List<Integer> res = new ArrayList<>();
        stream.peek(res::add); // nothing will be done here as there is no termination operation declared
        assertEquals(0, res.size());

        stream = Stream.of(1, 2, 3);
        stream.peek(res::add).count(); // nothing will be done here as there is no termination operation declared
        assertIterableEquals(asList(1, 2, 3), res);
    }

    @Test
    void testIntRange() {
        IntStream stream = IntStream.range(1, 5);
        List<Integer> res = stream.boxed().collect(Collectors.toList());

        assertIterableEquals(asList(1, 2, 3, 4), res);
    }

    @Test
    void testStreamIterate() {
        List<Integer> res = new ArrayList<>();
        IntStream.iterate(0, i -> i + 1)
            .limit(3)
            .forEach(res::add);

        assertIterableEquals(asList(0, 1, 2), res);
    }

    @Test
    void testRandomDoubles() {
        DoubleStream doubles = new SplittableRandom().doubles(10);
        doubles.map(item -> item * 100)
            .map(Math::round)
            .forEach(System.out::println);
    }

    @Test
    void testRandomInts() {
        IntStream ints = new SplittableRandom()
            .ints(0, 101)
            .limit(10000);
        List<Integer> intsList = ints.boxed().collect(Collectors.toList());

        assertEquals(true, intsList.stream().anyMatch(x -> x == 0));
        assertEquals(true, intsList.stream().anyMatch(x -> x == 100));
        assertEquals(false, intsList.stream().anyMatch(x -> x < 0));
        assertEquals(false, intsList.stream().anyMatch(x -> x > 100));
    }

    @Test
    void testListToMap() {
        Map<String, Long> map = Stream.of("a", "b", "c", "a")
            .collect(Collectors.groupingBy(x -> x, Collectors.counting()));

        assertEquals(3, map.size());

        assertEquals(2, (long) map.get("a"));
        assertEquals(1, (long) map.get("b"));
        assertEquals(1, (long) map.get("c"));
    }
}
