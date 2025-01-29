package io.recheck.uuidprotocol.common.querybuilder.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.firestore.v1.StructuredQuery;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryCompositeFilterDTO {

    public enum CompositeOperator {
        OR("OR"),
        AND("AND");

        private String text;

        CompositeOperator(String text) {
            this.text = text;
        }

        public String getText() {
            return this.text;
        }
    }

    private CompositeOperator compositeOperator;

    @JsonProperty("queryUnaryFilters")
    private List<QueryUnaryFilterDTO> queryUnaryFiltersDTO;

    @JsonProperty("queryCompositeFilters")
    private List<QueryCompositeFilterDTO> queryCompositeFiltersDTO;

    public QueryCompositeFilterDTO(CompositeOperator compositeOperator, List<QueryUnaryFilterDTO> queryUnaryFiltersDTO) {
        this.compositeOperator = compositeOperator;
        this.queryUnaryFiltersDTO = queryUnaryFiltersDTO;
    }

    public QueryCompositeFilter build() {
        QueryCompositeFilter queryCompositeFilter = new QueryCompositeFilter();
        if (compositeOperator != null) {
            queryCompositeFilter.setCompositeOperator(StructuredQuery.CompositeFilter.Operator.valueOf(this.compositeOperator.getText()));
        }

        if (queryUnaryFiltersDTO != null) {
            List<QueryUnaryFilter> queryUnaryFilters = new ArrayList<>();
            for (QueryUnaryFilterDTO queryUnaryFilterDTO : queryUnaryFiltersDTO) {
                queryUnaryFilters.add(queryUnaryFilterDTO.build());
            }
            queryCompositeFilter.setQueryUnaryFilters(queryUnaryFilters);
        }

        if (queryCompositeFiltersDTO != null) {
            List<QueryCompositeFilter> queryCompositeFilters = new ArrayList<>();
            for (QueryCompositeFilterDTO queryCompositeFilterDTO : queryCompositeFiltersDTO) {
                queryCompositeFilters.add(queryCompositeFilterDTO.build());
            }
            queryCompositeFilter.setQueryCompositeFilters(queryCompositeFilters);
        }

        return queryCompositeFilter;
    }

}

