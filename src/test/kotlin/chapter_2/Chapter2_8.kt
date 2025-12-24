package chapter_2

import AbstractPostgreSqlTest
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.core.minus
import kotlin.test.Test
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import withTransaction
import java.time.LocalDate

// https://www.postgresql.org/docs/current/tutorial-update.html
class Chapter2_8 : AbstractPostgreSqlTest() {

    @Test
    fun Updates() = withTransaction {
        SchemaUtils.create(WeatherTable)
        WeatherTable.insertDummyData()

        WeatherTable.selectAll().forEach { it.printRow() }
        WeatherTable.update({ WeatherTable.date greater LocalDate.of(1994, 11, 28) }) {
            it[tempHi] = tempHi - 2

            it[tempLo] = tempLo - 2
        }
        WeatherTable.selectAll().forEach { it.printRow() }
    }
}
private fun ResultRow.printRow() {
    println("city: ${this[WeatherTable.city]}, tempLo: ${this[WeatherTable.tempLo]}, tempHi: ${this[WeatherTable.tempHi]}, prcp: ${this[WeatherTable.prcp]}, date: ${this[WeatherTable.date]}")
}
