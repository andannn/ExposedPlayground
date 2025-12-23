package chapter_2

import AbstractPostgreSqlTest
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.div
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.core.plus
import kotlin.test.Test
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import withTransaction
import java.time.LocalDate

// https://www.postgresql.org/docs/current/tutorial-select.html
class Chapter2_5 : AbstractPostgreSqlTest() {

    @Test
    fun `Querying a Table`() {
        withTransaction {
            SchemaUtils.create(WeatherTable)
            // select all record
            WeatherTable.insert {
                it[city] = "San Francisco"
                it[tempLo] = 46
                it[tempHi] = 50
                it[prcp] = 0.25f
                it[date] = LocalDate.of(1994, 11, 27)
            }
            WeatherTable.insert {
                it[city] = "San Francisco"
                it[tempLo] = 43
                it[tempHi] = 57
                it[prcp] = 0f
                it[date] = LocalDate.of(1994, 11, 29)
            }
            WeatherTable.insert {
                it[city] = "Hayward"
                it[tempLo] = 37
                it[tempHi] = 54
                it[prcp] = 0f
                it[date] = LocalDate.of(1994, 11, 29)
            }

            // select a slice.
            WeatherTable.select(
                WeatherTable.city
            ).forEach { row ->
                val city = row[WeatherTable.city]

                println(city)
            }

            // select calculated row.
            val tempAvg = (WeatherTable.tempHi + WeatherTable.tempLo) / 2
            WeatherTable.select(WeatherTable.city, tempAvg).forEach { row ->
                val city = row[WeatherTable.city]
                val avg = row[tempAvg]

                println("$city - $avg")
            }

            // select with selector
            WeatherTable.selectAll().where {
                WeatherTable.city eq "San Francisco" and (WeatherTable.prcp greater 0f)
            }.forEach { row ->
                row.printRow()
            }

            // select with order
            // In this example, the sort order isn't fully specified,
            // and so you might get the San Francisco rows in either order.
            WeatherTable.selectAll().orderBy(WeatherTable.city).forEach { row ->
                row.printRow()
            }

            // order will not change
            WeatherTable.selectAll().orderBy(
                WeatherTable.city to SortOrder.ASC,
                WeatherTable.tempLo to SortOrder.ASC
            ).forEach { row ->
                row.printRow()
            }

            // Duplicated row is removed.
            WeatherTable.select(WeatherTable.city)
                .withDistinct()
                .forEach {
                    println(it[WeatherTable.city])
                }

            // distinct with order
            WeatherTable.select(WeatherTable.city)
                .withDistinct()
                .orderBy(WeatherTable.city)
                .forEach {
                    println(it[WeatherTable.city])
                }
        }
    }
}


private fun ResultRow.printRow() {
    println("city: ${this[WeatherTable.city]}, tempLo: ${this[WeatherTable.tempLo]}, tempHi: ${this[WeatherTable.tempHi]}, prcp: ${this[WeatherTable.prcp]}, date: ${this[WeatherTable.date]}")
}
