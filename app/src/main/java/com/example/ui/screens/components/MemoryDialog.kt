package com.example.ui.screens.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.example.data.local.entity.MemoryEventEntity
import com.example.model.EventType
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.MyanmarRedAccent
import java.time.LocalDate

@Composable
fun MemoryDialog(
  initialEvent: MemoryEventEntity? = null,
  onDismiss: () -> Unit,
  onSave: (
    id: String?,
    title: String,
    eventType: String,
    eventDate: String,
    description: String,
    photoUri: String?,
    isRecurringYearly: Boolean
  ) -> Unit,
  onDelete: ((String) -> Unit)? = null
) {
  var selectedType by remember {
    mutableStateOf(
      initialEvent?.let { EventType.fromName(it.eventType) } ?: EventType.ANNIVERSARY
    )
  }

  var title by remember {
    mutableStateOf(initialEvent?.title ?: selectedType.defaultTitle)
  }

  var eventDate by remember {
    mutableStateOf(initialEvent?.eventDate ?: LocalDate.now().toString())
  }

  var description by remember {
    mutableStateOf(initialEvent?.description ?: "")
  }

  var photoUri by remember {
    mutableStateOf(initialEvent?.photoUri)
  }

  var isRecurringYearly by remember {
    mutableStateOf(initialEvent?.isRecurringYearly ?: true)
  }

  var errorMessage by remember { mutableStateOf<String?>(null) }
  var showDeleteConfirmation by remember { mutableStateOf(false) }

  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      photoUri = uri.toString()
    }
  }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 16.dp)
        .testTag("dialog_memory_event")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (initialEvent == null) "New Memory Milestone" else "Edit Memory",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
          )
          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Event Type selection
        Text(
          text = "Event Type",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(EventType.entries.toTypedArray()) { type ->
            FilterChip(
              selected = selectedType == type,
              onClick = {
                selectedType = type
                if (title.isBlank() || title == selectedType.defaultTitle) {
                  title = type.defaultTitle
                }
              },
              label = { Text(type.displayName) }
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Event Title (အမှတ်တရ ခေါင်းစဉ်)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("input_memory_title")
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = eventDate,
          onValueChange = {
            eventDate = it
            errorMessage = null
          },
          label = { Text("Event Date (YYYY-MM-DD)") },
          placeholder = { Text("2025-08-15") },
          singleLine = true,
          isError = errorMessage != null,
          supportingText = errorMessage?.let { { Text(it, color = ExpenseRed) } },
          modifier = Modifier.fillMaxWidth().testTag("input_memory_date")
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("Story / Note (အမှတ်တရ မှတ်စု)") },
          placeholder = { Text("Write about your special moment together...") },
          minLines = 3,
          maxLines = 5,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Photo Picker Row
        Text(
          text = "Memory Photo",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          if (photoUri != null) {
            Image(
              painter = rememberAsyncImagePainter(photoUri),
              contentDescription = "Selected photo",
              modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(12.dp)),
              contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
          }

          OutlinedButton(
            onClick = {
              photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
              )
            },
            modifier = Modifier.testTag("button_pick_memory_photo")
          ) {
            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(if (photoUri != null) "Change Photo" else "Select Photo")
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Recurring yearly toggle
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .fillMaxWidth()
            .clickable { isRecurringYearly = !isRecurringYearly }
        ) {
          Checkbox(
            checked = isRecurringYearly,
            onCheckedChange = { isRecurringYearly = it }
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Remind every year (နှစ်စဉ် အထိမ်းအမှတ်)",
            style = MaterialTheme.typography.bodyMedium
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (initialEvent != null && onDelete != null) {
            IconButton(
              onClick = { showDeleteConfirmation = true },
              modifier = Modifier.testTag("button_delete_memory")
            ) {
              Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ExpenseRed)
            }
            Spacer(modifier = Modifier.weight(1f))
          }

          OutlinedButton(onClick = onDismiss) {
            Text("Cancel")
          }

          Spacer(modifier = Modifier.width(8.dp))

          Button(
            onClick = {
              if (title.isBlank()) {
                errorMessage = "Please enter an event title"
                return@Button
              }
              try {
                LocalDate.parse(eventDate)
              } catch (_: Exception) {
                errorMessage = "Invalid date format. Use YYYY-MM-DD"
                return@Button
              }

              onSave(
                initialEvent?.id,
                title,
                selectedType.name,
                eventDate,
                description,
                photoUri,
                isRecurringYearly
              )
              onDismiss()
            },
            colors = ButtonDefaults.buttonColors(containerColor = MyanmarRedAccent),
            modifier = Modifier.testTag("button_save_memory")
          ) {
            Text("Save Memory", color = Color.White)
          }
        }
      }
    }
  }

  if (showDeleteConfirmation && initialEvent != null && onDelete != null) {
    AlertDialog(
      onDismissRequest = { showDeleteConfirmation = false },
      title = { Text("Delete Memory?") },
      text = { Text("Are you sure you want to delete this memory milestone?") },
      confirmButton = {
        Button(
          onClick = {
            onDelete(initialEvent.id)
            showDeleteConfirmation = false
            onDismiss()
          },
          colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
        ) {
          Text("Delete", color = Color.White)
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteConfirmation = false }) {
          Text("Cancel")
        }
      }
    )
  }
}
