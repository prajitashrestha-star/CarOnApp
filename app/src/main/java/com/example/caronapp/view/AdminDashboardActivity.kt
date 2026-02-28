package com.example.caronapp.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.caronapp.model.CarModel
import com.example.caronapp.repository.CarRepoImpl
import com.example.caronapp.ui.theme.Blue
import com.example.caronapp.viewmodel.CarViewModel
import com.google.firebase.auth.FirebaseAuth

/* ========================= ACTIVITY ========================= */

class AdminDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AdminDashboardBody()
        }
    }
}

/* ========================= MAIN UI ========================= */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardBody() {
    val context = LocalContext.current
    val activity = context as? Activity

    val carViewModel = remember { CarViewModel(CarRepoImpl()) }
    var selectedTab by remember { mutableIntStateOf(0) }

    // Load all cars when screen opens
    LaunchedEffect(Unit) {
        carViewModel.getAllCars()
    }

    val carList by carViewModel.allCars.observeAsState(emptyList())

    // State for showing Add Car dialog
    var showAddCarDialog by remember { mutableStateOf(false) }

    // State for showing Edit Car dialog
    var editCar by remember { mutableStateOf<CarModel?>(null) }

    // State for showing Delete confirmation dialog
    var deleteCar by remember { mutableStateOf<CarModel?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Admin Panel",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Blue,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                actions = {
                    // Logout button
                    IconButton(onClick = {
                        FirebaseAuth.getInstance().signOut()
                        context.startActivity(Intent(context, LoginActivity::class.java))
                        activity?.finish()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout"
                        )
                    }
                }
            )
        },

        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = { showAddCarDialog = true },
                    shape = CircleShape,
                    containerColor = Blue,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Car")
                }
            }
        },

        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = Blue
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    label = { Text("Cars") },
                    icon = { Icon(Icons.Default.DirectionsCar, contentDescription = "Cars") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Blue,
                        selectedTextColor = Blue,
                        indicatorColor = Blue.copy(alpha = 0.1f)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    label = { Text("Users") },
                    icon = { Icon(Icons.Default.People, contentDescription = "Users") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Blue,
                        selectedTextColor = Blue,
                        indicatorColor = Blue.copy(alpha = 0.1f)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    label = { Text("Stats") },
                    icon = { Icon(Icons.Default.BarChart, contentDescription = "Stats") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Blue,
                        selectedTextColor = Blue,
                        indicatorColor = Blue.copy(alpha = 0.1f)
                    )
                )
            }
        }

    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                0 -> ManageCarsScreen(
                    carList = carList ?: emptyList(),
                    onEditCar = { editCar = it },
                    onDeleteCar = { deleteCar = it }
                )
                1 -> ViewUsersScreen()
                2 -> {
                    val userVm = remember { com.example.caronapp.viewmodel.UserViewModel(com.example.caronapp.repository.UserRepoImpl()) }
                    val bookingVm = remember { com.example.caronapp.viewmodel.BookingViewModel(com.example.caronapp.repository.BookingRepoImpl()) }
                    AdminStatsScreen(carList = carList ?: emptyList(), userViewModel = userVm, bookingViewModel = bookingVm)
                }
            }
        }
    }

    // --- ADD CAR DIALOG ---
    if (showAddCarDialog) {
        AddEditCarDialog(
            title = "Add New Car",
            car = null,
            onDismiss = { showAddCarDialog = false },
            onSave = { newCar ->
                carViewModel.addCar(newCar) { success, message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    if (success) {
                        showAddCarDialog = false
                        carViewModel.getAllCars()
                    }
                }
            }
        )
    }

    // --- EDIT CAR DIALOG ---
    if (editCar != null) {
        AddEditCarDialog(
            title = "Edit Car",
            car = editCar,
            onDismiss = { editCar = null },
            onSave = { updatedCar ->
                carViewModel.updateCar(updatedCar.carId, updatedCar) { success, message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    if (success) {
                        editCar = null
                        carViewModel.getAllCars()
                    }
                }
            }
        )
    }

    // --- DELETE CONFIRMATION DIALOG ---
    if (deleteCar != null) {
        AlertDialog(
            onDismissRequest = { deleteCar = null },
            title = { Text("Delete Car", fontWeight = FontWeight.Bold) },
            text = {
                Text("Are you sure you want to delete \"${deleteCar?.carName}\"? This action cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        carViewModel.deleteCar(deleteCar!!.carId) { success, message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            if (success) {
                                deleteCar = null
                                carViewModel.getAllCars()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { deleteCar = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/* =================== MANAGE CARS SCREEN =================== */

@Composable
fun ManageCarsScreen(
    carList: List<CarModel>,
    onEditCar: (CarModel) -> Unit,
    onDeleteCar: (CarModel) -> Unit
) {
    if (carList.isEmpty()) {
        // Empty state
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.DirectionsCar,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Color.LightGray
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "No cars added yet",
                    fontSize = 18.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Tap + to add your first car",
                    fontSize = 14.sp,
                    color = Color.LightGray
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Manage Cars (${carList.size})",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Blue
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            items(carList) { car ->
                CarAdminCard(
                    car = car,
                    onEdit = { onEditCar(car) },
                    onDelete = { onDeleteCar(car) }
                )
            }
        }
    }
}

/* =================== CAR ADMIN CARD =================== */

@Composable
fun CarAdminCard(
    car: CarModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Top row: Car name + availability badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    car.carName.ifEmpty { "${car.brand} ${car.model}" },
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF1A1A2E)
                )
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (car.isAvailable) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                ) {
                    Text(
                        text = if (car.isAvailable) "Available" else "Rented",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (car.isAvailable) Color(0xFF2E7D32) else Color(0xFFC62828)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Car details
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    DetailRow(label = "Brand", value = car.brand)
                    DetailRow(label = "Model", value = car.model)
                    DetailRow(label = "Year", value = car.year)
                    DetailRow(label = "Stock", value = "${car.stock} units")
                }
                Column(modifier = Modifier.weight(1f)) {
                    DetailRow(label = "Fuel", value = car.fuelType)
                    DetailRow(label = "Seats", value = car.seats)
                    DetailRow(label = "Transmission", value = car.transmission)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Price
            Text(
                "Rs. ${car.pricePerDay}/day",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Blue
            )

            // Description if present
            if (car.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    car.description,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Blue)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit", fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onDelete,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete", fontSize = 13.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row {
        Text(
            "$label: ",
            fontSize = 13.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        Text(
            value.ifEmpty { "-" },
            fontSize = 13.sp,
            color = Color(0xFF333333)
        )
    }
}

/* =================== ADD / EDIT CAR DIALOG =================== */

@Composable
fun AddEditCarDialog(
    title: String,
    car: CarModel?,
    onDismiss: () -> Unit,
    onSave: (CarModel) -> Unit
) {
    var carName by remember { mutableStateOf(car?.carName ?: "") }
    var brand by remember { mutableStateOf(car?.brand ?: "") }
    var model by remember { mutableStateOf(car?.model ?: "") }
    var year by remember { mutableStateOf(car?.year ?: "") }
    var pricePerDay by remember { mutableStateOf(car?.pricePerDay ?: "") }
    var stock by remember { mutableStateOf(car?.stock?.toString() ?: "1") }
    var imageUrl by remember { mutableStateOf(car?.imageUrl ?: "") }
    var description by remember { mutableStateOf(car?.description ?: "") }
    var fuelType by remember { mutableStateOf(car?.fuelType ?: "") }
    var seats by remember { mutableStateOf(car?.seats ?: "") }
    var transmission by remember { mutableStateOf(car?.transmission ?: "") }
    var isAvailable by remember { mutableStateOf(car?.isAvailable ?: true) }

    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                title,
                fontWeight = FontWeight.Bold,
                color = Blue
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminTextField(value = carName, onValueChange = { carName = it }, label = "Car Name")
                AdminTextField(value = brand, onValueChange = { brand = it }, label = "Brand")
                AdminTextField(value = model, onValueChange = { model = it }, label = "Model")
                AdminTextField(
                    value = year,
                    onValueChange = { year = it },
                    label = "Year",
                    keyboardType = KeyboardType.Number
                )
                AdminTextField(
                    value = pricePerDay,
                    onValueChange = { pricePerDay = it },
                    label = "Price Per Day (Rs.)",
                    keyboardType = KeyboardType.Number
                )
                AdminTextField(value = fuelType, onValueChange = { fuelType = it }, label = "Fuel Type (Petrol/Diesel/Electric)")
                AdminTextField(
                    value = seats,
                    onValueChange = { seats = it },
                    label = "Seats",
                    keyboardType = KeyboardType.Number
                )
                AdminTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = "Total Stock",
                    keyboardType = KeyboardType.Number
                )
                AdminTextField(value = transmission, onValueChange = { transmission = it }, label = "Transmission (Manual/Automatic)")
                AdminTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = "Image URL (optional)")
                AdminTextField(value = description, onValueChange = { description = it }, label = "Description (optional)")

                // Availability toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Available for Rent", modifier = Modifier.weight(1f))
                    Switch(
                        checked = isAvailable,
                        onCheckedChange = { isAvailable = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = Blue)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Validation
                    if (brand.isBlank() || model.isBlank() || pricePerDay.isBlank()) {
                        Toast.makeText(context, "Brand, Model & Price are required", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val newCar = CarModel(
                        carId = car?.carId ?: "",
                        carName = carName.ifBlank { "$brand $model" },
                        brand = brand,
                        model = model,
                        year = year,
                        pricePerDay = pricePerDay,
                        imageUrl = imageUrl,
                        isAvailable = isAvailable,
                        description = description,
                        fuelType = fuelType,
                        seats = seats,
                        transmission = transmission,
                        stock = stock.toIntOrNull() ?: 1
                    )
                    onSave(newCar)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Blue)
            ) {
                Text("Save", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AdminTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 13.sp) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color(0xFFF5F5F5),
            focusedContainerColor = Color(0xFFF5F5F5),
            focusedIndicatorColor = Blue,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

/* =================== VIEW USERS SCREEN =================== */

@Composable
fun ViewUsersScreen() {
    val userViewModel = remember {
        com.example.caronapp.viewmodel.UserViewModel(
            com.example.caronapp.repository.UserRepoImpl()
        )
    }

    LaunchedEffect(Unit) {
        userViewModel.getAllUser()
    }

    val userList by userViewModel.allUsers.observeAsState(emptyList())

    if (userList.isNullOrEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.People,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Color.LightGray
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "No registered users yet",
                    fontSize = 18.sp,
                    color = Color.Gray
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Registered Users (${userList?.size ?: 0})",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Blue
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            items(userList ?: emptyList()) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar circle
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape,
                            color = Blue.copy(alpha = 0.1f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = (user.firstName.firstOrNull()?.uppercase()
                                        ?: user.email.firstOrNull()?.uppercase()
                                        ?: "U"),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = Blue
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (user.firstName.isNotEmpty() || user.lastName.isNotEmpty())
                                    "${user.firstName} ${user.lastName}".trim()
                                else "No Name",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = user.email,
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                            if (user.contact.isNotEmpty()) {
                                Text(
                                    text = user.contact,
                                    fontSize = 13.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/* =================== STATS SCREEN =================== */

@Composable
fun AdminStatsScreen(
    carList: List<CarModel>,
    userViewModel: com.example.caronapp.viewmodel.UserViewModel,
    bookingViewModel: com.example.caronapp.viewmodel.BookingViewModel
) {
    LaunchedEffect(Unit) {
        userViewModel.getAllUser()
        bookingViewModel.getAllBookings()
    }

    val userList by userViewModel.allUsers.observeAsState(emptyList())
    val allBookings by bookingViewModel.allBookings.observeAsState(emptyList())

    val totalCars = carList.size
    val availableCars = carList.sumOf { it.stock }
    val rentedCars = allBookings?.count { it.status == "Confirmed" } ?: 0
    val totalRevenue = allBookings?.sumOf { it.totalPrice.toDoubleOrNull() ?: 0.0 } ?: 0.0
    val totalUsers = userList?.size ?: 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Dashboard Overview",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Blue
        )

        // Stats cards row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                label = "Total Cars Stock",
                value = "$availableCars",
                icon = Icons.Default.DirectionsCar,
                bgColor = Blue.copy(alpha = 0.1f),
                textColor = Blue
            )
            StatCard(
                modifier = Modifier.weight(1f),
                label = "Total Users",
                value = "$totalUsers",
                icon = Icons.Default.Group,
                bgColor = Color(0xFFE8F5E9),
                textColor = Color(0xFF2E7D32)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                label = "Active Bookings",
                value = "$rentedCars",
                icon = Icons.Default.CarRental,
                bgColor = Color(0xFFFFF3E0),
                textColor = Color(0xFFF57C00)
            )
            StatCard(
                modifier = Modifier.weight(1f),
                label = "Total Revenue",
                value = "Rs. ${String.format("%.0f", totalRevenue)}",
                icon = Icons.Default.AttachMoney,
                bgColor = Color(0xFFE8EAF6),
                textColor = Color(0xFF3F51B5)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Quick summary card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "Quick Summary",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Blue
                )
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFEEEEEE))
                Spacer(modifier = Modifier.height(12.dp))

                SummaryRow("Total Vehicle Models", "$totalCars models")
                SummaryRow("Total Available Stock (units)", "$availableCars units")
                SummaryRow("Total Registered Users", "$totalUsers users")
                SummaryRow("Active / Upcoming Bookings", "$rentedCars bookings")
                SummaryRow("Total Cash Flow", "Rs. ${String.format("%.0f", totalRevenue)}")
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    bgColor: Color,
    textColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = bgColor
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                value,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                label,
                fontSize = 13.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(value, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}
