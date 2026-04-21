package ni.edu.uam.practicacomponentesbasicos

import android.net.Uri
import android.util.Patterns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ni.edu.uam.practicacomponentesbasicos.ui.theme.PracticaComponentesBasicosTheme

private val ScreenPadding = 24.dp
private val SectionSpacing = 24.dp
private val ItemSpacing = 16.dp
private val CardShape = RoundedCornerShape(24.dp)
private val FieldShape = RoundedCornerShape(16.dp)

@Composable
fun LoginScreen() {
    var isDarkTheme by rememberSaveable { mutableStateOf(false) }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val isEmailValid by remember(email) { derivedStateOf { isValidEmail(email) } }
    val isPasswordValid by remember(password) { derivedStateOf { password.length >= 6 } }
    val canLogin = isEmailValid && isPasswordValid

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri }
    )

    PracticaComponentesBasicosTheme(darkTheme = isDarkTheme) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { innerPadding ->
            LoginContent(
                modifier = Modifier.padding(innerPadding),
                isDarkTheme = isDarkTheme,
                onThemeChange = { isDarkTheme = it },
                imageUri = selectedImageUri,
                onPickImage = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                email = email,
                onEmailChange = { email = it },
                password = password,
                onPasswordChange = { password = it },
                isPasswordVisible = isPasswordVisible,
                onPasswordVisibilityToggle = { isPasswordVisible = !isPasswordVisible },
                showEmailError = email.isNotEmpty() && !isEmailValid,
                showPasswordError = password.isNotEmpty() && !isPasswordValid,
                canLogin = canLogin,
                isLoading = isLoading,
                onLoginClick = {
                    if (!canLogin || isLoading) return@LoginContent
                    scope.launch {
                        isLoading = true
                        delay(1200)
                        isLoading = false
                        snackbarHostState.showSnackbar("Inicio de sesión simulado")
                    }
                }
            )
        }
    }
}

private fun isValidEmail(email: String): Boolean {
    return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

@Composable
private fun LoginContent(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    imageUri: Uri?,
    onPickImage: () -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onPasswordVisibilityToggle: () -> Unit,
    showEmailError: Boolean,
    showPasswordError: Boolean,
    canLogin: Boolean,
    isLoading: Boolean,
    onLoginClick: () -> Unit
) {
    val topColor by animateColorAsState(
        targetValue = if (isDarkTheme) {
            MaterialTheme.colorScheme.surfaceContainerHigh
        } else {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        },
        animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
        label = "backgroundTop"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(topColor, MaterialTheme.colorScheme.background)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(ScreenPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SectionSpacing)
        ) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(450)) + slideInVertically(tween(450)) { it / 3 }
            ) {
                LoginHeader(isDarkTheme = isDarkTheme, onThemeChange = onThemeChange)
            }

            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(550, delayMillis = 80)) +
                    slideInVertically(tween(550, delayMillis = 80)) { it / 4 }
            ) {
                ProfileImageSelector(imageUri = imageUri, onPickImage = onPickImage)
            }

            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(650, delayMillis = 140)) +
                    slideInVertically(tween(650, delayMillis = 140)) { it / 4 }
            ) {
                LoginForm(
                    email = email,
                    onEmailChange = onEmailChange,
                    password = password,
                    onPasswordChange = onPasswordChange,
                    isPasswordVisible = isPasswordVisible,
                    onPasswordVisibilityToggle = onPasswordVisibilityToggle,
                    showEmailError = showEmailError,
                    showPasswordError = showPasswordError
                )
            }

            LoginFooter(
                canLogin = canLogin,
                isLoading = isLoading,
                onLoginClick = onLoginClick
            )
        }
    }
}

@Composable
fun LoginHeader(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = CardShape,
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ScreenPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Bienvenido",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Inicia sesión para continuar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            ThemeToggle(
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange
            )
        }
    }
}

@Composable
private fun ThemeToggle(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    FilledTonalIconButton(
        onClick = { onThemeChange(!isDarkTheme) }
    ) {
        Crossfade(targetState = isDarkTheme, label = "themeIcon") { dark ->
            Icon(
                imageVector = if (dark) Icons.Default.DarkMode else Icons.Default.LightMode,
                contentDescription = if (dark) "Tema oscuro activado" else "Tema claro activado"
            )
        }
    }
}

@Composable
fun ProfileImageSelector(
    imageUri: Uri?,
    onPickImage: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (imageUri != null) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.outlineVariant
        },
        animationSpec = tween(400),
        label = "avatarBorderColor"
    )
    val borderWidth by animateDpAsState(
        targetValue = if (imageUri != null) 3.dp else 2.dp,
        animationSpec = tween(400),
        label = "avatarBorderWidth"
    )
    val avatarScale by animateFloatAsState(
        targetValue = if (imageUri != null) 1f else 0.96f,
        animationSpec = tween(400),
        label = "avatarScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(ItemSpacing)
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .scale(avatarScale)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(borderWidth, borderColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Crossfade(targetState = imageUri, label = "avatarTransition") { uri ->
                if (uri != null) {
                    AsyncImage(
                        model = uri,
                        contentDescription = "Imagen de perfil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        TextButton(onClick = onPickImage) {
            Icon(
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (imageUri == null) "Agregar Foto" else "Cambiar Foto",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun LoginForm(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onPasswordVisibilityToggle: () -> Unit,
    showEmailError: Boolean,
    showPasswordError: Boolean
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = CardShape,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(ItemSpacing)
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Correo Electrónico") },
                placeholder = { Text("ejemplo@uam.edu.ni") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = FieldShape,
                singleLine = true,
                isError = showEmailError,
                supportingText = {
                    AnimatedVisibility(
                        visible = showEmailError,
                        enter = fadeIn(tween(220)),
                        exit = fadeOut(tween(180))
                    ) {
                        Text("Ingresa un correo válido")
                    }
                }
            )

            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = if (isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    IconButton(onClick = onPasswordVisibilityToggle) {
                        Icon(
                            imageVector = if (isPasswordVisible) {
                                Icons.Default.VisibilityOff
                            } else {
                                Icons.Default.Visibility
                            },
                            contentDescription = if (isPasswordVisible) {
                                "Ocultar contraseña"
                            } else {
                                "Mostrar contraseña"
                            }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = FieldShape,
                singleLine = true,
                isError = showPasswordError,
                supportingText = {
                    AnimatedVisibility(
                        visible = showPasswordError,
                        enter = fadeIn(tween(220)) + slideInVertically(tween(220)) { -it / 2 },
                        exit = fadeOut(tween(180)) + slideOutVertically(tween(180)) { -it / 2 }
                    ) {
                        Text("Mínimo 6 caracteres")
                    }
                }
            )
        }
    }
}

@Composable
fun LoginFooter(
    canLogin: Boolean,
    isLoading: Boolean,
    onLoginClick: () -> Unit
) {
    val loginEnabled = canLogin && !isLoading
    val buttonBorderColor by animateColorAsState(
        targetValue = if (loginEnabled) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
        } else {
            MaterialTheme.colorScheme.outlineVariant
        },
        animationSpec = tween(280),
        label = "buttonBorder"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(ItemSpacing)
    ) {
        Button(
            onClick = onLoginClick,
            enabled = loginEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = FieldShape,
            border = BorderStroke(1.dp, buttonBorderColor),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Crossfade(targetState = isLoading, label = "loginButtonState") { loading ->
                if (loading) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text("Validando...")
                    }
                } else {
                    Text(
                        text = "Iniciar Sesión",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        AnimatedVisibility(visible = !canLogin && !isLoading, enter = fadeIn(), exit = fadeOut()) {
            Text(
                text = "Completa un correo válido y una contraseña de 6+ caracteres",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        TextButton(onClick = { /* Recuperar pass */ }) {
            Text(
                text = "¿Olvidaste tu contraseña?",
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
