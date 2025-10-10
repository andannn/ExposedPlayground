import org.example.MAX_VARCHAR_LENGTH
import org.jetbrains.exposed.v1.core.StdOutSqlLogger
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.stringLiteral
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.test.BeforeTest
import kotlin.test.Test

class DdlTest {

    @BeforeTest
    fun setUp() {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")
    }

    @Test
    fun `create table`() {
        val tasks = object : Table("tasks") {
            val id = integer("id").autoIncrement()
            val title = varchar("name", MAX_VARCHAR_LENGTH)
            val description = varchar("description", MAX_VARCHAR_LENGTH).nullable()
            val isCompleted = bool("completed").default(false)
            val withComment = varchar("withComment", length = MAX_VARCHAR_LENGTH).nullable()
                .withDefinition("COMMENT", stringLiteral("Test Comment"))
            val invisible = varchar("invisible", length = MAX_VARCHAR_LENGTH).nullable().withDefinition("INVISIBLE")
        }

        withTransaction {
            /**
             * default() -> DEFAULT FALSE
             * nullable() -> NOT NULL
             * withDefinition("INVISIBLE") -> INVISIBLE
             */
            SchemaUtils.create(tasks)
        }
    }

    @Test
    fun `create table with auth commit`() {
        withTransaction {
            SchemaUtils.createDatabase("newDb")
        }
    }
}

fun <T> withTransaction(
    block : JdbcTransaction.() -> T
) {
    transaction {
        addLogger(StdOutSqlLogger)
        block()
    }
}