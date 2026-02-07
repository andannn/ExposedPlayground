import org.jetbrains.kotlin.konan.target.Distribution
import java.io.File

object KonanPrebuiltsSetup {
    fun createKonanDistribution(konanHome: File) =
        Distribution(
            konanHome = konanHome.canonicalPath,
            onlyDefaultProfiles = false,
        )
}
