package chapter_2

import AbstractPostgreSqlTest
import kotlin.test.Test
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insert
import withTransaction
import java.time.LocalDate

// https://www.postgresql.org/docs/current/tutorial-populate.html
class Chapter2_4 : AbstractPostgreSqlTest() {

    @Test
    fun `Insert Statement`() {
        withTransaction {
            SchemaUtils.create(WeatherTable)
            WeatherTable.insert {
                it[city] = "San Francisco"
                it[tempLo] = 46
                it[tempHi] = 50
                it[prcp] = 0.25f
                it[date] = LocalDate.of(1994, 11, 27)
            }
        }
    }

    @Test
    fun `Insert postgre point type`() {
        withTransaction {
            SchemaUtils.create(CityTable)
            CityTable.insert {
                it[name] = "San Francisco"
                it[location] = Point(37.7749f, -122.4194f)
            }
        }
    }
}
