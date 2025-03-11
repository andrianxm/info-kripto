package com.andrian.infokripto.ui.screen.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.andrian.infokripto.di.Injection
import com.andrian.infokripto.helper.formatToRupiah
import com.andrian.infokripto.ui.ViewModelFactory
import com.andrian.infokripto.ui.common.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    marketId: String, viewModel: DetailViewModel = viewModel(
        factory = ViewModelFactory(
            Injection.provideRepository(
                LocalContext.current
            )
        )
    ), onBack: () -> Unit
) {
    val marketState by viewModel.marketState.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()

    LaunchedEffect(marketId) {
        viewModel.getMarketDetail(marketId)
        viewModel.checkIfFavorite(marketId)
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text(text = "Market Detail") }, navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back",
                    modifier = Modifier.semantics { contentDescription = "about_page" }
                )
            }
        }, actions = {
            if (marketState is UiState.Success) {
                val market = (marketState as UiState.Success).data
                IconButton(onClick = { viewModel.toggleFavorite(market) }) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else Color.Gray
                    )
                }
            }
        })
    }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (marketState) {
                is UiState.Loading -> {
                    CircularProgressIndicator()
                }

                is UiState.Success -> {
                    val market = (marketState as UiState.Success).data
                    val imageLoader = ImageLoader.Builder(LocalContext.current).components {
                        add(SvgDecoder.Factory())
                    }.build()

                    val painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current).data(market.logoUrl)
                            .crossfade(true).build(), imageLoader = imageLoader
                    )

                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painter,
                            contentDescription = "Market Logo",
                            modifier = Modifier.size(60.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = market.symbol, style = MaterialTheme.typography.headlineLarge)
                        Text(
                            text = market.base_currency,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            listOf(
                                "Last Price" to market.last,
                                "Buy Price" to market.buy,
                                "Sell Price" to market.sell,
                            ).forEach { (title, value) ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                                        .padding(12.dp), contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = title, fontWeight = FontWeight.Bold)
                                        Text(text = formatToRupiah(value))
                                    }
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                listOf(
                                    "24h High" to market.high, "24h Low" to market.low
                                ).forEach { (title, value) ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(8.dp)
                                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                                            .padding(12.dp), contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(text = title, fontWeight = FontWeight.Bold)
                                            Text(text = formatToRupiah(value))
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        val priceColor = if (market.priceChange >= 0) Color.Green else Color.Red
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .border(1.dp, priceColor, RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "24h Change: ${market.priceChange}%",
                                color = priceColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                is UiState.Error -> {
                    val errorMessage = (marketState as UiState.Error).errorMessage
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Gagal memuat data!", color = Color.Red)
                        Text(text = errorMessage, fontStyle = FontStyle.Italic)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.getMarketDetail(marketId) }) {
                            Text(text = "Coba Lagi")
                        }
                    }
                }
            }
        }
    }
}
