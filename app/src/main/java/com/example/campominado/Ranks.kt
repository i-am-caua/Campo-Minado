package com.example.campominado

import android.os.Bundle
import android.widget.Toast
import org.json.JSONArray
import java.io.File
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.campominado.ui.theme.CampoMinadoTheme

class Ranks : ComponentActivity() {
    val resultados = mutableListOf<ResultadoPartida>()

    fun carregarRanks() {
        try {
            val arquivo = File(filesDir, "ranks.json")
            if (!arquivo.exists()) return
            val lista = JSONArray(arquivo.readText())
            resultados.clear()
            for (i in 0 until lista.length()) {
                val partida = lista.getJSONObject(i)
                val resultado = ResultadoPartida(
                    nome = partida.getString("nome"),
                    m = partida.getInt("m"),
                    n = partida.getInt("n"),
                    nivel = partida.getString("nivel"),
                    segundos = partida.getInt("segundos"),
                    cliques = partida.getInt("cliques"),
                    porcentagemBombas = partida.getInt("porcentagemBombas"),
                    horarioVitoria = partida.optString("horarioVitoria", "")
                )
                resultados.add(resultado)
            }
            // O menor tempo aparece primeiro.
            resultados.sortBy { it.segundos }
        } catch (erro: Exception) {
            Toast.makeText(this, "Não foi possível carregar os ranks.", Toast.LENGTH_LONG).show()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.DarkGray.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(Color.DarkGray.toArgb())
        )
        carregarRanks()
        setContent {
            CampoMinadoTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Ranks") },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.DarkGray,
                                titleContentColor = Color.Cyan
                            )
                        )
                    }
                ) { paddingValues ->
                    Column(
                        modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Top 10 de cada nível • Menor tempo primeiro", color = Color.LightGray)
                        Column(
                            modifier = Modifier.weight(1f).fillMaxWidth()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            for (nivel in listOf("Fácil", "Médio", "Difícil", "Personalizado")) {
                                Text("Rank $nivel", color = Color.Cyan, fontSize = 24.sp)

                                // A lista já está ordenada pelo tempo. Pegamos até 10 deste nível.
                                val rankNivel = mutableListOf<ResultadoPartida>()
                                for (partida in resultados) {
                                    if (partida.nivel == nivel && rankNivel.size < 10) {
                                        rankNivel.add(partida)
                                    }
                                }

                                if (rankNivel.isEmpty()) {
                                    Text("Nenhuma vitória neste nível ainda.", color = Color.LightGray)
                                }
                                for (indice in rankNivel.indices) {
                                    val partida = rankNivel[indice]
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text("${indice + 1}º • ${partida.nome}", color = Color.White, fontSize = 20.sp)
                                        Text("${partida.segundos} segundos", color = Color.Cyan, fontSize = 22.sp)
                                        Text("${partida.m} × ${partida.n} • ${partida.porcentagemBombas}% de bombas", color = Color.LightGray)
                                        Text("Cliques: ${partida.cliques}", color = Color.White)
                                        Text(
                                            text = if (partida.horarioVitoria.isEmpty()) "Horário não registrado" else "Vitória: ${partida.horarioVitoria}",
                                            color = Color.LightGray
                                        )
                                    }
                                }
                            }
                        }
                        Button(onClick = { finish() }) {
                            Text("Voltar")
                        }
                    }
                }
            }
        }
    }
}
