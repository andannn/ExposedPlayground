package chapter_2

import AbstractPostgreSqlTest
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import withTransaction
import kotlin.test.Test

// https://www.postgresql.org/docs/current/tutorial-table.html
class Chapter2_3 : AbstractPostgreSqlTest() {

    @Test
    fun `Creating a New Table`() {
        withTransaction {
            SchemaUtils.create(WeatherTable)
        }
    }

    @Test
    fun `PostgreSQL point type`() {
        withTransaction {
            SchemaUtils.create(CityTable)
        }
    }
}
