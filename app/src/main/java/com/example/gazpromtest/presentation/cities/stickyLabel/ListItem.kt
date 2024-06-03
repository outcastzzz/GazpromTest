package com.example.gazpromtest.presentation.cities.stickyLabel

import com.example.gazpromtest.domain.entity.City

data class ListItem(
    var city: City
): StickyListItem {
    override val initial: Char
        get() = city.city!!.first()
}