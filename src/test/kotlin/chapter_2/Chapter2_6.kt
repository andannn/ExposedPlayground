package chapter_2

import AbstractPostgreSqlTest
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.div
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.core.plus
import kotlin.test.Test
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import withTransaction
import java.time.LocalDate

// https://www.postgresql.org/docs/current/tutorial-join.html
class Chapter2_6 : AbstractPostgreSqlTest() {

    @Test
    fun `Joins Between Tables`() = withTransaction {

    }
}
