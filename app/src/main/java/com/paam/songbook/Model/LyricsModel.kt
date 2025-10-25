import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class Lyrics(
    val id: String,
    val verses: List<String>,
    val chorus: String
)

