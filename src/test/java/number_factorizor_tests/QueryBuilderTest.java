package number_factorizor_tests;

import org.junit.Test;
import org.number_factorizor.QueryBuilder;

import static org.junit.Assert.assertEquals;

public class QueryBuilderTest {
    @Test
    public void givenBuildSelectQueryMethod_whenAskedForAllTableRows_thenSelectAllFromTableQueryIsPrepared() {
        QueryBuilder queryBuilder = new QueryBuilder();
        int columnsNumber = 1;
        String expectedQuery = "SELECT ? FROM ?";
        assertEquals(expectedQuery, queryBuilder.buildSelectQuery(columnsNumber).toString());
    }

    @Test
    public void givenBuildSelectQueryMethod_whenAskedForTwoSpecificTableColumns_thenSelectAllFromTableQueryIsPrepared() {
        QueryBuilder queryBuilder = new QueryBuilder();
        int columnsNumber = 2;
        String expectedQuery = "SELECT ?, ? FROM ?";
        assertEquals(expectedQuery, queryBuilder.buildSelectQuery(columnsNumber).toString());
    }

    @Test
    public void givenBuildSelectQueryMethod_whenCalledWithSingleCondition_thenSelectWithWhereClauseIsPrepared() {
        QueryBuilder queryBuilder = new QueryBuilder();
        int columnsNumber = 2;
        int argsNumber = 1;
        String expectedQuery = "SELECT ?, ? FROM ? WHERE ?=?";
        assertEquals(expectedQuery, queryBuilder.buildSelectQuery(columnsNumber, argsNumber).toString());
    }

    @Test
    public void givenBuildSelectQueryMethod_whenCalledWithTwoConditions_thenSelectWithWhereClauseIsPrepared() {
        QueryBuilder queryBuilder = new QueryBuilder();
        int columnsNumber = 2;
        int argsNumber = 2;
        String expectedQuery = "SELECT ?, ? FROM ? WHERE ?=? AND ?=?";
        assertEquals(expectedQuery, queryBuilder.buildSelectQuery(columnsNumber, argsNumber).toString());
    }
}
