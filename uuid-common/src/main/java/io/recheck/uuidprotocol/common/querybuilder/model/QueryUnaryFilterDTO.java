package io.recheck.uuidprotocol.common.querybuilder.model;

import com.google.firestore.v1.StructuredQuery;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryUnaryFilterDTO {

    public enum UnaryOperator {
        EQUAL("EQUAL"),
        NOT_EQUAL("NOT_EQUAL"),
        GREATER_THAN("GREATER_THAN"),
        GREATER_THAN_OR_EQUAL("GREATER_THAN_OR_EQUAL"),
        LESS_THAN("LESS_THAN"),
        LESS_THAN_OR_EQUAL("LESS_THAN_OR_EQUAL"),
        IN("IN"),
        NOT_IN("NOT_IN"),
        ARRAY_CONTAINS("ARRAY_CONTAINS"),
        ARRAY_CONTAINS_ANY("ARRAY_CONTAINS_ANY");

        private String text;

        UnaryOperator(String text) {
            this.text = text;
        }

        public String getText() {
            return this.text;
        }
    }

    @NotBlank
    private String field;
    @NotNull
    private UnaryOperator operator;
    @NotEmpty
    @Size(min = 1, max = 3)
    private List<Object> value;


    public QueryUnaryFilter build() {
        QueryUnaryFilter queryUnaryFilter = new QueryUnaryFilter();
        queryUnaryFilter.setField(field);
        queryUnaryFilter.setOperator(StructuredQuery.FieldFilter.Operator.valueOf(operator.getText()));
        queryUnaryFilter.setValue(value);
        return queryUnaryFilter;
    }

}
