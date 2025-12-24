package chapter_4

import AbstractPostgreSqlTest
import WeatherTable
import insertDummyData
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.select
import withTransaction
import kotlin.test.Test
import kotlin.test.assertEquals

class chapter4_1 : AbstractPostgreSqlTest() {

    @Test
    fun `Key words and unquoted identifiers are case-insensitive`() = withTransaction {
        SchemaUtils.create(WeatherTable)
        WeatherTable.insertDummyData()

        exec("UPDATE weather SET temp_hi = 19 WHERE city = 'San Francisco'")
        assertHighTempEq(19)
        exec("uPDaTE WeAtheR SeT temp_hi = 20 WHeRE city = 'San Francisco'")
        assertHighTempEq(20)
    }

    // A delimited identifier is always an identifier, never a key word.
    @Test
    fun `quoted identifier`() = withTransaction {
        SchemaUtils.create(WeatherTable)
        WeatherTable.insertDummyData()

        exec("""
                UPDATE "weather" SET "temp_hi" = 19 WHERE "city" = 'San Francisco'
        """.trimIndent())
        assertHighTempEq(19)
    }

    // Two string constants that are only separated by whitespace with at least one newline are concatenated and effectively treated as if the string had been written as one constant.
    @Test
    fun `String Constants`() = withTransaction {
        SchemaUtils.create(WeatherTable)
        WeatherTable.insertDummyData()

        exec("""
                UPDATE "weather" SET "temp_hi" = 19 WHERE "city" = 'San '
                'Francisco';
        """.trimIndent())
        assertHighTempEq(19)
    }

    private fun assertHighTempEq(value: Int) {
        WeatherTable.select(WeatherTable.tempHi)
            .where { WeatherTable.city eq "San Francisco" }
            .forEach {
                assertEquals(value, it[WeatherTable.tempHi])
            }
    }
}