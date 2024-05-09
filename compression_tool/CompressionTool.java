import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class CompressionTool{

    public static void main(String[] args) {
    
        Map<Character,Long> charFrequencies = getCharFrequencies("abbcdbccdaabbeeebeab");
        System.out.println(charFrequencies);
        PriorityQueue<Integer> priorityQueue = new PriorityQueue<>(Comparator.reverseOrder());

        priorityQueue.add(100);
        priorityQueue.add(10);
        priorityQueue.add(15);
        priorityQueue.add(11);
        priorityQueue.add(134);

        System.out.println(priorityQueue);
        

    }


    public static  Map<Character,Long> getCharFrequencies(String str){
        
        return str.chars().mapToObj(c->(char)c)
                          .collect(Collectors.groupingBy(c ->c,TreeMap::new,Collectors.counting()));
                          
    }
}