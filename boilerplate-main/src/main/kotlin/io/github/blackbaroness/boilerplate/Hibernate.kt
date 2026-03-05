package io.github.blackbaroness.boilerplate

import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.TransactionManagementException
import java.util.stream.Stream

inline fun <reified T> Session.fetchAll(): Stream<T> {
    val query = this.criteriaBuilder.createQuery(T::class.java)
    return createQuery(query.select(query.from(T::class.java))).resultStream
}

// A copy of SessionFactory#inTransaction, but with an inline action
inline fun <T> SessionFactory.inTransactionInline(action: (Session) -> T): T {
    return openSession().use { session ->
        val transaction = session.beginTransaction()

        try {
            val result = action.invoke(session)

            if (!transaction.isActive)
                throw TransactionManagementException("Execution of action caused managed transaction to be completed")

            transaction.commit()

            result
        } catch (exception: Exception) {
            // an error happened in the action or during commit()
            if (transaction.isActive) {
                try {
                    transaction.rollback()
                } catch (e: java.lang.RuntimeException) {
                    exception.addSuppressed(e)
                }
            }
            throw exception
        }
    }
}
