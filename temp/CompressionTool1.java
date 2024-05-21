import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

class HuffmanNodeTemp implements Serializable {
    char ch;
    int val;
    HuffmanNodeTemp right;
    HuffmanNodeTemp left;

    public HuffmanNodeTemp() {
    }

    public HuffmanNodeTemp(char ch, int val) {
        this.ch = ch;
        this.val = val;
    }
}

class MyComparator1 implements Comparator<HuffmanNodeTemp> {
    public int compare(HuffmanNodeTemp x, HuffmanNodeTemp y) {
        return x.val - y.val;
    }
}

public class CompressionTool1 {

    public static void main(String[] args) {
        // Example file path
        String filePath = "D:\\jskr456\\CodingChallenges.fyi\\compression_tool\\135-0.txt";

        if (!filePath.endsWith(".txt") || !new File(filePath).isFile()) {
            System.err.println("Invalid file");
            System.exit(0);
        }

        // Step 1: Read the file content
        String str = readFileContent(filePath);

        // Step 2: Get character frequencies
        Map<Character, Integer> charFrequencies = getCharFrequencies(str);
        charFrequencies.forEach((key, value) -> System.out.println(key + " | " + value));

        // Step 3: Create Huffman Tree
        HuffmanNodeTemp root = createTree(charFrequencies, charFrequencies.size());

        // Step 4: Generate Huffman Codes
        Map<Character, String> codes = new TreeMap<>();
        generatePrefixCodeTable(root, "", codes);
        codes.forEach((key, value) -> System.out.println(key + " | " + value));

        // Step 5: Serialize Huffman Tree and Encoded Data
        String compressedFilePath = "huffmancode.bin";
        writeCompressedFile(root, codes, str, compressedFilePath);

        // Step 6: Decode the File
        String decompressedFilePath = "decompressed.txt";
        decodeFile(compressedFilePath, decompressedFilePath);
    }

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

    private static HuffmanNodeTemp createTree(Map<Character, Integer> charFrequencies, int n) {
        PriorityQueue<HuffmanNodeTemp> minHeap = new PriorityQueue<>(n, new MyComparator1());

        for (Character ch : charFrequencies.keySet()) {
            HuffmanNodeTemp huffmanNode = new HuffmanNodeTemp(ch, charFrequencies.get(ch));
            minHeap.add(huffmanNode);
        }

        HuffmanNodeTemp root = null;
        while (minHeap.size() > 1) {
            HuffmanNodeTemp node1 = minHeap.poll();
            HuffmanNodeTemp node2 = minHeap.poll();

            HuffmanNodeTemp parentNode = new HuffmanNodeTemp('|', node1.val + node2.val);
            parentNode.left = node1;
            parentNode.right = node2;
            root = parentNode;

            minHeap.add(parentNode);
        }
        return root;
    }

    public static void generatePrefixCodeTable(HuffmanNodeTemp root, String code, Map<Character, String> codes) {
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

    public static Map<Character, Integer> getCharFrequencies(String str) {
        return str.chars().mapToObj(c -> (char) c)
                .collect(Collectors.groupingBy(c -> c, TreeMap::new, Collectors.summingInt(c -> 1)));
    }

    public static void writeCompressedFile(HuffmanNodeTemp root, Map<Character, String> codes, String text, String compressedFilePath) {
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

    public static void decodeFile(String compressedFilePath, String outputFilename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(compressedFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilename))) {
            // Deserialize Huffman Tree
            HuffmanNodeTemp root = (HuffmanNodeTemp) ois.readObject();

            // Read binary data
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            int byteData;
            while ((byteData = ois.read()) != -1) {
                byteOut.write(byteData);
            }

            // Convert bytes to binary string
            StringBuilder bitString = new StringBuilder();
            for (byte b : byteOut.toByteArray()) {
                /*
                 Convert the byte array from ByteArrayOutputStream to its binary string representation
                 and append it to the StringBuilder bitString.

                 Step-by-step explanation:
                 1. byteOut.toByteArray() converts the contents of the ByteArrayOutputStream to a byte array.
                 2. This loop iterates over each byte in the byte array.
                 3. Integer.toBinaryString(b & 0xFF) converts the byte to an unsigned integer and then to its binary string.
                    - b & 0xFF: Ensures the byte is treated as an unsigned 8-bit value.
                    - Integer.toBinaryString(int i): Converts the integer to a binary string.
                    - Example: if b is -1 (11111111 in binary as a signed byte), b & 0xFF results in 255 (11111111 in binary as unsigned).
                 4. String.format("%8s", ...) formats the binary string to be at least 8 characters wide.
                    - If the binary string has fewer than 8 characters, spaces are added to the beginning.
                    - Example: "101" becomes "     101".
                 5. .replace(' ', '0'): Replaces all spaces in the formatted string with zeros.
                    - Continuing the example: "     101" becomes "00000101".
                 6. bitString.append(...): Appends the 8-character binary string to the StringBuilder bitString.
                   */
                // Convert the byte to its unsigned integer representation and then to its binary string
                String binaryString = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
                // Append the binary string to bitString
                bitString.append(binaryString);
            }


            // Decode binary string to original text
            HuffmanNodeTemp currentNode = root;
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
