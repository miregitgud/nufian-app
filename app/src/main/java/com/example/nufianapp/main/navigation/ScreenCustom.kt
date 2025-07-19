package com.example.nufianapp.main.navigation

sealed class ScreenCustom(val route: String) {

    data object OnboardingScreenCustom: ScreenCustom("onboarding_screen")
    data object HomeScreenCustom: ScreenCustom("home_screen")
    data object PreAuthScreenCustom: ScreenCustom("pre_auth_screen")
    data object AuthScreenCustom: ScreenCustom("auth_screen")
    data object SetPasswordScreenCustom: ScreenCustom("set_password_screen")
    data object ProfileScreenCustom: ScreenCustom("profile_screen")
    data object ChatScreenCustom: ScreenCustom("chat_screen")
    data object CompleteProfileScreenCustom: ScreenCustom("complete_profile_screen")
    data object AddForumCustom : ScreenCustom("add_forum")
    data object AddCertificateScreenCustom : ScreenCustom("add_certificate")
    data object AddProjectCustom : ScreenCustom("add_project")
    data object DiscoverScreenCustom: ScreenCustom("discover_screen")
    data object SettingsScreenCustom : ScreenCustom("settings")
    data object ChangePasswordCustom : ScreenCustom("change_password")
    data object EditProfileCustom : ScreenCustom("edit_profile")
    data object NotificationCustom : ScreenCustom("notification")
    data object WelcomeScreenCustom : ScreenCustom("welcome_screen")
    data object SplashCustom : ScreenCustom("splash")
    data object NewsCustom : ScreenCustom("news")
    data object AddNews : ScreenCustom("add_news")
    data object BannedScreenCustom : ScreenCustom("banned_screen")

    data object ProfilePreviewCustom : ScreenCustom("profile_preview/{userId}") {
        fun createRoute(userId: String) = "profile_preview/$userId"
    }

    data object DetailForumScreenCustom : ScreenCustom("home/{forumId}/{forumUserPostId}/{isEnabled}")  {
        fun createRoute(forumId: String, forumUserPostId: String, isEnabled: Boolean): String =
            "home/$forumId/$forumUserPostId/$isEnabled"
    }

    data object DetailNewsScreenCustom : ScreenCustom("news/{newsId}") {
        fun createRoute(newsId: String) = "news/$newsId"
    }

    data object DetailProjectScreenCustom : ScreenCustom("project/{userId}/{projectId}") {
        fun createRoute(userId: String, projectId: String) = "project/$userId/$projectId"
    }


}