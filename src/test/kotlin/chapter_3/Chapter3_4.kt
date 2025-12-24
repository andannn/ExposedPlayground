package chapter_3

import AbstractPostgreSqlTest
import withTransaction
import kotlin.test.Test

// https://www.postgresql.org/docs/current/tutorial-transactions.html
class Chapter3_4 : AbstractPostgreSqlTest() {

    @Test
    fun transactions() = withTransaction {

    }
}

