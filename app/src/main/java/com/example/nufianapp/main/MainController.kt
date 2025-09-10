package com.example.nufianapp.main

import android.content.Context
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.nufianapp.main.navigation.BottomBar
import com.example.nufianapp.main.navigation.ScreenCustom
import com.example.nufianapp.presentation.screens.auth.AuthenticationScreen
import com.example.nufianapp.presentation.screens.auth.PreAuthScreen
import com.example.nufianapp.presentation.screens.auth.SetPasswordScreen
import com.example.nufianapp.presentation.screens.onboarding.OnboardingScreen
import com.example.nufianapp.presentation.screens.onboarding.viewmodel.OnboardingViewModel
import com.example.nufianapp.presentation.screens.profile.ProfileScreen
import com.example.nufianapp.presentation.screens.profile.viewmodel.UserViewModel
import com.example.nufianapp.ui.theme.Blue
import com.example.nufianapp.R
import com.example.nufianapp.presentation.screen.settings.SettingActivity
import com.example.nufianapp.presentation.screens.auth.BannedScreen
import com.example.nufianapp.presentation.screens.auth.CompleteProfileScreen
import com.example.nufianapp.presentation.screens.chat.ChatScreen
import com.example.nufianapp.presentation.screens.discover.DiscoverBaseScreen
import com.example.nufianapp.presentation.screens.home.add.AddForumScreen
import com.example.nufianapp.presentation.screens.home.detail.DetailForumScreen
import com.example.nufianapp.presentation.screens.home.view.HomeScreen
import com.example.nufianapp.presentation.screens.news.add.AddNewsScreen
import com.example.nufianapp.presentation.screens.news.detail.DetailNewsScreen
import com.example.nufianapp.presentation.screens.news.view.NewsScreen
import com.example.nufianapp.presentation.screens.notification.NotificationScreen
import com.example.nufianapp.presentation.screens.profile.AddCertificateScreen
import com.example.nufianapp.presentation.screens.profile.AddProjectScreen
import com.example.nufianapp.presentation.screens.profile.components.DetailProject
import com.google.firebase.auth.FirebaseAuth
import com.example.nufianapp.presentation.screens.profile.EditProfileActivity
import com.example.nufianapp.presentation.screens.profile.ProfilePreviewScreen
import com.example.nufianapp.presentation.screens.splash.SplashScreen

fun checkAuthenticationRequired(
    currentRoute: String?,
    authRequiredRoutes: Set<String>,
    currentUserId: String?
): Boolean {
    return currentRoute != null &&
            currentRoute in authRequiredRoutes &&
            currentUserId.isNullOrEmpty()
}

@Composable
fun SessionExpiredDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = "Session Expired",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Your session has expired. Please log in again to continue.")
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirm,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Blue
                    )
                ) {
                    Text("Log In")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Blue
                    )
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun MainController(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    context: Context,
    onboardingViewModel: OnboardingViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel(),
    startDestination: String,
    deepLinkOobCode: String?
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = remember { mutableStateOf(false) }

    val avatarUrl by userViewModel.avatarUrl.collectAsState()

    val currentUserId = userViewModel.currentUserId

    var isAuthenticated by remember { mutableStateOf(currentUserId != null) }

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // Update authentication status whenever app resumes
                isAuthenticated = userViewModel.currentUserId != null
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    DisposableEffect(Unit) {
        val authListener = FirebaseAuth.AuthStateListener { auth ->
            isAuthenticated = auth.currentUser != null
        }
        FirebaseAuth.getInstance().addAuthStateListener(authListener)
        onDispose {
            FirebaseAuth.getInstance().removeAuthStateListener(authListener)
        }
    }

    val routesWithoutBottomBar = setOf(
        ScreenCustom.OnboardingScreenCustom.route,
        ScreenCustom.SetPasswordScreenCustom.route,
        ScreenCustom.PreAuthScreenCustom.route,
        ScreenCustom.AuthScreenCustom.route,
        ScreenCustom.CompleteProfileScreenCustom.route,
        ScreenCustom.AddForumCustom.route,
        ScreenCustom.WelcomeScreenCustom.route,
        ScreenCustom.ProfileScreenCustom.route,
        ScreenCustom.SettingsScreenCustom.route,
        ScreenCustom.EditProfileCustom.route,
        ScreenCustom.SplashCustom.route,
        ScreenCustom.DetailForumScreenCustom.route,
        ScreenCustom.ChangePasswordCustom.route,
        ScreenCustom.AddCertificateScreenCustom.route,
        ScreenCustom.AddProjectCustom.route,
        ScreenCustom.DetailProjectScreenCustom.route,
        ScreenCustom.NotificationCustom.route,
        ScreenCustom.ProfilePreviewCustom.route,
        ScreenCustom.AddNews.route,
        ScreenCustom.DetailNewsScreenCustom.route,
        ScreenCustom.BannedScreenCustom.route
    )
    val routesRequiringAuth = setOf(
        ScreenCustom.HomeScreenCustom.route,
        ScreenCustom.ChatScreenCustom.route,
        ScreenCustom.DiscoverScreenCustom.route,
        ScreenCustom.ProfileScreenCustom.route,
        ScreenCustom.DetailProjectScreenCustom.route,
        ScreenCustom.AddCertificateScreenCustom.route,
        ScreenCustom.AddProjectCustom.route,
        ScreenCustom.SettingsScreenCustom.route,
        ScreenCustom.NotificationCustom.route,
        ScreenCustom.ProfilePreviewCustom.route,
        ScreenCustom.DetailForumScreenCustom.route,
    )
    val shouldShowBottomBar = currentRoute != null && currentRoute !in routesWithoutBottomBar
    val shouldShowFab = currentRoute == ScreenCustom.HomeScreenCustom.route
    var showSessionExpiredDialog by remember { mutableStateOf(false) }
    val needsAuthentication = checkAuthenticationRequired(
        currentRoute = currentRoute,
        authRequiredRoutes = routesRequiringAuth,
        currentUserId = currentUserId
    )
    val userState by userViewModel.currentUser.collectAsState()

    LaunchedEffect(isAuthenticated, currentUserId) {
        if (isAuthenticated && currentUserId != null) {
            userViewModel.observeCurrentUser(currentUserId)
        }
    }

    LaunchedEffect(userState?.isBanned) {
        if (userState?.isBanned == true) {
            FirebaseAuth.getInstance().signOut()
            navController.navigate(ScreenCustom.BannedScreenCustom.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    // Effect to update dialog state when auth status or route changes
    LaunchedEffect(currentRoute, isAuthenticated) {
        val isProtected = currentRoute in routesRequiringAuth
        val isUnauthenticated = currentUserId.isNullOrEmpty()

        if (isProtected && isUnauthenticated) {
            // Navigate away immediately
            navController.navigate(ScreenCustom.PreAuthScreenCustom.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated && currentUserId != null) {
            userViewModel.loadAvatarUrl()
        }
    }


    // Function to handle logout and navigation to auth screen
    fun handleLogout() {
        FirebaseAuth.getInstance().signOut()
        // Navigate to authentication screen
        navController.navigate(ScreenCustom.AuthScreenCustom.route) {
            popUpTo(0)
            launchSingleTop = true
        }
    }

    // Session expired dialog
    SessionExpiredDialog(
        showDialog = showSessionExpiredDialog,
        onDismiss = {
            showSessionExpiredDialog = false
            // Navigate back to a non-protected screen
            navController.navigate(ScreenCustom.PreAuthScreenCustom.route) {
                popUpTo(0)
                launchSingleTop = true
            }
        },
        onConfirm = {
            showSessionExpiredDialog = false
            handleLogout()
        }
    )

    Box(modifier = modifier.fillMaxSize()) {
        // NavHost with NO padding so it extends behind bottom bar
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.fillMaxSize(), // No padding applied
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            composable(ScreenCustom.SplashCustom.route) {
                SplashScreen(
                    navigateToOnboarding = {
                        navController.popBackStack()
                        navController.navigate(ScreenCustom.OnboardingScreenCustom.route)
                    },
                    navigateToHome = {
                        navController.popBackStack()
                        navController.navigate(ScreenCustom.HomeScreenCustom.route)
                    },
                    navigateToSetPassword = {
                        navController.navigate(ScreenCustom.SetPasswordScreenCustom.route)
                    },
                    deepLinkOobCode = deepLinkOobCode,
                    navigateToSignIn = {
                        navController.navigate(ScreenCustom.AuthScreenCustom.route)
                    }
                )
            }
            composable(
                route = ScreenCustom.OnboardingScreenCustom.route,
            ) {
                OnboardingScreen(navController = navController)
            }

            composable(
                ScreenCustom.EditProfileCustom.route,
                enterTransition = {
                    return@composable fadeIn()
                },
                popEnterTransition = {
                    return@composable fadeIn()
                },
                exitTransition = {
                    return@composable fadeOut()
                },
                popExitTransition = {
                    return@composable fadeOut()
                }
            ) {
                EditProfileActivity(
                    navigateToProfile = {
                        navController.popBackStack()
                        navController.navigate(ScreenCustom.ProfileScreenCustom.route)
                    },
                    navigateBack = { navController.popBackStack() }
                )
            }

            composable(ScreenCustom.BannedScreenCustom.route) {
                BannedScreen(navController = navController)
            }

            composable(ScreenCustom.DiscoverScreenCustom.route) {
                DiscoverBaseScreen(
                    navigateToConnectProfile = { userId ->
                        navController.navigate(ScreenCustom.ProfilePreviewCustom.createRoute(userId))
                    },
                    navigateToProfile = {navController.navigate(ScreenCustom.ProfileScreenCustom.route)}
                )
            }
            composable(ScreenCustom.AddCertificateScreenCustom.route) {
                AddCertificateScreen(
                    navigateBack = { navController.popBackStack() }
                )
            }
            composable(ScreenCustom.AddProjectCustom.route) {
                AddProjectScreen(
                    navigateBack = { navController.popBackStack() }
                )
            }

            composable(
                ScreenCustom.AddForumCustom.route,
                enterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Up,
                        tween(700)
                    )
                },
                popEnterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Up,
                        tween(700)
                    )
                },
                exitTransition = {
                    return@composable slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Down,
                        tween(700)
                    )
                },
                popExitTransition = {
                    return@composable slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Down,
                        tween(700)
                    )
                }
            ) {
                AddForumScreen(
                    navigateBack = {
                        navController.navigateUp()
                    },
                    navigateToForum = {
                        navController.navigate(ScreenCustom.HomeScreenCustom.route)
                    },
                )
            }

            composable(ScreenCustom.AddNews.route) {
                AddNewsScreen(
                    navigateBack = {
                        navController.navigateUp()
                    },
                    navigateToNews = {
                        navController.navigate(ScreenCustom.NewsCustom.route)
                    }
                )
            }

            composable(
                route = ScreenCustom.DetailNewsScreenCustom.route,
                arguments = listOf(navArgument("newsId") { type = NavType.StringType }),
            ) {
                val newsId = it.arguments?.getString("newsId") ?: ""
                DetailNewsScreen(
                    newsId = newsId,
                    navigateBack = {
                        navController.navigateUp()
                    }
                )
            }

            composable(route = ScreenCustom.HomeScreenCustom.route) {
                HomeScreen(
                    navigateToDetail = { forumId, forumUserPostId, isEnabled ->
                        navController.navigate(
                            ScreenCustom.DetailForumScreenCustom.createRoute(
                                forumId,
                                forumUserPostId,
                                isEnabled
                            )
                        )
                    },
                    navigateToNotification = {
                        navController.navigate(ScreenCustom.NotificationCustom.route)
                    },
                    navigateToProfilePreview = { userId ->
                        navController.navigate(
                            ScreenCustom.ProfilePreviewCustom.createRoute(
                                userId
                            )
                        )
                    },
                    showBottomBar = showBottomBar
                )
            }

            composable(
                ScreenCustom.NotificationCustom.route
            ) {
                NotificationScreen(
                    navigateBack = {
                        navController.popBackStack()
                    },
                )
            }

            composable(
                ScreenCustom.ProfilePreviewCustom.route,
                enterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Up,
                        tween(700)
                    )
                }
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId")
                ProfilePreviewScreen(
                    navigateBack = { navController.popBackStack() },
                    navigateToSetting = { navController.navigate(ScreenCustom.SettingsScreenCustom.route) },
                    userId = userId ?: "",
                    navigateToDetailProject = { userId, projectId -> navController.navigate(ScreenCustom.DetailProjectScreenCustom.createRoute(
                        userId,
                        projectId
                    ) )},
                    navigateToDetail = { forumId, forumUserPostId, isEnabled ->
                        navController.navigate(
                            ScreenCustom.DetailForumScreenCustom.createRoute(
                                forumId,
                                forumUserPostId,
                                isEnabled
                            )
                        )
                    }
                )
            }

            composable(
                ScreenCustom.SettingsScreenCustom.route,
                enterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        tween(700)
                    )
                },
                popEnterTransition = {
                    return@composable fadeIn()
                },
                exitTransition = {
                    return@composable fadeOut()
                },
                popExitTransition = {
                    return@composable slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        tween(700)
                    )
                }
            ) {
                SettingActivity(
                    navigateBack = { navController.popBackStack() },
                    navigateToSignIn = {
                        navController.navigate(ScreenCustom.AuthScreenCustom.route) {
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }
                        }
                    },
                    navigateToEditProfile = { navController.navigate(ScreenCustom.EditProfileCustom.route) },
                    navigateToChangePassword = { navController.navigate(ScreenCustom.ChangePasswordCustom.route) }
                )
            }

            composable(
                route = ScreenCustom.DetailForumScreenCustom.route,
                arguments = listOf(
                    navArgument("forumId") { type = NavType.StringType },
                    navArgument("forumUserPostId") { type = NavType.StringType },
                    navArgument("isEnabled") { type = NavType.BoolType } // Add isEnabled argument
                )
            ) { backStackEntry ->
                val forumId = backStackEntry.arguments?.getString("forumId") ?: ""
                val forumUserPostId = backStackEntry.arguments?.getString("forumUserPostId") ?: ""
                val isEnabled =
                    backStackEntry.arguments?.getBoolean("isEnabled") ?: false // Default value

                DetailForumScreen(
                    forumId = forumId,
                    forumUserPostId = forumUserPostId,
                    isEnabled = isEnabled,
                    navigateBack = {
                        navController.navigateUp()
                    },
                    navigateToProfilePreview = { userId ->
                        navController.navigate(
                            ScreenCustom.ProfilePreviewCustom.createRoute(
                                userId
                            )
                        )
                    }
                )
            }

            composable(ScreenCustom.NewsCustom.route) {
                NewsScreen(
                    navigateToDetail = { newsId ->
                        navController.navigate(ScreenCustom.DetailNewsScreenCustom.createRoute(newsId))
                    },
                    navigateToAddNews = {
                        navController.navigate(ScreenCustom.AddNews.route)
                    },
                )
            }

            composable(
                route = ScreenCustom.PreAuthScreenCustom.route,
            ) {
                PreAuthScreen(
                    navController = navController,
                    onboardingViewModel
                )
            }

            composable(
                route = ScreenCustom.ChatScreenCustom.route,
            ) {
                ChatScreen(
                    navController = navController
                )
            }

            composable(
                route = ScreenCustom.AuthScreenCustom.route,
                enterTransition = {
                    slideInVertically(initialOffsetY = { -it }, animationSpec = tween(500))
                }
            ) {
                AuthenticationScreen(navController = navController)
            }

            composable(ScreenCustom.ProfileScreenCustom.route) {
                ProfileScreen(
                    navigateToSignIn = {
                        navController.navigate(ScreenCustom.AuthScreenCustom.route) {
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }
                        }
                    },
                    navigateBack = { navController.popBackStack() },
                    navigateToSetting = { navController.navigate(ScreenCustom.SettingsScreenCustom.route) },
                    navigateToAddCertificate = { navController.navigate(ScreenCustom.AddCertificateScreenCustom.route) },
                    navigateToAddProject = { navController.navigate(ScreenCustom.AddProjectCustom.route) },
                    navigateToDetailProject = { userId, projectId ->
                        navController.navigate(
                            ScreenCustom.DetailProjectScreenCustom.createRoute(
                                userId,
                                projectId
                            )
                        )
                    },
                    navigateToDetail = { forumId, forumUserPostId, isEnabled ->
                        navController.navigate(
                            ScreenCustom.DetailForumScreenCustom.createRoute(
                                forumId,
                                forumUserPostId,
                                isEnabled
                            )
                        )
                    }
                )
            }

            composable(
                route = ScreenCustom.DetailProjectScreenCustom.route,
                arguments = listOf(
                    navArgument("userId") { type = NavType.StringType },
                    navArgument("projectId") { type = NavType.StringType }
                ),
            ) {
                val projectId = it.arguments?.getString("projectId") ?: ""
                val userId = it.arguments?.getString("userId") ?: ""
                DetailProject(
                    projectId = projectId,
                    userId = userId,
                    navigateBack = { navController.popBackStack() }
                )
            }

            composable(route = ScreenCustom.SetPasswordScreenCustom.route) {
                SetPasswordScreen(
                    navController = navController,
                    onPasswordSet = {
                        navController.navigate(ScreenCustom.AuthScreenCustom.route) {
                            popUpTo(ScreenCustom.SetPasswordScreenCustom.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(route = ScreenCustom.CompleteProfileScreenCustom.route) {
                CompleteProfileScreen(
                    onProfileSaved = {
                        navController.navigate(ScreenCustom.AuthScreenCustom.route) {
                            popUpTo(0)
                            launchSingleTop = true
                        }
                    },
                    userViewModel = userViewModel
                )
            }
        }
        // Bottom UI elements layered on top
        Column(Modifier.align(Alignment.BottomCenter)) {
            // Floating Action Button - Only shown on HomeScreen
            if (showBottomBar.value && shouldShowFab) {
                Box(Modifier
                    .fillMaxWidth()
                    .zIndex(1f)
                    .padding(end = 20.dp),
                    contentAlignment = Alignment.BottomEnd) {
                    FloatingActionButton(
                        onClick = { navController.navigate(ScreenCustom.AddForumCustom.route) },
                        containerColor = Blue,
                        shape = CircleShape,
                        modifier = Modifier
                            .offset(y = (0).dp)
                            .size(64.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_item_add),
                            contentDescription = "Add",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }

            // Bottom Bar
            AnimatedVisibility(
                visible = showBottomBar.value && shouldShowBottomBar,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(durationMillis = 500)
                ),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(durationMillis = 300)
                )
            ) {
                BottomBar(
                    navController,
                    avatarUrl = avatarUrl
                )
            }
        }
    }
}