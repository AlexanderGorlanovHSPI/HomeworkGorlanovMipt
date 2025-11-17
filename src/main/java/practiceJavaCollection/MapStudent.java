package practiceJavaCollection;

import java.util.*;
import java.util.stream.Collectors;

public class MapStudent {
    public static HashMap<Integer, Student> createHashMap(){
        return new HashMap<>();
    }

    public static TreeMap<Integer, Student> createTreeMap(){
        return new TreeMap<>(Collections.reverseOrder());
    }

    public static List<Student> findStudentsByGradeRange(Map<Integer, Student> map, double minGrade, double maxGrade){
        if (map == null || map.isEmpty()) {
            return new ArrayList<>();
        } if (minGrade > maxGrade){
            throw new IllegalArgumentException("minGrade не может быть больше maxGrade");
        }

        return map.values().stream().filter(student -> student.getGrade() >= minGrade && student.getGrade() <= maxGrade).collect(Collectors.toList());
    }

    public static List<Student> getTopNStudents(TreeMap<Integer, Student> map, int n){
        if (map == null || map.isEmpty()){
            return new ArrayList<>();
        }
        if (n <= 0){
            throw new IllegalArgumentException("n должно быть положительным числом");
        }

        List<Student> result = new ArrayList<>();
        int count = 0;

        for (Student student : map.values()){
            if (count >= n){
                break;
            }
            result.add(student);
            count++;
        }

        return result;
    }
}
