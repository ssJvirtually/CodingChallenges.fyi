import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Temp {

    public static void main(String[] args) throws IOException {

        List<String> strings = Files.readAllLines(Path.of("D:\\jskr456\\CodingChallenges.fyi\\ccwc\\ccwc.bat"));

        strings.forEach(System.out::println);


    }

}
