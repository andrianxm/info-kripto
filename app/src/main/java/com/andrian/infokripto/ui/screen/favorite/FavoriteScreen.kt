package com.andrian.infokripto.ui.screen.favorite

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.andrian.infokripto.di.Injection
import com.andrian.infokripto.helper.formatToRupiah
import com.andrian.infokripto.model.MarketItemData
import com.andrian.infokripto.ui.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    viewModel: FavoriteViewModel = viewModel(
        factory = ViewModelFactory(
            Injection.provideRepository(
                LocalContext.current
            )
        )
    ), navToDetail: (String) -> Unit
) {
    val favorites by viewModel.favoriteMarkets.collectAsState()

    Scaffold(topBar = {
        TopAppBar(title = {
            Text(
                text = "List Favorite",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        })
    }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (favorites.isEmpty()) {
                Text(text = "Belum ada market favorit!")
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(favorites, key = { it.id }) { market ->
                        MarketItem(market, onClick = { navToDetail(market.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun MarketItem(
    market: MarketItemData, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    val painter = rememberMarketImage(market.logoUrl)

    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painter,
                contentDescription = "Market Logo",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            MarketInfo(market)
        }
    }
}

@Composable
fun rememberMarketImage(url: String): Painter {
    val context = LocalContext.current
    val imageLoader = remember {
        ImageLoader.Builder(context).components { add(SvgDecoder.Factory()) }.build()
    }
    return rememberAsyncImagePainter(
        model = ImageRequest.Builder(context).data(url).crossfade(true).size(80).build(),
        imageLoader = imageLoader
    )
}

@Composable
fun MarketInfo(market: MarketItemData) {
    Column {
        Text(
            text = market.base_currency,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Harga Terakhir: ${formatToRupiah(market.last)}",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Beli: ${formatToRupiah(market.buy)} | Jual: ${formatToRupiah(market.sell)}",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}
