package com.example.nufianapp.presentation.core

import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Magenta
import com.example.nufianapp.R
import com.example.nufianapp.data.model.ForumCategory
import com.example.nufianapp.ui.theme.ClearBlue
import com.example.nufianapp.ui.theme.Graphite
import com.example.nufianapp.ui.theme.Red
import com.example.nufianapp.ui.theme.Tosca

object Constants {
    //App
    const val TAG = "AppTag"

    //Buttons
    const val SIGN_IN_BUTTON = "Sign in"
    const val RESET_PASSWORD_BUTTON = "Reset"
    const val SIGN_UP_BUTTON = "Sign up"

    //Menu Items
    const val SIGN_OUT_ITEM = "Sign out"
    const val REVOKE_ACCESS_ITEM = "Revoke Access"

    //Screens
    const val ONBOARDING_ViewModel_SCREEN = "OnboardingViewModel"
    const val SIGN_IN_SCREEN = "Sign in"
    const val FORGOT_PASSWORD_SCREEN = "Forgot password"
    const val SIGN_UP_SCREEN = "Sign up"
    const val VERIFY_EMAIL_SCREEN = "Verify email"
    const val PROFILE_SCREEN = "Profile"

    //Labels
    const val NAME_LABEL = "Name"
    const val EMAIL_LABEL = "Email"
    const val INTEREST_LABEL = "Interest"
    const val PASSWORD_LABEL = "Password"

    //Useful
    const val EMPTY_EMAIL = ""
    const val EMPTY_NAME = ""
    const val EMPTY_INTEREST = ""
    const val EMPTY_PASSWORD = ""
    const val EMPTY_CONFIRM_PASSWORD = ""
    const val EMPTY_ORGANIZATION = ""
    const val EMPTY_START_DATE = ""
    const val EMPTY_END_DATE = ""
    const val EMPTY_CREDENTIAL_ID = ""

    //Texts
    const val FORGOT_PASSWORD = "Forgot password?"
    const val WELCOME_MESSAGE = "Welcome to our app."
    const val ALREADY_VERIFIED = "Already verified?"
    const val SPAM_EMAIL = "If not, please also check the spam folder."

    //Messages
    const val VERIFY_EMAIL_MESSAGE = "We've sent you an email with a link to verify the email."
    const val EMAIL_NOT_VERIFIED_MESSAGE = "Your email is not verified."
    const val RESET_PASSWORD_MESSAGE = "We've sent you an email with a link to reset the password."
    const val REVOKE_ACCESS_MESSAGE = "You need to re-authenticate before revoking the access."
    const val ACCESS_REVOKED_MESSAGE = "Your access has been revoked."

    const val TAG_DETAIL_NEWS_ERROR = "DETAIL_NEWS_ERROR"
    const val TAG_NEWS_ERROR = "NEWS_ERROR"
    const val TAG_DETAIL_FORUM_ERROR = "DETAIL_FORUM_ERROR"

    const val URL_LINK =
        "https://assets.teenvogue.com/photos/63d00c508c0b255d9ed45428/1:1/w_3712,h_3712,c_limit/GettyImages-1246471994.jpg"
    const val URL_LINK2 =
        "https://preview.redd.it/tzuyu-i-think-shes-one-of-the-few-kpop-idols-who-are-9-10-v0-mfc9d65n7otb1.jpg?width=735&format=pjpg&auto=webp&s=4a285b2807972250c1877541d08b1a34eb0b68d2"
    const val URL_LINK3 = "https://www.wowkeren.com/display/images/photo/2020/12/08/00342924.jpg"

    //Error Messages
    const val SENSITIVE_OPERATION_MESSAGE =
        "This operation is sensitive and requires recent authentication. Log in again before retrying this request."

    val PROGRAMS = listOf(
        "Independent Study",
        "Internship"
    )

    val LEARNING_PATH_INTERNSHIP = listOf(
        "Strategic Project Designer",
        "Strategic Project Account",
        "Strategic Project Specialist",
        "UI/UX Designer",
        "Web Developer",
        "Visual/Graphic Designer",
        "Marketing Communications",
        "Mobile Developer",
        "Student Relation & Administration",
        "Event & Community",
        "Public Relation",
        "Project Manager",
        "Game Developer",
        "Social Media Specialist"
    )

    val LEARNING_PATH_INDEPENDENT_STUDY = listOf(
        "Mobile",
        "Web",
        "HCAI",
        "Game",
        "AAI",
        "HCRH",
    )


    val IMAGES_PATTERN = listOf(
        R.drawable.img_pattern_dark_1,
        R.drawable.img_pattern_dark_2,
        R.drawable.img_pattern_dark_3
    )


    val batchColors = listOf(
        Blue,
        Red,
        ClearBlue,
        Tosca,
        Magenta,
        Graphite
    )

    val FORUM_CATEGORY = listOf(
        ForumCategory(name = "Official Announcement"),
        ForumCategory(name = "General"),
        ForumCategory(name = "Discussion"),
        ForumCategory(name = "Recruit/Collab"),
        ForumCategory(name = "Fluff"),
        ForumCategory(name = "Shout out"),
        ForumCategory(name = "Showcase"),
        ForumCategory(name = "Tips & Tricks")
    )
}