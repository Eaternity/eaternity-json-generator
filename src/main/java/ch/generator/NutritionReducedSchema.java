package ch.generator;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import static ch.generator.NutritionReducedSchema.ID;
import static ch.generator.NutritionReducedSchema.NAME;
import static ch.generator.NutritionReducedSchema.NUTR_CHANGE_FACTOR;
import static ch.generator.NutritionReducedSchema.PROCESS;

/**
 *
 */
@JsonPropertyOrder({ID, NAME, PROCESS, NUTR_CHANGE_FACTOR})
public class NutritionReducedSchema {

    static final String ID = "id";
    static final String NAME = "name";
    static final String PROCESS = "process";
    static final String NUTR_CHANGE_FACTOR = "nutr-change-factor";

    private String id;
    private String name;
    private String process;
    private String nutrChangeFactor;

    public NutritionReducedSchema() { }

    public NutritionReducedSchema(final String id, final String name, final String process, final String nutrChangeFactor) { }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(final String process) {
        this.process = process;
    }

    public String getNutrChangeFactor() {
        return nutrChangeFactor;
    }

    public void setNutrChangeFactor(final String nutrChangeFactor) {
        this.nutrChangeFactor = nutrChangeFactor;
    }
}
