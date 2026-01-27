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

// https://www.postgresql.org/docs/18/queries-table-expressions.html#QUERIES-LATERAL
class chapter_7_2 : AbstractPostgreSqlTest() {
    @Test
    fun `Joins Between Tables`() =
        withTransaction {
            val t1 =
                object : Table("t1") {
                    val id = integer("id")
                    val name = varchar("name", 80)
                }

            val t2 =
                object : Table("t2") {
                    val id = integer("id")
                    val name2 = varchar("name", 80)
                }

            SchemaUtils.create(t1, t2)
            t1.insert {
                it[id] = 1
                it[name] = "1"
            }
            t1.insert {
                it[id] = 2
                it[name] = "2"
            }
            t2.insert {
                it[id] = 1
                it[name2] = "1"
            }
            // inner join
            t1
                .join(
                    t2,
                    JoinType.INNER,
                    additionalConstraint = { t1.name eq t2.name2 },
                ).selectAll()
                .count()
                .also { assertEquals(1, it) }

            // left join
            t1
                .join(
                    t2,
                    JoinType.LEFT,
                    additionalConstraint = { t1.name eq t2.name2 },
                ).selectAll()
                .any {
                    it.getOrNull(t2.name2) == null
                }.also {
                    assertTrue(it)
                }
        }

    @Test
    fun `The WHERE Clause`() =
        withTransaction {
            val t1 =
                object : Table("t1") {
                    val id = integer("id")
                    val name = varchar("name", 80)
                }
            val t2 =
                object : Table("t2") {
                    val id = integer("id")
                    val name2 = varchar("name", 80)
                }

            SchemaUtils.create(t1, t2)
            t1.insert {
                it[id] = 1
                it[name] = "1"
            }
            t2.insert {
                it[id] = 1
                it[name2] = "1"
            }

            // SQL: SELECT COUNT(*) FROM t1 WHERE t1.id > 5
            t1
                .selectAll()
                .where {
                    t1.id greater 5
                }.count()
                .also {
                    assertEquals(0, it)
                }

            // SQL: SELECT COUNT(*) FROM t1 WHERE t1.id IN (1, 2, 3)
            t1
                .selectAll()
                .where {
                    t1.id inList listOf(1, 2, 3)
                }.count()
                .also {
                    assertEquals(1, it)
                }

            // SQL: SELECT COUNT(*) FROM t1 WHERE t1.id IN (SELECT t2.id FROM t2)
            val subQuery = t2.select(t2.id)
            t1
                .selectAll()
                .where {
                    t1.id inSubQuery subQuery
                }.count()
                .also {
                    assertEquals(1, it)
                }

            // SQL: SELECT COUNT(*) FROM t1 WHERE t1.id IN (SELECT t2.id FROM t2 WHERE t2."name" = '1')
            val subQuery2 = t2.select(t2.id).where { t2.name2 eq "1" }
            t1
                .selectAll()
                .where {
                    t1.id inSubQuery subQuery2
                }.count()
                .also {
                    assertEquals(1, it)
                }

            // SQL: SELECT COUNT(*) FROM t1 WHERE EXISTS (SELECT t2.id FROM t2 WHERE t2."name" = '1')
            val subQuery3 = t2.select(t2.id).where { t2.name2 eq "1" }
            t1
                .selectAll()
                .where {
                    exists(subQuery3)
                }.count()
                .also {
                    assertEquals(1, it)
                }
        }

    @Test
    fun `The GROUP BY and HAVING Clauses`() =
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

            // if a table is grouped, columns that are not listed in GROUP BY cannot be referenced except in aggregate expressions.
            assertFails(
                "Column \"t1.name\" must be in the GROUP BY list; SQL statement:\n" +
                    "SELECT t1.id, t1.\"name\" FROM t1 GROUP BY t1.id [90016-224]",
            ) {
                t1
                    .selectAll()
                    .groupBy(t1.id)
                    .onEach {}
            }

            val sumID = t1.id.sum()
            t1
                .select(t1.id, sumID)
                .groupBy(t1.id)
                .forEach {
                    assertEquals(1, it[t1.id])
                    assertEquals(2, it[sumID])
                }

            t1
                .select(t1.id)
                .groupBy(t1.id)
                .having {
                    t1.id eq 1
                }.onEach {
                    assertEquals(1, it[t1.id])
                }
        }
}
