package com.yakshagana.loka.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.yakshagana.loka.model.Event
import com.yakshagana.loka.util.copyToPrivateStorage
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val ManagerDateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")

private fun fileNameFromUri(uri: Uri?): String = uri?.lastPathSegment?.substringAfterLast('/') ?: "Not selected"

@Composable
fun ManagerEventsScreen(
    events: List<Event>,
    managerEvents: List<Event>,
    status: String?,
    onClearStatus: () -> Unit,
    onAddEvent: (melaName: String, title: String, venue: String, dateTime: LocalDateTime, lat: Double, lon: Double, thumbnailUri: String?) -> Unit
) {
    val context = LocalContext.current

    var showAddForm by remember { mutableStateOf(false) }
    var melaName by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var venue by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf(LocalDate.now().toString()) }
    var timeText by remember { mutableStateOf("19:30") }
    var latText by remember { mutableStateOf("13.3409") }
    var lonText by remember { mutableStateOf("74.7421") }
    var thumbUri by remember { mutableStateOf<Uri?>(null) }
    var proofUri by remember { mutableStateOf<Uri?>(null) }
    var formError by remember { mutableStateOf<String?>(null) }

    val thumbnailPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        thumbUri = uri
    }
    val proofPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        proofUri = uri
    }

    fun resetForm() {
        melaName = ""
        title = ""
        venue = ""
        dateText = LocalDate.now().toString()
        timeText = "19:30"
        latText = "13.3409"
        lonText = "74.7421"
        thumbUri = null
        proofUri = null
        formError = null
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.09f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Gavel, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Mela Schedule Submission",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        "Please review and follow the submission policy before adding an event.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = androidx.compose.ui.unit.TextUnit.Unspecified
                        )
                    )
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                "1) Provide only verified mela details. Fake or misleading entries may be removed and account action may be taken.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "2) Invitation proof is mandatory. Upload a valid invite/poster that matches the schedule details.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "3) Ensure date, time, venue, and map location are accurate. Repeated false submissions can be permanently blocked.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    if (!showAddForm) {
                        Button(
                            onClick = { showAddForm = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "Proceed to Add Event",
                                style = MaterialTheme.typography.labelLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        if (showAddForm) {
            item {
                Card(shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Event Details Form", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        OutlinedTextField(value = melaName, onValueChange = { melaName = it }, label = { Text("Mela Name") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Prasanga Title") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = venue, onValueChange = { venue = it }, label = { Text("Venue") }, modifier = Modifier.fillMaxWidth())
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(value = dateText, onValueChange = { dateText = it }, label = { Text("Date yyyy-MM-dd") }, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = timeText, onValueChange = { timeText = it }, label = { Text("Time HH:mm") }, modifier = Modifier.weight(1f))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(value = latText, onValueChange = { latText = it }, label = { Text("Latitude") }, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = lonText, onValueChange = { lonText = it }, label = { Text("Longitude") }, modifier = Modifier.weight(1f))
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            Button(onClick = { thumbnailPicker.launch(arrayOf("image/*")) }, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.Image, contentDescription = null)
                                Spacer(Modifier.width(6.dp))
                                Text("Upload Thumbnail")
                            }
                            Button(onClick = { proofPicker.launch(arrayOf("image/*", "application/pdf")) }, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.UploadFile, contentDescription = null)
                                Spacer(Modifier.width(6.dp))
                                Text("Upload Proof")
                            }
                        }

                        Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Image, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(Modifier.width(6.dp))
                                    Text("Thumbnail: ${fileNameFromUri(thumbUri)}", maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(Modifier.width(6.dp))
                                    Text("Proof: ${fileNameFromUri(proofUri)}", maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            Button(onClick = {
                                formError = null
                                val date = runCatching { LocalDate.parse(dateText.trim()) }.getOrNull()
                                val time = runCatching { LocalTime.parse(timeText.trim()) }.getOrNull()
                                val lat = latText.trim().toDoubleOrNull()
                                val lon = lonText.trim().toDoubleOrNull()
                                if (melaName.isBlank() || title.isBlank() || venue.isBlank() || date == null || time == null || lat == null || lon == null) {
                                    formError = "Please fill valid values. Date: yyyy-MM-dd, Time: HH:mm"
                                    return@Button
                                }
                                if (proofUri == null) {
                                    formError = "Please upload invitation proof before submitting."
                                    return@Button
                                }
                                val savedThumbUri = copyToPrivateStorage(context, thumbUri)
                                onAddEvent(melaName, title, venue, LocalDateTime.of(date, time), lat, lon, savedThumbUri?.toString())
                                showAddForm = false
                                resetForm()
                            }) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null)
                                Spacer(Modifier.width(6.dp))
                                Text("Submit for Listing")
                            }
                            TextButton(onClick = { showAddForm = false; resetForm() }) { Text("Cancel") }
                        }

                        if (formError != null) {
                            Text(formError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }

        if (status != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = status, style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f))
                        TextButton(onClick = onClearStatus) { Text("Clear") }
                    }
                }
            }
        }

        item {
            Text("Your Added Events", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }

        if (managerEvents.isEmpty()) {
            item {
                Text("No events added by manager yet.", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            items(managerEvents.sortedByDescending { it.dateTime }) { event ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(event.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(event.melaName, color = MaterialTheme.colorScheme.primary)
                        Text(event.venue, style = MaterialTheme.typography.bodyMedium)
                        Text(event.dateTime.format(ManagerDateTimeFormatter), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    val uri = Uri.parse("geo:${event.latitude},${event.longitude}?q=${event.latitude},${event.longitude}(${Uri.encode(event.venue)})")
                                    startActivity(context, Intent(Intent.ACTION_VIEW, uri), null)
                                }
                            ) { Text("Map App") }
                            Button(
                                onClick = {
                                    val url = "https://www.openstreetmap.org/?mlat=${event.latitude}&mlon=${event.longitude}#map=12/${event.latitude}/${event.longitude}"
                                    startActivity(context, Intent(Intent.ACTION_VIEW, Uri.parse(url)), null)
                                }
                            ) { Text("Browser Map") }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }

        item {
            Text("All Events", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }

        if (events.isEmpty()) {
            item { Text("No events available yet.", style = MaterialTheme.typography.bodyMedium) }
        } else {
            items(events.sortedBy { it.dateTime }) { event ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(event.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Text("${event.melaName} • ${event.venue}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
