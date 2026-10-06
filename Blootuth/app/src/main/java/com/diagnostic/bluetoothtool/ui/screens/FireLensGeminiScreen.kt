package com.diagnostic.bluetoothtool.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diagnostic.bluetoothtool.model.AudioPresetType
import com.diagnostic.bluetoothtool.model.ClassroomAudioState
import com.diagnostic.bluetoothtool.model.GeminiChatMessage
import com.diagnostic.bluetoothtool.model.TokenUsageMetrics
import com.diagnostic.bluetoothtool.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FireLensGeminiScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val messages by viewModel.geminiMessages.collectAsState()
    val isGenerating by viewModel.isGeminiStreaming.collectAsState()
    val activeMetrics by viewModel.activeTokenMetrics.collectAsState()
    val audioState by viewModel.classroomAudioState.collectAsState()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val quickPrompts = listOf(
        "🎓 Summarize this lecture topic",
        "📐 Explain formula step-by-step",
        "👓 Inspect Fire-Lens hardware & battery",
        "⚡ Explain Zephyr RTOS & DSP pipeline",
        "📝 Generate 3 quick exam review questions"
    )

    LaunchedEffect(messages.size, isGenerating) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "GEMINI AI ASSISTANT",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 1.2.sp
                                ),
                                color = Color(0xFF80D8FF)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF00E676).copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E676))
                            ) {
                                Text(
                                    text = "FIRE-LENS LINKED",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00E676),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Classroom Audio & Live Token Analytics Engine",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF80CBC4),
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF00E5FF)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.clearGeminiChat() }) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear Chat",
                            tint = Color(0xFFFF5252)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0A1118)
                )
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .background(Color(0xFF070D12))
                    .padding(8.dp)
            ) {
                // Quick prompt suggestions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickPrompts.forEach { prompt ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF13222F),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00BCD4).copy(alpha = 0.4f)),
                            modifier = Modifier.clickable {
                                viewModel.askGeminiWithTokens(prompt)
                            }
                        ) {
                            Text(
                                text = prompt,
                                fontSize = 11.sp,
                                color = Color(0xFF80DEEA),
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                // Question Input Field
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        placeholder = {
                            Text(
                                text = "Ask question or request classroom audio...",
                                color = Color(0xFF546E7A),
                                fontSize = 13.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00E5FF),
                            unfocusedBorderColor = Color(0xFF1E384D),
                            focusedContainerColor = Color(0xFF0B1721),
                            unfocusedContainerColor = Color(0xFF0B1721),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 3,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = {
                            if (inputText.isNotBlank()) {
                                viewModel.askGeminiWithTokens(inputText)
                                inputText = ""
                                focusManager.clearFocus()
                            }
                        })
                    )

                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                viewModel.askGeminiWithTokens(inputText)
                                inputText = ""
                                focusManager.clearFocus()
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF00E5FF), Color(0xFF7C4DFF))
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        enabled = !isGenerating
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = Color.Black
                            )
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFF050B10)
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Live Token Metrics HUD Card
                TokenMetricsHudCard(metrics = activeMetrics, isGenerating = isGenerating)
            }

            item {
                // Classroom Audio Pipe & Controls
                ClassroomAudioControlCard(
                    audioState = audioState,
                    onPlayStop = {
                        if (audioState.isPlaying) {
                            viewModel.stopClassroomAudio()
                        } else if (audioState.activeText.isNotBlank()) {
                            viewModel.playClassroomAudio(audioState.activeText)
                        }
                    },
                    onSpeedChange = { viewModel.setClassroomSpeechRate(it) },
                    onProfileSelect = { viewModel.selectAudioPreset(it) }
                )
            }

            items(messages) { message ->
                GeminiChatMessageCard(
                    message = message,
                    isAudioPlaying = audioState.isPlaying && audioState.activeText == message.content,
                    onPlayAudio = { viewModel.playClassroomAudio(message.content) },
                    onStopAudio = { viewModel.stopClassroomAudio() }
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun TokenMetricsHudCard(
    metrics: TokenUsageMetrics,
    isGenerating: Boolean
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1924)),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isGenerating) Color(0xFF00E5FF) else Color(0xFF1E384D)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LIVE TOKEN USAGE & METRICS",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color(0xFF00E5FF)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (isGenerating) Color(0xFF7C4DFF).copy(alpha = 0.3f) else Color(0xFF263238),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isGenerating) Color(0xFF7C4DFF) else Color(0xFF37474F)
                    )
                ) {
                    Text(
                        text = if (isGenerating) "STREAMING TOKENS" else "IDLE / READY",
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = if (isGenerating) Color(0xFFE040FB) else Color(0xFF90A4AE),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TokenMetricBadge(
                    label = "PROMPT TOKENS",
                    value = "${metrics.promptTokens}",
                    color = Color(0xFF80D8FF)
                )
                TokenMetricBadge(
                    label = "CANDIDATE TOKENS",
                    value = "${metrics.candidateTokens}",
                    color = Color(0xFF00E676)
                )
                TokenMetricBadge(
                    label = "TOTAL TOKENS",
                    value = "${metrics.totalTokens}",
                    color = Color(0xFFFFD700)
                )
                TokenMetricBadge(
                    label = "SPEED",
                    value = "${"%.1f".format(metrics.generationSpeedTokPerSec)} t/s",
                    color = Color(0xFFFF4081)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Model: ${metrics.modelName}",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF78909C)
                )
                Text(
                    text = "Latency: ${metrics.latencyMs} ms",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF00E5FF)
                )
            }
        }
    }
}

@Composable
fun TokenMetricBadge(label: String, value: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color(0xFF070F17), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(
            text = value,
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black,
            color = color
        )
        Text(
            text = label,
            fontSize = 8.sp,
            fontFamily = FontFamily.Monospace,
            color = Color(0xFF90A4AE)
        )
    }
}

@Composable
fun ClassroomAudioControlCard(
    audioState: ClassroomAudioState,
    onPlayStop: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    onProfileSelect: (AudioPresetType) -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0E1F2C)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00BCD4).copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = null,
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "CLASSROOM AUDIO STREAMER",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFF80D8FF)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (audioState.isPlaying) Color(0xFF00E676).copy(alpha = 0.25f) else Color(0xFF263238),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (audioState.isPlaying) Color(0xFF00E676) else Color(0xFF455A64)
                    )
                ) {
                    Text(
                        text = if (audioState.isPlaying) "🔊 PLAYING TO GLASS" else "BT A2DP READY",
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = if (audioState.isPlaying) Color(0xFF00E676) else Color(0xFF80CBC4),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Audio profile buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AudioPresetType.values().forEach { preset ->
                    val isSelected = audioState.activeProfile.type == preset
                    FilterChip(
                        selected = isSelected,
                        onClick = { onProfileSelect(preset) },
                        label = {
                            Text(
                                text = when (preset) {
                                    AudioPresetType.CLASSROOM_VOCAL -> "🎓 Vocal Clarity"
                                    AudioPresetType.STEALTH_WHISPER -> "🤫 Stealth Whisper"
                                    AudioPresetType.LECTURE_SPEED_2X -> "⚡ 1.75x Fast Review"
                                    AudioPresetType.HIGH_CLARITY_BOOST -> "📢 Gain Boost (+6dB)"
                                },
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF00E5FF).copy(alpha = 0.25f),
                            selectedLabelColor = Color(0xFF00E5FF),
                            containerColor = Color(0xFF132332),
                            labelColor = Color(0xFF90A4AE)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Playback Speed Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Speed: ${"%.2f".format(audioState.speechRate)}x",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF80D8FF),
                    modifier = Modifier.width(90.dp)
                )
                Slider(
                    value = audioState.speechRate,
                    onValueChange = onSpeedChange,
                    valueRange = 0.75f..2.25f,
                    steps = 5,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF00E5FF),
                        activeTrackColor = Color(0xFF00E5FF),
                        inactiveTrackColor = Color(0xFF263238)
                    )
                )
            }
        }
    }
}

@Composable
fun GeminiChatMessageCard(
    message: GeminiChatMessage,
    isAudioPlaying: Boolean,
    onPlayAudio: () -> Unit,
    onStopAudio: () -> Unit
) {
    val isUser = message.sender == "User"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(0.92f),
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
        ) {
            Card(
                shape = RoundedCornerShape(
                    topStart = 14.dp,
                    topEnd = 14.dp,
                    bottomStart = if (isUser) 14.dp else 2.dp,
                    bottomEnd = if (isUser) 2.dp else 14.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isUser) Color(0xFF004D40) else Color(0xFF112233)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isUser) Color(0xFF00BFA5) else Color(0xFF00BCD4).copy(alpha = 0.4f)
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = message.sender.uppercase(),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            color = if (isUser) Color(0xFF64FFDA) else Color(0xFF00E5FF)
                        )

                        if (!isUser && message.tokenMetrics != null) {
                            Text(
                                text = "${message.tokenMetrics.totalTokens} tokens • ${message.tokenMetrics.latencyMs}ms",
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF90A4AE)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = message.content,
                        fontSize = 13.sp,
                        color = Color.White,
                        lineHeight = 18.sp
                    )

                    if (!isUser) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    if (isAudioPlaying) onStopAudio() else onPlayAudio()
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isAudioPlaying) Color(0xFFFF5252) else Color(0xFF00B0FF)
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (isAudioPlaying) Icons.Default.Stop else Icons.Default.VolumeUp,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isAudioPlaying) "Stop Audio" else "Play in Class",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
