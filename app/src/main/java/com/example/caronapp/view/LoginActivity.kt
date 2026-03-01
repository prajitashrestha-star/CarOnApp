package com.example.caronapp.view


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.caronapp.R
import com.example.caronapp.repository.UserRepoImpl
import com.example.caronapp.ui.theme.Blue
import com.example.caronapp.viewmodel.UserViewModel

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoginBody()
        }
    }
}

@Composable
fun LoginBody() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var visibility by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as Activity
    val userViewModel = remember { UserViewModel(com.example.caronapp.repository.UserRepoImpl()) }

    // --- SEEDED ADMIN CREDENTIALS (Hardcoded) ---
    // These are checked locally BEFORE Firebase Auth
    // No need to create this account in Firebase Console
    val adminEmail = "admin@caronapp.com"
    val adminPassword = "Admin@123"

    val fieldModifier = Modifier.fillMaxWidth()

    // --- MATCHING REGISTRATION COLORS ---
    val inputColors = TextFieldDefaults.colors(
        // Use LightGrayBackground here to match RegistrationActivity
        unfocusedContainerColor = com.example.caronapp.ui.theme.LightGrayBackground,
        focusedContainerColor = com.example.caronapp.ui.theme.LightGrayBackground,
        focusedIndicatorColor = Blue,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Matches Registration background
            .padding(horizontal = 28.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        Text(
            text = "CarOn",
            style = TextStyle(fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Blue)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // --- Email Field ---
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Email", fontWeight = FontWeight.Light) },
            modifier = fieldModifier.testTag("emailInput"),
            shape = RoundedCornerShape(12.dp),
            colors = inputColors,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Password Field ---
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Password", fontWeight = FontWeight.Light) },
            modifier = fieldModifier.testTag("passwordInput"),
            visualTransformation = if (visibility) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { visibility = !visibility }) {
                    Icon(
                        painter = painterResource(
                            if (!visibility) R.drawable.baseline_visibility_off_24
                            else R.drawable.baseline_visibility_24
                        ),
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = inputColors,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- Login Button ---
        Button(
            onClick = {
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                } else if (email.trim().equals(adminEmail, ignoreCase = true) && password.trim() == adminPassword) {
                    // ADMIN LOGIN — validated locally, bypass Firebase Auth
                    Toast.makeText(context, "Welcome, Admin!", Toast.LENGTH_SHORT).show()
                    context.startActivity(Intent(context, AdminDashboardActivity::class.java))
                    activity.finish()
                } else {
                    // REGULAR USER LOGIN — via Firebase Auth
                    userViewModel.login(email, password) { success, message ->
                        if (success) {
                            val firebaseUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                            if (firebaseUser?.isEmailVerified == true) {
                                // Verified user → go to User Dashboard
                                context.startActivity(Intent(context, DashboardActivity::class.java))
                                activity.finish()
                            } else {
                                Toast.makeText(context, "Please verify your email via Gmail first.", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(context, "Login Failed: $message", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .testTag("loginButton"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Blue)
        ) {
            Text("Log In", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Forgot your password? Click Here",
            fontSize = 13.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.clickable {
                context.startActivity(Intent(context, ForgetPasswordActivity::class.java))
            }
        )

        Spacer(modifier = Modifier.height(30.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
            Text("  OR  ", color = Color.Gray, fontSize = 12.sp)
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
        }

        Spacer(modifier = Modifier.height(30.dp))

        // --- Gmail Login ---
        OutlinedButton(
            onClick = { Toast.makeText(context, "Gmail login not implemented", Toast.LENGTH_SHORT).show() },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.gmail),
                contentDescription = "Gmail",
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Log in with Gmail", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(30.dp))

        // --- Bottom SignUp Text ---
        Row(modifier = Modifier.padding(bottom = 30.dp)) {
            Text("Don't have an account? ", color = Color.Gray)
            Text(
                "SignUp",
                color = Blue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    context.startActivity(Intent(context, RegistrationActivity::class.java))
                    activity.finish()
                }
            )
        }
    }
}
@Preview
@Composable
fun PreviewLogin(){
    LoginBody()
}