import com.github.kyay10.kotlinnulldefaults.NullDefaults
import org.jetbrains.annotations.Nullable


data class Configuration @JvmOverloads constructor(@Nullable @NullDefaults val data: String = "42")

@NullDefaults
data class MyClass @JvmOverloads constructor(
  // The @Nullable makes sure that Java doesn't see false warnings when passing null to this. At the same time, Kotlin
  // absolutely ignores it and still views the parameters as Not-Null
  @Nullable val name: String = "",
  @Nullable val addresses: List<String> = emptyList(),
  val optionalConfig: Configuration? = null
)

@JvmOverloads
fun makeNetworkCall(
  rootUrl: String = "example.com",
  @Nullable @NullDefaults addressCount: Int? = 2,
  @Nullable @NullDefaults requestUrl: String = createUrlFrom2Parts(rootUrl, addressCount.toString())
): List<String> {
  println(rootUrl)
  println(addressCount)
  println(requestUrl)
  return listOf("Hello", "World")
}

@JvmOverloads
@NullDefaults
fun createUrlFrom2Parts(@Nullable firstPart: String = "foo", @Nullable secondPart: String = "bar") = "$firstPart/$secondPart"

fun main() {
  Main.main(null)
}
