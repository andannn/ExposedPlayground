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

//  Value Expressions

class chapter4_2 : AbstractPostgreSqlTest() {

    @Test
    fun `Column References`() = withTransaction {
        SchemaUtils.create(WeatherTable)
        WeatherTable.insertDummyData()

        exec("UPDATE weather SET weather.temp_hi = 19 WHERE city = 'San Francisco'")
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