package chapter_5

import AbstractPostgreSqlTest
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.CurrentTimestamp
import org.jetbrains.exposed.v1.javatime.datetime
import org.jetbrains.exposed.v1.javatime.timestamp
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import withTransaction
import kotlin.test.Test
import kotlin.test.assertEquals

// https://www.postgresql.org/docs/18/ddl-default.html
class chapter5_2 : AbstractPostgreSqlTest() {

    @Test
    fun `default value`() = withTransaction {
        SchemaUtils.create(TableWithDefault)

        TableWithDefault.insert {
        }
        assertEquals(1, TableWithDefault.selectAll().count())
        assertEquals(
            "defaultValue",
            TableWithDefault.select(TableWithDefault.city).first().get(TableWithDefault.city)
        )
    }

    @Test
    fun `create default value with expression using sql`() = withTransaction {
        exec(
            """
            CREATE TABLE IF NOT EXISTS weather1 (created_at timestamp DEFAULT CURRENT_TIMESTAMP);
            INSERT INTO weather1  DEFAULT VALUES;
        """.trimIndent()
        )

        exec(
            """
            SELECT * FROM weather1;
        """.trimIndent()
        ) {
            while (it.next()) {
                it.getTimestamp(it.findColumn("created_at")).also { println("JQN $it") }
            }
        }
    }

    @Test
    fun `create default value with expression using exposed`() = withTransaction {
        SchemaUtils.create(TableWithDefault2)

        TableWithDefault2.insert {
        }
        assertEquals(1, TableWithDefault2.selectAll().count())
        TableWithDefault2.select(TableWithDefault2.createAt).first().get(TableWithDefault2.createAt).also {
            println("JQN $it")
        }
    }

}

private object TableWithDefault : Table("weather") {
    val city = varchar("city", 80).default("defaultValue")
}

private object TableWithDefault2 : Table("weather") {
    val createAt =
        timestamp("created_at").defaultExpression(CurrentTimestamp)
}
