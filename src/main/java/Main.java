import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class Main {
    public static void main(String[] args) {

        // ══════════════════════════════════════════════
        //  CRÉATION DE STREAMS
        // ══════════════════════════════════════════════

        List<String> list = List.of("a", "b", "c");
        Stream<String> s1 = list.stream();               // Depuis une List
        Stream<String> s2 = Stream.of("a", "b", "c");   // Depuis des valeurs directes
        Stream<String> s3 = Stream.empty();              // Stream vide

        // -- Streams primitifs --
        IntStream ints = IntStream.range(0, 10);        // [0, 10[
        IntStream ints2 = IntStream.of(1, 2, 3, 4, 5);  // Valeurs explicites
        IntStream ints3 = IntStream.rangeClosed(0, 10); // [0, 10]

        // -- Streams infinis (limités avec .limit()) --
        Stream<Integer> s4 = Stream.iterate(0, n -> n + 1).limit(10);  // 0, 1, 2, ..., 9
        Stream<String> s5 = Stream.generate(() -> "Hello").limit(5);  // "Hello" x5

        // -- Depuis un tableau --
        String[] array = {"a", "b", "c"};
        Stream<String> s6 = Arrays.stream(array);

        // ⚠ Stream null → NullPointerException à l'utilisation
        Stream<String> s7 = null;
        // s7.map(String::toUpperCase).forEach(System.out::println);


        // ══════════════════════════════════════════════
        //  OPÉRATIONS INTERMÉDIAIRES  (lazy / chaînées)
        //  → Ne s'exécutent que lorsqu'une opération
        //    terminale est appelée.
        //  Exemples : filter, map, sorted, distinct,
        //             limit, skip, peek, flatMap
        // ══════════════════════════════════════════════

        // -- filter : garde uniquement les éléments qui satisfont le prédicat --
        List<String> longs = Stream.of("cat", "elephant", "ox", "hippopotamus")
                .filter(s -> s.length() > 3)
                .toList();

        // -- map : transforme chaque élément en un autre type/valeur --
        List<Integer> lengths = Stream.of("cat", "elephant", "ox", "")
                .map(String::length)
                .toList();

        // -- Chaînage : filter → map → sorted --
        List<String> names = Stream.of(
                        new Employee("Alice", 30, 50000, "Femme"),
                        new Employee("Bob", 25, 40000, "Homme"),
                        new Employee("Charlie", 35, 60000, "Homme"))
                .filter(e -> e.age() > 30)
                .map(Employee::name)
                .sorted()
                .toList();

        // -- peek : inspecte chaque élément sans modifier le stream (utile pour déboguer) --
        List<String> peeked = Stream.of("cat", "elephant", "ox", "hippopotamus")
                .filter(s -> s.length() > 3)
                .peek(s -> System.out.println("Filtered value: " + s))
                .map(String::toUpperCase)
                .peek(s -> System.out.println("Mapped value: " + s))
                .toList();

        // -- sorted : tri selon l'ordre naturel ou un Comparator personnalisé --
        List<Employee> employees = List.of(
                new Employee("Alice", 30, 50000, "Femme"),
                new Employee("Bob", 25, 40000, "Homme"),
                new Employee("Charlie", 35, 60000, "Homme")
        );

        // Tri par salaire décroissant
        employees.stream()
                .sorted(Comparator.comparing(Employee::salary).reversed())
                .forEach(System.out::println);

        // Tri par âge, puis par salaire (multi-critères)
        employees.stream()
                .sorted(Comparator.comparing(Employee::age).thenComparing(Employee::salary))
                .forEach(System.out::println);

        // -- distinct : supprime les doublons (basé sur equals) --
        List<String> distinct = Stream.of("cat", "dog", "cat", "elephant", "dog")
                .distinct()
                .toList();

        // -- limit : conserve au plus N éléments
        //    boxed : convertit IntStream → Stream<Integer> --
        List<Integer> limited = IntStream.range(0, 100)
                .limit(10)
                .boxed()
                .toList();

        // -- skip : ignore les N premiers éléments --
        List<Integer> skipped = IntStream.range(0, 100)
                .skip(10)
                .boxed()
                .toList();

        // -- flatMap : transforme chaque élément en un Stream, puis aplatit le tout en un seul Stream --
        List<String> flatMapped = Stream.of("cat,dog", "elephant,hippopotamus")
                .flatMap(s -> Arrays.stream(s.split(",")))
                .toList();

        // -- mapToInt : transforme en int et retourne un IntStream --
        List<Integer> mapToInt = Stream.of("cat", "elephant", "ox", "hippopotamus")
                .mapToInt(String::length)
                .boxed()
                .toList();

        // -- mapToDouble : transforme en double et retourne un DoubleStream --
        List<Double> mapToDouble = Stream.of("cat", "elephant", "ox", "hippopotamus")
                .mapToDouble(String::length)
                .boxed()
                .toList();


        // ══════════════════════════════════════════════
        //  OPÉRATIONS TERMINALES  (eager / déclenchantes)
        //  → Déclenchent l'exécution du pipeline. Après ça, le Stream est consommé et ne peut plus être utilisé.
        //  Exemples : forEach, collect, reduce, count,
        //             anyMatch, allMatch, noneMatch,
        //             findFirst, findAny
        // ══════════════════════════════════════════════

        // -- reduce : combine les éléments deux par deux jusqu'à obtenir un seul résultat.
        //             Retourne un Optional (vide si le stream est vide) --
        Optional<String> longest = Stream.of("cat", "elephant", "ox")
                .reduce((a, b) -> a.length() >= b.length() ? a : b);

        // -- collect : accumule les éléments dans une collection ou un autre résultat
        //              via un Collector (ex. Collectors.toList(), groupingBy, joining…) --

        List<String> collected = Stream.of("cat", "elephant", "ox")
                .collect(toList());

        // -- collect -> Set : supprime les doublons -- L'ordre n'est pas garanti
        // Pourquoi l'ordre est non garanti dans un Set ?
        // toSet() crée un HashSet dont l'ordre est déterminé par les hashcodes — imprévisible. Pour un ordre stable, il faut toCollection(LinkedHashSet::new).
        Set<String> collectedSet = Stream.of("cat", "dog", "cat", "elephant", "dog")
                .collect(Collectors.toSet());

        // -- collect -> Map : crée une Map à partir du Stream --
        Map<String, Integer> nameToAge = Stream.of(
                        new Employee("Alice", 30, 50000, "Femme"),
                        new Employee("Bob", 25, 40000, "Homme"),
                        new Employee("Charlie", 35, 60000, "Homme"))
                .collect(Collectors.toMap(Employee::name, Employee::age));

        // -- collect -> Map avec gestion des doublons : si plusieurs employés ont le même nom, on garde l'âge maximum --
        Map<String, Integer> nameToMaxAge = Stream.of(
                        new Employee("Alice", 30, 50000, "Femme"),
                        new Employee("Bob", 25, 40000, "Homme"),
                        new Employee("Charlie", 35, 60000, "Homme"),
                        new Employee("Alice", 32, 55000, "Femme")) // Doublon de nom
                .collect(Collectors.toMap(
                        Employee::name,
                        Employee::age,
                        Integer::max // En cas de doublon, garde l'âge maximum
                ));

        // -- Collectors avancés : groupingBy, partitioningBy, joining, summarizingInt, etc. --

        // -- La valeur par défaut est une ArrayList.
        // Le downstream implicite est Collectors.toList(). La Map retournée est une HashMap — pas d'ordre garanti.

        // -- groupingBy: Grouper par age → Map<Integer, List<Employee>>
        Map<Integer, List<Employee>> byAge = Stream.of(
                        new Employee("Alice", 30, 50000, "Femme"),
                        new Employee("Bob", 25, 40000, "Homme"),
                        new Employee("Charlie", 35, 60000, "Homme"))
                .collect(groupingBy(Employee::age));

        // -- groupingBy avec classification personnalisée : Junior (< 25), Mid (25-30), Senior (> 30)
        Map<String, List<Employee>> byAgeRange = Stream.of(
                        new Employee("Alice", 30, 50000, "Femme"),
                        new Employee("Bob", 25, 40000, "Homme"),
                        new Employee("Charlie", 35, 60000, "Homme"))
                .collect(groupingBy(it -> {
                    if (it.age > 30) return "Senior";
                    else if (it.age > 25) return "Mid";
                    else return "Junior";
                }));

        // -- groupingBy avec un clé null (ici, genre inconnu) -- Output : {"Femme": [...], "Homme": [...], "Inconnu": [...]}
        Map<String, List<Employee>> byGender = Stream.of(
                        new Employee("Alice", 30, 50000, "Femme"),
                        new Employee("Bob", 25, 40000, "Homme"),
                        new Employee("Charlie", 35, 60000, "Homme"),
                        new Employee("Dana", 28, 45000, null)) // Genre inconnu
                .collect(groupingBy(el -> el.gender() != null ? el.gender : "Inconnu"));

        // -- groupingBy avec un downstream collector : grouper par genre, puis calculer le salaire moyen par genre --
        Map<String, Double> avgSalaryByGender = Stream.of(
                        new Employee("Alice", 30, 50000, "Femme"),
                        new Employee("Bob", 25, 40000, "Homme"),
                        new Employee("Charlie", 35, 60000, "Homme"),
                        new Employee("Dana", 28, 45000, null)) // Genre inconnu
                .collect(groupingBy(
                        el -> el.gender() != null ? el.gender : "Inconnu",
                        Collectors.averagingDouble(Employee::salary)));
        // -- groupingBy avec un downstream collector : grouper par salaire haut/bas, puis compter le nombre d'employés dans chaque groupe --

        Map<String, Long> salaryCountByRange = Stream.of(
                        new Employee("Alice", 30, 50000, "Femme"),
                        new Employee("Bob", 25, 40000, "Homme"),
                        new Employee("Charlie", 35, 60000, "Homme"))
                .collect(groupingBy(
                        el -> el.salary() > 45000 ? "Haut" : "Bas",
                        Collectors.counting()));

        // -- groupingBy avec un downstream collector : grouper par âge, puis trouver l'employé le mieux payé dans chaque groupe d'âge --
        Map<Integer, Optional<Employee>> bestPaidByAge = Stream.of(
                        new Employee("Alice", 30, 50000, "Femme"),
                        new Employee("Bob", 25, 40000, "Homme"),
                        new Employee("Charlie", 35, 60000, "Homme"),
                        new Employee("Dana", 30, 55000, "Femme")) // Même âge que Alice, mais mieux payée
                .collect(groupingBy(
                        Employee::age,
                        Collectors.maxBy(Comparator.comparing(Employee::salary))));

        // -- groupingBy  avec un downstream collector : grouper par âge, puis trouver l'employé le mieux payé dans chaque groupe d'âge, en retournant null au lieu d'Optional.empty() si le groupe est vide --
        Map<Integer, Employee> bestPaidByAge2 = Stream.of(
                        new Employee("Alice", 30, 50000, "Femme"),
                        new Employee("Bob", 25, 40000, "Homme"),
                        new Employee("Charlie", 35, 60000, "Homme"),
                        new Employee("Dana", 30, 55000, "Femme")) // Même âge que Alice, mais mieux payée
                .collect(groupingBy(
                        Employee::age,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparing(Employee::salary)),
                                opt -> opt.orElse(null) // Convertit Optional<Employee> en Employee (null si vide)
                        )
                ));

        // -- groupingBy avec un downstream collector : grouper par genre, puis retourner la somme des salaires pour chaque genre --
        Map<String, Double> totalSalaryByGender = Stream.of(
                        new Employee("Alice", 30, 50000, "Femme"),
                        new Employee("Bob", 25, 40000, "Homme"),
                        new Employee("Charlie", 35, 60000, "Homme"),
                        new Employee("Dana", 28, 45000, null)) // Genre inconnu
                .collect(groupingBy(
                        el -> el.gender() != null ? el.gender() : "Inconnu",
                        Collectors.summingDouble(Employee::salary)));

        // -- groupingBy avec un downstream collector : grouper par genre, puis retourner les noms des salariés --
        Map<String, List<String>> namesByGender = Stream.of(
                        new Employee("Alice", 30, 50000, "Femme"),
                        new Employee("Bob", 25, 40000, "Homme"),
                        new Employee("Charlie", 35, 60000, "Homme"),
                        new Employee("Dana", 28, 45000, null)) // Genre inconnu
                .collect(groupingBy(
                        el -> el.gender() != null ? el.gender() : "Inconnu",
                        Collectors.mapping(Employee::name, toList())));

        // -- groupingBy avec un downstream collector : grouper par genre, puis retourner les noms des salariés sous forme d'une chaîne de caractères séparée par des virgules --
        Map<String, String> namesByGenderJoined = Stream.of(
                        new Employee("Alice", 30, 50000, "Femme"),
                        new Employee("Bob", 25, 40000, "Homme"),
                        new Employee("Charlie", 35, 60000, "Homme"),
                        new Employee("Dana", 28, 45000, null)) // Genre inconnu
                .collect(groupingBy(
                        el -> el.gender() != null ? el.gender() : "Inconnu",
                        Collectors.mapping(Employee::name, joining(", "))));

        // -- groupingBy avec multi-niveaux : grouper par genre, puis par tranche d'âge (Junior/Mid/Senior) --
        Map<String, Map<String, List<Employee>>> byGenderAndAgeRange = Stream.of(
                        new Employee("Alice", 30, 50000, "Femme"),
                        new Employee("Bob", 25, 40000, "Homme"),
                        new Employee("Charlie", 35, 60000, "Homme"),
                        new Employee("Dana", 28, 45000, null)) // Genre inconnu
                .collect(groupingBy(el -> el.gender() != null ? el.gender() : "Inconnu", groupingBy(
                        el -> el.age > 30 ? "Senior" : el.age > 25 ? "Mid" : "Junior",
                        toList()
                )));

        // -- groupingBy avec multi-niveaux : grouper par genre, puis par tranche d'âge (Junior/Mid/Senior), puis compter le nombre d'employés dans chaque sous-groupe avec un type de supplier --
        Map<String, Map<String, Long>> countByGenderAndAgeRange = Stream.of(
                        new Employee("Alice", 30, 50000, "Femme"),
                        new Employee("Bob", 25, 40000, "Homme"),
                        new Employee("Charlie", 35, 60000, "Homme"),
                        new Employee("Dana", 28, 45000, null)) // Genre inconnu
                .collect(groupingBy(
                        el -> el.gender() != null ? el.gender() : "Inconnu",
                        LinkedHashMap::new,
                        groupingBy(
                                el -> el.age() > 30 ? "Senior" : el.age() > 25 ? "Mid" : "Junior",
                                LinkedHashMap::new,
                                Collectors.counting()
                        ))
                );

        // -- groupingBy avec multi-niveaux : grouper par genre, puis par tranche d'âge (Junior/Mid/Senior), puis retourner la liste des employés dans chaque sous-groupe avec un type de supplier --
        Map<String, List<Employee>> employeesByGender = Stream.of(
                        new Employee("Alice", 30, 50000, "Femme"),
                        new Employee("Bob", 25, 40000, "Homme"),
                        new Employee("Charlie", 35, 60000, "Homme"),
                        new Employee("Dana", 28, 45000, null)) // Genre inconnu
                .collect(groupingBy(
                                el -> el.gender() != null ? el.gender() : "Inconnu",
                                LinkedHashMap::new,
                                toList()
                        )
                );

        // -- partitioningBy : sépare les éléments en deux groupes selon un prédicat (true/false) --
        //-- partitioningBy : sépare les éléments en deux groupes selon un prédicat (true/false) --  Output : {true: [...], false: [...]}
        Map<Boolean, List<Employee>> partitionedBySalary = Stream.of(
                        new Employee("Alice", 30, 50000, "Femme"),
                        new Employee("Bob", 25, 40000, "Homme"),
                        new Employee("Charlie", 35, 60000, "Homme"))
                .collect(partitioningBy(e -> e.salary() > 45000));

        // -- partitioningBy avec un downstream collector : séparer les éléments en deux groupes selon un prédicat, puis compter le nombre d'employés dans chaque groupe --
        Map<Boolean, Long> countPartitionedBySalary = Stream.of(
                        new Employee("Alice", 30, 50000, "Femme"),
                        new Employee("Bob", 25, 40000, "Homme"),
                        new Employee("Charlie", 35, 60000, "Homme"))
                .collect(partitioningBy(e -> e.salary() > 45000, Collectors.counting()));


    }

    record Employee(String name, int age, double salary, String gender) {
    }
}