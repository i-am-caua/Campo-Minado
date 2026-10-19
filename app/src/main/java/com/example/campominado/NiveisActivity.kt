package com.example.campominado

import com.example.campominado.ui.theme.CampoMinadoTheme

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class NiveisActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.DarkGray.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(Color.DarkGray.toArgb())
        )
        setContent {
            CampoMinadoTheme {
                TelaNiveis { m, n, bomba, nome ->
                    val intent = Intent(this@NiveisActivity, CampoTela::class.java).apply {
                        putExtra("m", m)
                        putExtra("n", n)
                        putExtra("bomba", bomba)
                        putExtra("nome", nome)
                    }
                    startActivity(intent)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaNiveis(onIniciarJogo: (Int, Int, Int, String) -> Unit) {

    var m by remember { mutableStateOf(0) }
    var n by remember { mutableStateOf(0) }
    var personalizado by remember { mutableStateOf(false) }
    var textoM by remember { mutableStateOf("") }
    var textoN by remember { mutableStateOf("") }
    var pedirNome by remember { mutableStateOf(false) }
    var nome by remember { mutableStateOf("") }
    var porcentagemBombas by remember { mutableStateOf(0) }
    var textoB by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Campo Minado",
                        color = Color.Cyan
                    )

                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.DarkGray,
                    titleContentColor = Color.Cyan
                ),


            )
        }

    ) { paddingValues ->

        Column(
            modifier = Modifier.fillMaxSize().background(color = Color.DarkGray).padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Escolha o nivel",
                color = Color.White,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 32.dp)

            )
            Button(
                onClick = {
                    n = 10
                    m = 10
                    porcentagemBombas = 15
                    pedirNome = true
                },
                modifier = Modifier
                    .width(200.dp)
                    .height(48.dp)
                    .padding(bottom = 10.dp)
            ) {
                Text( text = "Facil")
            }
            Button(
                onClick = {
                    n = 16
                    m = 16
                    porcentagemBombas = 18
                    pedirNome = true
                },
                modifier = Modifier
                    .width(200.dp)
                    .height(48.dp)
                    .padding(bottom = 10.dp)
            ) {
                Text( text = "Médio")
            }
            Button(
                onClick = {
                    m = 30
                    n = 16
                    porcentagemBombas = 21
                    pedirNome = true
                },
                modifier = Modifier
                    .width(200.dp)
                    .height(48.dp)
                    .padding(bottom = 10.dp)
            ) {
                Text( text = "Dificil")
            }
            Button(
                onClick = {
                    personalizado = true

                },
                modifier = Modifier
                    .width(200.dp)
                    .height(48.dp)
                    .padding(bottom = 10.dp)
            ) {
                Text( text = "Personalizado")
            }

            if (personalizado) {
                AlertDialog(
                    onDismissRequest = {personalizado = false}
                ) {
                    Column(
                        modifier = Modifier.background(color = Color.DarkGray).width(300.dp).padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {
                        OutlinedTextField(
                            value = textoM,
                            onValueChange = {
                                textoM = it
                                m = it.toIntOrNull() ?: 0
                            },
                            label = {Text(
                                "Digite o M da matriz",
                                color = Color.White
                                )},
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.LightGray
                            )
                        )
                        OutlinedTextField(
                            value = textoN,
                            onValueChange = {
                                textoN = it
                                n = it.toIntOrNull() ?: 0

                            },
                            label = {Text(
                                "Digite o N da matriz",
                                color = Color.White
                            )},
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.LightGray
                            )
                        )
                        OutlinedTextField(
                            value = textoB,
                            onValueChange = {
                                textoB = it
                                porcentagemBombas = it.toIntOrNull() ?: 0
                            },
                            singleLine = true,
                            label = {Text("Digite a % de bombas", color = Color.White)},
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.LightGray
                            )
                        )
                        Button(
                            onClick = {
                                m = textoM.toInt()
                                n = textoN.toInt()
                                porcentagemBombas = textoB.toInt()
                                personalizado = false
                                pedirNome = true
                            },
                            enabled = (textoM.toIntOrNull() ?: 0) > 0 &&
                                (textoN.toIntOrNull() ?: 0) > 0 &&
                                (textoB.toIntOrNull() ?: -1) in 0..100
                        ) {
                            Text ("Continuar")
                        }
                    }

                }
            }
            if (pedirNome) {
                AlertDialog(
                    onDismissRequest = {pedirNome = false}
                ) {
                    Column(
                        modifier = Modifier.width(300.dp).background(color = Color.DarkGray).padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedTextField(
                            value = nome,
                            onValueChange = {
                                nome = it
                            },
                            singleLine = true,
                            label = {Text(
                                "Digite seu nome",
                                color = Color.White
                            )},
                            colors = OutlinedTextFieldDefaults.colors (
                                unfocusedBorderColor = Color.LightGray
                            )

                        )
                        Button(
                            onClick = {
                                pedirNome = false
                                onIniciarJogo(m, n, porcentagemBombas, nome.trim())
                            },
                            enabled = nome.isNotBlank()
                        ) {
                            Text("Iniciar jogo")
                        }
                    }

                }
            }
        }
    }


}


