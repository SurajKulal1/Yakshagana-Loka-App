package com.yakshagana.loka.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.yakshagana.loka.model.Event
import com.yakshagana.loka.util.getThumbnailModel
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    event: Event,
    onBack: () -> Unit,
    context: android.content.Context
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            
            // Thumbnail with border
            item {
                val context = LocalContext.current
                val thumbnailModel = getThumbnailModel(context, event.thumbnailUri)
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(4.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (thumbnailModel != null && event.thumbnailUri != null) {
                            AsyncImage(
                                model = thumbnailModel,
                                contentDescription = event.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text(
                                text = event.title.first().toString().uppercase(),
                                style = MaterialTheme.typography.displayMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Professional event details card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Event title with premium styling
                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.5).sp,
                                lineHeight = 38.sp
                            ),
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        
                        // Mela name with elegant styling
                        Surface(
                            modifier = Modifier
                                .wrapContentWidth()
                                .clip(RoundedCornerShape(12.dp)),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = event.melaName,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 0.2.sp
                                ),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                        
                        // Brief description with professional styling
                        event.briefDescription?.let { briefDesc ->
                            if (briefDesc.isNotBlank()) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp)),
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                                    tonalElevation = 2.dp
                                ) {
                                    Text(
                                        text = briefDesc,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontSize = 16.sp,
                                            lineHeight = 24.sp,
                                            letterSpacing = 0.1.sp,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Professional details with enhanced styling
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            tonalElevation = 3.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Venue with premium styling
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Surface(
                                        modifier = Modifier.size(48.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier
                                                .size(24.dp)
                                                .padding(12.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = "Venue",
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                letterSpacing = 0.3.sp
                                            ),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = event.venue,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Medium,
                                                letterSpacing = 0.1.sp
                                            ),
                                            color = Color.Black
                                        )
                                    }
                                }
                                
                                // Date & Time with premium styling
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Surface(
                                        modifier = Modifier.size(48.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CalendarMonth,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier
                                                .size(24.dp)
                                                .padding(12.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = "Date & Time",
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                letterSpacing = 0.3.sp
                                            ),
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                        Text(
                                            text = try {
                                                "${event.dateTime.toLocalDate()} at ${event.dateTime.toLocalTime()}"
                                            } catch (e: Exception) {
                                                "Date not available"
                                            },
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Medium,
                                                letterSpacing = 0.1.sp
                                            ),
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                        
                        // Contact information with premium styling
                        event.contactInfo?.let { contact ->
                            if (contact.isNotBlank()) {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.2f),
                                    tonalElevation = 2.dp
                                ) {
                                    Column(
                                        modifier = Modifier.padding(20.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Surface(
                                                modifier = Modifier.size(40.dp),
                                                shape = RoundedCornerShape(10.dp),
                                                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.LocationOn,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.tertiary,
                                                    modifier = Modifier
                                                        .size(20.dp)
                                                        .padding(10.dp)
                                                )
                                            }
                                            Text(
                                                text = "Contact Information",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.SemiBold,
                                                    letterSpacing = 0.2.sp
                                                ),
                                                color = MaterialTheme.colorScheme.tertiary
                                            )
                                        }
                                        Text(
                                            text = contact,
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontSize = 17.sp,
                                                lineHeight = 26.sp,
                                                letterSpacing = 0.1.sp
                                            ),
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                        
                        // Full description with premium styling
                        event.description?.let { fullDesc ->
                            if (fullDesc.isNotBlank()) {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    tonalElevation = 3.dp,
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(20.dp),
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Text(
                                            text = "Event Description",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 0.2.sp
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = fullDesc,
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontSize = 17.sp,
                                                lineHeight = 28.sp,
                                                letterSpacing = 0.05.sp
                                            ),
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                                            }
                }
            }

            // Open in Map button with premium styling
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 8.dp,
                    tonalElevation = 4.dp
                ) {
                    Button(
                        onClick = {
                            val intent = android.content.Intent(
                                android.content.Intent.ACTION_VIEW,
                                android.net.Uri.parse("geo:${event.latitude},${event.longitude}?q=${event.title}")
                            )
                            intent.setPackage("com.google.android.apps.maps")
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                // Fallback to any map app
                                val fallbackIntent = android.content.Intent(
                                    android.content.Intent.ACTION_VIEW,
                                    android.net.Uri.parse("geo:${event.latitude},${event.longitude}")
                                )
                                context.startActivity(fallbackIntent)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 6.dp,
                            pressedElevation = 8.dp
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Open in Map",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.3.sp
                                )
                            )
                        }
                    }
                }
            }

            // Invitation proof with premium professional design
            item {
                val context = LocalContext.current
                val invitationModel = getThumbnailModel(context, event.invitationProof)
                
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    shadowElevation = 8.dp,
                    tonalElevation = 4.dp,
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Premium header with enhanced styling
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp)),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                            tonalElevation = 3.dp
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Surface(
                                    modifier = Modifier.size(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Image,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .size(24.dp)
                                            .padding(12.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Invitation Proof",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.3.sp
                                        ),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Official event invitation",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            letterSpacing = 0.1.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        
                        // Invitation proof image with enhanced styling
                        if (invitationModel != null && event.invitationProof != null) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp),
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surface,
                                shadowElevation = 6.dp,
                                tonalElevation = 2.dp
                            ) {
                                AsyncImage(
                                    model = invitationModel,
                                    contentDescription = "Invitation Proof",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(16.dp))
                                )
                            }
                        } else {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp),
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Image,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "No invitation proof available",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
