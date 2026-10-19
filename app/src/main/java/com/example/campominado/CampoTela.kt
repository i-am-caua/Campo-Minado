package com.example.campominado

import com.example.campominado.ui.theme.CampoMinadoTheme

import android.content.Context
import android.content.Intent
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.Random
import java.util.Date
import java.util.Locale
import java.text.SimpleDateFormat
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class CampoTela : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.DarkGray.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(Color.DarkGray.toArgb())
        )
        val m = intent.getIntExtra("m", 10)
        val n = intent.getIntExtra("n", 10)
        val bomba = intent.getIntExtra("bomba", 15)
        val nome = intent.getStringExtra("nome") ?: "Jogador"
        setContent {
            CampoMinadoTheme {
                CampoMinadoTela(m, n, bomba, nome)
            }
        }
    }
}
// Em Kotlin, data class representa a estrutura dos dados de uma vitória.
data class ResultadoPartida(
    val nome: String,
    val m: Int,
    val n: Int,
    val nivel: String,
    val segundos: Int,
    val cliques: Int,
    val porcentagemBombas: Int,
    val horarioVitoria: String = ""
)

fun descobrirNivel(m: Int, n: Int, porcentagemBombas: Int): String = when {
    m == 10 && n == 10 && porcentagemBombas == 15 -> "Fácil"
    m == 16 && n == 16 && porcentagemBombas == 18 -> "Médio"
    m == 30 && n == 16 && porcentagemBombas == 21 -> "Difícil"
    else -> "Personalizado"
}

fun jogadorVenceu(matriz: Array<IntArray>, escolheu: Array<BooleanArray>): Boolean {
    for (i in matriz.indices) {
        for (j in matriz[i].indices) {
            if (matriz[i][j] != -1 && !escolheu[i][j]) return false
            if (matriz[i][j] == -1 && escolheu[i][j]) return false
        }
    }
    return true
}

// Acrescenta a vitória ao JSON sem apagar as partidas anteriores.
fun salvarResultado(context: Context, nome: String, m: Int, n: Int, bomba: Int, segundos: Int, cliques: Int, horarioVitoria: String): Boolean {
    val resultado = ResultadoPartida(nome, m, n, descobrirNivel(m, n, bomba), segundos, cliques, bomba, horarioVitoria)
    try {
        val arquivo = File(context.filesDir, "ranks.json")
        val lista = if (arquivo.exists()) JSONArray(arquivo.readText()) else JSONArray()
        val partida = JSONObject()
        partida.put("nome", resultado.nome)
        partida.put("m", resultado.m)
        partida.put("n", resultado.n)
        partida.put("nivel", resultado.nivel)
        partida.put("segundos", resultado.segundos)
        partida.put("cliques", resultado.cliques)
        partida.put("porcentagemBombas", resultado.porcentagemBombas)
        partida.put("horarioVitoria", resultado.horarioVitoria)
        lista.put(partida)
        arquivo.writeText(lista.toString(2))
        return true
    } catch (erro: Exception) {
        Toast.makeText(context, "Não foi possível salvar o resultado.", Toast.LENGTH_LONG).show()
        return false
    }
}

fun gerarTabuleiro (m : Int, n : Int, bombas : Int): Array<IntArray> {
    val random = Random()
    val porcentagem = bombas.toDouble() / 100
    val quantidadeMinas = Math.round((m * n).toDouble() * porcentagem)
    var minasColocadas = 0
    val matriz = Array (m) { IntArray (n) }

    while (minasColocadas < quantidadeMinas) {
        for (i in 0 until m) {
            for (j in 0 until n) {
                if (minasColocadas < quantidadeMinas && random.nextInt(100) < bombas && matriz[i][j] != -1) {
                    matriz[i][j] = -1
                    minasColocadas++
                    for (dl in -1..1) {
                        for (dc in -1..1) {
                            val vizinhoLinha = i + dl
                            val vizinhoColuna = j + dc
                            if (vizinhoLinha in 0 until m && vizinhoColuna in 0 until n) {
                                if (matriz[vizinhoLinha][vizinhoColuna] != -1) {
                                    matriz[vizinhoLinha][vizinhoColuna]++
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    return matriz
}
fun revelarCasas (i : Int, j : Int, matriz : Array<IntArray>, escolheu : Array<BooleanArray>, bandeiras : Array<BooleanArray>, m : Int, n : Int) {
    if (i !in 0 until m || j !in 0 until n || escolheu[i][j] || bandeiras[i][j]) return
    escolheu[i][j] = true
    if (matriz[i][j] == 0 || matriz[i][j] == 1) {
        for (dl in -1..1) {
            for (dc in -1..1) {
                val vizinhoLinha = i + dl
                val vizinhoColuna = j + dc
                if (vizinhoLinha in 0 until m && vizinhoColuna in 0 until n && !escolheu[vizinhoLinha][vizinhoColuna]) {
                    if (matriz[vizinhoLinha][vizinhoColuna] == 0 || matriz[vizinhoLinha][vizinhoColuna] == 1) {
                        revelarCasas(vizinhoLinha, vizinhoColuna, matriz, escolheu, bandeiras, m, n)
                    }
                }
            }
        }
    }
}
@Composable
fun Cronometro(segundos: Int) {
    Text(text = "%02d:%02d".format(segundos / 60, segundos % 60))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoMinadoTela(
    m: Int,
    n: Int,
    bomba: Int,
    nome: String
) {
    val context = LocalContext.current
    var matriz by remember { mutableStateOf(gerarTabuleiro(m, n, bomba)) }
    var escolheu by remember { mutableStateOf(Array (m) { BooleanArray (n)}) }
    var bandeiras by remember { mutableStateOf(Array(m) { BooleanArray(n) }) }
    var modoBandeira by remember { mutableStateOf(false) }
    var perdeu by remember { mutableStateOf(false) }
    var cliques by remember { mutableStateOf(0) }
    var venceu by remember { mutableStateOf(false) }
    var segundos by remember { mutableStateOf(0) }
    var resultadoSalvo by remember { mutableStateOf(false) }
    var horarioVitoria by remember { mutableStateOf("") }

    LaunchedEffect(cliques > 0, perdeu, venceu) {
        while (cliques > 0 && !perdeu && !venceu) {
            delay(1000L)
            segundos++
        }
    }

    if (venceu || perdeu) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(if (venceu) "Você venceu, $nome!" else "Você perdeu, $nome!") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Nível: ${descobrirNivel(m, n, bomba)}")
                    Text("Tempo: ${segundos}s • Cliques: $cliques")
                    if (venceu && !resultadoSalvo) {
                        Text("O resultado ainda não foi salvo.")
                        TextButton(onClick = {
                            resultadoSalvo = salvarResultado(context, nome, m, n, bomba, segundos, cliques, horarioVitoria)
                        }) {
                            Text("Tentar salvar novamente")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    enabled = !venceu || resultadoSalvo,
                    onClick = {
                        matriz = gerarTabuleiro(m, n, bomba)
                        escolheu = Array(m) { BooleanArray(n) }
                        bandeiras = Array(m) { BooleanArray(n) }
                        modoBandeira = false
                        perdeu = false
                        venceu = false
                        cliques = 0
                        segundos = 0
                        resultadoSalvo = false
                        horarioVitoria = ""
                    }
                ) { Text("Reiniciar") }
            },
            dismissButton = {
                if (venceu) {
                    TextButton(enabled = resultadoSalvo, onClick = {
                        context.startActivity(Intent(context, Ranks::class.java))
                    }) {
                        Text("Ver ranks")
                    }
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = when {
                                perdeu -> "Perdeu"
                                venceu -> "Vitória"
                                else -> "Campo Minado"
                            },
                            color = if (perdeu) Color.Red else Color.Cyan
                        )
                        Text("Cliques: $cliques\tTempo:")
                        Cronometro(segundos)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.DarkGray,
                    titleContentColor = Color.White
                )
            )
        }

    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {
            Button(
                onClick = { modoBandeira = !modoBandeira },
                enabled = !perdeu && !venceu
            ) {
                Text(if (modoBandeira) "Modo: 🚩 Bandeira" else "Modo: Revelar")
            }
            for (i in 0 until m) {
                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) {
                    for (j in 0 until n) {
                        val foiEscolhido = escolheu[i][j]
                        val temBandeira = bandeiras[i][j]
                        val valor = matriz[i][j]
                        val corFundo = when {
                            !foiEscolhido -> Color.LightGray
                            valor == 0 -> Color.Cyan
                            valor == -1 -> Color.Red
                            else -> Color.White
                        }
                        val texto = when {
                            temBandeira -> "🚩"
                            !foiEscolhido -> ""
                            valor == -1 -> "-1"
                            else -> valor.toString()
                        }

                        Box(
                            modifier = Modifier.weight(1f).fillMaxSize().background(corFundo)
                                .border(0.5.dp, Color.Gray)
                                .clickable(enabled = !foiEscolhido && !perdeu && !venceu && (modoBandeira || !temBandeira)) {
                                    if (modoBandeira) {
                                        val novasBandeiras = Array(bandeiras.size) { r -> bandeiras[r].clone() }
                                        novasBandeiras[i][j] = !temBandeira
                                        bandeiras = novasBandeiras
                                        return@clickable
                                    }
                                    cliques++
                                    val novoEscolheu = Array(escolheu.size) { r -> escolheu[r].clone() }
                                    if (valor == -1) {
                                        novoEscolheu[i][j] = true
                                        escolheu = novoEscolheu
                                        perdeu = true
                                    } else {
                                        revelarCasas(i, j, matriz, novoEscolheu, bandeiras, m, n)
                                        escolheu = novoEscolheu
                                        if (jogadorVenceu(matriz, novoEscolheu)) {
                                            // Guarda o horário exato da vitória, mesmo se precisar tentar salvar novamente.
                                            horarioVitoria = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
                                            venceu = true
                                            resultadoSalvo = salvarResultado(context, nome, m, n, bomba, segundos, cliques, horarioVitoria)
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = texto,
                                fontSize = 14.sp,

                                color = if (valor == -1 && foiEscolhido) Color.White else Color.Black
                            )
                        }
                    }
                }
            }
        }
    }


}
