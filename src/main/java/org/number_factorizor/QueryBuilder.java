package org.number_factorizor;

public class QueryBuilder {
    StringBuilder query;
    public StringBuilder buildSelectQuery(int columnsNumber) {
        return buildSelectQuery(columnsNumber, 0);
    }

    public StringBuilder buildSelectQuery(int columnsNumber, int argsNumber) {
        query = new StringBuilder();
        query.append("SELECT ");
        addColumnNamesToQuery(columnsNumber);
        query.append(" FROM ?");
        addWhereConditionToQuery(argsNumber);
        return query;
    }

    private void addColumnNamesToQuery(int columnsNumbers) {
        for (int i = 0; i < columnsNumbers; i++) {
            if (i > 0) {
                query.append(", ");
            }
            query.append("?");
        }
    }

    private void addWhereConditionToQuery(int argsNumber) {
        if (argsNumber > 0) {
            query.append(" WHERE ");
            for (int i = 0; i < argsNumber; i++) {
                if (i > 0) {
                    query.append(" AND ");
                }
                query.append("?=?");
            }
        }
    }
}
