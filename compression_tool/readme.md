# CompressionTool

CompressionTool is a Java-based application that performs file compression and decompression using Huffman coding. This tool supports encoding text files into compressed binary files and decoding them back to their original text format.

## Features

- **Encode**: Compress text files using Huffman coding.
- **Decode**: Decompress binary files back to their original text format.

## Requirements

- Java 11 or higher
- Maven (for building the project)

## Building the Project

To build the project, follow these steps:

1. Clone the repository:

    ```sh
    git clone https://github.com/yourusername/compression_tool.git
    cd compression_tool
    ```

2. Use Maven to build the project:

    ```sh
    mvn clean package
    ```

   The command will generate a JAR file in the `target` directory, typically named `compression_tool-1.0-SNAPSHOT.jar`.

## Usage

### Command-Line Options

- `-o, --operation`: The operation to perform: `encode` or `decode`. (Required)
- `-i, --input`: The input file path for encoding or decoding. (Required)
- `-d, --decoded`: The output file path for decoded content. (Required for decoding)
- `-h, --help`: Show help message and exit.
- `-V, --version`: Print version information and exit.

### Examples

#### Encoding a File

To encode a text file (`input.txt`):

```sh
java -jar out/artifacts/compression_tool_jar/compression_tool.jar --operation encode --input path/to/input.txt
```


### project learnings
1. file input
2. file output
3. String operations
4. Heap
5. Tree
6. bit manipulation
7. serialization
8. deserialization