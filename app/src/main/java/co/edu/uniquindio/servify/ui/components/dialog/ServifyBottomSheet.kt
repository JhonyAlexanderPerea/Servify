package co.edu.uniquindio.servify.ui.components.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurface
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle
import co.edu.uniquindio.servify.ui.theme.ServifyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServifyBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        contentWindowInsets = { WindowInsets.navigationBars }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            if (!title.isNullOrBlank()) {
                Text(
                    text = title,
                    style = ServifyTextStyle.TitleTight.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                    color = ServifyOnSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Bottom Sheet Light", showBackground = true)
@Composable
fun ServifyBottomSheetPreviewLight() {
    ServifyTheme(darkTheme = false) {
        ServifyBottomSheet(
            title = "Filtrar Resultados",
            onDismissRequest = {}
        ) {
            Text(
                text = "Opciones de filtrado por precio y ubicación",
                style = ServifyTextStyle.Small
            )
        }
    }
}
