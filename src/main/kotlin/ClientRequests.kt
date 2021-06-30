import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import com.google.gson.Gson
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.http.*
import model.Feeds
import model.SubRedditModel

class ClientRequests {
companion object{
    var client: HttpClient = HttpClient(io.ktor.client.engine.cio.CIO)
    private const val intiUrl = "https://www.reddit.com/r/"
    fun urlMaker(sub:String): String {
        val url = "$intiUrl$sub/top.json?limit=100"
        print(url)
        return url
    }
    fun urlMaker(after:String,sub: String): String {
      val  url = "$intiUrl$sub/top.json?limit=100&after=$after"
        print(url)
        return url
    }
    val subs = arrayOf("HolUp","cursedcomments","memes","Memes_Of_The_Dank","dankmemes","ComedyCemetery")
}
    suspend fun getSubData(lastPage:Int): Feeds {
      //  val subreddit = Gson().fromJson<SubRedditModel>()
        val feed = Feeds().also {
            it.pageNo = lastPage+1
        }
            for ((i, s) in subs.withIndex())
            {
                feed.children.add(Gson().fromJson( client.get<HttpResponse>(urlMaker(s)){
                    method = HttpMethod.Get
                }.receive<String>(),SubRedditModel::class.java).data.also { it.sub = s })
            }
        return feed
    }


}