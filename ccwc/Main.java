import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Main{


    public static void main(String[] args) {
        
        
        Arrays.asList(args).stream().forEach(System.out::println);
        System.out.println("");

        String operation = "";
        String filePath = "";
        
        for(String arg:args){
            if(arg.startsWith("-")){
                operation = arg;
            }
            if(arg.endsWith(".txt")){
                filePath = arg;
            }
        }
        
        List<String> commands = List.of("-c","-l","-w");

        if(operation == null || operation.isBlank()){
            final String fp = filePath; 
            commands.stream().forEach(command->proccessCommand(command,fp)); 
        }

        else{
            proccessCommand(operation, filePath);
        }
    }

    public static void proccessCommand(String command,String filePath){
        Path path = Paths.get(filePath);
        File file = new File(filePath);

        switch(command){
            case "-c":
            System.out.println("file size : " + file.length());
            break;
            case "-l":
                try {
                    System.out.println("number of lines : "+Files.readAllLines(Paths.get(filePath)).size());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            break;
            case "-w":
                System.out.println("total words : "+countWords(filePath));
            break;
            case "-m":
                try {
                    System.out.println(Files.readString(path).toCharArray().length);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    
    }

    public static Integer countWords(String filePath){
        Integer totalWords = 0 ;
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            for(String line : lines){
             if(!line.isEmpty()){
                String[] wordsInLine =  line.trim().split(" ");
                totalWords += wordsInLine.length;
             }   
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return totalWords;

    }
}