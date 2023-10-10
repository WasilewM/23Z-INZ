package org.number_factorizor;

import org.common.SqlStatementDataTypes;
import java.util.ArrayList;

public class QueryBuilder {
    public StringBuilder prepareSelectQuery(ArrayList<String> columnNames, String tableName) {
        return prepareSelectQuery(columnNames, tableName, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public StringBuilder prepareSelectQuery(ArrayList<String> columnNames, String tableName, ArrayList<String> keys, ArrayList<SqlStatementDataTypes> valuesTypes, ArrayList<Object> values) {
        if (areGivenArrayListsValid(keys, valuesTypes, values)) {
            throw new IllegalArgumentException("ArrayLists keys, valuesTypes and values have to be of equal size");
        }
        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        addColumnNamesToQuery(columnNames, query);
        addTableNameToQuery(tableName, query);
        addWhereConditionToQuery(keys, values, query);
        return query;
    }

    private static boolean areGivenArrayListsValid(ArrayList<String> keys, ArrayList<SqlStatementDataTypes> valuesTypes, ArrayList<Object> values) {
        return keys.size() != values.size() || keys.size() != valuesTypes.size();
    }

    private static void addColumnNamesToQuery(ArrayList<String> columnNames, StringBuilder query) {
        int counter = 1;
        for (String cn: columnNames) {
            if (counter > 1) {
                query.append(", ");
            }
            query.append(cn);
            counter += 1;
        }
    }

    private static void addTableNameToQuery(String tableName, StringBuilder query) {
        query.append(" FROM ");
        query.append(tableName);
    }

    private void addWhereConditionToQuery(ArrayList<String> keys, ArrayList<Object> values, StringBuilder query) {
        if (values.size() > 0) {
            query.append(" WHERE ");
            int counter = 1;
            for (String k : keys) {
                if (counter > 1) {
                    query.append(" AND ");
                }
                query.append(k);
                query.append("=?");
                counter += 1;
            }
        }
    }
}
