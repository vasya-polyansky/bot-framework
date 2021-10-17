package framework

//val app = TelegramApp(
//    bot = telegramBot("TOKEN???"),
//    stateStore = MyMemoryStateStore(GREETING)
//)
//
//val Greeting = app.state(
//    init = { send("Text me something") },
//    dispose = { send("Bye") }
//) {
//    onText("hello") {
//        send("Hi")
//        setState(MainMenu)
//    }
//}
//
//val MainMenu = app.state {
//    onText("") {
//        send("Not what?")
//    }


interface Stage {
    suspend fun init()

    suspend fun dispose()

    suspend fun handle()
}

