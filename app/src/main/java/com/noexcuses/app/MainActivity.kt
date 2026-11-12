package com.noexcuses.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Black = Color(0xFF101010)
private val Panel = Color(0xFF202020)
private val Gold = Color(0xFFD6B56D)
private val Muted = Color(0xFFAAAAAA)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("no_excuses", MODE_PRIVATE)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(
                background = Black, surface = Panel, primary = Gold,
                onBackground = Color.White, onSurface = Color.White
            )) {
                AppHome(
                    saved = prefs.getStringSet("done", emptySet()) ?: emptySet(),
                    onSave = { prefs.edit().putStringSet("done", it).apply() }
                )
            }
        }
    }
}

@Composable
fun AppHome(saved: Set<String>, onSave: (Set<String>) -> Unit) {
    val context = LocalContext.current
    val tasks = listOf(
        "Wake up on time" to "6:00 AM",
        "Drink water & get ready" to "6:15 AM",
        "Exercise / walk" to "6:30 AM",
        "Read or study" to "7:15 AM",
        "Plan the day's priorities" to "8:00 AM",
        "Review the day & sleep on time" to "10:30 PM"
    )
    var completed by remember { mutableStateOf(saved.toMutableSet()) }
    var tab by remember { mutableIntStateOf(0) }
    var customTask by remember { mutableStateOf("") }
    val progress = tasks.count { completed.contains(it.first) } * 100 / tasks.size

    Scaffold(
        containerColor = Black,
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF181818), contentColor = Gold) {
                NavigationBarItem(selected = tab == 0, onClick = { tab = 0 },
                    icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") })
                NavigationBarItem(selected = tab == 1, onClick = { tab = 1 },
                    icon = { Icon(Icons.Default.CheckCircle, null) }, label = { Text("Routine") })
                NavigationBarItem(selected = tab == 2, onClick = { tab = 2 },
                    icon = { Icon(Icons.Default.PlayCircle, null) }, label = { Text("Motivation") })
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(Black).padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("NO EXCUSES.", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Black)
                        Text("YOUR LIFE. YOUR RULES.", color = Gold, fontSize = 10.sp, letterSpacing = 2.sp)
                    }
                    Icon(Icons.Default.DarkMode, null, tint = Gold, modifier = Modifier.size(28.dp))
                }
            }
            if (tab == 0) {
                item {
                    Column {
                        Text("Good day, Gaurav.", color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.Bold)
                        Text("One focused day can change your direction.", color = Muted)
                    }
                }
                item { ProgressCard(progress) }
                item { QuoteCard("Don't wait for motivation. Build discipline.") }
                item {
                    Text("YOUR DAILY SYSTEM", color = Gold, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MiniCard("☀", "Morning")
                        MiniCard("◎", "Focus")
                        MiniCard("↗", "Goals")
                    }
                }
                item {
                    Button(onClick = { tab = 1 }, colors = ButtonDefaults.buttonColors(containerColor = Gold),
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                        Text("OPEN MY ROUTINE", color = Black, fontWeight = FontWeight.Bold)
                    }
                }
            } else if (tab == 1) {
                item {
                    Text("DAILY ROUTINE", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    Text("Small actions. Repeated daily.", color = Muted)
                }
                items(tasks.size) { index ->
                    val (name, time) = tasks[index]
                    TaskRow(name, time, completed.contains(name)) {
                        completed = completed.toMutableSet().also {
                            if (it.contains(name)) it.remove(name) else it.add(name)
                        }
                        onSave(completed)
                    }
                }
                item {
                    OutlinedTextField(value = customTask, onValueChange = { customTask = it },
                        label = { Text("Add a personal goal") }, modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Gold, focusedLabelColor = Gold,
                            unfocusedBorderColor = Muted, cursorColor = Gold
                        ))
                }
                item {
                    Text("Custom task editing will be added in a future update.",
                        color = Muted, fontSize = 12.sp)
                }
            } else {
                item {
                    Text("WHEN YOU FEEL LOW", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                    Text("You don't need a perfect day. Just take the next step.", color = Muted)
                }
                item { QuoteCard("You are allowed to rest. You are not allowed to give up on yourself.") }
                item {
                    VideoCard("5-minute motivation", "Open motivational videos on YouTube") {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=5+minute+motivational+videos")))
                    }
                }
                item {
                    VideoCard("Discipline & consistency", "Watch a short motivational talk") {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=discipline+consistency+motivation")))
                    }
                }
                item {
                    Text("Video links need an internet connection. YouTube opens separately.",
                        color = Muted, fontSize = 12.sp)
                }
            }
            item {
                Text("DISCIPLINE OVER MOOD  •  ONE DAY AT A TIME", color = Muted,
                    fontSize = 10.sp, letterSpacing = 1.sp, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
            }
        }
    }
}

@Composable
fun ProgressCard(progress: Int) {
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.padding(18.dp)) {
            Text("TODAY'S PROGRESS", color = Gold, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
            Text("$progress%", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Bold)
            LinearProgressIndicator(progress = { progress / 100f }, modifier = Modifier.fillMaxWidth().height(7.dp),
                color = Gold, trackColor = Color(0xFF414141))
            Text("Keep showing up.", color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
fun QuoteCard(quote: String) {
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF2A251B)),
        shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.padding(18.dp)) {
            Text("✦  DAILY REMINDER", color = Gold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text("“$quote”", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
fun TaskRow(name: String, time: String, checked: Boolean, onClick: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = checked, onCheckedChange = { onClick() },
                colors = CheckboxDefaults.colors(checkedColor = Gold, checkmarkColor = Black))
            Column(Modifier.weight(1f)) {
                Text(name, color = if (checked) Muted else Color.White, fontWeight = FontWeight.Medium)
                Text(time, color = Muted, fontSize = 12.sp)
            }
            if (checked) Icon(Icons.Default.CheckCircle, null, tint = Gold)
        }
    }
}

@Composable
fun RowScope.MiniCard(symbol: String, label: String) {
    Card(Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = Panel),
        shape = RoundedCornerShape(14.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text(symbol, color = Gold, fontSize = 23.sp)
            Text(label, color = Color.White, fontSize = 12.sp)
        }
    }
}

@Composable
fun VideoCard(title: String, subtitle: String, onClick: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.PlayCircleFilled, null, tint = Gold, modifier = Modifier.size(34.dp))
            Column(Modifier.weight(1f).padding(start = 12.dp)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold)
                Text(subtitle, color = Muted, fontSize = 12.sp)
            }
            Icon(Icons.Default.OpenInNew, null, tint = Gold)
        }
    }
}
