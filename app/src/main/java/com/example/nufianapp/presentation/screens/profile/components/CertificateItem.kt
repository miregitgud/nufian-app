package com.example.nufianapp.presentation.screens.profile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.nufianapp.R
import com.example.nufianapp.data.model.Certificate
import com.example.nufianapp.presentation.core.components.ButtonIcon
import com.example.nufianapp.presentation.core.components.PrimaryButtonSmall
import com.example.nufianapp.ui.theme.Blue
import com.example.nufianapp.ui.theme.Graphite
import com.example.nufianapp.presentation.core.components.content.EnlargedImageDialog
import com.example.nufianapp.ui.theme.White

@Composable
fun CertificateItem(
    certificate: Certificate,
    modifier: Modifier = Modifier,
    onDeleteCertificate: (Certificate) -> Unit,
    isProfile: Boolean = false,
    onViewCertificate: (Certificate) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var scale by remember { mutableStateOf(1f) }
    var showDropdown by remember { mutableStateOf(false) }

    Card(
        modifier = modifier,
        border = BorderStroke(1.dp, Blue),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {

        Box(
            modifier = Modifier
                .height(175.dp)
                .background(White)
                .padding(16.dp)
                .clickable {
                    onViewCertificate(certificate) // Invoke onViewCertificate callback
                }
        ) {
            if (isProfile) {
                onDeleteCertificate.let {
                    Box {
                        ButtonIcon(
                            onClickButton = { showDropdown = true },
                            iconRes = R.drawable.icon_triple_dots,
                            tint = Graphite
                        )

                        DropdownMenu(
                            expanded = showDropdown,
                            onDismissRequest = { showDropdown = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Delete Certificate?") },
                                onClick = {
                                    showDropdown = false
                                    onDeleteCertificate(certificate)
                                }
                            )
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                AsyncImage(
                    model = certificate.certificateImageUrl,
                    contentDescription = "Certificate Image",
                    modifier = Modifier
                        .width(120.dp)
                        .height(150.dp)
                        .padding(end = 16.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(2f),
                    verticalArrangement = Arrangement.Top
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = certificate.name,
                                color = Color.Black,
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = certificate.organization,
                                color = Blue,
                                style = MaterialTheme.typography.headlineSmall,
                                maxLines = 3
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = certificate.credentialId,
                        color = Graphite,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    PrimaryButtonSmall(
                        onClick = { showDialog = true },
                        textButton = "View Certificate"
                    )
                }
            }

            if (showDialog) {
                EnlargedImageDialog(
                    imageUrl = certificate.certificateImageUrl,
                    scale = scale,
                    setScale = { newScale -> scale = newScale },
                    onDismiss = { showDialog = false }
                )
            }
        }
    }
}