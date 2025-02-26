package com.example.dessertclicker.ui

import com.example.dessertclicker.data.Datasource
import com.example.dessertclicker.model.Dessert

data class GameUiState(
    val desserts: List<Dessert> = Datasource.dessertList,
    val revenue : Int = 0,
    var dessertsSold: Int = 0,
    val currentDessertIndex: Int = 0,
    val currentDessertPrice: Int = desserts[currentDessertIndex].price,
    val currentDessertImageId: Int = desserts[currentDessertIndex].imageId,
)