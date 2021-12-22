package com.example.nmbcompose.bean

class Article : ArrayList<ArticleItem>()

data class ArticleItem(
    val admin: String,
    val content: String,
    val email: String,
    val ext: String,
    val id: String,
    val img: String,
    val name: String,
    val now: String,
    val replyCount: String,
    val replys: List<Reply>,
    val title: String,
    val userid: String
)

data class Reply(
    val admin: String,
    val content: String,
    val email: String,
    val ext: String,
    val id: String,
    val img: String,
    val name: String,
    val now: String?,
    val title: String,
    val userid: String
)