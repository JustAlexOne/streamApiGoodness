package com.justalex.streams.tests;

import com.justalex.streams.users.Gender;
import com.justalex.streams.users.NameAge;
import com.justalex.streams.users.User;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static com.justalex.streams.users.Gender.FEMALE;
import static com.justalex.streams.users.Gender.MALE;
import static java.util.Arrays.asList;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GroupingByExamples {

    /**
     * Simple Grouping by a Single Column
     * reference - http://www.baeldung.com/java-groupingby-collector
     */
    @Test
    void testGroupBy1() {
        List<User> users = asList(
            new User("John", 15, MALE),
            new User("Rob", 11, FEMALE),
            new User("Carl", 11, MALE)
        );
        Map<Gender, List<User>> mapByGender = users.stream().collect(groupingBy(User::getGender));
        List<User> maleUsers = asList(
            new User("John", 15, MALE),
            new User("Carl", 11, MALE)
        );
        assertEquals(maleUsers, mapByGender.get(MALE));
        assertEquals(asList(new User("Rob", 11, FEMALE)), mapByGender.get(FEMALE));
    }

    /**
     * Grouping by with a Complex Map Key Type
     * reference - http://www.baeldung.com/java-groupingby-collector
     */
    @Test
    void testGroupBy2() {
        List<User> users = asList(
            new User("John", 15, MALE),
            new User("Rob", 11, FEMALE),
            new User("Carl", 11, MALE),
            new User("John", 15, FEMALE)
        );

        Map<NameAge, List<User>> mapNameAge = users.stream().collect(groupingBy(user -> new NameAge(user.getName(), user.getAge())));
        List<User> usersByNameAge = asList(
            new User("John", 15, MALE),
            new User("John", 15, FEMALE)
        );
        assertEquals(usersByNameAge, mapNameAge.get(new NameAge("John", 15)));
    }

    /**
     * Modifying the Returned Map Value Type
     * reference - http://www.baeldung.com/java-groupingby-collector
     */
    @Test
    void testGroupBy3() {
        List<User> users = asList(
            new User("John", 15, MALE),
            new User("Rob", 11, FEMALE),
            new User("Carl", 11, MALE)
        );
        Map<Gender, Set<User>> mapUsersSetByGender = users.stream().collect(groupingBy(User::getGender, toSet()));
        List<User> maleUsers = asList(
            new User("John", 15, MALE),
            new User("Carl", 11, MALE)
        );
        // set is not an ordered collection
        assertEquals(2, mapUsersSetByGender.get(MALE).size());
        assertEquals(1, mapUsersSetByGender.get(FEMALE).size());
    }

    /**
     * Providing a Secondary Group By Collector
     * reference - http://www.baeldung.com/java-groupingby-collector
     */
    @Test
    void testGroupBy4() {
        List<User> users = asList(
            new User("John", 15, MALE),
            new User("John", 19, FEMALE),
            new User("John", 21, MALE),
            new User("Rob", 11, FEMALE),
            new User("Carl", 11, MALE)
        );
        Map<String, Map<Gender, List<User>>> res = users.stream().collect(groupingBy(User::getName, groupingBy(User::getGender)));

        List<User> maleJohnsUsers = asList(
            new User("John", 15, MALE),
            new User("John", 21, MALE)
        );
        assertEquals(maleJohnsUsers, res.get("John").get(MALE));
    }

    /**
     * Getting the Average from Grouped Results
     * reference - http://www.baeldung.com/java-groupingby-collector
     */
    @Test
    void testGroupBy5() {
        List<User> users = asList(
            new User("John", 9, MALE),
            new User("John", 21, MALE),
            new User("John", 19, FEMALE)
        );
        Map<Gender, Double> averageAgeByGender = users.stream().collect(groupingBy(User::getGender, Collectors.averagingInt(User::getAge)));
        double averageAge = (21 + 9) / 2;
        assertEquals(averageAge, averageAgeByGender.get(MALE).doubleValue());
    }

    /**
     * Getting the Sum from Grouped Results
     * reference - http://www.baeldung.com/java-groupingby-collector
     */
    @Test
    void testGroupBy6() {
        List<User> users = asList(
            new User("John", 9, MALE),
            new User("John", 21, MALE),
            new User("John", 19, FEMALE)
        );
        Map<Gender, Integer> totalAgePerGender = users.stream().collect(groupingBy(User::getGender, Collectors.summingInt(User::getAge)));
        int totalAge = 21 + 9;
        assertEquals(totalAge, totalAgePerGender.get(MALE).intValue());
    }

    /**
     * Getting the Maximum or Minimum from Grouped Results
     * reference - http://www.baeldung.com/java-groupingby-collector
     */
    @Test
    void testGroupBy7() {
        List<User> users = asList(
            new User("John", 9, MALE),
            new User("John", 21, MALE),
            new User("John", 19, FEMALE)
        );
        Map<Gender, Optional<User>> usersByGenderWithMaxAge = users.stream().collect(groupingBy(User::getGender, Collectors.maxBy(comparingInt(User::getAge))));
        int maxAge = 21;
        assertEquals(maxAge, usersByGenderWithMaxAge.get(MALE).orElseThrow(IllegalArgumentException::new).getAge());

        Map<Gender, Optional<User>> usersByGenderWithMinAge = users.stream().collect(groupingBy(User::getGender, Collectors.minBy(comparingInt(User::getAge))));
        int minAge = 9;
        assertEquals(minAge, usersByGenderWithMinAge.get(MALE).orElseThrow(IllegalArgumentException::new).getAge());
    }

    /**
     * Getting a Summary for an Attribute of Grouped Results
     * reference - http://www.baeldung.com/java-groupingby-collector
     */
    @Test
    void testGroupBy8() {
        List<User> users = asList(
            new User("John", 9, MALE),
            new User("John", 21, MALE),
            new User("John", 19, FEMALE)
        );
        Map<Gender, IntSummaryStatistics> byGenderIntSummary = users.stream().collect(groupingBy(User::getGender, summarizingInt(User::getAge)));
        assertEquals(2, byGenderIntSummary.get(MALE).getCount());
        assertEquals(21, byGenderIntSummary.get(MALE).getMax());
        assertEquals(9, byGenderIntSummary.get(MALE).getMin());
        assertEquals((21 + 9) / 2, byGenderIntSummary.get(MALE).getAverage());
    }

    /**
     * Mapping Grouped Results to a Different Type
     * reference - http://www.baeldung.com/java-groupingby-collector
     */
    @Test
    void testGroupBy9() {
        List<User> users = asList(
            new User("John", 9, MALE),
            new User("John", 21, MALE),
            new User("Bob", 19, FEMALE)
        );
        Map<Gender, String> collect = users.stream().collect(groupingBy(User::getGender, mapping(User::getName, joining(", "))));
        assertEquals("John, John", collect.get(MALE));
        assertEquals("Bob", collect.get(FEMALE));
    }

    /**
     * Modifying the Return Map Type
     * reference - http://www.baeldung.com/java-groupingby-collector
     */
    @Test
    void testGroupBy10() {
        List<User> users = asList(
            new User("John", 9, MALE),
            new User("John", 21, MALE),
            new User("Bob", 19, FEMALE)
        );
        EnumMap<Gender, List<User>> enumMapUsersByGender = users.stream().collect(groupingBy(User::getGender, () -> new EnumMap<>(Gender.class), toList()));
        List<User> maleUsers = asList(new User("John", 9, MALE), new User("John", 21, MALE));
        List<User> femaleUsers = asList(new User("Bob", 19, FEMALE));
        assertEquals(maleUsers, enumMapUsersByGender.get(MALE));
        assertEquals(femaleUsers, enumMapUsersByGender.get(FEMALE));
    }

    /**
     * Concurrent Grouping By Collector
     * reference - http://www.baeldung.com/java-groupingby-collector
     */
    @Test
    void testGroupBy11() {
        List<User> users = asList(
            new User("John", 9, MALE),
            new User("John", 21, MALE),
            new User("Bob", 19, FEMALE)
        );
        ConcurrentMap<Gender, List<User>> usersByGenderConcurrent = users.parallelStream().collect(groupingByConcurrent(User::getGender));
        List<User> maleUsers = asList(new User("John", 9, MALE), new User("John", 21, MALE));
        List<User> femaleUsers = asList(new User("Bob", 19, FEMALE));

        // concurrent execution doesn't guarantee order
        assertEquals(2, usersByGenderConcurrent.get(MALE).size());
        assertEquals(1, usersByGenderConcurrent.get(FEMALE).size());
    }

    /**
     * Count duplicates in a list and group with unique keys
     */
    @Test
    void testGroupBy0() {
        List<String> strings = asList("a", "b", "c", "a", "d", "c", "a");

        Map<String, Long> stringsByCount = strings.stream().collect(groupingBy(x -> x, counting()));

        assertEquals(3, stringsByCount.get("a").longValue());
        assertEquals(1, stringsByCount.get("b").longValue());
        assertEquals(2, stringsByCount.get("c").longValue());
        assertEquals(1, stringsByCount.get("d").longValue());
    }
}
