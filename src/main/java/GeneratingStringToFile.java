/*
  Нужно написать программу, которая на основании приложенного файла генерирует текстовый файл, содержащий все строки, переданные в функцию NStr в следующем формате.

  «Номер строки в файле» : «язык строки = language code» : «сама строка на указанном языке = actual lines of text»

  Пример для первой строки из файла

  730: en : EXTERNAL CONNECTION: %1

  730: ru : ВНЕШНЕЕ СОЕДИНЕНИЕ: %1

  730: vi : KẾT NỐI NGOÀI: %1

  730: ro : CONEXIUNEA EXTERNĂ: %1'
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GeneratingStringToFile {

    private static final String INPUT_FILE_NAME = "src/main/resources/ObjectModule.bsl";
    private static final String OUTPUT_FILE_NAME = "src/main/resources/output.txt";
    private static final String FUNCTION_SEARCH = "NStr(";
    private static int numberLine = 0;
    private static String generatedString = "";
    private static int startSubstringIndex = 0;
    private static int endSubStringIndex = 0;

    public static void main(String[] args) {

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(INPUT_FILE_NAME))) {
            String lines = bufferedReader.readLine();

            while (lines != null) {
                numberLine = numberLine + 1;

                while (lines.contains(FUNCTION_SEARCH)) {
                    startSubstringIndex = lines.indexOf(FUNCTION_SEARCH) + FUNCTION_SEARCH.length() + 1;
                    endSubStringIndex = lines.indexOf("'\"", startSubstringIndex); //

                    if (lines.contains("'\"")) {
                        lines = extractFragment(lines); // select the required fragment of the line
                    } else {
                        break;
                    }
                }

                if (endSubStringIndex < 0) {  // if the function (NStr) continues on the next line, concat the lines
                    lines = lines + bufferedReader.readLine();
                } else {
                    lines = bufferedReader.readLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileWriter fileWriter = new FileWriter(OUTPUT_FILE_NAME)) {
            fileWriter.write(generatedString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String extractFragment(String line) {
        String[] array = line.substring(startSubstringIndex, endSubStringIndex).split(";"); // divide the string into lang codes

        for (int i = 0; i < array.length; i++) {
            array[i] = array[i].replace("'", "");
            array[i] = array[i].replace("|", "");
            array[i] = array[i].replace("=", ":");
            array[i] = array[i].replaceAll("\\s+", " ").trim();  // "replaceAll" for removing white space when transferring a line, "trim" to remove white space before lang code
            generatedString = generatedString + numberLine + ": " + array[i] + "\n";
        }
        return line.substring(endSubStringIndex);
    }
}
