package com.yakshagana.loka

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.yakshagana.loka.notifications.EventReminderWorker
import com.yakshagana.loka.ui.screens.ArtistsScreen
import com.yakshagana.loka.ui.screens.CalendarScreen
import com.yakshagana.loka.ui.screens.EventDetailScreen
import com.yakshagana.loka.ui.screens.RadioScreen
import com.yakshagana.loka.ui.screens.TonightScreen
import com.yakshagana.loka.data.FakeContentRepository
import com.yakshagana.loka.data.LocalEventStorage
import com.yakshagana.loka.ui.theme.YakshaganaLokaTheme
import com.yakshagana.loka.util.PosterShare
import com.yakshagana.loka.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YakshaganaLokaTheme {
                SimpleAppRoot()
            }
        }
    }
}

@Composable
private fun SimpleAppRoot() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val viewModel: MainViewModel = rememberMainViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navItems = listOf(
                    BottomNavItem("tonight", "Tonight") { Icon(Icons.Default.NightsStay, contentDescription = null) },
                    BottomNavItem("calendar", "Calendar") { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                    BottomNavItem("artists", "Artists") { Icon(Icons.Default.Groups, contentDescription = null) },
                    BottomNavItem("radio", "Radio") { Icon(Icons.Default.MusicNote, contentDescription = null) }
                )
                
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                navItems.forEach { item ->
                    NavigationBarItem(
                        icon = { item.icon() },
                        label = { Text(item.label) },
                        selected = currentDestination?.route == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "tonight",
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            composable("tonight") {
                TonightScreen(
                    events = state.managerEvents,
                    onShare = { PosterShare.shareEventPoster(context, it) },
                    onRemind = { EventReminderWorker.scheduleReminders(context, it.id, it.dateTime) },
                    onEventClick = { event ->
                        navController.navigate("event_detail/${event.id}")
                    }
                )
            }
            composable("event_detail/{eventId}") {
                val eventId = it.arguments?.getString("eventId") ?: return@composable
                val event = state.allEvents.find { it.id == eventId } ?: state.managerEvents.find { it.id == eventId }
                if (event != null) {
                    EventDetailScreen(
                        event = event,
                        onBack = { navController.popBackStack() },
                        context = context
                    )
                }
            }
            composable("artists") {
                ArtistsScreen(
                    query = state.artistQuery,
                    artists = state.artists,
                    veshaProfiles = state.veshaProfiles,
                    selectedVeshaId = state.selectedVeshaId,
                    onQueryChanged = viewModel::updateArtistQuery,
                    onVeshaSelected = viewModel::updateSelectedVesha
                )
            }
            composable("calendar") {
                CalendarScreen(
                    selectedDate = state.selectedDate,
                    selectedDateEvents = state.selectedDateEvents,
                    allEvents = state.allEvents,
                    managerEvents = state.managerEvents,
                    status = state.managerStatus,
                    onDateChange = viewModel::updateSelectedDate,
                    onClearStatus = viewModel::clearManagerStatus,
                    onAddEvent = viewModel::addManagerEvent,
                    onEditEvent = viewModel::updateManagerEvent,
                    onDeleteEvent = viewModel::deleteManagerEvent
                )
            }
            composable("radio") {
                RadioScreen(clips = state.audioClips)
            }
        }
    }
}

@Composable
private fun rememberMainViewModel(): MainViewModel {
    val context = androidx.compose.ui.platform.LocalContext.current
    return androidx.lifecycle.viewmodel.compose.viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                require(modelClass.isAssignableFrom(MainViewModel::class.java)) {
                    "Unknown ViewModel: ${modelClass.name}"
                }
                return MainViewModel(
                    FakeContentRepository(),
                    LocalEventStorage(context)
                ) as T
            }
        }
    )
}

data class BottomNavItem(val route: String, val label: String, val icon: @Composable () -> Unit)

@Composable
private fun AppRoot() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val viewModel: MainViewModel = rememberMainViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    val navItems = remember {
        listOf(
            BottomNavItem("tonight", "Tonight") { Icon(Icons.Default.NightsStay, contentDescription = null) },
            BottomNavItem("calendar", "Calendar") { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
            BottomNavItem("artists", "Artists") { Icon(Icons.Default.Groups, contentDescription = null) },
            BottomNavItem("radio", "Radio") { Icon(Icons.Default.MusicNote, contentDescription = null) }
        )
    }

    AskNotificationPermissionOnce()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            NavigationBar {
                navItems.forEach { item ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = item.icon,
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "tonight",
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            composable("tonight") {
                TonightScreen(
                    events = state.allEvents,
                    onShare = { PosterShare.shareEventPoster(context, it) },
                    onRemind = { EventReminderWorker.scheduleReminders(context, it.id, it.dateTime) }
                )
            }
            composable("calendar") {
                CalendarScreen(
                    selectedDate = state.selectedDate,
                    selectedDateEvents = state.selectedDateEvents,
                    allEvents = state.allEvents,
                    managerEvents = state.managerEvents,
                    status = null,
                    onDateChange = { /* Handle date change */ },
                    onClearStatus = { /* Handle clear status */ },
                    onAddEvent = { melaName, title, venue, dateTime, endDateTime, lat, lon, thumbnailUri, invitationProof, contactInfo, briefDescription, description ->
                        viewModel.addManagerEvent(melaName, title, venue, dateTime, endDateTime, lat, lon, thumbnailUri, invitationProof, contactInfo, briefDescription, description)
                        navController.popBackStack()
                    },
                    onEditEvent = { eventId, melaName, title, venue, dateTime, endDateTime, lat, lon, thumbnailUri, invitationProof, contactInfo, briefDescription, description ->
                        viewModel.updateManagerEvent(eventId, melaName, title, venue, dateTime, endDateTime, lat, lon, thumbnailUri, invitationProof, contactInfo, briefDescription, description)
                        navController.popBackStack()
                    },
                    onDeleteEvent = { eventId ->
                        viewModel.deleteManagerEvent(eventId)
                    },
                    onEventClick = { event ->
                        navController.navigate("event_detail/${event.id}")
                    }
                )
            }
            composable("artists") {
                ArtistsScreen(
                    query = state.artistQuery,
                    artists = state.artists,
                    veshaProfiles = state.veshaProfiles,
                    selectedVeshaId = state.selectedVeshaId,
                    onQueryChanged = viewModel::updateArtistQuery,
                    onVeshaSelected = viewModel::updateSelectedVesha
                )
            }
            // Temporarily disabled EventDetailScreen to test
            /*
            composable("eventDetail/{eventId}") {
                val eventId = it.arguments?.getString("eventId")
                val event = remember(eventId) {
                    state.allEvents.find { e -> e.id == eventId } ?: state.managerEvents.find { e -> e.id == eventId }
                }
                event?.let {
                    EventDetailScreen(
                        event = it,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
            */
            composable("radio") {
                RadioScreen(clips = state.audioClips)
            }
        }
    }
}

@Composable
private fun AskNotificationPermissionOnce() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}
    LaunchedEffect(Unit) {
        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}
