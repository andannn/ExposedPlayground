package chapter_2

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ColumnType
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.date
import org.jetbrains.exposed.v1.jdbc.insert
import java.time.LocalDate

object WeatherTable : Table("weather") {
    val city = varchar("city", 80)
    val tempLo = integer("temp_lo")
    val tempHi = integer("temp_hi")
    val prcp = float("prcp")
    val date = date("date")


}
fun WeatherTable.insertDummyData()  {
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
}

object CityTable : Table("cities") {
    val name = varchar("name", 80)
    val location = point("location")
}

data class Point(val x: Float, val y: Float)

fun Table.point(name: String): Column<Point> =
    registerColumn(name, PostgresPointColumnType())

class PostgresPointColumnType : ColumnType<Point>() {
    override fun sqlType(): String = "point"

    override fun valueFromDB(value: Any): Point {
        val str = value.toString()
        return try {
            val content = str.trim('(', ')') // 去掉首尾的括号 -> "10.5, 20.5"
            val parts = content.split(",")   // 分割 -> ["10.5", " 20.5"]
            Point(parts[0].trim().toFloat(), parts[1].trim().toFloat())
        } catch (e: Exception) {
            error("无法解析 Point 类型: $str")
        }
    }

    override fun notNullValueToDB(value: Point): Any {
        // 返回 PostgreSQL 能识别的字符串格式
        return "(${value.x},${value.y})"
    }

    override fun nonNullValueToString(value: Point): String {
        return "'(${value.x},${value.y})'"
    }
}