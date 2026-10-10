package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.R
import com.example.data.local.entity.MemoryEventEntity
import com.example.model.EventType
import com.example.ui.screens.components.CoupleCodeDialog
import com.example.ui.screens.components.MemoryDialog
import com.example.ui.screens.components.MyanmarOrnamentalDivider
import com.example.ui.theme.MyanmarRedAccent
import com.example.ui.theme.RomanticRose
import com.example.ui.theme.WarmGold
import com.example.ui.viewmodel.LoveViewModel
import com.example.util.CoupleCalculations
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoveScreen(
  viewModel: LoveViewModel,
  modifier: Modifier = Modifier
) {
  val couple by viewModel.couple.collectAsStateWithLifecycle()
  val partner by viewModel.partner.collectAsStateWithLifecycle()
  val events by viewModel.events.collectAsStateWithLifecycle()

  var showAddMemoryDialog by remember { mutableStateOf(false) }
  var selectedEventForEdit by remember { mutableStateOf<MemoryEventEntity?>(null) }
  var showCoupleCodeDialog by remember { mutableStateOf(false) }
  var showEditStartDateDialog by remember { mutableStateOf(false) }

  val startDateStr = couple?.relationshipStartDate ?: "2025-08-15"
  val parsedStartDate = try {
    LocalDate.parse(startDateStr, DateTimeFormatter.ISO_LOCAL_DATE)
  } catch (_: Exception) {
    CoupleCalculations.DEFAULT_RELATIONSHIP_START_DATE
  }

  val today = LocalDate.now()
  val daysTogether = CoupleCalculations.calculateDaysTogether(parsedStartDate, today)
  val (yearsTogether, monthsTogether) = CoupleCalculations.calculateYearsAndMonthsTogether(parsedStartDate, today)
  val nextAnniversary = CoupleCalculations.calculateNextAnniversary(parsedStartDate, today)
  val anniversaryCountdown = CoupleCalculations.calculateAnniversaryCountdown(parsedStartDate, today)

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "Our Love & Memories",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              text = "တို့နှစ်ယောက်ရဲ့ အချစ်နှင့် အမှတ်တရများ",
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
              color = MyanmarRedAccent
            )
          }
        },
        actions = {
          IconButton(onClick = { showCoupleCodeDialog = true }) {
            Icon(Icons.Default.Link, contentDescription = "Couple Connection", tint = MyanmarRedAccent)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.background
        )
      )
    },
    floatingActionButton = {
      FloatingActionButton(
        onClick = { showAddMemoryDialog = true },
        containerColor = MyanmarRedAccent,
        contentColor = Color.White,
        modifier = Modifier.testTag("fab_add_memory")
      ) {
        Icon(Icons.Default.Add, contentDescription = "Add Memory")
      }
    },
    containerColor = MaterialTheme.colorScheme.background,
    modifier = modifier.fillMaxSize()
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Hero Image & Love Counter Card
      item {
        Card(
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column {
            // Hero banner image
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
            ) {
              Image(
                painter = painterResource(id = R.drawable.couple_hero_banner),
                contentDescription = "Romantic couple under stars",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
              )
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .background(
                    Brush.verticalGradient(
                      colors = listOf(
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.65f)
                      )
                    )
                  )
              )
              Text(
                text = "Forever & Always 💕",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                ),
                modifier = Modifier
                  .align(Alignment.BottomStart)
                  .padding(14.dp)
              )
            }

            // Stats row
            Column(modifier = Modifier.padding(16.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = "DAYS TOGETHER",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      letterSpacing = 1.sp
                    ),
                    color = MyanmarRedAccent
                  )
                  Text(
                    text = "$daysTogether Days",
                    style = MaterialTheme.typography.headlineLarge.copy(
                      fontWeight = FontWeight.ExtraBold,
                      fontSize = 32.sp
                    ),
                    color = MyanmarRedAccent
                  )
                  if (yearsTogether > 0 || monthsTogether > 0) {
                    Text(
                      text = "$yearsTogether Years, $monthsTogether Months",
                      style = MaterialTheme.typography.labelMedium,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }

                Surface(
                  shape = RoundedCornerShape(16.dp),
                  color = WarmGold.copy(alpha = 0.12f),
                  modifier = Modifier.padding(4.dp)
                ) {
                  Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                    Text(
                      text = "NEXT ANNIVERSARY",
                      style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                      color = WarmGold
                    )
                    Text(
                      text = "$anniversaryCountdown Days",
                      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                      color = WarmGold
                    )
                    Text(
                      text = nextAnniversary.format(CoupleCalculations.dateFormatter),
                      style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              // Start date badge
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                  .clickable { showEditStartDateDialog = true }
                  .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    Icons.Default.DateRange,
                    contentDescription = null,
                    tint = MyanmarRedAccent,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Started: $startDateStr",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                  )
                }
                Icon(
                  Icons.Default.Edit,
                  contentDescription = "Edit start date",
                  tint = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(14.dp)
                )
              }
            }
          }
        }
      }

      // Couple Code status card
      item {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(WarmGold.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Link, contentDescription = null, tint = WarmGold, modifier = Modifier.size(18.dp))
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = if (couple?.isConnected == true) "Partner: ${partner?.displayName ?: "Connected"}" else "Couple Code: ${couple?.coupleCode ?: "DREAM-2025"}",
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                  text = if (couple?.isConnected == true) "Two hearts in sync 💕" else "Tap to view code or connect partner",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            OutlinedButton(
              onClick = { showCoupleCodeDialog = true },
              shape = RoundedCornerShape(10.dp)
            ) {
              Text(if (couple?.isConnected == true) "Manage" else "Connect")
            }
          }
        }
      }

      item {
        MyanmarOrnamentalDivider()
      }

      item {
        Text(
          text = "Our Milestones & Memories (${events.size})",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
      }

      if (events.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 30.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                Icons.Default.Favorite,
                contentDescription = null,
                tint = MyanmarRedAccent.copy(alpha = 0.5f),
                modifier = Modifier.size(40.dp)
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "No milestones added yet",
                style = MaterialTheme.typography.titleMedium
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Tap + to add your first date, anniversary, or special memory!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      } else {
        items(events, key = { it.id }) { event ->
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { selectedEventForEdit = event }
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              if (event.photoUri != null) {
                Image(
                  painter = rememberAsyncImagePainter(event.photoUri),
                  contentDescription = null,
                  modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(12.dp)),
                  contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
              } else {
                Box(
                  modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MyanmarRedAccent.copy(alpha = 0.12f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    Icons.Default.Favorite,
                    contentDescription = null,
                    tint = MyanmarRedAccent,
                    modifier = Modifier.size(20.dp)
                  )
                }
                Spacer(modifier = Modifier.width(12.dp))
              }

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = event.title,
                  style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = WarmGold,
                    modifier = Modifier.size(12.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = event.eventDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                  if (event.isRecurringYearly) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "• Annual",
                      style = MaterialTheme.typography.labelSmall,
                      color = MyanmarRedAccent
                    )
                  }
                }
                if (event.description.isNotBlank()) {
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                  )
                }
              }
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(80.dp))
      }
    }
  }

  // Add Memory Dialog
  if (showAddMemoryDialog) {
    MemoryDialog(
      onDismiss = { showAddMemoryDialog = false },
      onSave = { id, title, type, date, desc, photo, recurring ->
        viewModel.saveEvent(id, title, type, date, desc, photo, recurring)
      }
    )
  }

  // Edit Memory Dialog
  if (selectedEventForEdit != null) {
    MemoryDialog(
      initialEvent = selectedEventForEdit,
      onDismiss = { selectedEventForEdit = null },
      onSave = { id, title, type, date, desc, photo, recurring ->
        viewModel.saveEvent(id, title, type, date, desc, photo, recurring)
      },
      onDelete = { id ->
        viewModel.deleteEvent(id)
      }
    )
  }

  // Couple Code Dialog
  if (showCoupleCodeDialog) {
    CoupleCodeDialog(
      currentCode = couple?.coupleCode ?: "DREAM-2025",
      isConnected = couple?.isConnected ?: false,
      partnerName = partner?.displayName,
      onGenerateCode = viewModel::generateCoupleCode,
      onConnectCode = { code, onResult -> viewModel.connectWithCode(code, onResult) },
      onDisconnect = {},
      onDismiss = { showCoupleCodeDialog = false }
    )
  }

  // Edit Relationship Start Date Dialog
  if (showEditStartDateDialog) {
    var dateInput by remember { mutableStateOf(startDateStr) }
    var error by remember { mutableStateOf<String?>(null) }
    AlertDialog(
      onDismissRequest = { showEditStartDateDialog = false },
      title = { Text("Change Relationship Start Date") },
      text = {
        Column {
          Text(
            text = "Enter the day your love story officially began (YYYY-MM-DD):",
            style = MaterialTheme.typography.bodyMedium
          )
          Spacer(modifier = Modifier.height(10.dp))
          OutlinedTextField(
            value = dateInput,
            onValueChange = {
              dateInput = it
              error = null
            },
            label = { Text("Start Date (YYYY-MM-DD)") },
            placeholder = { Text("2025-08-15") },
            singleLine = true,
            isError = error != null,
            supportingText = error?.let { { Text(it) } }
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            try {
              LocalDate.parse(dateInput)
              viewModel.updateRelationshipStartDate(dateInput)
              showEditStartDateDialog = false
            } catch (_: Exception) {
              error = "Invalid date format. Use YYYY-MM-DD"
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = MyanmarRedAccent)
        ) {
          Text("Update", color = Color.White)
        }
      },
      dismissButton = {
        TextButton(onClick = { showEditStartDateDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}
