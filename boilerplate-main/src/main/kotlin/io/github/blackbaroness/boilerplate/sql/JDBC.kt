package io.github.blackbaroness.boilerplate.sql

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import javax.sql.DataSource

inline fun <T> DataSource.connect(action: ConnectionWrapper.() -> T): T =
    connection.use { ConnectionWrapper(it).run(action) }

@JvmInline
value class ConnectionWrapper(val connection: Connection) {

    inline fun query(sql: String, configurator: PreparedStatement.() -> Unit = {}): ResultSetWrapper {
        val statement = connection.prepareStatement(
            sql,
            ResultSet.TYPE_FORWARD_ONLY,
            ResultSet.CONCUR_READ_ONLY
        ).apply(configurator)

        return ResultSetWrapper(statement, statement.executeQuery())
    }

    inline fun forEachPaged(table: String, idColumn: String, pageSize: Long = 1_000, action: (ResultSet) -> Unit) {
        var lastId: Any? = null

        while (true) {
            val query = if (lastId == null) {
                "SELECT * FROM $table ORDER BY $idColumn ASC LIMIT $pageSize;"
            } else {
                "SELECT * FROM $table WHERE $idColumn > ? ORDER BY $idColumn ASC LIMIT $pageSize;"
            }

            var gotAnyRows = false
            query(query) {
                fetchSize = 1_000
                if (lastId != null) {
                    setObject(1, lastId)
                }
            }.forEach { rs ->
                action.invoke(rs)
                lastId = rs.getObject(idColumn)
                gotAnyRows = true
            }

            if (!gotAnyRows) {
                break
            }
        }
    }

    class ResultSetWrapper(
        private val statement: PreparedStatement,
        val resultSet: ResultSet,
    ) : AutoCloseable {

        inline fun <T> first(map: (ResultSet) -> T): T {
            return firstOrNull(map) ?: error("Expected at least one row, but none returned")
        }

        inline fun <T> firstOrNull(map: (ResultSet) -> T): T? {
            use { _ ->
                return if (resultSet.next()) map(resultSet) else null
            }
        }

        inline fun <T> map(mapper: (ResultSet) -> T): List<T> {
            use { _ ->
                return buildList {
                    while (resultSet.next()) add(mapper(resultSet))
                }
            }
        }

        inline fun forEach(consumer: (ResultSet) -> Unit) {
            use { _ ->
                while (resultSet.next()) consumer(resultSet)
            }
        }

        override fun close() {
            resultSet.close()
            statement.close()
        }
    }
}
