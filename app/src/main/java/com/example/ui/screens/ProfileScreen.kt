package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.data.local.entity.UserEntity
import com.example.model.ZodiacSign
import com.example.ui.screens.components.MyanmarOrnamentalDivider
import com.example.ui.theme.MyanmarRedAccent
import com.example.ui.theme.RomanticRose
import com.example.ui.theme.WarmGold
import com.example.ui.viewmodel.ProfileViewModel
import com.example.util.CoupleCalculations
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
  viewModel: ProfileViewModel,
  onNavigateToSettings: () -> Unit,
  modifier: Modifier = Modifier
) {
  val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
  val partnerUser by viewModel.partnerUser.collectAsStateWithLifecycle()
  val couple by viewModel.couple.collectAsStateWithLifecycle()

  var showEditProfileDialog by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "Couple Profiles",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              text = "နှစ်ယောက်ကိုယ်ရေးအချက်အလက်",
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
              color = MyanmarRedAccent
            )
          }
        },
        actions = {
          IconButton(onClick = onNavigateToSettings) {
            Icon(Icons.Default.Settings, contentDescription = "Settings")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.background
        )
      )
    },
    containerColor = MaterialTheme.colorScheme.background,
    modifier = modifier.fillMaxSize()
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      item {
        // My Profile Card
        ProfileCard(
          title = "My Profile (ကျွန်ုပ်၏ ကိုယ်ရေးအကျဉ်း)",
          user = currentUser,
          isCurrentUser = true,
          onEdit = { showEditProfileDialog = true }
        )
      }

      item {
        MyanmarOrnamentalDivider()
      }

      item {
        // Partner Profile Card
        if (couple?.isConnected == true && partnerUser != null) {
          ProfileCard(
            title = "Partner Profile (ချစ်သူ၏ ကိုယ်ရေးအကျဉ်း)",
            user = partnerUser,
            isCurrentUser = false,
            onEdit = null
          )
        } else {
          // Unconnected Partner Card placeholder
          Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Box(
                modifier = Modifier
                  .size(56.dp)
                  .clip(CircleShape)
                  .background(WarmGold.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = WarmGold, modifier = Modifier.size(28.dp))
              }
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = "Partner Not Connected Yet",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Connect with your partner via Couple Code in Settings or the Our Love tab to see their shared profile here!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(80.dp))
      }
    }
  }

  // Edit Profile Dialog
  if (showEditProfileDialog) {
    EditProfileDialog(
      initialUser = currentUser,
      onDismiss = { showEditProfileDialog = false },
      onSave = { name, bday, bio, photo ->
        viewModel.saveProfile(name, bday, bio, photo)
      }
    )
  }
}

@Composable
private fun ProfileCard(
  title: String,
  user: UserEntity?,
  isCurrentUser: Boolean,
  onEdit: (() -> Unit)?
) {
  val bday = user?.birthDate ?: "2000-01-01"
  val zodiac = try {
    val parsed = LocalDate.parse(bday)
    CoupleCalculations.calculateZodiacSign(parsed.monthValue, parsed.dayOfMonth)
  } catch (_: Exception) {
    ZodiacSign.ARIES
  }

  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (onEdit != null) {
          IconButton(onClick = onEdit, modifier = Modifier.testTag("button_edit_profile")) {
            Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = MyanmarRedAccent, modifier = Modifier.size(18.dp))
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Photo
        Box(
          modifier = Modifier
            .size(76.dp)
            .clip(CircleShape)
            .background(
              Brush.linearGradient(
                colors = listOf(RomanticRose.copy(alpha = 0.2f), WarmGold.copy(alpha = 0.2f))
              )
            )
            .border(2.dp, if (isCurrentUser) MyanmarRedAccent else WarmGold, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          if (user?.photoUri != null) {
            Image(
              painter = rememberAsyncImagePainter(user.photoUri),
              contentDescription = "Profile Photo",
              modifier = Modifier.fillMaxSize().clip(CircleShape),
              contentScale = ContentScale.Crop
            )
          } else {
            Icon(
              Icons.Default.Person,
              contentDescription = null,
              tint = if (isCurrentUser) MyanmarRedAccent else WarmGold,
              modifier = Modifier.size(40.dp)
            )
          }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = user?.displayName ?: if (isCurrentUser) "Me" else "My Partner",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Born: ${user?.birthDate ?: "Not set"}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          if (!user?.bio.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "\"${user?.bio}\"",
              style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Western Zodiac Box
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = WarmGold.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(1.dp, WarmGold.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(WarmGold.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = zodiac.symbol,
              style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "${zodiac.signName} (${zodiac.burmeseName})",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = WarmGold
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = zodiac.dateRange,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = zodiac.romanticTrait,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }
  }
}

@Composable
fun EditProfileDialog(
  initialUser: UserEntity?,
  onDismiss: () -> Unit,
  onSave: (name: String, birthDate: String, bio: String, photoUri: String?) -> Unit
) {
  var displayName by remember { mutableStateOf(initialUser?.displayName ?: "") }
  var birthDate by remember { mutableStateOf(initialUser?.birthDate ?: "2000-01-01") }
  var bio by remember { mutableStateOf(initialUser?.bio ?: "") }
  var photoUri by remember { mutableStateOf(initialUser?.photoUri) }
  var error by remember { mutableStateOf<String?>(null) }

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
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        Text(
          text = "Edit Profile",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Photo picker row
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          Box(
            modifier = Modifier
              .size(60.dp)
              .clip(CircleShape)
              .background(WarmGold.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            if (photoUri != null) {
              Image(
                painter = rememberAsyncImagePainter(photoUri),
                contentDescription = null,
                modifier = Modifier.fillMaxSize().clip(CircleShape),
                contentScale = ContentScale.Crop
              )
            } else {
              Icon(Icons.Default.Person, contentDescription = null, tint = WarmGold)
            }
          }

          Spacer(modifier = Modifier.width(14.dp))

          OutlinedButton(
            onClick = {
              photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
              )
            }
          ) {
            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Choose Photo")
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
          value = displayName,
          onValueChange = { displayName = it },
          label = { Text("Display Name (နာမည်)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("input_profile_name")
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = birthDate,
          onValueChange = {
            birthDate = it
            error = null
          },
          label = { Text("Date of Birth (YYYY-MM-DD)") },
          placeholder = { Text("1999-11-12") },
          singleLine = true,
          isError = error != null,
          supportingText = error?.let { { Text(it) } },
          modifier = Modifier.fillMaxWidth().testTag("input_profile_birthdate")
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = bio,
          onValueChange = { bio = it },
          label = { Text("Bio / Romantic Note") },
          placeholder = { Text("A few words about you or your dreams...") },
          minLines = 2,
          maxLines = 4,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          OutlinedButton(onClick = onDismiss) {
            Text("Cancel")
          }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = {
              try {
                LocalDate.parse(birthDate)
              } catch (_: Exception) {
                error = "Invalid birth date format. Use YYYY-MM-DD"
                return@Button
              }
              onSave(displayName, birthDate, bio, photoUri)
              onDismiss()
            },
            colors = ButtonDefaults.buttonColors(containerColor = MyanmarRedAccent),
            modifier = Modifier.testTag("button_save_profile")
          ) {
            Text("Save Profile", color = Color.White)
          }
        }
      }
    }
  }
}
