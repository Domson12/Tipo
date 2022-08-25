package eu.tuto.tipo


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.tuto.tipo.components.InputField
import eu.tuto.tipo.ui.theme.TipoTheme
import eu.tuto.tipo.util.calculateTotalPerPerson
import eu.tuto.tipo.util.calculateTotalTip
import eu.tuto.tipo.widgets.RoundedIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                MainContent()
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    TipoTheme {
        Surface(color = MaterialTheme.colors.background) {
            content()
        }
    }
}

@Preview
@Composable
fun TopHeader(totalPerPerson: Double = 134.0) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(9.dp)
            .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))),
        color = Color(0xFFE9D7F7)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val total = "%.2f".format(totalPerPerson)
            Text(
                text = "Za osobę",
                style = MaterialTheme.typography.h5
            )
            Text(
                text = "$$total",
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}


@Preview
@Composable
fun MainContent() {
    val splitByState = remember {
        mutableStateOf(1)
    }
    val tipAmountState = remember {
        mutableStateOf(0.0)
    }
    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }
    BillForm(
        splitByState = splitByState,
        tipAmountState = tipAmountState,
        totalPerPersonState = totalPerPersonState
    ) {}
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    splitByState: MutableState<Int>,
    tipAmountState: MutableState<Double>,
    totalPerPersonState: MutableState<Double>,
    onValChange: (String) -> Unit = {},

    ) {
    val totalBillState = remember {
        mutableStateOf("")
    }
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    val sliderPositionState = remember {
        mutableStateOf(0f)
    }
    val tipPercentage = (sliderPositionState.value * 100).toInt()

    Column() {

        TopHeader(totalPerPerson = totalPerPersonState.value)

        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(corner = CornerSize(8.dp)),
            border = BorderStroke(width = 2.dp, color = Color.LightGray)
        ) {
            Column(
                modifier = Modifier.padding(6.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {

                InputField(
                    valueState = totalBillState,
                    labelId = "Wpisz cenę",
                    enabled = true,
                    isSingleLine = true,
                    onAction = KeyboardActions {
                        if (!validState) return@KeyboardActions
                        onValChange(totalBillState.value.trim())

                        keyboardController?.hide()
                    })

                if (validState) {
                    Row(
                        modifier = modifier.padding(3.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "podziel",
                            modifier = Modifier.align(
                                alignment = Alignment.CenterVertically
                            )
                        )
                        Spacer(modifier = Modifier.width(120.dp))
                        Row(
                            modifier = Modifier.padding(horizontal = 3.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            RoundedIconButton(imageVector = Icons.Default.Remove,
                                onClick = {
                                    splitByState.value =
                                        if (splitByState.value > 1) splitByState.value - 1 else 1

                                    totalPerPersonState.value = calculateTotalPerPerson(
                                        totalBill = totalBillState.value.toDouble(),
                                        splitBy = splitByState.value,
                                        tipPercentage = tipPercentage
                                    )

                                })
                            Text(
                                text = "${splitByState.value}",
                                modifier = modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 9.dp, end = 9.dp)
                            )
                            RoundedIconButton(imageVector = Icons.Default.Add,
                                onClick = {
                                    splitByState.value = splitByState.value + 1
                                    totalPerPersonState.value = calculateTotalPerPerson(
                                        totalBill = totalBillState.value.toDouble(),
                                        splitBy = splitByState.value,
                                        tipPercentage = tipPercentage
                                    )
                                })
                        }
                    }
                    //Tip Row
                    Row(
                        modifier = modifier
                            .padding(horizontal = 3.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "napiwek",
                            modifier = Modifier.align(alignment = Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.width(200.dp))
                        Text(
                            text = "$ ${tipAmountState.value}",
                            modifier = Modifier.align(alignment = Alignment.CenterVertically)
                        )
                    }
                    //Tip percentage
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(text = "$tipPercentage %")

                        Spacer(modifier = Modifier.height(14.dp))

                        //Slider
                        Slider(
                            value = sliderPositionState.value,
                            onValueChange = { newVal ->
                                sliderPositionState.value = newVal
                                tipAmountState.value =
                                    calculateTotalTip(
                                        totalBill = totalBillState.value.toDouble(),
                                        tipPercentage = tipPercentage
                                    )
                                totalPerPersonState.value = calculateTotalPerPerson(
                                    totalBill = totalBillState.value.toDouble(),
                                    splitBy = splitByState.value,
                                    tipPercentage = tipPercentage
                                )

                            },
                            modifier = Modifier.padding(
                                start = 16.dp,
                                end = 16.dp
                            ),
                            steps = 5
                        )
                    }
                } else {
                    Box() {}
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TipoTheme {
        MyApp {
            Text(text = "Hello again!")
        }
    }
}