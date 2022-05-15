package com.example.mumulcom

import android.content.Context
import androidx.appcompat.app.AppCompatActivity

fun saveJwt(context: Context, jwt: String) {
    val spf = context.getSharedPreferences("profile", AppCompatActivity.MODE_PRIVATE)
    val editor = spf.edit()

    editor.putString("jwt", jwt)
    editor.apply()
}

fun getJwt(context: Context): String {
    val spf = context.getSharedPreferences("profile", AppCompatActivity.MODE_PRIVATE)

    return spf.getString("jwt", "")!!
}

fun saveUserIdx(context: Context, userIdx: Long) {
    val spf = context.getSharedPreferences("profile", AppCompatActivity.MODE_PRIVATE)
    val editor = spf.edit()

    editor.putLong("userIdx", userIdx)
    editor.apply()
}

fun getUserIdx(context: Context): Long {
    val spf = context.getSharedPreferences("profile", AppCompatActivity.MODE_PRIVATE)

    return spf.getLong("userIdx", 0)
}

fun saveEmail(context: Context, email: String) {
    val spf = context.getSharedPreferences("profile", AppCompatActivity.MODE_PRIVATE)
    val editor = spf.edit()

    editor.putString("email", email)
    editor.apply()
}

fun getEmail(context: Context): String {
    val spf = context.getSharedPreferences("profile", AppCompatActivity.MODE_PRIVATE)

    return spf.getString("email", "")!!
}

fun saveName(context: Context, name: String) {
    val spf = context.getSharedPreferences("profile", AppCompatActivity.MODE_PRIVATE)
    val editor = spf.edit()

    editor.putString("name", name)
    editor.apply()
}

fun getName(context: Context): String {
    val spf = context.getSharedPreferences("profile", AppCompatActivity.MODE_PRIVATE)

    return spf.getString("name", "")!!
}

fun saveNickname(context: Context, nickname: String) {
    val spf = context.getSharedPreferences("profile", AppCompatActivity.MODE_PRIVATE)
    val editor = spf.edit()

    editor.putString("nickname", nickname)
    editor.apply()
}

fun getNickname(context: Context): String {
    val spf = context.getSharedPreferences("profile", AppCompatActivity.MODE_PRIVATE)

    return spf.getString("nickname", "")!!
}

fun saveGroup(context: Context, group: String) {
    val spf = context.getSharedPreferences("profile", AppCompatActivity.MODE_PRIVATE)
    val editor = spf.edit()

    editor.putString("group", group)
    editor.apply()
}

fun getGroup(context: Context): String {
    val spf = context.getSharedPreferences("profile", AppCompatActivity.MODE_PRIVATE)

    return spf.getString("group", "")!!
}

fun saveCategories(context: Context, categories: MutableList<String>) {
    val spf = context.getSharedPreferences("profile", AppCompatActivity.MODE_PRIVATE)
    val editor = spf.edit()

    editor.putStringSet("categories", categories.toSet())
    editor.apply()
}

fun getCategories(context: Context): MutableList<String> {
    val spf = context.getSharedPreferences("profile", AppCompatActivity.MODE_PRIVATE)

    return spf.getStringSet("categories", setOf<String>())?.toMutableList()!!
}

fun saveProfileImgUrl(context: Context, profileImgUrl: String) {
    val spf = context.getSharedPreferences("profile", AppCompatActivity.MODE_PRIVATE)
    val editor = spf.edit()

    editor.putString("profileImgUrl", profileImgUrl)
    editor.apply()
}

fun getProfileImgUrl(context: Context): String {
    val spf = context.getSharedPreferences("profile", AppCompatActivity.MODE_PRIVATE)

    return spf.getString("profileImgUrl", "")!!
}

fun saveAnnounceIdx(context: Context, announceIdx: Long) {
    val spf = context.getSharedPreferences("announce", AppCompatActivity.MODE_PRIVATE)
    val editor = spf.edit()

    editor.putLong("announceIdx", announceIdx)
    editor.apply()
}

fun getAnnounceIdx(context: Context): Long {
    val spf = context.getSharedPreferences("announce", AppCompatActivity.MODE_PRIVATE)

    return spf.getLong("announceIdx", 0)
}