package org.number_factorizor;

import java.util.ArrayList;

public class QueryBuilder {
    private StringBuilder query;

    public QueryBuilder() {
        this.query = new StringBuilder();
    }

    public StringBuilder buildSelectQuery(ArrayList<String> columnNames, String tableName) {
        return buildSelectQuery(columnNames, tableName, 0);
    }

    public StringBuilder buildSelectQuery(ArrayList<String> columnNames, String tableName, int numberOfConditions) {

        query = new StringBuilder();
        query.append("SELECT ");
        addColumnNamesToQuery(columnNames);
        addTableNameToQuery(tableName);
        addWhereConditionToQuery(numberOfConditions);
        return query;
    }

    private void addColumnNamesToQuery(ArrayList<String> columnNames) {
        int counter = 1;
        for (String cn : columnNames) {
            if (counter > 1) {
                query.append(", ");
            }
            query.append(cn);
            counter += 1;
        }
    }

    private void addTableNameToQuery(String tableName) {
        query.append(" FROM ");
        query.append(tableName);
    }

    private void addWhereConditionToQuery(int numberOfConditions) {
        if (numberOfConditions > 0) {
            query.append(" WHERE ");
            for (int i = 0; i < numberOfConditions; i++) {
                if (i > 0) {
                    query.append(" AND ");
                }
                query.append("?=?");
            }
        }
    }
}
