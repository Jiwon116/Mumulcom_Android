package com.example.mumulcom

class AlarmGeneralItem (
    var questionIdx: Long,
    var noticeContent: String,
    var title: String,
    var bigCategoryName: String,
    var type: Int,
    var noticeCategoryIdx: Int,
): AlarmListItem(TYPE_GENERAL)