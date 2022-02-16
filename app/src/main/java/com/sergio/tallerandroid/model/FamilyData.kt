package com.sergio.tallerandroid.model

import androidx.annotation.StringRes
import com.sergio.tallerandroid.R

enum class FamilyData(val id: Int, @StringRes val familyName: Int) {
    MAMMAL(0, R.string.family_mammal_text),
    REPTILE(1, R.string.family_reptile_text),
    FISH(2, R.string.family_fish_text),
    BIRD(3, R.string.family_bird_text);

    companion object {
        fun fromId(id: Int): FamilyData = values().first { it.id == id }

        fun getList(): List<Int> {
            return values().map {
                it.familyName
            }
        }
    }
}