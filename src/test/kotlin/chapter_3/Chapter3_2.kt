package chapter_3

import AbstractPostgreSqlTest
import chapter_2.CityTable
import chapter_2.CityTable.location
import chapter_2.CityTable.name
import chapter_2.Point
import chapter_2.WeatherTable
import chapter_2.insertDummyData
import chapter_2.point
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.core.minus
import org.jetbrains.exposed.v1.javatime.date
import kotlin.test.Test
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import withTransaction
import java.time.LocalDate

// https://www.postgresql.org/docs/current/tutorial-views.html
class Chapter2_9 : AbstractPostgreSqlTest() {

    @Test
    fun Deletions() = withTransaction {
        SchemaUtils.create(WeatherTable, CityTable)
        exec(
            """
        CREATE VIEW myview AS
            SELECT name, temp_lo, temp_hi, prcp, date, location
                FROM weather, cities
                WHERE city = name;
        """.trimIndent()
        )

        WeatherTable.insertDummyData()
        CityTable.insert {
            it[name] = "San Francisco"
            it[location] = Point(-194f, 53f)
        }

        Myview.selectAll()
            .forEach {
                it.printRow()
            }
    }
}

private fun ResultRow.printRow() {
    println("${this[Myview.name]}, ${this[Myview.date]}, ${this[Myview.tempLo]} to ${this[Myview.tempHi]}, ${this[Myview.location]}")
}
object Myview : Table("myview") {
    val tempLo = integer("temp_lo")
    val tempHi = integer("temp_hi")
    val prcp = float("prcp")
    val date = date("date")
    val name = varchar("name", 80)
    val location = point("location")
}
