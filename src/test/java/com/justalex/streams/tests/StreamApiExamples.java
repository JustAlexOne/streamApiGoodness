package com.justalex.streams.tests;

import com.justalex.streams.users.User;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.justalex.streams.users.Gender.FEMALE;
import static com.justalex.streams.users.Gender.MALE;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

public class StreamApiExamples {

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

    /**
     * Form a map of the list like: key = unique list item, value = count of duplicating items in the list
     */
    @Test
    void testListToMap() {
        Map<String, Long> map = Stream.of("a", "b", "c", "a")
            .collect(Collectors.groupingBy(x -> x, Collectors.counting()));

        assertEquals(3, map.size());

        assertEquals(2, map.get("a").longValue());
        assertEquals(1, map.get("b").longValue());
        assertEquals(1, map.get("c").longValue());
    }

    @Test
    void testJoinStrings1() {
        StringJoiner joiner = new StringJoiner(", ");
        joiner.add("a");
        joiner.add("b");
        joiner.add("c");

        String joined = joiner.toString();

        assertEquals("a, b, c", joined);
    }

    @Test
    void testJoinStrings2() {
        String joined = Stream.of("a", "b", "c")
            .collect(Collectors.joining(", "));
        assertEquals("a, b, c", joined);
    }

    @Test
    void testFindElementsInListByProperty() {
        List<User> users = asList(
            new User("John", 15, MALE),
            new User("Rob", 11, FEMALE),
            new User("Carl", 11, MALE)
        );

        List<String> usersWithNames = asList("John", "Rob");

        List<User> result = new ArrayList<>();
        usersWithNames.forEach(userName -> users.stream().filter(user -> userName.equals(user.getName())).findFirst().ifPresent(result::add));

        assertEquals(2, result.size());
        assertTrue(result.contains(new User("John", 15, MALE)));
        assertTrue(result.contains(new User("Rob", 11, FEMALE)));
        assertFalse(result.contains(new User("Carl", 11, MALE)));
    }

    @Test
    void testSubMapHashMap() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);

        Map<String, Integer> newMap = map.keySet().stream().limit(2).collect(Collectors.toMap(key -> key, map::get));

        assertEquals(2, newMap.size());
        assertTrue(newMap.containsKey("a"));
        assertTrue(newMap.containsKey("b"));
    }

    @Test
    void testSortMap() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("a", 2);
        map.put("b", 1);
        map.put("c", 3);

        LinkedHashMap<String, Integer> result = map.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        ArrayList<Integer> listSorted = new ArrayList<>(result.values());
        assertEquals(Integer.valueOf(1), listSorted.get(0));
        assertEquals(Integer.valueOf(2), listSorted.get(1));
        assertEquals(Integer.valueOf(3), listSorted.get(2));

        LinkedHashMap<String, Integer> resultReversed = map.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        LinkedHashMap<String, Integer> resultReversed_example2 = map.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getValue)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        ArrayList<Integer> listSortedReversed = new ArrayList<>(resultReversed.values());
        assertEquals(Integer.valueOf(3), listSortedReversed.get(0));
        assertEquals(Integer.valueOf(2), listSortedReversed.get(1));
        assertEquals(Integer.valueOf(1), listSortedReversed.get(2));

        // Convert directly to list
        List<Integer> resList = map.entrySet().stream().sorted(Comparator.comparing(Map.Entry<String, Integer>::getValue).reversed()).map(Map.Entry::getValue).collect(Collectors.toList());
        assertEquals(Integer.valueOf(3), resList.get(0));
        assertEquals(Integer.valueOf(2), resList.get(1));
        assertEquals(Integer.valueOf(1), resList.get(2));
    }
}
