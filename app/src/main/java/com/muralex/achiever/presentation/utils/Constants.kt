package com.muralex.achiever.presentation.utils

import com.muralex.achiever.R

object Constants {

    const val API_KEY = ""
    const val BASE_URL = "https://pixabay.com"
    const val CONTACT_EMAIL = "study.languages.online@gmail.com"

    const val DATABASE_NAME = "app_database"
    const val ITEM_ID_KEY = "item"

    const val ITEM_NEW_ID = "new"
    const val ITEM_NEW_LIST_ID = "new"

    const val CHANNEL_ID = "com.example.tasker.channel"
    const val WORK_TAG = "tag"

    const val IMAGES_FOLDER_ICONS = "file:///android_asset/"

    val SAMPLE_IMAGE = IMAGES_FOLDER_ICONS + "icons/todo-list.png"

    const val DATA_FILENAME = "file_name"

    const val GROUP_TITLE_LIMIT = 60
    const val GROUP_DESC_LIMIT = 80
    const val ITEM_TITLE_LIMIT = 80
    const val ITEM_DESC_LIMIT = 100
    const val ITEM_TEXT_LIMIT = 200

    const val GROUP_LIST_START_COUNT = 25
    const val GROUP_LIST_LEFT_BEFORE_ADD = 8
    const val GROUP_LIST_SCROLL_ADD = 15

    const val HOME_LIST_INIT_DELAY: Long = 800

    const val TASK_LIST_START_COUNT = 25
    const val TASK_LIST_LEFT_BEFORE_ADD = 8
    const val TASK_LIST_SCROLL_ADD = 15

    const val GROUP_DETAIL_ITEM_ID = "group_detail"

    const val ITEM_STATUS_MODE_MANUAL = "manual"
    const val ITEM_STATUS_MODE_AUTO = "auto"
    const val ITEM_STATUS_MODE_AUTO_NEW = "auto_new"


    val ITEM_STATUS_MODES = listOf(ITEM_STATUS_MODE_MANUAL, ITEM_STATUS_MODE_AUTO, ITEM_STATUS_MODE_AUTO_NEW)

    const val COMPLETION_TYPE_NO_DATE = "no_date"
    const val COMPLETION_TYPE_DUE_DATE = "due_date"
    const val COMPLETION_TYPE_REPEATED = "repeated"
    const val COMPLETION_TYPE_REPEAT_PERIOD = "repeated_period"
    const val COMPLETION_TYPE_REPEATED_AFTER = "repeat_after"


    val ITEM_COMPLETION_TYPE = listOf(
        COMPLETION_TYPE_NO_DATE,
        COMPLETION_TYPE_DUE_DATE,
        COMPLETION_TYPE_REPEATED,
        COMPLETION_TYPE_REPEAT_PERIOD,
        COMPLETION_TYPE_REPEATED_AFTER
    )


    const val REPEAT_PERIOD_DEFAULT = "1_day"
    val REPEAT_PERIODS = listOf("day", "week", "month", "year")

    enum class Action {
        Click,
        LongClick,
        Edit,
        Delete,
        Archive,
        Unarchive,
        MoveToTop,
        EditStatus,
        TodoStatus,
        Pin,
        Unpin,
        OpenGroup,
        MenuClick
    }

    val ITEM_STATUS = listOf (
        "none",
        "red",
        "gray",
        "yellow",
        "blue",
        "green"
    )

    val ITEM_STATUS_ICONS = listOf (
        R.drawable.ic_status_gray,
        R.drawable.ic_status_red,
        R.drawable.ic_status_gray,
        R.drawable.ic_status_yellow,
        R.drawable.ic_status_blue,
        R.drawable.ic_status_green
    )

    val ITEM_STATUS_COLORS = listOf (
        R.color.status_gray,
        R.color.status_red,
        R.color.status_gray,
        R.color.status_yellow,
        R.color.status_blue,
        R.color.status_green
    )

    val ITEM_ICONS = listOf (
        "flower.png",
        "flower_blue_l.png",
        "flower_red_l.png",
        "flower_gray_l.png",
        "image_green_l.png",
        "image_blue_l.png",
        "image_red_l.png",
        "image_gray_l.png",
        "pers_green_l.png",
        "pers_blue_l.png",
        "pers_red_l.png",
        "pers_gray_l.png"
    )

}