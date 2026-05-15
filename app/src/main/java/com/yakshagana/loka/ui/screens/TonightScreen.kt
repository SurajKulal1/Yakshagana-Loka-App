package com.yakshagana.loka.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocationOn
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
import java.time.LocalDateTime

@Composable
fun TonightScreen(
    events: List<Event>,
    onShare: (Event) -> Unit,
    onRemind: (Event) -> Unit,
    onEventClick: (Event) -> Unit = {}
) {
    val now = LocalDateTime.now()
    val today = now.toLocalDate()
    val tomorrow = today.plusDays(1)
    
    // Filter events: Today's Shows (within 24 hours) and Upcoming Shows
    val todaysShows = events.filter { event ->
        val eventDateTime = event.dateTime
        val eventDate = eventDateTime.toLocalDate()
        val isToday = eventDate == today || eventDate == tomorrow && eventDateTime.hour <= 6 // Early morning counts as tonight
        val isWithin24Hours = !eventDateTime.isBefore(now.minusHours(24)) && !eventDateTime.isBefore(now)
        isToday && isWithin24Hours
    }
    
    val upcomingShows = events.filter { event ->
        val eventDateTime = event.dateTime
        val eventDate = eventDateTime.toLocalDate()
        val isAfterTomorrow = eventDate.isAfter(tomorrow)
        val isTomorrowAfter6AM = eventDate == tomorrow && eventDateTime.hour > 6
        isAfterTomorrow || isTomorrowAfter6AM
    }.sortedBy { it.dateTime }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        
        // Today's Shows Section
        item {
            Text(
                text = "Today's Shows",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        if (todaysShows.isNotEmpty()) {
            items(todaysShows) { event ->
                SimpleEventCard(event = event, onClick = { onEventClick(event) })
            }
        } else {
            item {
                Text(
                    text = "No shows scheduled for today",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
        }

        // Upcoming Shows Section
        item {
            Text(
                text = "Upcoming Shows",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        if (upcomingShows.isNotEmpty()) {
            items(upcomingShows) { event ->
                SimpleEventCard(event = event, onClick = { onEventClick(event) })
            }
        } else {
            item {
                Text(
                    text = "No upcoming shows scheduled",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun SimpleEventCard(
    event: Event,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            // Safe thumbnail loading with fallback
            Card(
                modifier = Modifier.size(96.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    val context = LocalContext.current
                    val thumbnailModel = getThumbnailModel(context, event.thumbnailUri)
                    
                    if (thumbnailModel != null && event.thumbnailUri != null) {
                        AsyncImage(
                            model = thumbnailModel,
                            contentDescription = event.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // Fallback to text if no thumbnail
                        Text(
                            text = event.title.first().toString().uppercase(),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Event details - exact Calendar layout
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                
                Text(
                    text = event.melaName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                )

                // Date and Time in same Row - exactly like Calendar
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = try {
                                java.time.format.DateTimeFormatter.ofPattern("dd MMM").format(event.dateTime)
                            } catch (e: Exception) {
                                "Date"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = try {
                                java.time.format.DateTimeFormatter.ofPattern("hh:mm a").format(event.dateTime)
                            } catch (e: Exception) {
                                "Time"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Location in separate Row - exactly like Calendar
                Row(
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = event.venue,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
