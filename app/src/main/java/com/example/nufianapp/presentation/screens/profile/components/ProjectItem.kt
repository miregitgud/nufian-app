package com.example.nufianapp.presentation.screens.profile.components

import android.util.Log
import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.nufianapp.R
import com.example.nufianapp.data.model.Project
import com.example.nufianapp.presentation.core.GlideImageLoader
import com.example.nufianapp.presentation.core.components.ButtonIcon
import com.example.nufianapp.ui.theme.Blue
import com.example.nufianapp.ui.theme.Graphite
import com.example.nufianapp.ui.theme.White
import java.util.Date

@Composable
fun ProjectItem(
    project: Project,
    modifier: Modifier = Modifier,
    isProfile: Boolean = false,
    navigateToDetailProject: (String, String) -> Unit,
    onDeleteProject: (Project) -> Unit
) {
    val context = LocalContext.current
    var showDropdown by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
        ) {
            // Background image
            AndroidView(factory = { contextImage ->
                ImageView(contextImage).apply {
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }
            }, modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp)), update = { imageView ->
                project.projectImageUrl.let {
                    GlideImageLoader.loadImage(context, it, imageView)
                }
            })

            // Black overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(alpha = 0.4f))
            )

            // Overlay Text (Project info)
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
            ) {
                Text(
                    text = project.projectName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = project.description,
                    color = Color.White,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "By ${project.projectOwner}",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }

            // Delete button (Dropdown)
            if (isProfile) {
                Box(modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)) {
                    ButtonIcon(
                        onClickButton = { showDropdown = true },
                        iconRes = R.drawable.icon_triple_dots,
                        tint = White
                    )

                    DropdownMenu(
                        expanded = showDropdown,
                        onDismissRequest = { showDropdown = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Delete Project?") },
                            onClick = {
                                showDropdown = false
                                onDeleteProject(project)
                            }
                        )
                    }
                }
            }

            // View Project button
            Card(
                colors = CardDefaults.cardColors(containerColor = Blue),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(8.dp)
                    .fillMaxWidth()
                    .height(40.dp)
                    .clickable {
                        navigateToDetailProject(project.userId,project.projectId)
                        Log.d("ProjectItem", "Project ID: ${project.projectId}, Project User: ${project.userId}" )
                               },
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "View Project",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun ProjectItemPreview() {
    val project = Project(
        projectId = "1",
        userId = "user1",
        projectImageUrl = "https://example.com/image.jpg",
        projectName = "Sample Project",
        projectOwner = "Ayyash",
        description = "This is a sample project description.",
        linkProject = "https://example.com/project",
        createdAt = Date() // You can specify a date here
    )

    ProjectItem(
        project = project,
        modifier = Modifier,
        isProfile = true,
        onDeleteProject = {},
        navigateToDetailProject = { _, _ -> },
    )
}