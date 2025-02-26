package com.example.dessertclicker.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.dessertclicker.R
import com.example.dessertclicker.model.Dessert
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class GameViewModel : ViewModel() {

    // Game UI state
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()


    fun clickDessertUpdate() {
        // Update the revenue
        val newRevenue = uiState.value.revenue + uiState.value.currentDessertPrice
        val newDessertsSold = uiState.value.dessertsSold+1
        updateGameState(newRevenue, newDessertsSold)
    }


    /**
     * Determine which dessert to show.
     */
    fun determineDessertToShow(desserts: List<Dessert>, dessertsSold: Int): Dessert {
        var dessertToShow = desserts.first()
        for (dessert in desserts) {
            if (dessertsSold >= dessert.startProductionAmount) {
                dessertToShow = dessert
            } else {
                // The list of desserts is sorted by startProductionAmount. As you sell more desserts,
                // you'll start producing more expensive desserts as determined by startProductionAmount
                // We know to break as soon as we see a dessert who's "startProductionAmount" is greater
                // than the amount sold.
                break
            }
        }

        return dessertToShow
    }

    /**
     * Share desserts sold information using ACTION_SEND intent
     */
    fun shareSoldDessertsInformation(intentContext: Context, dessertsSold: Int, revenue: Int) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                intentContext.getString(R.string.share_text, dessertsSold, revenue)
            )
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)

        try {
            ContextCompat.startActivity(intentContext, shareIntent, null)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                intentContext,
                intentContext.getString(R.string.sharing_not_available),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun updateGameState(newRevenue: Int, newDessertsSold: Int) {

        // Show the next dessert
        val dessertToShow = determineDessertToShow(uiState.value.desserts, newDessertsSold)


        _uiState.update { currentState ->
            currentState.copy(
                revenue = newRevenue,
                dessertsSold = newDessertsSold,
                currentDessertIndex = uiState.value.desserts.indexOf(dessertToShow),
                currentDessertPrice = dessertToShow.price,
                currentDessertImageId = dessertToShow.imageId

            )
        }
    }

}