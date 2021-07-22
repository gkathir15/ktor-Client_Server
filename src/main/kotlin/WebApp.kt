/*
 * Copyright 2018 Google LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.gson.Gson
import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.RENDEZVOUS
import model.Children
import model.Data
import model.Feeds
import model.SubRedditModel
import java.util.concurrent.SynchronousQueue

fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(Routing) {
        get("/") {
            call.respond( Gson().toJson(Global.list))
        }
    }

}
class Global  {
    companion object {
        @JvmField
        val list = ArrayList<Feeds>(0)
        fun fetchData(lastPage:Int)
        {
            GlobalScope.launch(Dispatchers.Default) {
                Global.list.add(ClientRequests().getSubData(lastPage))
            }
        }
    }
}



@KtorExperimentalAPI
fun main() {
    Global.fetchData(0)
    val port = System.getenv("PORT")?.toInt() ?: 8080

    embeddedServer(CIO, port, watchPaths = listOf("build"), module = Application::module).start(true)
}
