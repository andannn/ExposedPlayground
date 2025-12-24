package chapter_2

import AbstractPostgreSqlTest
import org.jetbrains.exposed.v1.core.Expression
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.count
import org.jetbrains.exposed.v1.core.div
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.less
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.core.max
import org.jetbrains.exposed.v1.core.plus
import kotlin.test.Test
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import withTransaction
import java.time.LocalDate

// https://www.postgresql.org/docs/current/tutorial-agg.html
class Chapter2_7 : AbstractPostgreSqlTest() {

    @Test
    fun `Aggregate Functions`() = withTransaction {
        SchemaUtils.create(WeatherTable)
        WeatherTable.insertDummyData()

        // select max low temp
        val maxTempExpression = WeatherTable.tempLo.max()
        WeatherTable.select(maxTempExpression)
            .forEach {
                println(it[maxTempExpression])
            }

        // select max low temp by city
        WeatherTable.select(WeatherTable.city)
            .where {
                WeatherTable.tempLo inList WeatherTable.select(maxTempExpression)
                    .mapNotNull { it[maxTempExpression] }
            }
            .forEach { row ->
                println("Hottest City: ${row[WeatherTable.city]}")
            }

        val countExpr = WeatherTable.city.count()
        WeatherTable.select(
            WeatherTable.city,
            countExpr,
            maxTempExpression
        )
            .groupBy(WeatherTable.city)
            .forEach { row ->
                val city = row[WeatherTable.city]
                val count = row[countExpr]
                val max = row[maxTempExpression]

                println("City: $city, Entries: $count, Max Low: $max")
            }

        // filter result of aggregate functions
        WeatherTable.select(
            WeatherTable.city,
            countExpr,
            maxTempExpression
        )
            .groupBy(WeatherTable.city)
            .having {
                maxTempExpression.less(40)
            }
            .forEach { row ->
                val city = row[WeatherTable.city]
                val count = row[countExpr]
                val max = row[maxTempExpression]

                println("City: $city, Entries: $count, Max Low: $max")
            }

        // Like match
        WeatherTable.select(
            WeatherTable.city,
            countExpr,
            maxTempExpression
        )
            .where { WeatherTable.city like "S%" }
            .groupBy(WeatherTable.city)
            .forEach { row ->
                val city = row[WeatherTable.city]
                val count = row[countExpr]
                val max = row[maxTempExpression]

                println("City: $city, Entries: $count, Max Low: $max")
            }
    }
}
