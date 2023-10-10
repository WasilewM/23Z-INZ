package number_factorizor_tests;

import org.common.SqlStatementDataTypes;
import org.junit.Test;
import org.number_factorizor.QueryBuilder;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
    public void givenBuildSelectQueryMethod_whenKeysArrayListDoesNotMatchOtherListsSizes_thenIllegalArgumentExceptionIsThrown() {
        QueryBuilder queryBuilder = new QueryBuilder();
        ArrayList<String> columnNames = new ArrayList<>();
        columnNames.add("col1");
        columnNames.add("col2");
        String tableName = "table1";
        ArrayList<String> keys = new ArrayList<>();
        keys.add("a");
        ArrayList<SqlStatementDataTypes> valuesTypes = new ArrayList<>();
        ArrayList<Object> values = new ArrayList<>();

        boolean exceptionCaught = false;
        try {
            queryBuilder.buildSelectQuery(columnNames, tableName, keys, valuesTypes, values);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "ArrayLists keys, valuesTypes and values have to be of equal size");
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);
    }



    @Test
    public void givenBuildSelectQueryMethod_whenValuesTypesArrayListDoesNotMatchOtherListsSizes_thenIllegalArgumentExceptionIsThrown() {
        QueryBuilder queryBuilder = new QueryBuilder();
        ArrayList<String> columnNames = new ArrayList<>();
        columnNames.add("col1");
        columnNames.add("col2");
        String tableName = "table1";
        ArrayList<String> keys = new ArrayList<>();
        keys.add("a");
        ArrayList<SqlStatementDataTypes> valuesTypes = new ArrayList<>();
        ArrayList<Object> values = new ArrayList<>();
        values.add("b");

        boolean exceptionCaught = false;
        try {
            queryBuilder.buildSelectQuery(columnNames, tableName, keys, valuesTypes, values);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "ArrayLists keys, valuesTypes and values have to be of equal size");
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);
    }

    @Test
    public void givenBuildSelectQueryMethod_whenValuesArrayListDoesNotMatchOtherListsSizes_thenIllegalArgumentExceptionIsThrown() {
        QueryBuilder queryBuilder = new QueryBuilder();
        ArrayList<String> columnNames = new ArrayList<>();
        columnNames.add("col1");
        columnNames.add("col2");
        String tableName = "table1";
        ArrayList<String> keys = new ArrayList<>();
        keys.add("a");
        ArrayList<SqlStatementDataTypes> valuesTypes = new ArrayList<>();
        valuesTypes.add(SqlStatementDataTypes.STRING);
        ArrayList<Object> values = new ArrayList<>();

        boolean exceptionCaught = false;
        try {
            queryBuilder.buildSelectQuery(columnNames, tableName, keys, valuesTypes, values);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "ArrayLists keys, valuesTypes and values have to be of equal size");
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);
    }

    @Test
    public void givenBuildSelectQueryMethod_whenCalledWithEqualNumberOfKeysAndValues_thenSelectWithWhereClauseIsPrepared() {
        QueryBuilder queryBuilder = new QueryBuilder();
        ArrayList<String> columnNames = new ArrayList<>();
        columnNames.add("col1");
        columnNames.add("col2");
        String tableName = "table1";
        ArrayList<String> keys = new ArrayList<>();
        keys.add("a");
        ArrayList<SqlStatementDataTypes> valuesTypes = new ArrayList<>();
        valuesTypes.add(SqlStatementDataTypes.STRING);
        ArrayList<Object> values = new ArrayList<>();
        values.add("b");

        String expectedQuery = "SELECT col1, col2 FROM table1 WHERE a=?";

        assertEquals(expectedQuery, queryBuilder.buildSelectQuery(columnNames, tableName, keys, valuesTypes, values).toString());
    }

    @Test
    public void givenBuildSelectQueryMethod_whenCalledWithEqualNumberOfKeysAndValuesAndValuesAreStrings_thenSelectWithWhereClauseIsPrepared() {
        QueryBuilder queryBuilder = new QueryBuilder();
        ArrayList<String> columnNames = new ArrayList<>();
        columnNames.add("col1");
        columnNames.add("col2");
        String tableName = "table1";
        ArrayList<String> keys = new ArrayList<>();
        keys.add("a");
        keys.add("X");
        ArrayList<SqlStatementDataTypes> valuesTypes = new ArrayList<>();
        valuesTypes.add(SqlStatementDataTypes.STRING);
        valuesTypes.add(SqlStatementDataTypes.STRING);
        ArrayList<Object> values = new ArrayList<>();
        values.add("b");
        values.add("1a");

        String expectedQuery = "SELECT col1, col2 FROM table1 WHERE a=? AND X=?";

        assertEquals(expectedQuery, queryBuilder.buildSelectQuery(columnNames, tableName, keys, valuesTypes, values).toString());
    }
}
