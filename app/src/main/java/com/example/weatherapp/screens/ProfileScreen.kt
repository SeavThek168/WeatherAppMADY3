package com.example.weatherapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.auth.AuthViewModel

// CamWeather theme colors
private val PurplePrimary = Color(0xFF667eea)
private val PurpleSecondary = Color(0xFF764ba2)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToSettings: () -> Unit = {},
    onNavigateToFAQ: () -> Unit = {},
    onNavigateToLegal: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = viewModel()
) {
    val uiState by authViewModel.uiState.collectAsState()
    val user = uiState.user
    val isLoggedIn = uiState.isLoggedIn
    
    var showEditNameDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showLogoutConfirm by remember { mutableStateOf(false) }
    
    // Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            authViewModel.clearError()
        }
    }
    
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            authViewModel.clearSuccessMessage()
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
                .verticalScroll(rememberScrollState())
                .padding(padding)
        ) {
            // Header with gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(PurplePrimary, PurpleSecondary)
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // App Logo/Title
                    Text(
                        text = "🇰🇭",
                        fontSize = 32.sp
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "CamWeather",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Profile Avatar
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!isLoggedIn) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Guest",
                                modifier = Modifier.size(48.dp),
                                tint = PurplePrimary
                            )
                        } else {
                            Text(
                                text = user?.displayName?.firstOrNull()?.uppercase() ?: user?.email?.firstOrNull()?.uppercase() ?: "U",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = PurplePrimary
                            )
                        }
                    }
                }
            }
            
            // User Info Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-20).dp)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isLoggedIn) {
                        Text(
                            text = user?.displayName ?: "User",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = user?.email ?: "",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AssistChip(
                                onClick = { showEditNameDialog = true },
                                label = { Text("Edit Name", fontSize = 12.sp) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Edit, 
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )
                            AssistChip(
                                onClick = { showChangePasswordDialog = true },
                                label = { Text("Change Password", fontSize = 12.sp) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Lock, 
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )
                        }
                    } else {
                        Text(
                            text = "Guest User",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Sign in to sync your data",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onNavigateToLogin,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                        ) {
                            Icon(Icons.Default.Login, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sign In / Sign Up")
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Menu Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    ProfileMenuItem(
                        icon = Icons.Default.Settings,
                        title = "Settings",
                        subtitle = "App preferences",
                        onClick = onNavigateToSettings
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileMenuItem(
                        icon = Icons.Default.QuestionAnswer,
                        title = "FAQ",
                        subtitle = "Frequently asked questions",
                        onClick = onNavigateToFAQ
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileMenuItem(
                        icon = Icons.Default.Description,
                        title = "Legal",
                        subtitle = "Terms and privacy",
                        onClick = onNavigateToLegal
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileMenuItem(
                        icon = Icons.Default.Info,
                        title = "About",
                        subtitle = "App information",
                        onClick = onNavigateToAbout
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Logout Button (only if logged in)
            if (isLoggedIn) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showLogoutConfirm = true }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Log Out",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Red
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
    
    // Edit Name Dialog
    if (showEditNameDialog) {
        var newName by remember { mutableStateOf(user?.displayName ?: "") }
        
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = { Text("Edit Display Name") },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Display Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank()) {
                            authViewModel.updateDisplayName(newName)
                            showEditNameDialog = false
                        }
                    },
                    enabled = newName.isNotBlank() && !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White
                        )
                    } else {
                        Text("Save")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Change Password Dialog
    if (showChangePasswordDialog) {
        var newPassword by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        
        AlertDialog(
            onDismissRequest = { showChangePasswordDialog = false },
            title = { Text("Change Password") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        isError = confirmPassword.isNotEmpty() && newPassword != confirmPassword
                    )
                    if (confirmPassword.isNotEmpty() && newPassword != confirmPassword) {
                        Text(
                            text = "Passwords don't match",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Password must be at least 6 characters",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPassword == confirmPassword && newPassword.length >= 6) {
                            authViewModel.updatePassword(newPassword)
                            showChangePasswordDialog = false
                        }
                    },
                    enabled = newPassword == confirmPassword && newPassword.length >= 6 && !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White
                        )
                    } else {
                        Text("Update")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangePasswordDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Logout Confirmation Dialog
    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text("Log Out") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                Button(
                    onClick = {
                        authViewModel.signOut()
                        showLogoutConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Log Out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(PurplePrimary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PurplePrimary,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
        
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.Gray
        )
    }
}
