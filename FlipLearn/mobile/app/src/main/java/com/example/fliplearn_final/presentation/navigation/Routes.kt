package com.example.fliplearn_final.presentation.navigation

import com.example.fliplearn_final.R

sealed class Routes(val route: String) {
    data object Start : Routes("start")
    data object SignIn : Routes("sign_in")
    data object SignUp : Routes("sign_up")

    data object Main : Routes("main")

}



sealed class BottomNavItem(val route: String, val icon: Int, val label: String) {
    object Home : BottomNavItem("home", R.drawable.ic_home, "Home")
    object Add : BottomNavItem("add", R.drawable.ic_add, "Add")
    object Profile : BottomNavItem("profile", R.drawable.ic_profile, "Profile")

    companion object {
        val items = listOf(Home, Add, Profile)
    }
}


sealed class CreateNavItem(val route: String) {
    object CreateFolder : CreateNavItem("create_folder")
    object CreateDictionary : CreateNavItem("create_dictionary")
}

sealed class  HideRoutes (val route: String){
    object FolderDetailed: HideRoutes("folder_detail")
    object DictionaryDetailed: HideRoutes("dictionary_detail")
    object Test1: HideRoutes("test1")
    object Test2: HideRoutes("test2")
    object Test3: HideRoutes("test3")

}