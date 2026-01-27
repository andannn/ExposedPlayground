package chapter_7

import AbstractPostgreSqlTest
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.jdbc.*
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import withTransaction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertTrue

// https://www.postgresql.org/docs/18/queries-select-lists.html
class chapter_7_3 : AbstractPostgreSqlTest() {
    @Test
    fun `DISTINCT`() =
        withTransaction {
            val t1 =
                object : Table("t1") {
                    val id = integer("id")
                    val name = varchar("name", 80)
                }

            SchemaUtils.create(t1)

            t1.insert {
                it[id] = 1
                it[name] = "Alice"
            }

            t1.insert {
                it[id] = 1
                it[name] = "Bob"
            }

            t1.insert {
                it[id] = 1
                it[name] = "Alice"
            }

            t1
                .selectAll()
                .withDistinct()
                .count()
                .also { assertEquals(2, it) }
            t1
                .select(t1.id)
                .withDistinct()
                .count()
                .also { assertEquals(1, it) }

            val groupCount = t1.id.count()
            t1
                .select(t1.id, groupCount)
                .withDistinct()
                .first()
                .also {
                    assertEquals(1, it[t1.id])
                    assertEquals(3, it[groupCount])
                }
        }
}
