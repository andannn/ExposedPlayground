package chapter_5

import AbstractPostgreSqlTest
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import withTransaction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

// https://www.postgresql.org/docs/18/ddl-generated-columns.html
class chapter5_4 : AbstractPostgreSqlTest() {

    @Test
    fun `Generated Columns Sql`() = withTransaction {
        exec(
            """
            CREATE TABLE people (
                height_cm real,
                height_in real GENERATED ALWAYS AS (height_cm / 2.54)
            );
            INSERT INTO people (height_cm) VALUES (123)
        """.trimIndent()
        )
        exec(
            """
            SELECT * FROM people;
        """.trimIndent()
        ) {
            while (it.next()) {
                it.getString(it.findColumn("height_in")).also { println("JQN $it") }
            }
        }
    }

    @Test
    fun `insert Generated Columns Sql will be failed`() = withTransaction {
        assertFails {
            exec(
                """
            CREATE TABLE people (
                height_cm real,
                height_in real GENERATED ALWAYS AS (height_cm / 2.54)
            );
            INSERT INTO people (height_cm, height_in) VALUES (123, 23)
        """.trimIndent()
            )
        }
    }

    @Test
    fun `Generated Columns exposed`() = withTransaction {
        SchemaUtils.create(PeopleTable)
        PeopleTable.insert { it[heightCm] = 123f }
        assertEquals(48.425198f, PeopleTable.select(PeopleTable.heightIn).first()[PeopleTable.heightIn])
    }

    @Test
    fun `Generated Columns exposed failed`() = withTransaction {
        SchemaUtils.create(PeopleTable)
        assertFails {
            PeopleTable.insert {
                it[heightCm] = 123f
                it[heightIn] = 23f
            }
        }
    }

}

private object PeopleTable : Table("people") {
    val heightCm =  float("height_cm")
    val heightIn = float("height_in")
        .databaseGenerated()
        .withDefinition("GENERATED ALWAYS AS (height_cm / 2.54)")

}