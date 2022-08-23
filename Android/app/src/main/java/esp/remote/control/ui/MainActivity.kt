package esp.remote.control.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import esp.remote.control.ui.theme.MatrixControllerTheme
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import esp.remote.control.R
import esp.remote.control.data.Connection

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MatrixControllerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainView()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainView(viewModel: MainActivityViewModel = viewModel()) {
    val address by viewModel.address.observeAsState()
    val addressCorrect by viewModel.addressCorrect.observeAsState()
    val ids by viewModel.idsList.observeAsState()
    val selectedId by viewModel.selectedId.observeAsState()

    BackdropScaffold(
        backLayerBackgroundColor = MaterialTheme.colorScheme.tertiary,
        frontLayerBackgroundColor = MaterialTheme.colorScheme.surface,
        backLayerContent = { Column(modifier = Modifier.padding(8.dp)) {
            TextField(value = address!!, onValueChange = { viewModel.setAddress(it) },
                      label = { Text(text = stringResource(R.string.address))},
                      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                      isError = !addressCorrect!!,
                      colors = TextFieldDefaults.textFieldColors(
                          cursorColor = MaterialTheme.colorScheme.onTertiary,
                          focusedIndicatorColor = MaterialTheme.colorScheme.onTertiary,
                          textColor = Color.White,
                          unfocusedLabelColor = MaterialTheme.colorScheme.onSecondary,
                          focusedLabelColor = MaterialTheme.colorScheme.onTertiary,
                          errorLabelColor = Color.Red
                      ))
        }
            val lContext = LocalContext.current
            Button(
                onClick = {
                    viewModel.checkAndConnect{
                        Toast.makeText(lContext, when(it){
                            Connection.SUCCESS -> lContext.getString(R.string.msg_success)
                            Connection.FAIL -> lContext.getString(R.string.msg_fail)
                            else -> lContext.getString(R.string.msg_unknown)
                        }, Toast.LENGTH_SHORT).show()
                    }
                }, colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colorScheme.primary, contentColor = Color.White
                ), modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.btn_connect))
            }
        },
        frontLayerContent = { LazyColumn(modifier = Modifier.fillMaxWidth(),
                                         horizontalAlignment = Alignment.CenterHorizontally) {
            item {
                Surface(
                    modifier = Modifier
                        .padding(20.dp)
                        .width(300.dp)
                        .height(300.dp),
                    shape = RoundedCornerShape(20.dp),
                    shadowElevation = 5.dp
                ) {
                    Circuit(viewModel)
                }
                ids?.let{
                    Column(modifier = Modifier.height(150.dp).fillMaxWidth(),
                           horizontalAlignment = Alignment.CenterHorizontally) {
                        LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 80.dp)){
                            items(it.size){ index ->
                                Button(onClick = { viewModel.setId(it[index]) },
                                       modifier = Modifier.padding(10.dp),
                                       colors = ButtonDefaults.buttonColors(
                                           backgroundColor = if (
                                               selectedId !=null
                                               && selectedId == it[index]
                                           )
                                               MaterialTheme.colorScheme.onPrimary
                                           else MaterialTheme.colorScheme.primary,
                                           contentColor = MaterialTheme.colorScheme.surface)) {
                                    Text(text = it[index].toString())
                                }
                            }
                        }
                    }
                }
            }
        } },
        appBar = { TopAppBar(
            contentColor = Color.White, backgroundColor = MaterialTheme.colorScheme.secondary,
            title = { Text(text = stringResource(id = R.string.app_name))}
        ) }
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MatrixControllerTheme {
        MainView()
        //Circuit()
    }
}