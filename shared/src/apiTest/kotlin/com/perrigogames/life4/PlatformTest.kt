package com.perrigogames.life4

import kotlinx.coroutines.runBlocking

actual fun <T> runTest(block: suspend () -> T) { runBlocking { block() } }

class SqlDelightTestJvm : GoalSqlTest()