package com.example.caronapp.view


import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* -------------------- DATA MODELS -------------------- */

data class DashboardItem(
    val title: String,
    val description: String,
    val status: String
)

data class NavItem(val label: String)

/* -------------------- ACTIVITY -------------------- */

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DashboardBody()
        }
    }
}

/* -------------------- DASHBOARD UI -------------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardBody() {

    val context = LocalContext.current
    var selectedIndex by remember { mutableIntStateOf(0) }

    val navItems = listOf(
        NavItem("Bookings"),
        NavItem("Contracts"),
        NavItem("Handover")
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "CaronApp",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            )
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    Toast.makeText(context, "Add New Booking", Toast.LENGTH_SHORT).show()
                },
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },

        bottomBar = {
            NavigationBar {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        label = { Text(item.label) },
                        icon = {}
                    )
                }
            }
        }

    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedIndex) {
                0 -> BookingScreen()
                1 -> ContractScreen()
                2 -> VehicleHandoverScreen()
            }
        }
    }
}

/* -------------------- SCREENS -------------------- */

@Composable
fun BookingScreen() {

    val bookings = listOf(
        DashboardItem("Booking #101", "Toyota Corolla - 3 Days", "Confirmed"),
        DashboardItem("Booking #102", "Hyundai i20 - 1 Day", "Pending")
    )

    DashboardList(title = "Active Bookings", items = bookings)
}

@Composable
fun ContractScreen() {

    val contracts = listOf(
        DashboardItem("Contract #C01", "Signed by Customer", "Completed"),
        DashboardItem("Contract #C02", "Awaiting Signature", "Pending")
    )

    DashboardList(title = "Contract Finalization", items = contracts)
}

@Composable
fun VehicleHandoverScreen() {

    val handovers = listOf(
        DashboardItem("Handover #H01", "Vehicle Given to Customer", "Done"),
        DashboardItem("Handover #H02", "Scheduled Tomorrow", "Upcoming")
    )

    DashboardList(title = "Vehicle Handover", items = handovers)
}

/* -------------------- REUSABLE LIST UI -------------------- */

@Composable
fun DashboardList(title: String, items: List<DashboardItem>) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            Text(
                title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(items) { item ->
            DashboardCard(item)
        }
    }
}

@Composable
fun DashboardCard(item: DashboardItem) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                item.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(item.description, color = Color.DarkGray)

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                "Status: ${item.status}",
                color = if (item.status == "Completed" || item.status == "Done")
                    Color(0xFF2E7D32) else Color(0xFFF57C00),
                fontWeight = FontWeight.Medium
            )
        }
    }
}


