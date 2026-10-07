package co.edu.uniquindio.servify.ui.components.input

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.servify.ui.theme.ServifyError
import co.edu.uniquindio.servify.ui.theme.ServifyFieldContainer
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurface
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurfaceVariant
import co.edu.uniquindio.servify.ui.theme.ServifyOutline
import co.edu.uniquindio.servify.ui.theme.ServifyPrimary
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle

private val FieldShape = RoundedCornerShape(
    topStart = 6.dp,
    topEnd = 6.dp
)

@Composable
fun ServifyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    trailingIconDescription: String? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    isError: Boolean = false,
    supportingText: String? = null
) {

    var focused by remember { mutableStateOf(false) }

    val floated = focused || value.isNotEmpty()

    val labelTop by animateDpAsState(
        targetValue = if (floated) 0.dp else 4.dp,
        animationSpec = tween(150),
        label = "labelTop"
    )

    val labelSize by animateFloatAsState(
        targetValue = if (floated) 12f else 14f,
        animationSpec = tween(150),
        label = "labelSize"
    )

    val indicatorColor = when {
        isError -> ServifyError
        focused -> ServifyPrimary
        else -> ServifyOutline
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focused = it.isFocused },
            textStyle = ServifyTextStyle.Small.copy(
                color = ServifyOnSurface
            ),
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            cursorBrush = SolidColor(ServifyPrimary),
            decorationBox = { innerTextField ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(FieldShape)
                        .background(ServifyFieldContainer)
                        .drawBehind {

                            val stroke = 2.dp.toPx()

                            drawRect(
                                color = indicatorColor,
                                topLeft = Offset(0f, size.height - stroke),
                                size = Size(size.width, stroke)
                            )
                        }
                        .padding(
                            start = 12.dp,
                            end = 12.dp,
                            top = 20.dp,
                            bottom = 10.dp
                        ),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {

                    if (leadingIcon != null) {

                        Icon(
                            imageVector = leadingIcon,
                            contentDescription = null,
                            tint = ServifyOnSurfaceVariant,
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .size(20.dp)
                        )
                    }

                    Box(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = label,
                            style = TextStyle(
                                fontSize = labelSize.sp,
                                lineHeight = (16f + (labelSize - 12f) * 2f).sp,
                                letterSpacing = 0.sp
                            ),
                            color = ServifyOnSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.offset(y = labelTop)
                        )

                        Box(
                            modifier = Modifier.padding(top = 12.dp)
                        ) {
                            innerTextField()
                        }
                    }

                    if (trailingIcon != null) {

                        Icon(
                            imageVector = trailingIcon,
                            contentDescription = trailingIconDescription,
                            tint = ServifyOnSurfaceVariant,
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .size(20.dp)
                                .clickable(
                                    enabled = onTrailingIconClick != null,
                                    role = Role.Button,
                                    onClick = { onTrailingIconClick?.invoke() }
                                )
                        )
                    }
                }
            }
        )

        if (supportingText != null) {

            Text(
                text = supportingText,
                style = ServifyTextStyle.Caption,
                color =
                    if (isError) ServifyError
                    else ServifyOnSurfaceVariant,
                modifier = Modifier.padding(
                    start = 12.dp,
                    end = 12.dp,
                    top = 4.dp
                )
            )
        }
    }
}
