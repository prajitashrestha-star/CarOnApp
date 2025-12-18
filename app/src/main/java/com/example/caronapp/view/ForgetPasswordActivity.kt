package com.example.caronapp.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.caronapp.repository.UserRepoImpl
import com.example.caronapp.viewmodel.UserViewModel
import com.example.caronapp.R

class ForgetPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ForgetPasswordBody()
        }
    }
}

@Composable
fun ForgetPasswordBody() {
    var email by remember { mutableStateOf("") }
    val context = LocalContext.current
    val activity = context as? android.app.Activity
    val userViewModel= remember { UserViewModel(UserRepoImpl()) }

    val inputColors = TextFieldDefaults.colors(
        unfocusedContainerColor = Color(0xFFF8F8F8),
        focusedContainerColor = Color(0xFFF8F8F8),
        focusedIndicatorColor = Color(0xFF0095F6),
        unfocusedIndicatorColor = Color.Transparent
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 28.dp), // Unified side padding
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // --- Logo Text ---
        Text(
            text = "CarOn",
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0095F6)
            )
        )

        // --- Adjusted Image Logo ---
        Image(
            painter = painterResource(id = R.drawable.logoo),
            contentDescription = "CarOn Logo",
            modifier = Modifier
                .size(150.dp) // Professional size
                .padding(vertical = 16.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            "Reset Password",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            "Enter your Gmail address and we will send you a link to get back into your account.",
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 10.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- Email Field ---
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Email Address", fontWeight = FontWeight.Light) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = inputColors,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- Reset Button ---
        Button(
            onClick = {
                if (email.isNotEmpty()){
                    userViewModel.forgetPassword(email){
                        success,message ->
                        Toast.makeText(context,message, Toast.LENGTH_LONG).show()
                        if (success)activity?.finish()
                    }
//                if (email.isNotEmpty()){
//                    userViewModel.forgotpassword(email){
//                        success, message ->
//                        Toast.makeText(context,message,Toast.LENGTH_LONG).show()
//                        if (success)activity?.finish()
//                    }
                } else {
                    Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0095F6)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Send Reset Link", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Back to Login ---
        Text(
            "Back to Login",
            color = Color(0xFF0095F6),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable {
                activity?.finish()
            }
        )
    }
}