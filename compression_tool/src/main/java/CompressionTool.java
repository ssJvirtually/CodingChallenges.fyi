import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

// Huffman tree node
class HuffmanNode implements Serializable {
    char ch;
    int val;
    HuffmanNode right;
    HuffmanNode left;

    public HuffmanNode() {
    }

    public HuffmanNode(char ch, int val) {
        this.ch = ch;
        this.val = val;
    }
}

// Comparator for Huffman tree
class MyComparator implements Comparator<HuffmanNode> {
    public int compare(HuffmanNode x, HuffmanNode y) {
        return x.val - y.val;
    }
}

@Command(name = "CompressionTool", mixinStandardHelpOptions = true, version = "CompressionTool 1.0",
        description = "Encodes and decodes files using Huffman coding.")
public class CompressionTool implements Runnable {

    @Option(names = {"-o", "--operation"}, description = "The operation to perform: encode or decode", required = true)
    private String operation;

    @Option(names = {"-i", "--input"}, description = "The input file path for encoding or decoding", required = true)
    private String inputFilename;

    @Option(names = {"-d", "--decoded"}, description = "The output file path for decoded content")
    private String decodedOutputFilename;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new CompressionTool()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        switch (operation) {
            case "encode":
                encodeFile(inputFilename);
                break;
            case "decode":
                decodeFile(inputFilename, decodedOutputFilename);
                break;
            default:
                System.err.println("Invalid operation. Use 'encode' or 'decode'.");
        }
    }

    public static void encodeFile(String filePath) {
        Path path = Path.of(filePath);
        if (!new File(filePath).isFile()) {
            System.err.println("Invalid file");
            System.exit(0);
        }

        // Step 1: Read the file content
        String str = readFileContent(filePath);

        // Step 2: Get character frequencies & Create Huffman Tree
        Map<Character, Integer> charFrequencies = getCharFrequencies(str);
        charFrequencies.forEach((key, value) -> System.out.println(key + " | " + value));
        HuffmanNode root = createTree(charFrequencies, charFrequencies.size());

        // Step 3: Generate Huffman Codes
        Map<Character, String> codes = new TreeMap<>();
        generatePrefixCodeTable(root, "", codes);
        codes.forEach((key, value) -> System.out.println(key + " | " + value));

        // Step 4 5: Serialize Huffman Tree and Encoded Data
        String compressedFilePath = path.getParent() + File.separator + path.getFileName().toString().replace(".txt", "") + ".bin";
        writeCompressedFile(root, codes, str, compressedFilePath);
    }

    /**
     * Reads the content of a file and returns it as a string.
     *
     * @param filePath the path of the file to be read
     * @return the content of the file as a string
     */
    private static String readFileContent(String filePath) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * Creates a Huffman tree from the given character frequencies.
     *
     * @param charFrequencies
     * @param n
     * @return
     */
    private static HuffmanNode createTree(Map<Character, Integer> charFrequencies, int n) {
        PriorityQueue<HuffmanNode> minHeap = new PriorityQueue<>(n, new MyComparator());

        for (Character ch : charFrequencies.keySet()) {
            HuffmanNode huffmanNode = new HuffmanNode(ch, charFrequencies.get(ch));
            minHeap.add(huffmanNode);
        }

        HuffmanNode root = null;
        while (minHeap.size() > 1) {
            HuffmanNode node1 = minHeap.poll();
            HuffmanNode node2 = minHeap.poll();

            HuffmanNode parentNode = new HuffmanNode('|', node1.val + node2.val);
            parentNode.left = node1;
            parentNode.right = node2;
            root = parentNode;

            minHeap.add(parentNode);
        }
        return root;
    }

    /**
     * Generates a prefix code table for the given Huffman tree.
     *
     * @param root  the root node of the Huffman tree
     * @param code  the current code being generated
     * @param codes the map to store the generated prefix codes
     */
    public static void generatePrefixCodeTable(HuffmanNode root, String code, Map<Character, String> codes) {
        if (root == null) {
            return;
        }

        if (root.left == null && root.right == null) {
            codes.put(root.ch, code);
            return;
        }

        generatePrefixCodeTable(root.left, code + "0", codes);
        generatePrefixCodeTable(root.right, code + "1", codes);
    }

    /**
     * Calculates the frequency of each character in a given string.
     *
     * @param str the input string
     * @return a map containing each character and its frequency
     */
    public static Map<Character, Integer> getCharFrequencies(String str) {
        return str.chars().mapToObj(c -> (char) c)
                .collect(Collectors.groupingBy(c -> c, TreeMap::new, Collectors.summingInt(c -> 1)));
    }

    /**
     * Writes the compressed file to a binary file.
     *
     * @param root
     * @param codes
     * @param text
     * @param compressedFilePath
     */
    public static void writeCompressedFile(HuffmanNode root, Map<Character, String> codes, String text, String compressedFilePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(compressedFilePath))) {
            // Serialize Huffman Tree
            oos.writeObject(root);

            // Convert text to encoded binary string
            StringBuilder encodedText = new StringBuilder();
            for (char c : text.toCharArray()) {
                encodedText.append(codes.get(c));
            }

            // Convert binary string to bytes and write to file
            writeBits(oos, encodedText.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes a given bit string to an ObjectOutputStream as a byte array.
     *
     * @param oos       the ObjectOutputStream to write to
     * @param bitString the bit string to write
     * @throws IOException if an I/O error occurs
     */
    private static void writeBits(ObjectOutputStream oos, String bitString) throws IOException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        int byteVal = 0;
        int bitCount = 0;

        for (char bit : bitString.toCharArray()) {
            byteVal = (byteVal << 1) | (bit - '0');
            bitCount++;

            if (bitCount == 8) {
                byteOut.write(byteVal);
                byteVal = 0;
                bitCount = 0;
            }
        }

        if (bitCount > 0) {
            byteVal = byteVal << (8 - bitCount);
            byteOut.write(byteVal);
        }

        oos.write(byteOut.toByteArray());
    }

    /**
     * Decodes the compressed file and writes the decoded text to an output file.
     *
     * @param compressedFilePath
     * @param outputFilename
     */
    public static void decodeFile(String compressedFilePath, String outputFilename) {
        if (outputFilename == null || outputFilename.isEmpty()) {
            System.err.println("Output filename for decoding is required.");
            System.exit(1);
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(compressedFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilename))) {
            // Deserialize Huffman Tree
            HuffmanNode root = (HuffmanNode) ois.readObject();

            // Read binary data
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            int byteData;
            while ((byteData = ois.read()) != -1) {
                byteOut.write(byteData);
            }

            // Convert bytes to binary string
            StringBuilder bitString = new StringBuilder();
            for (byte b : byteOut.toByteArray()) {
                bitString.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
            }

            // Decode binary string to original text
            HuffmanNode currentNode = root;
            for (char bit : bitString.toString().toCharArray()) {
                currentNode = (bit == '0') ? currentNode.left : currentNode.right;

                if (currentNode.left == null && currentNode.right == null) {
                    writer.write(currentNode.ch);
                    currentNode = root;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
