@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.yakshagana.loka.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import coil.compose.AsyncImage
import com.yakshagana.loka.model.Artist
import com.yakshagana.loka.model.VeshaArtistProfile
import com.yakshagana.loka.model.VeshaProfile

private data class InfoSlide(
    val question: String,
    val answer: String
)

private const val UnifiedSlideHeightDp = 560

private fun normalizeArtistText(raw: String): String =
    raw
        .replace(";", ".")
        .replace(" · ", ", ")
        .replace("–", " and ")
        .replace("'", "'")
        .replace("'", "'")
        .trim()

private fun asSingleParagraph(block: String): String =
    normalizeArtistText(block)
        .replace("\n", " ")
        .replace(Regex("\\s+"), " ")
        .trim()

private fun VeshaArtistProfile.roleAndPlaceParagraph(): String {
    val roleLine = normalizeArtistText(stageTitle)
    val region = normalizeArtistText(place)
    return asSingleParagraph(
        when {
            roleLine.isEmpty() -> region
            region.isEmpty() -> roleLine
            else -> "$roleLine $region"
        }
    )
}

private fun VeshaArtistProfile.artistDetailsParagraph(): String {
    val parts = buildList {
        val rp = roleAndPlaceParagraph()
        if (rp.isNotEmpty()) add(rp)
        val style = asSingleParagraph(styleNote)
        if (style.isNotEmpty()) add(style)
        achievements.forEach { line ->
            val a = asSingleParagraph(line)
            if (a.isNotEmpty()) add(a)
        }
    }
    return parts.joinToString(" ")
}

@Composable
fun ArtistsScreen(
    query: String,
    artists: List<Artist>,
    veshaProfiles: List<VeshaProfile>,
    selectedVeshaId: String?,
    onQueryChanged: (String) -> Unit,
    onVeshaSelected: (String) -> Unit
) {
    val selected = remember(veshaProfiles, selectedVeshaId) {
        veshaProfiles.firstOrNull { it.id == selectedVeshaId } ?: veshaProfiles.firstOrNull()
    }
    var openedVeshaId by remember { mutableStateOf<String?>(null) }
    val openedVesha = remember(veshaProfiles, openedVeshaId) {
        veshaProfiles.firstOrNull { it.id == openedVeshaId }
    }

    if (openedVesha != null) {
        VeshaDetailsPage(
            vesha = openedVesha,
            onBack = { openedVeshaId = null }
        )
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { Spacer(modifier = Modifier.height(12.dp)) }

        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                tonalElevation = 2.dp,
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
                                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f),
                                    MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.65f)
                                )
                            )
                        )
                        .border(
                            width = 1.4.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(22.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        Text(
                            text = "Artist Encyclopedia",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.3.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Choose a vesha and discover artist journeys, signature style, and stage identity in a clean showcase.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                lineHeight = 21.sp,
                                letterSpacing = 0.08.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                singleLine = true,
                label = { Text("Search artist or role") }
            )
        }

        item {
            VeshaGrid(
                veshas = veshaProfiles,
                selectedVeshaId = selected?.id,
                onVeshaSelected = { id ->
                    onVeshaSelected(id)
                    openedVeshaId = id
                }
            )
        }

        selected?.let {
            item {
                Text(
                    text = "Tap a vesha card to open a dedicated details page.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (query.isNotBlank()) {
            item {
                Text(
                    text = "Search Results",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            items(artists) { artist ->
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(artist.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(artist.role, color = MaterialTheme.colorScheme.tertiary)
                        Text(artist.bio, modifier = Modifier.padding(top = 4.dp))
                        Text("Known veshas: ${artist.veshas.joinToString()}", modifier = Modifier.padding(top = 6.dp))
                        artist.gallery.firstOrNull()?.let { imageUrl ->
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = artist.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }
    }
}

@Composable
private fun VeshaGrid(
    veshas: List<VeshaProfile>,
    selectedVeshaId: String?,
    onVeshaSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        veshas.forEach { vesha ->
            val selected = vesha.id == selectedVeshaId
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        width = if (selected) 1.4.dp else 1.dp,
                        color = if (selected) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        } else {
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)
                        },
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { onVeshaSelected(vesha.id) },
                tonalElevation = if (selected) 5.dp else 1.dp,
                color = if (selected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                }
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(190.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f),
                                shape = RoundedCornerShape(14.dp)
                            ),
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                    ) {
                        if (vesha.imageRes != null) {
                            AsyncImage(
                                model = vesha.imageRes,
                                contentDescription = "${vesha.name} image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Box(contentAlignment = androidx.compose.ui.Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = "Thumbnail placeholder",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = vesha.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            lineHeight = 26.sp,
                            letterSpacing = 0.2.sp
                        ),
                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = vesha.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 21.sp,
                            letterSpacing = 0.06.sp
                        ),
                        color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = if (selected) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
                        } else {
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.72f)
                        }
                    ) {
                        Text(
                            text = "Tap to open artist showcase",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 0.2.sp),
                            color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VeshaDetailsPage(
    vesha: VeshaProfile,
    onBack: () -> Unit
) {
    val veshaSlides = remember(vesha) {
        buildList {
            add(
                InfoSlide(
                    question = "What is unique about ${vesha.name} on stage?",
                    answer = "${vesha.description} ${vesha.significance}"
                )
            )
            add(
                InfoSlide(
                    question = "How is this vesha performed in live prasangas?",
                    answer = "The role is recognized through ${vesha.characteristics.joinToString(", ").lowercase()} and appears in ${vesha.performanceContexts.joinToString(", ").lowercase()}."
                )
            )
            add(
                InfoSlide(
                    question = "What is history behind this performance tradition?",
                    answer = vesha.historyNote
                )
            )
            add(
                InfoSlide(
                    question = "How did this form evolve in modern melas?",
                    answer = vesha.evolutionNote
                )
            )
            add(
                InfoSlide(
                    question = "How do prasanga and literature shape this role?",
                    answer = "${vesha.prasangaNote} ${vesha.literatureNote}"
                )
            )
            add(
                InfoSlide(
                    question = "Which instruments and music genre define experience?",
                    answer = "${vesha.instrumentNote} ${vesha.musicGenreNote}"
                )
            )
            add(
                InfoSlide(
                    question = "Which melas are famous for presenting this vesha?",
                    answer = vesha.famousMelas.joinToString(", ")
                )
            )
        }
    }
    val veshaPagerState = rememberPagerState(pageCount = { veshaSlides.size })
    val artistPagerState = rememberPagerState(pageCount = { vesha.artists.size.coerceAtLeast(1) })
    val currentVeshaPage = veshaPagerState.currentPage.coerceIn(0, veshaSlides.lastIndex.coerceAtLeast(0))
    val currentArtistPage = artistPagerState.currentPage.coerceIn(0, vesha.artists.lastIndex.coerceAtLeast(0))

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = 10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 2.dp,
                shadowElevation = 0.dp,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                    Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                        Text(
                            text = "Artist encyclopedia",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontSize = 12.sp,
                                letterSpacing = 0.6.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = vesha.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 18.sp,
                                lineHeight = 24.sp,
                                letterSpacing = 0.1.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "${currentVeshaPage + 1}/${veshaSlides.size}",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontSize = 12.sp,
                            letterSpacing = 0.6.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
            }
        }

        item {
            HorizontalPager(
                state = veshaPagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(UnifiedSlideHeightDp.dp),
                beyondBoundsPageCount = 1
            ) { page ->
                VeshaSlidePage(
                    slide = veshaSlides[page],
                    accentSeed = page,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        item {
            SlidePagerDots(
                pageCount = veshaSlides.size,
                currentPage = currentVeshaPage,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 0.dp, bottom = 0.dp)
            )
        }

        if (vesha.artists.isNotEmpty()) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 0.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Artist Details Showcase",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.25.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.75f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${currentArtistPage + 1}/${vesha.artists.size}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                HorizontalPager(
                    state = artistPagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(UnifiedSlideHeightDp.dp),
                    beyondBoundsPageCount = 1,
                    key = { page -> page }
                ) { page ->
                    ArtistDetailSection(
                        artist = vesha.artists[page],
                        accentSeed = page + 20,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            item {
                SlidePagerDots(
                    pageCount = vesha.artists.size,
                    currentPage = currentArtistPage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 0.dp, bottom = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun SlidePagerDots(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val active = index == currentPage
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(if (active) 9.dp else 7.dp)
                    .clip(CircleShape)
                    .background(
                        if (active) MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                        else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)
                    )
            )
        }
    }
}

@Composable
private fun VeshaSlidePage(
    slide: InfoSlide,
    accentSeed: Int,
    modifier: Modifier = Modifier,
    topDarkFraction: Float = 0.42f,
    answerSectionSpacing: Dp = 8.dp,
    answerLineHeight: TextUnit = 22.sp
) {
    val scheme = MaterialTheme.colorScheme
    val accentShift = (accentSeed % 3) * 0.04f
    val topBrush = remember(accentSeed, scheme) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF1C1B22).copy(alpha = 0.92f + accentShift),
                Color(0xFF2A2833).copy(alpha = 0.88f + accentShift),
                scheme.surfaceContainerHighest.copy(alpha = 0.35f)
            )
        )
    }
    val scroll = rememberScrollState()

    BoxWithConstraints(modifier = modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
        val topFraction = topDarkFraction.coerceIn(0.32f, 0.58f)
        val topH = maxHeight * topFraction
        val bottomH = maxHeight * (1f - topFraction)

        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(containerColor = scheme.surface),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.15.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.58f)
            )
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(topH)
                        .background(topBrush)
                        .padding(horizontal = 22.dp, vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = slide.question,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = 19.sp,
                            lineHeight = 26.sp,
                            letterSpacing = 0.25.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeightStyle = LineHeightStyle(
                                alignment = LineHeightStyle.Alignment.Center,
                                trim = LineHeightStyle.Trim.Both
                            )
                        ),
                        color = Color(0xFFF2F0F7),
                        textAlign = TextAlign.Center
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(bottomH)
                        .background(scheme.surfaceContainerLow.copy(alpha = 0.55f))
                        .verticalScroll(scroll)
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(answerSectionSpacing)
                ) {
                    Text(
                        text = slide.answer,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 15.sp,
                            lineHeight = answerLineHeight,
                            letterSpacing = 0.08.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Normal
                        ),
                        color = scheme.onSurface,
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
    }
}

@Composable
private fun ArtistDetailSection(
    artist: VeshaArtistProfile,
    accentSeed: Int,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val accentShift = (accentSeed % 3) * 0.04f
    val topBrush = remember(accentSeed, scheme) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF1C1B22).copy(alpha = 0.92f + accentShift),
                Color(0xFF2A2833).copy(alpha = 0.88f + accentShift),
                scheme.surfaceContainerHighest.copy(alpha = 0.35f)
            )
        )
    }
    val scroll = rememberScrollState()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = scheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.15.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.58f)
        )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(178.dp)
                    .padding(start = 10.dp, top = 10.dp, end = 10.dp)
                    .background(topBrush)
                    .clip(RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                ProfessionalDpPlaceholder(displayName = artist.name.trim())
            }
            Text(
                text = artist.name.trim(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    letterSpacing = 0.2.sp
                ),
                color = scheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .clip(RoundedCornerShape(16.dp)),
                color = scheme.surfaceContainerLow.copy(alpha = 0.55f),
                tonalElevation = 0.dp,
                shadowElevation = 0.dp
            ) {
                Text(
                    text = artist.artistDetailsParagraph(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scroll)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 16.sp,
                        lineHeight = 25.sp,
                        letterSpacing = 0.08.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Normal
                    ),
                    color = scheme.onSurface.copy(alpha = 0.92f),
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

private val ArtistProfileDpSize = 140.dp

@Composable
private fun ProfessionalDpPlaceholder(displayName: String) {
    val ring = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
    val base = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.96f)
    val overlay = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    Box(
        modifier = Modifier
            .size(ArtistProfileDpSize)
            .clip(CircleShape)
            .background(Brush.radialGradient(colors = listOf(overlay, base), radius = 380f))
            .border(width = 2.dp, color = ring, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // subtle "bust" plate behind icon to feel less empty
        Box(
            modifier = Modifier
                .size(86.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.55f))
        )
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = displayName,
            modifier = Modifier.size(54.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.42f)
        )
    }
}

@Composable
private fun ArtistPhotoInSlide(
    artist: VeshaArtistProfile,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        ProfessionalDpPlaceholder(displayName = artist.name.trim())
    }
}

@Composable
private fun ArtistCombinedSlidePage(
    artist: VeshaArtistProfile,
    accentSeed: Int,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val accentShift = (accentSeed % 3) * 0.04f
    val topBrush = remember(accentSeed, scheme) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF1C1B22).copy(alpha = 0.92f + accentShift),
                Color(0xFF2A2833).copy(alpha = 0.88f + accentShift),
                scheme.surfaceContainerHighest.copy(alpha = 0.35f)
            )
        )
    }
    val scroll = rememberScrollState()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 6.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = scheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.15.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.58f)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(bottom = 8.dp)
        ) {
            ArtistPhotoInSlide(
                artist = artist,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(178.dp)
                    .padding(start = 10.dp, top = 10.dp, end = 10.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
            Text(
                text = artist.name.trim(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    letterSpacing = 0.2.sp
                ),
                color = scheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .clip(RoundedCornerShape(16.dp)),
                color = scheme.surfaceContainerLow.copy(alpha = 0.55f),
                tonalElevation = 0.dp,
                shadowElevation = 0.dp
            ) {
                Text(
                    text = artist.artistDetailsParagraph(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scroll)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 16.sp,
                        lineHeight = 25.sp,
                        letterSpacing = 0.08.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Normal
                    ),
                    color = scheme.onSurface.copy(alpha = 0.92f),
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

@Composable
private fun remember(accentSeed: Int, scheme: androidx.compose.material3.ColorScheme): androidx.compose.ui.graphics.Brush {
    val accentColors = listOf(
        Color(0xFF1C1B22),
        Color(0xFF2A2833),
        Color(0xFF3A4856)
    )
    val accentColor = accentColors[accentSeed % accentColors.size]
    val baseColors = listOf(
        scheme.surfaceContainerHighest,
        scheme.surfaceContainerLow,
        scheme.surfaceContainer
    )
    val baseColor = baseColors[accentSeed % baseColors.size]
    return Brush.verticalGradient(
        colors = listOf(
            accentColor.copy(alpha = 0.92f),
            accentColor.copy(alpha = 0.88f),
            baseColor.copy(alpha = 0.35f)
        )
    )
}
