package com.example.campominado

import org.junit.Assert.*
import org.junit.Test

class PartidaTest {
    @Test
    fun identificaOsTresNiveis() {
        assertEquals("Fácil", descobrirNivel(10, 10, 15))
        assertEquals("Médio", descobrirNivel(16, 16, 18))
        assertEquals("Difícil", descobrirNivel(30, 16, 21))
    }

    @Test
    fun dimensoesOuPorcentagensDiferentesSaoPersonalizadas() {
        assertEquals("Personalizado", descobrirNivel(10, 10, 30))
        assertEquals("Personalizado", descobrirNivel(16, 30, 21))
        assertEquals("Personalizado", descobrirNivel(3, 7, 15))
    }

    @Test
    fun venceRevelandoTodasAsCasasSegurasEmMatrizRetangular() {
        val matriz = arrayOf(intArrayOf(-1, 1, 0), intArrayOf(1, 1, 0))
        val escolheu = arrayOf(booleanArrayOf(false, true, true), booleanArrayOf(true, true, true))
        assertTrue(jogadorVenceu(matriz, escolheu))
    }

    @Test
    fun naoVenceEnquantoExistirCasaSeguraFechada() {
        val matriz = arrayOf(intArrayOf(-1, 1, 0))
        assertFalse(jogadorVenceu(matriz, arrayOf(booleanArrayOf(false, true, false))))
    }

    @Test
    fun revelarUmaMinaImpedeAVitoria() {
        val matriz = arrayOf(intArrayOf(-1, 1))
        assertFalse(jogadorVenceu(matriz, arrayOf(booleanArrayOf(true, true))))
    }

    @Test
    fun bandeiraProtegeCasaDaRevelacaoAutomatica() {
        val matriz = Array(2) { IntArray(3) }
        val escolheu = Array(2) { BooleanArray(3) }
        val bandeiras = Array(2) { BooleanArray(3) }
        bandeiras[0][1] = true
        revelarCasas(0, 0, matriz, escolheu, bandeiras, 2, 3)
        assertFalse(escolheu[0][1])
        assertFalse(jogadorVenceu(matriz, escolheu))
        bandeiras[0][1] = false
        revelarCasas(0, 1, matriz, escolheu, bandeiras, 2, 3)
        assertTrue(jogadorVenceu(matriz, escolheu))
    }

    @Test
    fun geracaoRespeitaQuantidadeDeBombasEContagemDosVizinhos() {
        for ((m, n) in listOf(2 to 7, 7 to 2)) {
            repeat(10) {
                val matriz = gerarTabuleiro(m, n, 21)
                assertEquals(Math.round(m * n * 0.21).toInt(), matriz.sumOf { linha -> linha.count { it == -1 } })
                for (i in 0 until m) for (j in 0 until n) {
                    if (matriz[i][j] != -1) {
                        val minas = (-1..1).sumOf { dl ->
                            (-1..1).count { dc ->
                                i + dl in 0 until m && j + dc in 0 until n && matriz[i + dl][j + dc] == -1
                            }
                        }
                        assertEquals(minas, matriz[i][j])
                    }
                }
            }
        }
    }
}
