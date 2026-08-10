package com.pjdev.pjdevmultiverseapp.navigation

object AppRoute {

    const val CHARACTER_LIST = "character_list"

    const val CHARACTER_DETAIL = "character_detail"

    const val CHARACTER_ID_ARGUMENT = "characterId"

    const val CHARACTER_DETAIL_ROUTE =
        "$CHARACTER_DETAIL/{$CHARACTER_ID_ARGUMENT}"

    fun characterDetail(
        characterId: Int,
    ): String {
        return "$CHARACTER_DETAIL/$characterId"
    }
}
