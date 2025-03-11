package com.andrian.infokripto.ui.screen.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.andrian.infokripto.di.Injection
import com.andrian.infokripto.helper.formatToRupiah
import com.andrian.infokripto.model.MarketItemData
import com.andrian.infokripto.ui.ViewModelFactory
import com.andrian.infokripto.ui.common.UiState

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(
        factory = ViewModelFactory(
            Injection.provideRepository(
                LocalContext.current
            )
        )
    ),
    navToDetail: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedSort by viewModel.selectedSort.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(searchQuery, viewModel::setSearchQuery)
        SortButtons(selectedSort, viewModel::setSelectedSort)
        Box(modifier = Modifier.weight(1f)) {
            when (uiState) {
                is UiState.Loading -> ShimmerLoadingList()
                is UiState.Success -> {
                    val marketList = (uiState as UiState.Success<List<MarketItemData>>).data
                    HomeContent(
                        marketList = marketList,
                        navigateToDetail = navToDetail,
                    )
                }

                is UiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Gagal memuat data!",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ShimmerLoadingList() {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(8) {
            ShimmerItem()
        }
    }
}

@Composable
fun ShimmerItem() {
    val transition = rememberInfiniteTransition(label = "")
    val shimmerAlpha by transition.animateFloat(
        initialValue = 0.3f, targetValue = 1f, animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    val shimmerBrush = Brush.horizontalGradient(
        colors = listOf(
            Color.Gray.copy(alpha = shimmerAlpha),
            Color.LightGray.copy(alpha = 0.5f),
            Color.Gray.copy(alpha = shimmerAlpha)
        )
    )
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(shimmerBrush)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(16.dp)
                        .background(shimmerBrush)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(150.dp)
                        .height(14.dp)
                        .background(shimmerBrush)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(14.dp)
                        .background(shimmerBrush)
                )
            }
        }
    }
}

@Composable
fun HomeContent(
    marketList: List<MarketItemData>,
    navigateToDetail: (String) -> Unit,
) {
    val listState = rememberLazyListState()
    var previousListSize by remember { mutableIntStateOf(marketList.size) }

    LaunchedEffect(marketList.size) {
        if (marketList.size > previousListSize) {
            listState.scrollToItem(0)
        }
        previousListSize = marketList.size
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            if (marketList.isNotEmpty()) {
                ListMarket(
                    marketList = marketList,
                    navigateToDetail = navigateToDetail,
                    listState = listState
                )
            } else {
                EmptyList(
                    warning = "Data tidak ditemukan", modifier = Modifier.testTag("emptyList")
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    val outlineColor = Color(0xFF2894B8)
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Cari koin...") },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = outlineColor, unfocusedBorderColor = Color.Gray
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
}

@Composable
fun SortButtons(selectedSort: String, onSortChange: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        SortButton(
            label = "Default", value = "default", selectedSort, onSortChange, Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(10.dp))
        SortButton(
            label = "Tertinggi", value = "highest", selectedSort, onSortChange, Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(10.dp))
        SortButton(
            label = "Terendah", value = "lowest", selectedSort, onSortChange, Modifier.weight(1f)
        )
    }
}

@Composable
fun SortButton(
    label: String,
    value: String,
    selectedSort: String,
    onSortChange: (String) -> Unit,
    modifier: Modifier
) {
    val outlineColor = Color(0xFF2894B8)
    OutlinedButton(
        onClick = { onSortChange(value) },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (selectedSort == value) outlineColor else Color.Gray),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = if (selectedSort == value) outlineColor else Color.Gray
        ),
        modifier = modifier
    ) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun EmptyList(
    warning: String,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center, modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = warning,
        )
    }
}

@Composable
fun ListMarket(
    marketList: List<MarketItemData>,
    navigateToDetail: (String) -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(items = marketList, key = { it.id }) { market ->
            Modifier.fillMaxWidth()
            MarketItem(market = market, modifier = Modifier
                .animateItem(
                    fadeInSpec = null,
                    fadeOutSpec = null,
                    placementSpec = tween(durationMillis = 200)
                )
                .clickable { navigateToDetail(market.id) })
        }
    }
}

@Composable
fun MarketItem(
    market: MarketItemData, modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageLoader = remember {
        ImageLoader.Builder(context).components { add(SvgDecoder.Factory()) }.build()
    }
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context).data(market.logoUrl).crossfade(true).size(80).build(),
        imageLoader = imageLoader
    )
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.fillMaxWidth(),
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
            Column {
                Text(
                    text = market.base_currency,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
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
    }
}
