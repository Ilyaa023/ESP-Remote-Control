package esp.remote.control.ui

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import esp.remote.control.R
import esp.remote.control.models.NeuronModel.Companion.EXTENSION_PIN
import esp.remote.control.models.NeuronModel.Companion.GND
import esp.remote.control.models.NeuronModel.Companion.OIN_1
import esp.remote.control.models.NeuronModel.Companion.OIN_2
import esp.remote.control.models.NeuronModel.Companion.PWR


@Composable
fun Circuit(viewModel: MainActivityViewModel){
    val keyVMStates by viewModel.keyStates.observeAsState()
    val resistance1 by viewModel.resistance1.observeAsState()
    val resistance2 by viewModel.resistance2.observeAsState()
    val keyStates = keyVMStates ?: arrayOf(0, 0, 0)
    val keysStates = arrayOf(0, 0, 0)

    keysStates[0] = when (keyStates[0]) {
        OIN_1 -> 1
        OIN_2 -> 2
        else -> 0
    }
    keysStates[1] = when (keyStates[1]){
        PWR -> 0
        else -> 1
    }
    keysStates[2] = when (keyStates[2]) {
        OIN_1 -> 1
        OIN_2 -> 0
        else -> 2
    }
    val backColor = MaterialTheme.colorScheme.surface

    Canvas(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTapGestures(onTap = { tapOffset ->
                val canvasWidth = size.width
                val canvasHeight = size.height
                val keysOffsets = arrayOf(
                    arrayOf(Offset(x = canvasWidth / 8f, y = canvasHeight / 8f),
                            Offset(x = canvasWidth / 8f, y = canvasHeight / 4f),
                            Offset(x = canvasWidth / 8f, y = canvasHeight / 8f * 3)),
                    arrayOf(Offset(x = canvasWidth * (1 / 3f - 1 / 16f),
                                   y = canvasHeight * (2 / 3f + 1 / 8f)),
                            Offset(x = canvasWidth * (1 / 3f + 1 / 16f),
                                   y = canvasHeight * (2 / 3f + 1 / 8f))),
                    arrayOf(Offset(x = canvasWidth / 8f * 7, y = canvasHeight / 8f),
                            Offset(x = canvasWidth / 8f * 7, y = canvasHeight / 4f),
                            Offset(x = canvasWidth / 8f * 7, y = canvasHeight / 8f * 3))
                )
                val comArray = arrayOf(
                    arrayOf(EXTENSION_PIN, OIN_1, OIN_2),
                    arrayOf(PWR, GND),
                    arrayOf(OIN_2, OIN_1, EXTENSION_PIN)
                )
                for (key in keysOffsets.indices) for (state in keysOffsets[key].indices)
                    if (keysOffsets[key][state]
                            .minus(tapOffset)
                            .getDistance() < 40) viewModel.setKey(key, comArray[key][state])
            })
        }) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val linesStartOffsets = arrayOf(
            Offset(x = canvasWidth / 4, y = canvasHeight / 4),
            Offset(x = canvasWidth / 3, y = canvasHeight / 4),
            Offset(x = canvasWidth * (1 / 3f + 1 / 16f), y = canvasHeight * (2 / 3f + 1 / 8f)),
            Offset(x = 0f, y = canvasHeight / 8),
            Offset(x = 0f, y = canvasHeight / 4),
            Offset(x = 0f, y = canvasHeight / 8 * 3),
            Offset(x = canvasWidth / 8 * 7, y = canvasHeight / 8),
            Offset(x = canvasWidth / 8 * 7, y = canvasHeight / 4),
            Offset(x = canvasWidth / 8 * 7, y = canvasHeight / 8 * 3),
            Offset(x = canvasWidth * (1 / 3f - 1 / 16f), y = canvasHeight * (2 / 3f + 1 / 8f)),
            Offset(x = 0f, y = canvasHeight * (2 / 3f + 3 / 16f)),
            Offset(x = 0f, y = canvasHeight * (2 / 3f + 9 / 32f)),
            Offset(x = canvasWidth / 16 * 15, y = canvasHeight / 8 * 3),
            Offset(x = canvasWidth / 4, y = canvasHeight / 4),
            Offset(x = canvasWidth / 4 * 3, y = canvasHeight / 4),
            Offset(x = canvasWidth / 3f, y = canvasHeight / 3f * 2f)
        )
        val linesEndOffset = arrayOf(
            Offset(x = canvasWidth * 0.75f, y = canvasHeight / 4),
            Offset(x = canvasWidth / 3, y = canvasHeight / 3 * 2),
            Offset(x = canvasWidth * (1 / 3f + 1 / 16f), y = canvasHeight * (2 / 3f + 3 / 16f)),
            Offset(x = canvasWidth / 8, y = canvasHeight / 8),
            Offset(x = canvasWidth / 8, y = canvasHeight / 4),
            Offset(x = canvasWidth / 8, y = canvasHeight / 8 * 3),
            Offset(x = canvasWidth, y = canvasHeight / 8),
            Offset(x = canvasWidth, y = canvasHeight / 4),
            Offset(x = canvasWidth / 16 * 15, y = canvasHeight / 8 * 3),
            Offset(x = canvasWidth * (1 / 3f - 1 / 16f), y = canvasHeight * (2 / 3f + 9 / 32f)),
            Offset(x = canvasWidth, y = canvasHeight * (2 / 3f + 3 / 16f)),
            Offset(x = canvasWidth, y = canvasHeight * (2 / 3f + 9 / 32f)),
            Offset(x = canvasWidth / 16 * 15, y = canvasHeight * (2 / 3f + 3 / 16f)),
            Offset(x = canvasWidth / 8, y = canvasHeight / 8 * (keysStates[0] + 1)),
            Offset(x = canvasWidth / 8 * 7, y = canvasHeight / 8 * (keysStates[2] + 1)),
            Offset(x = canvasWidth *
                    (1 / 3f + if (keysStates[1] == 1) 1 / 16f else - 1 / 16f),
                   y = canvasHeight * (2 / 3f + 1 / 8f))
        )
        val resistorsOffsets = arrayOf(
            arrayOf(Offset(x = canvasWidth * (1 / 3f - 1 / 24f) - 5f,
                           y = canvasHeight * (1 / 4f + 1 / 12f) - 5f),
                    Offset(x = canvasWidth * (1 / 3f - 1 / 24f),
                           y = canvasHeight * (1 / 4f + 1 / 12f))),
            arrayOf(Offset(x = canvasWidth * (1 / 3f + 1 / 12f) - 5f,
                           y = canvasHeight * (1 / 4f - 1 / 24f) - 5f),
                    Offset(x = canvasWidth * (1 / 3f + 1 / 12f),
                           y = canvasHeight * (1 / 4f - 1 / 24f)))
        )
        val resistorsSizes = arrayOf(
            arrayOf(Size(width = canvasWidth / 12 + 10f, height = canvasHeight / 4 + 10f),
                    Size(width = canvasWidth / 12, height = canvasHeight / 4)),
            arrayOf(Size(width = canvasWidth / 4 + 10f, height = canvasHeight / 12 + 10f),
                    Size(width = canvasWidth / 4, height = canvasHeight / 12))
        )
        val pointsOffset = arrayOf(
            Offset(x = canvasWidth / 3, y = canvasHeight / 4),
            Offset(x = canvasWidth / 16 * 15, y = canvasHeight * (2 / 3f + 3 / 16f)),
            Offset(x = canvasWidth * (1 / 3f + 1 / 16f), y = canvasHeight * (2 / 3f + 3 / 16f)),
            Offset(x = canvasWidth * (1 / 3f - 1 / 16f), y = canvasHeight * (2 / 3f + 9 / 32f))
        )
        val keyCirclesOffsets = arrayOf(
            arrayOf(Offset(x = canvasWidth / 8f, y = canvasHeight / 8f),
                    Offset(x = canvasWidth / 8f, y = canvasHeight / 4f),
                    Offset(x = canvasWidth / 8f, y = canvasHeight / 8f * 3)),
            arrayOf(Offset(x = canvasWidth * (1 / 3f - 1 / 16f),
                           y = canvasHeight * (2 / 3f + 1 / 8f)),
                    Offset(x = canvasWidth * (1 / 3f + 1 / 16f),
                           y = canvasHeight * (2 / 3f + 1 / 8f))),
            arrayOf(Offset(x = canvasWidth / 8f * 7, y = canvasHeight / 8f),
                    Offset(x = canvasWidth / 8f * 7, y = canvasHeight / 4f),
                    Offset(x = canvasWidth / 8f * 7, y = canvasHeight / 8f * 3))
        )
        //lines and switchers
        for (i in linesStartOffsets.indices)
            drawLine(start = linesStartOffsets[i], end = linesEndOffset[i],
                     color = Color.Gray, strokeWidth = 5F)
        //points
        for (o in pointsOffset)
            drawCircle(center = o, color = Color.Gray, radius = 10F)
        //resistors
        for(i in resistorsOffsets.indices){
            drawRect(color = Color.Gray, topLeft = resistorsOffsets[i][0],
                     size = resistorsSizes[i][0])
            drawRect(color = backColor, topLeft = resistorsOffsets[i][1],
                     size =resistorsSizes[i][1])
        }
        //key circles
        fun drawKeyCircle(center: Offset){
            drawCircle(center = center,
                       color = Color.Gray, radius = 20F)
            drawCircle(center = center,
                       color = backColor, radius = 15F)
        }
        for (key in keyCirclesOffsets.indices)
            for (state in keyCirclesOffsets[key].indices)
                if (keysStates[key] != state)
                    drawKeyCircle(keyCirclesOffsets[key][state])
    }
    //descriptions
    Text(text = stringResource(R.string.r1), modifier = Modifier.padding(60.dp, 130.dp), color = Color.Gray)
    Text(text = stringResource(R.string.r2), modifier = Modifier.padding(140.dp, 30.dp), color = Color.Gray)
    Text(text = stringResource(R.string.comparator),
         modifier = Modifier.padding(10.dp, 12.dp), fontSize = 10.sp, color = Color.Gray)
    Text(text = stringResource(R.string.oin_minus_1),
         modifier = Modifier.padding(10.dp, 49.5.dp), fontSize = 10.sp, color = Color.Gray)
    Text(text = stringResource(R.string.oin_minus_2),
         modifier = Modifier.padding(10.dp, 87.dp), fontSize = 10.sp, color = Color.Gray)
    Box(contentAlignment = Alignment.TopEnd) {
        Text(text = stringResource(R.string.oin_plus_2),
             modifier = Modifier.padding(10.dp, 12.dp), fontSize = 10.sp, color = Color.Gray)
        Text(text = stringResource(R.string.oin_plus_1),
             modifier = Modifier.padding(10.dp, 49.5.dp), fontSize = 10.sp, color = Color.Gray)
        Text(text = stringResource(R.string.balance_resistor),
             modifier = Modifier.padding(10.dp, 87.dp), fontSize = 10.sp, color = Color.Gray)
    }
    Box(contentAlignment = Alignment.BottomStart) {
        Text(text = stringResource(R.string.pwr),
             modifier = Modifier.padding(40.dp, 47.5.dp), fontSize = 10.sp, color = Color.Gray)
        Text(text = stringResource(R.string.gnd),
             modifier = Modifier.padding(135.dp, 47.5.dp), fontSize = 10.sp, color = Color.Gray)
    }
    Box(contentAlignment = Alignment.Center){
        Column(
            Modifier
                .padding(95.dp, 40.dp, 0.dp, 0.dp)
                .shadow(2.dp)
                .padding(6.dp)) {
            ResistorField("${stringResource(R.string.r1)}:", resistance1!!){
                viewModel.setRes(0, it)
            }
            ResistorField("${stringResource(R.string.r2)}:", resistance2!!){
                viewModel.setRes(1, it)
            }
        }
    }
}

@Composable
fun ResistorField(r: String, resistance: String, callback: (String) -> Unit){
        Row(Modifier.padding(3.dp)) {
            Text(text = r, color = Color.Gray)
            BasicTextField(
                modifier = Modifier
                    .width(50.dp)
                    .shadow(1.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = TextStyle(color = Color.Gray),
                value = resistance,
                onValueChange = callback
            )
        }
}