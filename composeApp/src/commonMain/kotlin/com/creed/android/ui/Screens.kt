package com.creed.android.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun SplashScreen(navController: NavHostController) { Text("Splash Screen") }

@Composable
fun LoginScreen(navController: NavHostController) { Text("Login Screen") }

@Composable
fun ProfileCompletionScreen(navController: NavHostController) { Text("Profile Completion Screen") }

@Composable
fun LocationPermissionScreen(navController: NavHostController) { Text("Location Permission Screen") }

@Composable
fun HomeScreen(navController: NavHostController) { Text("Home Screen") }

@Composable
fun SearchScreen(navController: NavHostController) { Text("Search Screen") }

@Composable
fun BookingsScreen(navController: NavHostController) { Text("Bookings Screen") }

@Composable
fun ProfileScreen(navController: NavHostController) { Text("Profile Screen") }

@Composable
fun ArenaDetailScreen(navController: NavHostController, arenaId: String) { Text("Arena Detail: $arenaId") }

@Composable
fun SlotPickerScreen(navController: NavHostController) { Text("Slot Picker Screen") }

@Composable
fun OrderConfirmationScreen(navController: NavHostController) { Text("Order Confirmation Screen") }

