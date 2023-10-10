package number_factorizor_tests;

import org.junit.Test;
import org.number_factorizor.QueryBuilder;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class QueryBuilderTest {
    @Test
    public void givenBuildSelectQueryMethod_whenAskedForAllTableRows_thenSelectAllFromTableQueryIsPrepared() {
        QueryBuilder queryBuilder = new QueryBuilder();
        ArrayList<String> columnNames = new ArrayList<>();
        columnNames.add("*");
        String tableName = "table1";
        String expectedQuery = "SELECT * FROM table1";

        assertEquals(expectedQuery, queryBuilder.buildSelectQuery(columnNames, tableName).toString());
    }

    @Test
    public void givenBuildSelectQueryMethod_whenAskedForTwoSpecificTableColumns_thenSelectAllFromTableQueryIsPrepared() {
        QueryBuilder queryBuilder = new QueryBuilder();
        ArrayList<String> columnNames = new ArrayList<>();
        columnNames.add("col1");
        columnNames.add("col2");
        String tableName = "table1";
        String expectedQuery = "SELECT col1, col2 FROM table1";

        assertEquals(expectedQuery, queryBuilder.buildSelectQuery(columnNames, tableName).toString());
    }

    @Test
    public void givenBuildSelectQueryMethod_whenCalledWithEqualNumberOfKeysAndValues_thenSelectWithWhereClauseIsPrepared() {
        QueryBuilder queryBuilder = new QueryBuilder();
        ArrayList<String> columnNames = new ArrayList<>();
        columnNames.add("col1");
        columnNames.add("col2");
        String tableName = "table1";
        int numberOfConditions = 1;
        String expectedQuery = "SELECT col1, col2 FROM table1 WHERE ?=?";

        assertEquals(expectedQuery, queryBuilder.buildSelectQuery(columnNames, tableName, numberOfConditions).toString());
    }

    @Test
    public void givenBuildSelectQueryMethod_whenCalledWithEqualNumberOfKeysAndValuesAndValuesAreStrings_thenSelectWithWhereClauseIsPrepared() {
        QueryBuilder queryBuilder = new QueryBuilder();
        ArrayList<String> columnNames = new ArrayList<>();
        columnNames.add("col1");
        columnNames.add("col2");
        String tableName = "table1";
        int numberOfConditions = 2;
        String expectedQuery = "SELECT col1, col2 FROM table1 WHERE ?=? AND ?=?";

        assertEquals(expectedQuery, queryBuilder.buildSelectQuery(columnNames, tableName, numberOfConditions).toString());
    }
}
