package ch.generator;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static ch.generator.Constants.FILENAME_NUTRITION_CHANGE;
import static ch.generator.Constants.FILENAME_NUTRITION_CHANGE_IDENTIFIER;
import static ch.generator.Constants.FILENAME_WORD_SEPARATOR;
import static ch.generator.Constants.NUTRITION_CHANGE_JSON_DIRECTORY;
import static ch.generator.Constants.JSON_FILE_SUFFIX;

/**
 * parses the file with name FILENAME_NUTRITION_CHANGE placed in the classpath of this module
 * an generates json files from that input into directory.
 */
public class JacksonCSVParser {

    public void parse() {
        InputStream input = this.getClass().getClassLoader().getResourceAsStream(FILENAME_NUTRITION_CHANGE);

        List<NutritionReducedSchema> data;
        try {
            data = readObjectsFromCsv(input);
            writeAsJson(data);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private List<NutritionReducedSchema> readObjectsFromCsv(final InputStream stream) throws IOException {
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = csvMapper.schemaFor(NutritionReducedSchema.class);
        schema = schema.withColumnSeparator(Constants.DEFAULT_CSV_DELIMITER);
        schema = schema.withHeader();

        MappingIterator<NutritionReducedSchema> mappingIterator
                = csvMapper.reader(NutritionReducedSchema.class).with(schema).readValues(stream);

        return mappingIterator.readAll();
    }

    private void writeAsJson(final List<NutritionReducedSchema> data) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        for (NutritionReducedSchema schema : data) {
            StringBuilder builder = new StringBuilder(NUTRITION_CHANGE_JSON_DIRECTORY);
            builder.append(File.separator).append(schema.getId()).append(FILENAME_WORD_SEPARATOR)
                    .append(schema.getName()).append(FILENAME_WORD_SEPARATOR)
                    .append(FILENAME_NUTRITION_CHANGE_IDENTIFIER).append(JSON_FILE_SUFFIX);
            File file = new File(builder.toString());
            mapper.writeValue(file, schema);
        }
    }
}
