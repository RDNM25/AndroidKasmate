package com.arunk.dashboardlogin

data class UserAccount(
    val username: String,
    val password: String
)
object UserRepository {
    private val users = mutableListOf<UserAccount>()

    fun register(username: String, password: String): Boolean {
        val exists = users.any { it.username.equals(username, ignoreCase = true) }
        if (exists) return false

        users.add(UserAccount(username, password))
        return true
    }

    fun authenticate(username: String, password: String): Boolean {
        return users.any {
            it.username.equals(username, ignoreCase = true) && it.password == password
        }
    }
}