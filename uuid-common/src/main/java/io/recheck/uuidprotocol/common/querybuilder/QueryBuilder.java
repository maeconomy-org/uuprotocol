package io.recheck.uuidprotocol.common.querybuilder;

import com.google.cloud.firestore.Filter;
import com.google.firestore.v1.StructuredQuery;
import io.recheck.uuidprotocol.common.exceptions.QueryBuildException;
import io.recheck.uuidprotocol.common.querybuilder.model.QueryCompositeFilter;
import io.recheck.uuidprotocol.common.querybuilder.model.QueryUnaryFilter;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;

public class QueryBuilder {

    @SneakyThrows
    public static Filter build(QueryCompositeFilter queryCompositeFilter) {
        if (queryCompositeFilter.getCompositeOperator() == null) {
            if (queryCompositeFilter.getQueryUnaryFilters() != null) {
                if (queryCompositeFilter.getQueryUnaryFilters().size() == 1) {
                    return buildUnaryFilter(queryCompositeFilter.getQueryUnaryFilters().get(0));
                }
                else if (queryCompositeFilter.getQueryUnaryFilters().size() > 1) {
                    throw new QueryBuildException("queryUnaryFilters.compositeOperator not found");
                }
            }

            if (queryCompositeFilter.getQueryCompositeFilters() != null) {
                if (queryCompositeFilter.getQueryCompositeFilters().size() == 1) {
                    return buildCompositeFilter(queryCompositeFilter.getQueryCompositeFilters().get(0));
                }
                else if (queryCompositeFilter.getQueryUnaryFilters().size() > 1) {
                    throw new QueryBuildException("queryCompositeFilter.compositeOperator not found");
                }
            }
        }
        else if (queryCompositeFilter.getQueryUnaryFilters() != null && queryCompositeFilter.getQueryUnaryFilters().size() > 1) {
            if (queryCompositeFilter.getQueryCompositeFilters() != null && queryCompositeFilter.getQueryCompositeFilters().size() > 1) {
                throw new QueryBuildException("can't build query (choose unary or composite)");
            }
            return buildCompositeFilter(queryCompositeFilter);
        }
        else if (queryCompositeFilter.getQueryCompositeFilters() != null && queryCompositeFilter.getQueryCompositeFilters().size() > 1) {
            return buildCompositeFilter(queryCompositeFilter.getQueryCompositeFilters(), queryCompositeFilter.getCompositeOperator());
        }
        throw new QueryBuildException("can't build query (no enough filters)");
    }

    @SneakyThrows
    private static Filter buildCompositeFilter(List<QueryCompositeFilter> queryCompositeFilter, StructuredQuery.CompositeFilter.Operator compositeOperator) {
        Filter filter = null;
        switch (compositeOperator) {
            case OR:
                List<Filter> unaryFiltersOr = new ArrayList<>();
                for (QueryCompositeFilter queryCompositeFilterOr : queryCompositeFilter) {
                    unaryFiltersOr.add(build(queryCompositeFilterOr));
                }
                filter = Filter.or(unaryFiltersOr.toArray(new Filter[0]));
                break;
            case AND:
                List<Filter> unaryFiltersAnd = new ArrayList<>();
                for (QueryCompositeFilter queryCompositeFilterAnd : queryCompositeFilter) {
                    unaryFiltersAnd.add(build(queryCompositeFilterAnd));
                }
                filter = Filter.and(unaryFiltersAnd.toArray(new Filter[0]));
                break;
            default:
                throw new QueryBuildException("queryCompositeFilter.compositeOperator not found");
        }
        return filter;
    }

    @SneakyThrows
    private static Filter buildCompositeFilter(QueryCompositeFilter queryCompositeFilter) {
        if (queryCompositeFilter.getCompositeOperator() == null) {
            throw new QueryBuildException("queryCompositeFilter.compositeOperator not found");
        }
        Filter filter = null;
        switch (queryCompositeFilter.getCompositeOperator()) {
            case OR:
                List<Filter> unaryFiltersOr = new ArrayList<>();
                for (QueryUnaryFilter queryUnaryFilter : queryCompositeFilter.getQueryUnaryFilters()) {
                    unaryFiltersOr.add(buildUnaryFilter(queryUnaryFilter));
                }
                filter = Filter.or(unaryFiltersOr.toArray(new Filter[0]));
                break;
            case AND:
                List<Filter> unaryFiltersAnd = new ArrayList<>();
                for (QueryUnaryFilter queryUnaryFilter : queryCompositeFilter.getQueryUnaryFilters()) {
                    unaryFiltersAnd.add(buildUnaryFilter(queryUnaryFilter));
                }
                filter = Filter.and(unaryFiltersAnd.toArray(new Filter[0]));
                break;
            default:
                throw new QueryBuildException("queryCompositeFilter.compositeOperator not found");
        }
        return filter;
    }

    @SneakyThrows
    private static Filter buildUnaryFilter(QueryUnaryFilter queryUnaryFilter) {
        if (queryUnaryFilter.getOperator() == null) {
            throw new QueryBuildException("queryUnaryFilter.operator not found");
        }
        Filter filter = null;
        switch (queryUnaryFilter.getOperator()) {
            case EQUAL:
                filter = Filter.equalTo(queryUnaryFilter.getField(), queryUnaryFilter.getValue().get(0));
                break;
            case NOT_EQUAL:
                filter = Filter.notEqualTo(queryUnaryFilter.getField(), queryUnaryFilter.getValue().get(0));
                break;
            case GREATER_THAN:
                filter = Filter.greaterThan(queryUnaryFilter.getField(), queryUnaryFilter.getValue().get(0));
                break;
            case GREATER_THAN_OR_EQUAL:
                filter = Filter.greaterThanOrEqualTo(queryUnaryFilter.getField(), queryUnaryFilter.getValue().get(0));
                break;
            case LESS_THAN:
                filter = Filter.lessThan(queryUnaryFilter.getField(), queryUnaryFilter.getValue().get(0));
                break;
            case LESS_THAN_OR_EQUAL:
                filter = Filter.lessThanOrEqualTo(queryUnaryFilter.getField(), queryUnaryFilter.getValue().get(0));
                break;
            case IN:
                filter = Filter.inArray(queryUnaryFilter.getField(), queryUnaryFilter.getValue());
                break;
            case NOT_IN:
                filter = Filter.notInArray(queryUnaryFilter.getField(), queryUnaryFilter.getValue());
                break;
            case ARRAY_CONTAINS:
                filter = Filter.arrayContains(queryUnaryFilter.getField(), queryUnaryFilter.getValue().get(0));
                break;
            case ARRAY_CONTAINS_ANY:
                filter = Filter.arrayContainsAny(queryUnaryFilter.getField(), queryUnaryFilter.getValue());
                break;
            default:
                throw new QueryBuildException("queryUnaryFilter.operator not found");
        }

        return filter;
    }

}
