package com.example.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class SupabaseClient(
  private var supabaseUrl: String = "",
  private var supabaseAnonKey: String = ""
) {

  private val client: OkHttpClient = OkHttpClient.Builder()
    .connectTimeout(15, TimeUnit.SECONDS)
    .readTimeout(15, TimeUnit.SECONDS)
    .build()

  private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

  fun updateCredentials(url: String, key: String) {
    this.supabaseUrl = url.trim().removeSuffix("/")
    this.supabaseAnonKey = key.trim()
  }

  fun isConfigured(): Boolean {
    return supabaseUrl.isNotBlank() &&
      supabaseUrl.startsWith("https://") &&
      supabaseAnonKey.isNotBlank()
  }

  suspend fun signUp(email: String, password: String): Result<String> = withContext(Dispatchers.IO) {
    if (!isConfigured()) {
      return@withContext Result.failure(IllegalStateException("Supabase credentials not configured in settings or .env"))
    }
    try {
      val payload = JSONObject().apply {
        put("email", email)
        put("password", password)
      }
      val request = Request.Builder()
        .url("$supabaseUrl/auth/v1/signup")
        .addHeader("apikey", supabaseAnonKey)
        .addHeader("Content-Type", "application/json")
        .post(payload.toString().toRequestBody(jsonMediaType))
        .build()

      val response = client.newCall(request).execute()
      val body = response.body?.string() ?: ""
      if (response.isSuccessful) {
        val json = JSONObject(body)
        val user = json.optJSONObject("user")
        val id = user?.optString("id") ?: json.optString("id")
        Result.success(id)
      } else {
        Result.failure(Exception("Sign up failed: ${response.code} $body"))
      }
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  suspend fun signIn(email: String, password: String): Result<String> = withContext(Dispatchers.IO) {
    if (!isConfigured()) {
      return@withContext Result.failure(IllegalStateException("Supabase credentials not configured in settings or .env"))
    }
    try {
      val payload = JSONObject().apply {
        put("email", email)
        put("password", password)
      }
      val request = Request.Builder()
        .url("$supabaseUrl/auth/v1/token?grant_type=password")
        .addHeader("apikey", supabaseAnonKey)
        .addHeader("Content-Type", "application/json")
        .post(payload.toString().toRequestBody(jsonMediaType))
        .build()

      val response = client.newCall(request).execute()
      val body = response.body?.string() ?: ""
      if (response.isSuccessful) {
        val json = JSONObject(body)
        val token = json.optString("access_token")
        Result.success(token)
      } else {
        Result.failure(Exception("Sign in failed: ${response.code} $body"))
      }
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  suspend fun upsertRecord(tableName: String, jsonString: String): Result<Unit> = withContext(Dispatchers.IO) {
    if (!isConfigured()) {
      return@withContext Result.failure(IllegalStateException("Supabase not configured"))
    }
    try {
      val request = Request.Builder()
        .url("$supabaseUrl/rest/v1/$tableName")
        .addHeader("apikey", supabaseAnonKey)
        .addHeader("Authorization", "Bearer $supabaseAnonKey")
        .addHeader("Prefer", "resolution=merge-duplicates")
        .addHeader("Content-Type", "application/json")
        .post(jsonString.toRequestBody(jsonMediaType))
        .build()

      val response = client.newCall(request).execute()
      if (response.isSuccessful || response.code == 201 || response.code == 204) {
        Result.success(Unit)
      } else {
        Result.failure(Exception("HTTP ${response.code}: ${response.body?.string()}"))
      }
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  suspend fun deleteRecord(tableName: String, id: String): Result<Unit> = withContext(Dispatchers.IO) {
    if (!isConfigured()) {
      return@withContext Result.failure(IllegalStateException("Supabase not configured"))
    }
    try {
      val request = Request.Builder()
        .url("$supabaseUrl/rest/v1/$tableName?id=eq.$id")
        .addHeader("apikey", supabaseAnonKey)
        .addHeader("Authorization", "Bearer $supabaseAnonKey")
        .delete()
        .build()

      val response = client.newCall(request).execute()
      if (response.isSuccessful || response.code == 204) {
        Result.success(Unit)
      } else {
        Result.failure(Exception("Delete failed: ${response.code}"))
      }
    } catch (e: Exception) {
      Result.failure(e)
    }
  }
}
