package com.arunk.dashboardlogin

import android.util.Base64
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

private data class UserAccount(
    val username: String,
    val salt: String,
    val passwordHash: String
)

object UserRepository {
    private const val STORAGE_KEY = "users"
    private const val HASH_ITERATIONS = 120_000
    private const val HASH_BITS = 256
    private val users = mutableListOf<UserAccount>()

    fun initialize() {
        users.clear()
        val savedUsers = LocalDataStore.read(STORAGE_KEY)?.let(::JSONArray) ?: return
        for (index in 0 until savedUsers.length()) {
            val account = savedUsers.optJSONObject(index) ?: continue
            users += UserAccount(
                username = account.optString("username"),
                salt = account.optString("salt"),
                passwordHash = account.optString("passwordHash")
            )
        }
    }

    fun register(username: String, password: String): Boolean {
        if (users.any { it.username.equals(username, ignoreCase = true) }) return false

        val salt = ByteArray(16).also(SecureRandom()::nextBytes)
        val account = UserAccount(
            username = username,
            salt = Base64.encodeToString(salt, Base64.NO_WRAP),
            passwordHash = hashPassword(password, salt)
        )
        users.add(account)
        if (!persist()) {
            users.remove(account)
            return false
        }
        return true
    }

    fun authenticate(username: String, password: String): Boolean {
        val account = users.firstOrNull { it.username.equals(username, ignoreCase = true) } ?: return false
        val salt = Base64.decode(account.salt, Base64.NO_WRAP)
        val expected = Base64.decode(account.passwordHash, Base64.NO_WRAP)
        val actual = Base64.decode(hashPassword(password, salt), Base64.NO_WRAP)
        return MessageDigest.isEqual(expected, actual)
    }

    private fun hashPassword(password: String, salt: ByteArray): String {
        val spec = PBEKeySpec(password.toCharArray(), salt, HASH_ITERATIONS, HASH_BITS)
        return try {
            val hash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
                .generateSecret(spec).encoded
            Base64.encodeToString(hash, Base64.NO_WRAP)
        } finally {
            spec.clearPassword()
        }
    }

    private fun persist(): Boolean {
        val savedUsers = JSONArray()
        users.forEach { account ->
            savedUsers.put(
                JSONObject()
                    .put("username", account.username)
                    .put("salt", account.salt)
                    .put("passwordHash", account.passwordHash)
            )
        }
        return LocalDataStore.write(STORAGE_KEY, savedUsers.toString())
    }
}
