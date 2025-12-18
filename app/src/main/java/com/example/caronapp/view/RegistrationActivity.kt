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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.caronapp.R
import com.example.caronapp.model.UserModel
import com.example.caronapp.repository.UserRepoImpl
import com.example.caronapp.ui.theme.Black
import com.example.caronapp.ui.theme.Blue
import com.example.caronapp.ui.theme.DarkGreen
import com.example.caronapp.ui.theme.LightGrayBackground
import com.example.caronapp.ui.theme.White
import com.example.caronapp.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

class RegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RegisterBody()
        }
    }
}

@Composable
fun RegisterBody() {
    // --- STATE VARIABLES ---
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var terms by remember { mutableStateOf(false) }

    val context = LocalContext.current
//    val userViewModel = remember { UserViewModel(UserRepoImpl()) }
    val userViewModel=remember { UserViewModel(UserRepoImpl()) }
    val activity = context as? Activity

    var passwordVisibility by remember { mutableStateOf(false) }
    var confirmPasswordVisibility by remember { mutableStateOf(false) }

    // This modifier ensures all fields look identical
    val fieldModifier = Modifier.fillMaxWidth()

    val inputColors = TextFieldDefaults.colors(
        unfocusedContainerColor = LightGrayBackground,
        focusedContainerColor = LightGrayBackground,
        focusedIndicatorColor = DarkGreen,
        unfocusedIndicatorColor = Color.Transparent
    )

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                // Apply horizontal padding here so EVERYTHING has space on the sides
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                "CarOn",
                style = TextStyle(fontSize = 35.sp, color = Blue),
                fontWeight = FontWeight.Bold)


            Text("Create a new account", color = Color.Gray,
                modifier = Modifier.padding(bottom = 30.dp))

            // --- Form Fields ---
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                placeholder = { Text("Email Address") },
                colors = inputColors,
                modifier = fieldModifier,
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                placeholder = { Text("Username") },
                colors = inputColors,
                modifier = fieldModifier,
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Password") },
                trailingIcon = {
                    IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                        Icon(
                            painter = painterResource(if (!passwordVisibility) R.drawable.baseline_visibility_off_24 else R.drawable.baseline_visibility_24),
                            contentDescription = null,
                            tint = Black
                        )
                    }
                },
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                colors = inputColors,
                modifier = fieldModifier,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it},
                placeholder = { Text("Confirm Password") },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisibility = !confirmPasswordVisibility }) {
                        Icon(
                            painter = painterResource(if (!confirmPasswordVisibility) R.drawable.baseline_visibility_off_24 else R.drawable.baseline_visibility_24),
                            contentDescription = null,
                            tint = Black
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                colors = inputColors,
                modifier = fieldModifier,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // --- Terms and Conditions ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = terms,
                    onCheckedChange = { terms = it },
                    colors = CheckboxDefaults.colors(checkedColor = DarkGreen)
                )
                Text("I agree to the Terms & Conditions", fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Sign Up Button ---


            Button(
                onClick = {
                    when {
                        email.isBlank() || username.isBlank() || password.isBlank() || confirmPassword.isBlank() -> {
                            Toast.makeText(
                                context,
                                "Please fill all fields.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        password != confirmPassword -> {
                            Toast.makeText(
                                context,
                                "Passwords do not match.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        !terms -> {
                            Toast.makeText(
                                context,
                                "Please agree to the Terms & Conditions.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {
                            userViewModel.register(
                                email,
                                password
                            ) { success, message, userId ->
                                if (success) {
                                    val model = UserModel(
                                        userId = userId,
                                        email = email,
                                        firstName = "", lastName = "", dob = "", contact = ""
                                    )
                                    userViewModel.addUserToDatabase(
                                        userId,
                                        model
                                    ) { dbSuccess, dbMessage ->
                                        if (dbSuccess) {
                                            // --- SEND REAL VERIFICATION EMAIL ---
                                            val firebaseUser =
                                                FirebaseAuth.getInstance().currentUser
                                            firebaseUser?.sendEmailVerification()
                                                ?.addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        Toast.makeText(
                                                            context,
                                                            "Verification email sent to $email",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                                }

                                            Toast.makeText(
                                                context,
                                                "Registration Successful!",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            // Navigate to Login
                                            val intent =
                                                Intent(context, LoginActivity::class.java)
                                            context.startActivity(intent)
                                            activity?.finish()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                dbMessage,
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Blue)
            ) {
                Text("Sign Up", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = White)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f)) // Used HorizontalDivider for M3
                Text("  OR  ", color = Color.Gray)
                HorizontalDivider(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Google Button ---
            OutlinedButton( // Changed to OutlinedButton for better "white" styling
                onClick = {
                    Toast.makeText(context, "Gmail login is not implemented", Toast.LENGTH_LONG).show()
                },
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
                Text("Sign up with Gmail", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(30.dp))

            // --- Footer Navigation ---
            Text(
                text = buildAnnotatedString {
                    append("Already have an account? ")
                    withStyle(SpanStyle(color = Blue, fontWeight = FontWeight.Bold)) {
                        append("Sign In")
                    }
                },
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .clickable {
                        val intent = Intent(context, LoginActivity::class.java)
                        context.startActivity(intent)
                        activity?.finish()
                    },
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegister(){
    RegisterBody()
}