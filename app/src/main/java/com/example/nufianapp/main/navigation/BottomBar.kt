package com.example.nufianapp.main.navigation

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.nufianapp.R
import com.example.nufianapp.ui.theme.Blue
import com.example.nufianapp.ui.theme.Charcoal
import com.example.nufianapp.ui.theme.DisabledColor
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("ContextCastToActivity")
@Composable
fun BottomBar(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    transparent: Boolean = false,
    avatarUrl: String?
) {
    val activity = LocalContext.current as? Activity
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    BackHandler {
        val currentDestination = navController.currentBackStackEntry?.destination?.route
        if (currentDestination != ScreenCustom.HomeScreenCustom.route) {
            navController.navigate(ScreenCustom.HomeScreenCustom.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                }
            }
        } else {
            activity?.moveTaskToBack(true)
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(20.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color.Transparent,
        border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.2f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Charcoal.copy(alpha = 0.9f), Charcoal.copy(alpha = 0.9f))))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
                    .alpha(0.5f)
            )

            CustomNavigationBar(navController = navController, avatarUrl = avatarUrl, modifier = modifier)
        }
    }
}


@Composable
fun CustomNavigationBar(
    navController: NavHostController,
    avatarUrl: String?,
    modifier: Modifier = Modifier
)
{
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val leftNavigationItems = listOf(
        NavigationItem(
            title = "Home",
            selectedIcon = R.drawable.home_active,
            unselectedIcon = R.drawable.home_inactive,
            screen = ScreenCustom.HomeScreenCustom
        ),
        NavigationItem(
            title = "Discover",
            selectedIcon = R.drawable.discover_active,
            unselectedIcon = R.drawable.discover_inactive,
            screen = ScreenCustom.DiscoverScreenCustom
        )
    )

    val rightNavigationItems = listOf(
        NavigationItem(
            title = "News",
            selectedIcon = R.drawable.news_active,
            unselectedIcon = R.drawable.news_inactive,
            screen = ScreenCustom.NewsCustom
        ),
        NavigationItem(
            title = "Profile",
            selectedIcon = R.drawable.img_avatar_default,
            unselectedIcon = R.drawable.img_avatar_default,
            screen = ScreenCustom.ProfileScreenCustom
        )
    )

    Log.d("BottomBarDebug", "CustomNavigationBar avatarUrl = $avatarUrl")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leftNavigationItems.forEach { item ->
                NavigationBarItem(
                    item = item,
                    selected = currentRoute == item.screen.route,
                    onItemClick = {
                        navController.navigate(item.screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            restoreState = true
                            launchSingleTop = true
                        }
                    },
                    avatarUrl = avatarUrl,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Right side items
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            rightNavigationItems.forEach { item ->
                NavigationBarItem(
                    item = item,
                    selected = currentRoute == item.screen.route,
                    onItemClick = {
                        navController.navigate(item.screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            restoreState = true
                            launchSingleTop = true
                        }
                    },
                    avatarUrl = avatarUrl,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
    }

@Composable
fun NavigationBarItem(
    item: NavigationItem,
    selected: Boolean,
    onItemClick: () -> Unit,
    avatarUrl: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Log.d("NavigationBarItem", "Avatar URL: $avatarUrl")
    Log.d("BottomBarDebug", "Item: ${item.title}, Route: ${item.screen.route}, AvatarUrl: $avatarUrl")

    val itemBackground = if (selected) {
        Modifier
            .background(
                color = Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 0.5.dp,
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
            )
    } else Modifier

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(vertical = 8.dp, horizontal = 4.dp)
            .then(itemBackground)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onItemClick)
            .padding(vertical = 8.dp, horizontal = 6.dp)
    ) {
        if (item.screen.route == ScreenCustom.ProfileScreenCustom.route) {
            val profileModifier = Modifier
                .then(
                    if (selected) Modifier
                        .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)), shape = CircleShape)
                        .background(Color.White.copy(alpha = 0.1f), shape = CircleShape)
                    else Modifier
                )
                .padding(1.dp)

            Box(
                modifier = profileModifier,
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(Blue.copy(alpha = 0.2f), CircleShape)
                            .blur(2.dp)
                    )
                }

                Log.d("AvatarDebug", "Final avatarUrl: $avatarUrl")

                val request = ImageRequest.Builder(context)
                    .data(avatarUrl.takeIf { !it.isNullOrBlank() })
                    .crossfade(true)
                    .build()

                AsyncImage(
                    model = request,
                    contentDescription = item.title,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape),
                    placeholder = painterResource(R.drawable.img_avatar_default),
                    error = painterResource(R.drawable.img_avatar_default),
                    fallback = painterResource(R.drawable.img_avatar_default)
                ) }
        } else {
            Box(contentAlignment = Alignment.Center) {
                if (selected) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Blue.copy(alpha = 0.2f), CircleShape)
                            .blur(2.dp)
                    )
                }

                Icon(
                    painter = painterResource(
                        id = if (selected) item.selectedIcon else item.unselectedIcon
                    ),
                    contentDescription = item.title,
                    tint = if (selected) Color.White else DisabledColor.copy(alpha = 0.7f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = item.title,
            style = MaterialTheme.typography.bodySmall,
            color = if (selected) Color.White else DisabledColor.copy(alpha = 0.7f)
        )

        if (selected) {
            Spacer(modifier = Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(Color.White, CircleShape)
            )
        }
    }
}

