package chapter_7

import AbstractPostgreSqlTest
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.jdbc.*
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import withTransaction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFalse
import kotlin.test.assertTrue

// https://www.postgresql.org/docs/18/queries-order.html
class chapter_7_5 : AbstractPostgreSqlTest() {
    @Test
    fun `Sorting Rows (ORDER BY) `() =
        withTransaction {
            val t1 =
                object : Table("t1") {
                    val id = integer("id")
                    val name = varchar("name", 80)
                }
            SchemaUtils.create(t1)

            t1.insert {
                it[id] = 1
                it[name] = "b"
            }

            t1.insert {
                it[id] = 2
                it[name] = "b"
            }

            t1.insert {
                it[id] = 2
                it[name] = "c"
            }

            t1.insert {
                it[id] = 2
                it[name] = "a"
            }

            t1.insert {
                it[id] = 1
                it[name] = "a"
            }

            // When more than one expression is specified, the later values are used to sort rows that are equal according to the earlier values.
            t1
                .selectAll()
                .orderBy(t1.id to SortOrder.ASC, t1.name to SortOrder.ASC)
                .iterator()
                .let {
                    it.next().also {
                        assertEquals(1, it[t1.id])
                        assertEquals("a", it[t1.name])
                    }
                    it.next().also {
                        assertEquals(1, it[t1.id])
                        assertEquals("b", it[t1.name])
                    }
                    it.next().also {
                        assertEquals(2, it[t1.id])
                        assertEquals("a", it[t1.name])
                    }
                    it.next().also {
                        assertEquals(2, it[t1.id])
                        assertEquals("b", it[t1.name])
                    }
                    it.next().also {
                        assertEquals(2, it[t1.id])
                        assertEquals("c", it[t1.name])
                    }
                }
        }
}
