package com.example.nufianapp.presentation.screens.profile.components

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.nufianapp.presentation.core.components.ButtonIcon
import com.example.nufianapp.presentation.screens.profile.viewmodel.ProjectViewModel
import com.example.nufianapp.ui.theme.Blue

@Composable
fun DetailProject(
    projectId: String,
    userId: String,
    projectViewModel: ProjectViewModel = hiltViewModel(),
    navigateBack: () -> Unit
) {
    val context = LocalContext.current
    val project by projectViewModel.project.collectAsState()
    var showConfirmationDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    LaunchedEffect(key1 = projectId, key2 = userId) {
        projectViewModel.fetchProject(userId, projectId)
    }

    project?.let {
        val isLinkValid = try {
            val uri = Uri.parse(it.linkProject)
            it.linkProject.isNotBlank() && uri.scheme in listOf("http", "https")
        } catch (e: Exception) {
            false
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ButtonIcon(onClickButton = navigateBack)

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = it.projectName,
                    color = Color(0xff000000),
                    style = TextStyle(fontSize = 24.sp),
                    fontWeight = FontWeight.Bold,
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(313.dp)
                        .height(148.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(model = it.projectImageUrl),
                        contentDescription = "Large Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    ) {
                        Text(
                            text = it.projectName,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = it.projectOwner,
                            color = Blue,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = "Time created: ${it.createdAt}",
                            color = Color.Black,
                            fontWeight = FontWeight.Normal,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    Spacer(modifier = Modifier.padding(horizontal = 16.dp))

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isLinkValid) Blue else Color.Gray
                        ),
                        modifier = Modifier
                            .size(width = 125.dp, height = 38.dp)
                            .clickable(enabled = isLinkValid) {
                                showConfirmationDialog = true
                            }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = if (isLinkValid) "View Project" else "Link not available",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "About",
                    color = Color.Black,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = it.description,
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth()
                )
            }

            if (showConfirmationDialog) {
                AlertDialog(
                    onDismissRequest = { showConfirmationDialog = false },
                    title = { Text("Open Project") },
                    text = {
                        Text("Do you want to open this project link in your browser?\n${it.linkProject}")
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showConfirmationDialog = false
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.linkProject))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Log.e("DetailProject", "Error opening link: ${e.message}")
                                }
                            }
                        ) {
                            Text("Yes")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showConfirmationDialog = false
                        }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

