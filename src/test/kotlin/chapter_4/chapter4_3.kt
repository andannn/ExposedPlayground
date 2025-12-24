package chapter_4

import AbstractPostgreSqlTest
import WeatherTable
import insertDummyData
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.lowerCase
import org.jetbrains.exposed.v1.core.substring
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.select
import withTransaction
import kotlin.test.Test
import kotlin.test.assertEquals

class chapter4_3 : AbstractPostgreSqlTest() {

    @Test
    fun `exposed buildIn function`() = withTransaction {
        SchemaUtils.create(WeatherTable)

        WeatherTable.insertDummyData()

        val cityLowerCase = WeatherTable.city.lowerCase()
        WeatherTable.select(cityLowerCase)
            .where { WeatherTable.city eq "San Francisco" }
            .forEach {
                assertEquals("san francisco", it[cityLowerCase])
            }

        val citySubstring = WeatherTable.city.substring(start = 1, length = 3)
        WeatherTable.select(citySubstring)
            .where { WeatherTable.city eq "San Francisco" }
            .forEach {
                assertEquals("San", it[citySubstring])
            }
    }
}