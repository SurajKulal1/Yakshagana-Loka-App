package com.yakshagana.loka.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.core.content.ContextCompat.startActivity
import com.yakshagana.loka.model.Event
import com.yakshagana.loka.util.copyToPrivateStorage
import com.yakshagana.loka.util.getThumbnailModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale

private val ManagerDateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")
private val ScheduleDateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
private val EventTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

private fun fileNameFromUri(uri: Uri?): String = uri?.lastPathSegment?.substringAfterLast('/') ?: "Not selected"

@Composable
fun CalendarScreen(
    selectedDate: LocalDate,
    selectedDateEvents: List<Event>,
    allEvents: List<Event>,
    managerEvents: List<Event>,
    status: String?,
    onDateChange: (LocalDate) -> Unit,
    onClearStatus: () -> Unit,
    onAddEvent: (melaName: String, title: String, venue: String, dateTime: LocalDateTime, endDateTime: LocalDateTime?, lat: Double, lon: Double, thumbnailUri: String?, invitationProof: String?, contactInfo: String?, briefDescription: String?, description: String?) -> Unit,
    onEditEvent: (eventId: String, melaName: String, title: String, venue: String, dateTime: LocalDateTime, endDateTime: LocalDateTime?, lat: Double, lon: Double, thumbnailUri: String?, invitationProof: String?, contactInfo: String?, briefDescription: String?, description: String?) -> Unit,
    onDeleteEvent: (eventId: String) -> Unit,
    onEventClick: (Event) -> Unit = {}
) {
    val context = LocalContext.current
    var visibleMonth by remember(selectedDate) { mutableStateOf(YearMonth.from(selectedDate)) }
    val eventDates = remember(allEvents) { allEvents.map { it.dateTime.toLocalDate() }.toSet() }
    
    var showAddForm by remember { mutableStateOf(false) }
    var showEditForm by remember { mutableStateOf(false) }
    var editingEventId by remember { mutableStateOf<String?>(null) }
    var melaName by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var venue by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf(LocalDate.now().toString()) }
    var startTimeText by remember { mutableStateOf("19:30") }
    var endTimeText by remember { mutableStateOf("23:30") }
    var latText by remember { mutableStateOf("13.3409") }
    var lonText by remember { mutableStateOf("74.7421") }
    var thumbUri by remember { mutableStateOf<Uri?>(null) }
    var proofUri by remember { mutableStateOf<Uri?>(null) }
    var contactInfo by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var briefDescription by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf<String?>(null) }

    val thumbnailPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            thumbUri = copyToPrivateStorage(context, it)
        }
    }
    val proofPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            proofUri = copyToPrivateStorage(context, it)
        }
    }

    fun resetForm() {
        melaName = ""
        title = ""
        venue = ""
        dateText = LocalDate.now().toString()
        startTimeText = "19:30"
        endTimeText = "23:30"
        latText = "13.3409"
        lonText = "74.7421"
        thumbUri = null
        proofUri = null
        contactInfo = ""
        description = ""
        briefDescription = ""
        formError = null
        editingEventId = null
    }

    fun populateEditForm(event: Event) {
        editingEventId = event.id
        melaName = event.melaName
        title = event.title
        venue = event.venue
        dateText = event.dateTime.toLocalDate().toString()
        startTimeText = event.dateTime.toLocalTime().toString()
        endTimeText = event.endDateTime?.toLocalTime()?.toString() ?: "23:30"
        latText = event.latitude.toString()
        lonText = event.longitude.toString()
        thumbUri = event.thumbnailUri?.let { Uri.parse(it) }
        proofUri = event.invitationProof?.let { Uri.parse(it) }
        contactInfo = event.contactInfo ?: ""
        description = event.description ?: ""
        briefDescription = event.briefDescription ?: ""
        formError = null
        showEditForm = true
    }

    fun submit() {
        formError = null
        val date = runCatching { LocalDate.parse(dateText.trim()) }.getOrNull()
        val startTime = runCatching { LocalTime.parse(startTimeText.trim()) }.getOrNull()
        val endTime = runCatching { LocalTime.parse(endTimeText.trim()) }.getOrNull()
        val lat = latText.trim().toDoubleOrNull()
        val lon = lonText.trim().toDoubleOrNull()
        if (melaName.isBlank() || title.isBlank() || venue.isBlank() || date == null || startTime == null || lat == null || lon == null) {
            formError = "Please fill valid values. Date: yyyy-MM-dd, Time: HH:mm"
            return
        }
        if (proofUri == null && editingEventId == null) {
            formError = "Please upload invitation proof before submitting."
            return
        }

        val startDateTime = LocalDateTime.of(date, startTime)
        val endDateTime = if (endTime != null) LocalDateTime.of(date, endTime) else null

        if (editingEventId != null) {
            onEditEvent(
                editingEventId!!,
                melaName,
                title,
                venue,
                startDateTime,
                endDateTime,
                lat,
                lon,
                thumbUri?.toString(),
                proofUri?.toString(),
                contactInfo.ifBlank { null },
                briefDescription.ifBlank { null },
                description.ifBlank { null }
            )
            showEditForm = false
        } else {
            onAddEvent(
                melaName,
                title,
                venue,
                startDateTime,
                endDateTime,
                lat,
                lon,
                thumbUri?.toString(),
                proofUri?.toString(),
                contactInfo.ifBlank { null },
                briefDescription.ifBlank { null },
                description.ifBlank { null }
            )
            showAddForm = false
        }
        resetForm()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Text(
                text = "Mela Calendar",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.size(4.dp))
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    CalendarCard(
                        visibleMonth = visibleMonth,
                        selectedDate = selectedDate,
                        eventDates = eventDates,
                        onPreviousMonth = { visibleMonth = visibleMonth.minusMonths(1) },
                        onNextMonth = { visibleMonth = visibleMonth.plusMonths(1) },
                        onDateSelected = { date ->
                            visibleMonth = YearMonth.from(date)
                            onDateChange(date)
                        }
                    )

                    if (selectedDateEvents.isNotEmpty()) {
                        selectedDateEvents.forEach { event ->
                            PremiumEventCard(
                                event = event,
                                isTonight = false,
                                onEdit = null,
                                onDelete = null,
                                onClick = { onEventClick(event) }
                            )
                        }
                    } else {
                        EmptyStateCard()
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.size(16.dp))
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { showAddForm = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Add New Event", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }

                    if (showAddForm) {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                Text(
                                    "Event Details Form",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                OutlinedTextField(
                                    value = melaName,
                                    onValueChange = { melaName = it },
                                    label = { Text("Mela Name") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                OutlinedTextField(
                                    value = title,
                                    onValueChange = { title = it },
                                    label = { Text("Prasanga Title") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                OutlinedTextField(
                                    value = venue,
                                    onValueChange = { venue = it },
                                    label = { Text("Venue") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                OutlinedTextField(
                                    value = contactInfo,
                                    onValueChange = { contactInfo = it },
                                    label = { Text("Contact Info") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                OutlinedTextField(
                                    value = briefDescription,
                                    onValueChange = { briefDescription = it },
                                    label = { Text("Brief Description") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                OutlinedTextField(
                                    value = description,
                                    onValueChange = { description = it },
                                    label = { Text("Full Description") },
                                    modifier = Modifier.fillMaxWidth(),
                                    minLines = 3,
                                    maxLines = 5,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    OutlinedTextField(
                                        value = dateText,
                                        onValueChange = { dateText = it },
                                        label = { Text("Date (yyyy-MM-dd)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp),
                                        leadingIcon = {
                                            Icon(Icons.Default.CalendarMonth, contentDescription = null)
                                        }
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                                        OutlinedTextField(
                                            value = startTimeText,
                                            onValueChange = { startTimeText = it },
                                            label = { Text("Start Time (HH:mm)") },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(14.dp),
                                            leadingIcon = {
                                                Icon(Icons.Default.AccessTime, contentDescription = null)
                                            }
                                        )
                                        OutlinedTextField(
                                            value = endTimeText,
                                            onValueChange = { endTimeText = it },
                                            label = { Text("End Time (HH:mm)") },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(14.dp),
                                            leadingIcon = {
                                                Icon(Icons.Default.AccessTime, contentDescription = null)
                                            }
                                        )
                                    }
                                }
                                
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                                    OutlinedTextField(
                                        value = latText,
                                        onValueChange = { latText = it },
                                        label = { Text("Latitude") },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    OutlinedTextField(
                                        value = lonText,
                                        onValueChange = { lonText = it },
                                        label = { Text("Longitude") },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                                    Button(
                                        onClick = { thumbnailPicker.launch(arrayOf("image/*")) },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        Icon(Icons.Default.Image, contentDescription = null)
                                        Spacer(Modifier.width(10.dp))
                                        Text("Upload Thumbnail")
                                    }
                                    Button(
                                        onClick = { proofPicker.launch(arrayOf("image/*", "application/pdf")) },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                                        )
                                    ) {
                                        Icon(Icons.Default.UploadFile, contentDescription = null)
                                        Spacer(Modifier.width(10.dp))
                                        Text("Upload Proof")
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Image, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Spacer(Modifier.width(10.dp))
                                            Text("Thumbnail: ${fileNameFromUri(thumbUri)}", maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.UploadFile, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Spacer(Modifier.width(10.dp))
                                            Text("Proof: ${fileNameFromUri(proofUri)}", maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                
                                Button(
                                    onClick = { submit() },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = androidx.compose.material3.ButtonDefaults.buttonElevation(
                                        defaultElevation = 4.dp
                                    )
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                                    Spacer(Modifier.width(10.dp))
                                    Text("Submit for Listing", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                }
                                
                                OutlinedButton(
                                    onClick = { showAddForm = false; resetForm() },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text("Cancel", fontWeight = FontWeight.SemiBold)
                                }

                                if (formError != null) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.errorContainer
                                    ) {
                                        Text(
                                            formError!!,
                                            color = MaterialTheme.colorScheme.onErrorContainer,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.padding(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (showEditForm) {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                Text(
                                    "Edit Event Details",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                OutlinedTextField(
                                    value = melaName,
                                    onValueChange = { melaName = it },
                                    label = { Text("Mela Name") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                OutlinedTextField(
                                    value = title,
                                    onValueChange = { title = it },
                                    label = { Text("Prasanga Title") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                OutlinedTextField(
                                    value = venue,
                                    onValueChange = { venue = it },
                                    label = { Text("Venue") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                OutlinedTextField(
                                    value = contactInfo,
                                    onValueChange = { contactInfo = it },
                                    label = { Text("Contact Info") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                OutlinedTextField(
                                    value = briefDescription,
                                    onValueChange = { briefDescription = it },
                                    label = { Text("Brief Description") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                OutlinedTextField(
                                    value = description,
                                    onValueChange = { description = it },
                                    label = { Text("Full Description") },
                                    modifier = Modifier.fillMaxWidth(),
                                    minLines = 3,
                                    maxLines = 5,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    OutlinedTextField(
                                        value = dateText,
                                        onValueChange = { dateText = it },
                                        label = { Text("Date (yyyy-MM-dd)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp),
                                        leadingIcon = {
                                            Icon(Icons.Default.CalendarMonth, contentDescription = null)
                                        }
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                                        OutlinedTextField(
                                            value = startTimeText,
                                            onValueChange = { startTimeText = it },
                                            label = { Text("Start Time (HH:mm)") },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(14.dp),
                                            leadingIcon = {
                                                Icon(Icons.Default.AccessTime, contentDescription = null)
                                            }
                                        )
                                        OutlinedTextField(
                                            value = endTimeText,
                                            onValueChange = { endTimeText = it },
                                            label = { Text("End Time (HH:mm)") },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(14.dp),
                                            leadingIcon = {
                                                Icon(Icons.Default.AccessTime, contentDescription = null)
                                            }
                                        )
                                    }
                                }
                                
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                                    OutlinedTextField(
                                        value = latText,
                                        onValueChange = { latText = it },
                                        label = { Text("Latitude") },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    OutlinedTextField(
                                        value = lonText,
                                        onValueChange = { lonText = it },
                                        label = { Text("Longitude") },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                                    Button(
                                        onClick = { thumbnailPicker.launch(arrayOf("image/*")) },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        Icon(Icons.Default.Image, contentDescription = null)
                                        Spacer(Modifier.width(10.dp))
                                        Text("Upload Thumbnail")
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Image, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Spacer(Modifier.width(10.dp))
                                            Text("Thumbnail: ${fileNameFromUri(thumbUri)}", maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                
                                Button(
                                    onClick = { submit() },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = androidx.compose.material3.ButtonDefaults.buttonElevation(
                                        defaultElevation = 4.dp
                                    )
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                                    Spacer(Modifier.width(10.dp))
                                    Text("Update Event", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                }
                                
                                OutlinedButton(
                                    onClick = { showEditForm = false; resetForm() },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text("Cancel", fontWeight = FontWeight.SemiBold)
                                }

                                if (formError != null) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.errorContainer
                                    ) {
                                        Text(
                                            formError!!,
                                            color = MaterialTheme.colorScheme.onErrorContainer,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.padding(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (managerEvents.isNotEmpty()) {
                        Text(
                            "Your Events",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        managerEvents.sortedByDescending { it.dateTime }.forEach { event ->
                            PremiumEventCard(
                                event = event,
                                isTonight = false,
                                onEdit = { editingEventId = event.id; showEditForm = true },
                                onDelete = { onDeleteEvent(event.id) },
                                onClick = { onEventClick(event) }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    } else {
                        Text(
                            "No events added by you yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

@Composable
private fun HeroHeader(date: String, eventCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Selected Date",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                    )
                    Text(
                        text = date,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$eventCount",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = if (eventCount == 1) "Show" else "Shows",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EmptyStateCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.EventAvailable,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
            Text(
                text = "No Mela Scheduled",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "No mela scheduled for this day",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PremiumEventCard(
    event: Event,
    isTonight: Boolean,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM")
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
    val thumbnailModel = getThumbnailModel(context, event.thumbnailUri)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick?.invoke() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (isTonight) BorderStroke(
            1.5.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        ) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier.size(96.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = thumbnailModel,
                        contentDescription = event.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isTonight) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "TONIGHT",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = event.melaName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = event.dateTime.format(dateFormatter),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = event.dateTime.format(timeFormatter),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
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
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (onEdit != null) {
                        TextButton(
                            onClick = onEdit,
                            modifier = Modifier.height(32.dp).padding(horizontal = 4.dp)
                        ) {
                            Text(
                                text = "Edit",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    if (onDelete != null) {
                        TextButton(
                            onClick = onDelete,
                            modifier = Modifier.height(32.dp).padding(horizontal = 4.dp)
                        ) {
                            Text(
                                text = "Delete",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarCard(
    visibleMonth: YearMonth,
    selectedDate: LocalDate,
    eventDates: Set<LocalDate>,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousMonth) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Previous month",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = visibleMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = visibleMonth.year.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onNextMonth) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Next month",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        CalendarLegend()
        CalendarWeekHeader()
        CalendarMonthGrid(
            visibleMonth = visibleMonth,
            selectedDate = selectedDate,
            eventDates = eventDates,
            onDateSelected = onDateSelected
        )
    }
}

@Composable
private fun CalendarLegend() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        LegendChip(
            label = "Selected",
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
            dotColor = MaterialTheme.colorScheme.primary
        )
        LegendChip(
            label = "Mela day",
            containerColor = Color(0xFFDCFCE7),
            dotColor = Color(0xFF16A34A)
        )
    }
}

@Composable
private fun LegendChip(
    label: String,
    containerColor: Color,
    dotColor: Color
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(containerColor)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(dotColor, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun CalendarWeekHeader() {
    Row(modifier = Modifier.fillMaxWidth()) {
        DayOfWeek.entries.forEach { day ->
            Text(
                text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CalendarMonthGrid(
    visibleMonth: YearMonth,
    selectedDate: LocalDate,
    eventDates: Set<LocalDate>,
    onDateSelected: (LocalDate) -> Unit
) {
    val firstDayOfMonth = visibleMonth.atDay(1)
    val leadingDays = firstDayOfMonth.dayOfWeek.value % 7
    val daysInMonth = visibleMonth.lengthOfMonth()
    val totalCells = ((leadingDays + daysInMonth + 6) / 7) * 7

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        for (weekStart in 0 until totalCells step 7) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (dayIndex in weekStart until weekStart + 7) {
                    val dayNumber = dayIndex - leadingDays + 1
                    val date = if (dayNumber in 1..daysInMonth) visibleMonth.atDay(dayNumber) else null
                    CalendarDayCell(
                        date = date,
                        isSelected = date == selectedDate,
                        hasEvent = date != null && date in eventDates,
                        onClick = { if (date != null) onDateSelected(date) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    date: LocalDate?,
    isSelected: Boolean,
    hasEvent: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val melaGreen = Color(0xFF16A34A)
    val eventBackground = Color(0xFFDCFCE7)
    val containerColor = when {
        date == null -> Color.Transparent
        isSelected -> MaterialTheme.colorScheme.primary
        hasEvent -> eventBackground
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
    }
    val textColor = when {
        date == null -> Color.Transparent
        isSelected -> MaterialTheme.colorScheme.onPrimary
        hasEvent -> melaGreen
        else -> MaterialTheme.colorScheme.onSurface
    }
    val borderColor = when {
        date == null -> Color.Transparent
        isSelected -> MaterialTheme.colorScheme.primary
        hasEvent -> melaGreen.copy(alpha = 0.3f)
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(20.dp))
            .background(containerColor)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable(enabled = date != null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (date != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = date.dayOfMonth.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                if (hasEvent && !isSelected) {
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .size(6.dp)
                            .background(melaGreen, CircleShape)
                    )
                } else {
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}
