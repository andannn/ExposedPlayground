package chapter_6

import AbstractPostgreSqlTest
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.jdbc.*
import withTransaction
import kotlin.test.Test
import kotlin.test.assertEquals

// https://www.postgresql.org/docs/18/dml-returning.html
class chapter_6_4 : AbstractPostgreSqlTest() {
// TODO: Use real postgre db to write RETURNING Clause.
    @Test
    fun `Returning Data from Modified Rows`() =
        withTransaction {
            val testTable =
                object : IdTable<Int>("test5") {
                    override val id: Column<EntityID<Int>> = integer("id").entityId()
                }

            SchemaUtils.create(testTable)

            testTable
                .insertAndGetId {
                    it[id] = 1
                }.also {
                    assertEquals(1, it.value)
                }
        }
}
