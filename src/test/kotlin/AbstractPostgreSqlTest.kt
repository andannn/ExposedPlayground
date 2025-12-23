import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import kotlin.test.BeforeTest

abstract class AbstractPostgreSqlTest {
    @BeforeTest
    fun setUp() {
        Database.connect(
            "jdbc:h2:mem:test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
            driver = "org.h2.Driver"
        )
        withTransaction {
            exec("CREATE DOMAIN IF NOT EXISTS point AS VARCHAR")
            exec("CREATE DOMAIN IF NOT EXISTS point AS GEOMETRY")
        }
    }
}