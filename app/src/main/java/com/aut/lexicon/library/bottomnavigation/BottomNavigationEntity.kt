package com.aut.lexicon.library.bottomnavigation

/**
 * 状态栏Item实体类
 * default : do not need text and badge
 */
class BottomNavigationEntity constructor(
    selectedIcon: Int,
    unSelectIcon: Int
) {
    var text: String? = null
        private set
    var selectedIcon: Int
        private set
    var unSelectIcon: Int
        private set
    var badgeNum = 0

    init {
        this.selectedIcon = selectedIcon
        this.unSelectIcon = unSelectIcon
    }

    //need text without badge
    constructor(text: String?, selectedIcon: Int, unSelectIcon: Int) : this(
        selectedIcon,
        unSelectIcon
    ) {
        this.text = text
    }

    //need text with badge
    constructor(text: String?, selectedIcon: Int, unSelectIcon: Int, badgeNum: Int) : this(
        text,
        selectedIcon,
        unSelectIcon
    ) {
        this.badgeNum = badgeNum
    }

    //do not need text
    constructor(selectedIcon: Int, unSelectIcon: Int, badgeNum: Int) : this(
        selectedIcon,
        unSelectIcon
    ) {
        this.badgeNum = badgeNum
    }

}