package chapter_3

import AbstractPostgreSqlTest
import WeatherTable
import insertDummyData
import org.jetbrains.exposed.v1.core.RowNumber
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.alias
import org.jetbrains.exposed.v1.core.avg
import org.jetbrains.exposed.v1.core.lessEq
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import withTransaction
import kotlin.test.Test

// https://www.postgresql.org/docs/current/tutorial-window.html
class Chapter3_5 : AbstractPostgreSqlTest() {

    @Test
    fun `Window Functions`() = withTransaction {
        SchemaUtils.create(WeatherTable)
        WeatherTable.insertDummyData()

        // partition by window funciton
        val avgPrcpWindow = WeatherTable.prcp.avg()
            .over()
            .partitionBy(WeatherTable.city)
            .alias("avg_prcp")

        WeatherTable.select(
            WeatherTable.city,
            WeatherTable.tempLo,
            WeatherTable.tempHi,
            WeatherTable.prcp,
            avgPrcpWindow
        ).forEach {
            println("${it[WeatherTable.city]}, ${it[WeatherTable.tempLo]}, ${it[WeatherTable.tempHi]}, ${it[WeatherTable.prcp]}, ${it[avgPrcpWindow]}")
        }

        // partition by with order
        val lowTempRankWindow = RowNumber()
            .over()
            .partitionBy(WeatherTable.city)
            .orderBy(WeatherTable.tempLo to SortOrder.DESC)
            .alias("low_temp_rank")
        WeatherTable.select(
            WeatherTable.city,
            WeatherTable.tempLo,
            WeatherTable.tempHi,
            WeatherTable.prcp,
            lowTempRankWindow
        ).forEach {
            println("${it[WeatherTable.city]}, ${it[WeatherTable.tempLo]}, ${it[WeatherTable.tempHi]}, ${it[WeatherTable.prcp]}, ${it[lowTempRankWindow]}")
        }

        // partition by with order
        val subQuery = WeatherTable.select(
            WeatherTable.city,
            WeatherTable.tempLo,
            WeatherTable.tempHi,
            WeatherTable.prcp,
            lowTempRankWindow
        ).alias("ss")
        subQuery.selectAll()
            .where {
                subQuery[lowTempRankWindow] lessEq  1L
            }
            .forEach {
                println("${it[subQuery[WeatherTable.city]]}, ${it[subQuery[WeatherTable.tempLo]]}, ${it[subQuery[WeatherTable.prcp]]}")
                // TODO: get the lowTempRankWindow
            }
    }
}

