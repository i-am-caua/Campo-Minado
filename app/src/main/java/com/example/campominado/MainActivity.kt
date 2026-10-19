package com.example.campominado

import android.content.Intent
import org.json.JSONArray
import java.io.File
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.example.campominado.ui.theme.CampoMinadoTheme

class MainActivity : ComponentActivity() {
    val resultados = mutableListOf<ResultadoPartida>()

    // Lê o JSON salvo pelo CampoTela e coloca as partidas na lista.
    fun carregarResultados() {
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
        carregarResultados()
        setContent {
            CampoMinadoTheme {

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                           title = {Text("Campo Minado", color = Color.Cyan)},
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.DarkGray,
                                titleContentColor = Color.Cyan
                            )
                        )
                    }
                ) { paddingValues ->
                    Column(
                        modifier = Modifier.fillMaxSize().padding(paddingValues).background(color = Color.DarkGray),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
                    ) {
                        Button(onClick = {
                            startActivity(Intent(this@MainActivity, NiveisActivity::class.java))
                        },
                            modifier = Modifier
                                .width(200.dp)
                                .height(48.dp)
                                .padding(bottom = 10.dp)
                        ) {
                            Text("Escolher nível")
                        }
                        Button(onClick = {
                            startActivity(Intent(this@MainActivity, Ranks::class.java))

                        },
                            modifier = Modifier
                                .width(200.dp)
                                .height(48.dp)
                                .padding(bottom = 10.dp)
                        ) {
                            Text("Ranks")
                        }
                    }
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        carregarResultados()
    }

}

