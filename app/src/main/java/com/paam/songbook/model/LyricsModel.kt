import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class Lyrics(
    val id: Int,
    val verses: List<String>,
    val chorus: String
)

