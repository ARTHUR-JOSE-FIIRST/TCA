import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import javax.sound.sampled.*;

public class SudokuTCA {

    // método de som Ló
    public static void tocarSom(String caminhoArquivoSom, boolean esperarTerminar) {

        try {

            File soundFile = new File(caminhoArquivoSom);
            if (!soundFile.exists()) {
                System.out.println("Arquivo de som não encontrado: " + caminhoArquivoSom);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);

            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);

            Clip audioClip = (Clip) AudioSystem.getLine(info);
            audioClip.open(audioStream);

            audioStream.close();

            audioClip.addLineListener(evento -> {
                if (evento.getType() == LineEvent.Type.STOP) {
                    audioClip.close();
                }
            });

            audioClip.start();

            if (esperarTerminar) {
                Thread.sleep(audioClip.getMicrosecondLength() / 1000);
            }
        } catch (UnsupportedAudioFileException e) {
            System.err.println("O formato de áudio não é suportado.");
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            System.err.println("Linha de áudio não disponível.");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo de áudio.");
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("O programa foi interrompido.");
            e.printStackTrace();
        }
    }

    public static int lerValorInteiro() {
        int numero = LER.nextInt();
        return numero;
    }

    public static String lerValorTexto() {
        String texto = LER.nextLine();
        return texto;
    }

    static Scanner LER = new Scanner(System.in);

    // Método para verificar se o número digitado ja existe na linha
    public static boolean valorExisteNaLinha(int[][] tabuleiro, int i, int numero) {
        for (int j = 0; j < tabuleiro[i].length; j++) {
            if (tabuleiro[i][j] == numero) {
                return true;
            }
        }
        return false;
    }

    // Método para verificar se o número digitado ja existe na coluna
    public static boolean valorExisteNaColuna(int[][] tabuleiro, int j, int numero) {
        for (int i = 0; i < tabuleiro.length; i++) {
            if (tabuleiro[i][j] == numero) {
                return true;
            }
        }
        return false;
    }

    // Método para verificar se o número digitado ja existe no bloco
    public static boolean valorExisteNoBloco(int[][] tabuleiro, int i, int j, int numero, int blocoLinha,
            int blocoColuna) {

        int inicioLinha = (i / blocoLinha) * blocoLinha;
        int inicioColuna = (j / blocoColuna) * blocoColuna;

        for (int k = 0; k < blocoLinha; k++) {
            for (int l = 0; l < blocoColuna; l++) {
                if (tabuleiro[inicioLinha + k][inicioColuna + l] == numero) {
                    return true;
                }
            }
        }
        return false;
    }

    // método pra imprimir o tábuleiro
    public static void imprimirTabuleiro(int[][] tabuleiro, int linhaAtual, int colunaAtual, int vida, int blocoLinha) {

        System.out.println("Vidas: " + vida);

        if (tabuleiro.length == 6) {
            System.out.println("+----------+----------+");
        } else if (tabuleiro.length == 9) {
            System.out.println("+----------+----------+----------+");
        }

        for (int i = 0; i < tabuleiro.length; i++) {

            for (int j = 0; j < tabuleiro[i].length; j++) {

                if (j == 0) {
                    System.out.print("| ");
                }

                String valor;

                if (tabuleiro[i][j] == 0) {
                    valor = ".";
                } else {
                    valor = String.valueOf(tabuleiro[i][j]);
                }

                if (i == linhaAtual && j == colunaAtual) {
                    System.out.print("\033[32m " + valor + " \033[0m");
                } else {
                    System.out.print(" " + valor + " ");
                }

                if ((j + 1) % 3 == 0) {
                    System.out.print("| ");
                }
            }

            System.out.println();

            if ((i + 1) % blocoLinha == 0) {
                if (tabuleiro.length == 6) {
                    System.out.println("+----------+----------+");
                } else if (tabuleiro.length == 9) {
                    System.out.println("+----------+----------+----------+");
                }
            }
        }
    }

    public static void limparTerminal() {
        System.out.print("\033[H\033[2J\033[3J");
        System.out.flush();
    }

    // Método de movimentação e preenchimento do tabluleiro
    public static int preencherTabulerio(int[][] tabuleiro, boolean[][] tabuleirofixos,
            int[] posicao, String tecla, int vida, int blocoColuna, int blocoLinha, int casas) {

        int numero;

        if (casas == 6) {

            switch (tecla) {

                case "w":
                    if (posicao[0] > 0) {
                        posicao[0]--;
                    }
                    break;

                case "s":
                    if (posicao[0] < (casas - 1)) {
                        posicao[0]++;
                    }
                    break;

                case "a":
                    if (posicao[1] > 0) {
                        posicao[1]--;
                    }
                    break;

                case "d":
                    if (posicao[1] < (casas - 1)) {
                        posicao[1]++;
                    }
                    break;

                case "1":
                    numero = 1;

                    if (!tabuleirofixos[posicao[0]][posicao[1]]
                            && tabuleiro[posicao[0]][posicao[1]] == 0) {

                        if (!valorExisteNaLinha(tabuleiro, posicao[0], numero)
                                && !valorExisteNaColuna(tabuleiro, posicao[1], numero)
                                && !valorExisteNoBloco(tabuleiro, posicao[0], posicao[1], numero, blocoLinha,
                                        blocoColuna)) {

                            tabuleiro[posicao[0]][posicao[1]] = numero;

                        } else {
                            vida--;
                        }
                    }
                    break;
                case "2":
                    numero = 2;

                    if (!tabuleirofixos[posicao[0]][posicao[1]]
                            && tabuleiro[posicao[0]][posicao[1]] == 0) {

                        if (!valorExisteNaLinha(tabuleiro, posicao[0], numero)
                                && !valorExisteNaColuna(tabuleiro, posicao[1], numero)
                                && !valorExisteNoBloco(tabuleiro, posicao[0], posicao[1], numero, blocoLinha,
                                        blocoColuna)) {

                            tabuleiro[posicao[0]][posicao[1]] = numero;

                        } else {
                            vida--;
                        }
                    }
                    break;
                case "3":
                    numero = 3;

                    if (!tabuleirofixos[posicao[0]][posicao[1]]
                            && tabuleiro[posicao[0]][posicao[1]] == 0) {

                        if (!valorExisteNaLinha(tabuleiro, posicao[0], numero)
                                && !valorExisteNaColuna(tabuleiro, posicao[1], numero)
                                && !valorExisteNoBloco(tabuleiro, posicao[0], posicao[1], numero, blocoLinha,
                                        blocoColuna)) {

                            tabuleiro[posicao[0]][posicao[1]] = numero;

                        } else {
                            vida--;
                        }
                    }
                    break;
                case "4":
                    numero = 4;

                    if (!tabuleirofixos[posicao[0]][posicao[1]]
                            && tabuleiro[posicao[0]][posicao[1]] == 0) {

                        if (!valorExisteNaLinha(tabuleiro, posicao[0], numero)
                                && !valorExisteNaColuna(tabuleiro, posicao[1], numero)
                                && !valorExisteNoBloco(tabuleiro, posicao[0], posicao[1], numero, blocoLinha,
                                        blocoColuna)) {

                            tabuleiro[posicao[0]][posicao[1]] = numero;

                        } else {
                            vida--;
                        }
                    }
                    break;
                case "5":
                    numero = 5;

                    if (!tabuleirofixos[posicao[0]][posicao[1]]
                            && tabuleiro[posicao[0]][posicao[1]] == 0) {

                        if (!valorExisteNaLinha(tabuleiro, posicao[0], numero)
                                && !valorExisteNaColuna(tabuleiro, posicao[1], numero)
                                && !valorExisteNoBloco(tabuleiro, posicao[0], posicao[1], numero, blocoLinha,
                                        blocoColuna)) {

                            tabuleiro[posicao[0]][posicao[1]] = numero;

                        } else {
                            vida--;
                        }
                    }
                    break;
                case "6":

                    numero = 6;

                    if (!tabuleirofixos[posicao[0]][posicao[1]]
                            && tabuleiro[posicao[0]][posicao[1]] == 0) {

                        if (!valorExisteNaLinha(tabuleiro, posicao[0], numero)
                                && !valorExisteNaColuna(tabuleiro, posicao[1], numero)
                                && !valorExisteNoBloco(tabuleiro, posicao[0], posicao[1], numero, blocoLinha,
                                        blocoColuna)) {

                            tabuleiro[posicao[0]][posicao[1]] = numero;

                        } else {
                            vida--;
                        }
                    }
                    break;

                case "0":

                    if (!tabuleirofixos[posicao[0]][posicao[1]]) {
                        tabuleiro[posicao[0]][posicao[1]] = 0;
                    }
                    break;
            }
        } else if (casas == 9) {

            switch (tecla) {

                case "w":
                    if (posicao[0] > 0) {
                        posicao[0]--;
                    }
                    break;

                case "s":
                    if (posicao[0] < (casas - 1)) {
                        posicao[0]++;
                    }
                    break;

                case "a":
                    if (posicao[1] > 0) {
                        posicao[1]--;
                    }
                    break;

                case "d":
                    if (posicao[1] < (casas - 1)) {
                        posicao[1]++;
                    }
                    break;

                case "1":
                    numero = 1;

                    if (!tabuleirofixos[posicao[0]][posicao[1]]
                            && tabuleiro[posicao[0]][posicao[1]] == 0) {

                        if (!valorExisteNaLinha(tabuleiro, posicao[0], numero)
                                && !valorExisteNaColuna(tabuleiro, posicao[1], numero)
                                && !valorExisteNoBloco(tabuleiro, posicao[0], posicao[1], numero, blocoLinha,
                                        blocoColuna)) {

                            tabuleiro[posicao[0]][posicao[1]] = numero;

                        } else {
                            vida--;
                        }
                    }
                    break;
                case "2":
                    numero = 2;

                    if (!tabuleirofixos[posicao[0]][posicao[1]]
                            && tabuleiro[posicao[0]][posicao[1]] == 0) {

                        if (!valorExisteNaLinha(tabuleiro, posicao[0], numero)
                                && !valorExisteNaColuna(tabuleiro, posicao[1], numero)
                                && !valorExisteNoBloco(tabuleiro, posicao[0], posicao[1], numero, blocoLinha,
                                        blocoColuna)) {

                            tabuleiro[posicao[0]][posicao[1]] = numero;

                        } else {
                            vida--;
                        }
                    }
                    break;
                case "3":
                    numero = 3;

                    if (!tabuleirofixos[posicao[0]][posicao[1]]
                            && tabuleiro[posicao[0]][posicao[1]] == 0) {

                        if (!valorExisteNaLinha(tabuleiro, posicao[0], numero)
                                && !valorExisteNaColuna(tabuleiro, posicao[1], numero)
                                && !valorExisteNoBloco(tabuleiro, posicao[0], posicao[1], numero, blocoLinha,
                                        blocoColuna)) {

                            tabuleiro[posicao[0]][posicao[1]] = numero;

                        } else {
                            vida--;
                        }
                    }
                    break;
                case "4":
                    numero = 4;

                    if (!tabuleirofixos[posicao[0]][posicao[1]]
                            && tabuleiro[posicao[0]][posicao[1]] == 0) {

                        if (!valorExisteNaLinha(tabuleiro, posicao[0], numero)
                                && !valorExisteNaColuna(tabuleiro, posicao[1], numero)
                                && !valorExisteNoBloco(tabuleiro, posicao[0], posicao[1], numero, blocoLinha,
                                        blocoColuna)) {

                            tabuleiro[posicao[0]][posicao[1]] = numero;

                        } else {
                            vida--;
                        }
                    }
                    break;
                case "5":
                    numero = 5;

                    if (!tabuleirofixos[posicao[0]][posicao[1]]
                            && tabuleiro[posicao[0]][posicao[1]] == 0) {

                        if (!valorExisteNaLinha(tabuleiro, posicao[0], numero)
                                && !valorExisteNaColuna(tabuleiro, posicao[1], numero)
                                && !valorExisteNoBloco(tabuleiro, posicao[0], posicao[1], numero, blocoLinha,
                                        blocoColuna)) {

                            tabuleiro[posicao[0]][posicao[1]] = numero;

                        } else {
                            vida--;
                        }
                    }
                    break;
                case "6":

                    numero = 6;

                    if (!tabuleirofixos[posicao[0]][posicao[1]]
                            && tabuleiro[posicao[0]][posicao[1]] == 0) {

                        if (!valorExisteNaLinha(tabuleiro, posicao[0], numero)
                                && !valorExisteNaColuna(tabuleiro, posicao[1], numero)
                                && !valorExisteNoBloco(tabuleiro, posicao[0], posicao[1], numero, blocoLinha,
                                        blocoColuna)) {

                            tabuleiro[posicao[0]][posicao[1]] = numero;

                        } else {
                            vida--;
                        }
                    }
                    break;

                case "7":
                    numero = 7;

                    if (!tabuleirofixos[posicao[0]][posicao[1]]
                            && tabuleiro[posicao[0]][posicao[1]] == 0) {

                        if (!valorExisteNaLinha(tabuleiro, posicao[0], numero)
                                && !valorExisteNaColuna(tabuleiro, posicao[1], numero)
                                && !valorExisteNoBloco(tabuleiro, posicao[0], posicao[1], numero, blocoLinha,
                                        blocoColuna)) {

                            tabuleiro[posicao[0]][posicao[1]] = numero;

                        } else {
                            vida--;
                        }
                    }
                    break;

                case "8":
                    numero = 8;

                    if (!tabuleirofixos[posicao[0]][posicao[1]]
                            && tabuleiro[posicao[0]][posicao[1]] == 0) {

                        if (!valorExisteNaLinha(tabuleiro, posicao[0], numero)
                                && !valorExisteNaColuna(tabuleiro, posicao[1], numero)
                                && !valorExisteNoBloco(tabuleiro, posicao[0], posicao[1], numero, blocoLinha,
                                        blocoColuna)) {

                            tabuleiro[posicao[0]][posicao[1]] = numero;

                        } else {
                            vida--;
                        }
                    }
                    break;

                case "9":
                    numero = 9;

                    if (!tabuleirofixos[posicao[0]][posicao[1]]
                            && tabuleiro[posicao[0]][posicao[1]] == 0) {

                        if (!valorExisteNaLinha(tabuleiro, posicao[0], numero)
                                && !valorExisteNaColuna(tabuleiro, posicao[1], numero)
                                && !valorExisteNoBloco(tabuleiro, posicao[0], posicao[1], numero, blocoLinha,
                                        blocoColuna)) {

                            tabuleiro[posicao[0]][posicao[1]] = numero;

                        } else {
                            vida--;
                        }
                    }
                    break;

                case "0":

                    if (!tabuleirofixos[posicao[0]][posicao[1]]) {
                        tabuleiro[posicao[0]][posicao[1]] = 0;
                    }
                    break;
            }
        }

        return vida;
    }

    // verificar quando o jogador venceu
    public static boolean jogoVencido(int[][] tabuleiro, int casas) {
        for (int i = 0; i < casas; i++) {
            for (int j = 0; j < casas; j++) {
                if (tabuleiro[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    // define as posiçoes ja preenchidas/fixas
    public static void sortearPosicoesFixas(int[][] tabuleiro,
            boolean[][] tabuleirofixos, int quantidadeFixos, int casas) {

        Random random = new Random();

        int quantidade = 0;

        while (quantidade < quantidadeFixos) {

            int linha = random.nextInt(casas);
            int coluna = random.nextInt(casas);

            if (!tabuleirofixos[linha][coluna]) {

                tabuleirofixos[linha][coluna] = true;
                quantidade++;
            }
        }

        for (int i = 0; i < casas; i++) {
            for (int j = 0; j < casas; j++) {

                if (!tabuleirofixos[i][j]) {
                    tabuleiro[i][j] = 0;
                }
            }
        }
    }

    public class CoresConsole {
        public static final String RESET = "\u001B[0m";
        public static final String VERMELHO = "\u001B[31m";
        public static final String VERDE = "\u001B[32m";
        public static final String AMARELO = "\u001B[33m";
        public static final String AZUL = "\u001B[34m";
        public static final String ROXO = "\u001B[35m";
        public static final String CIANO = "\u001B[36m";
        public static final String CINZA = "\u001B[90m";
        public static final String VERMELHO_BRILHANTE = "\u001B[91m";
        public static final String VERDE_BRILHANTE = "\u001B[92m";
        public static final String AMARELO_BRILHANTE = "\u001B[93m";
        public static final String AZUL_BRILHANTE = "\u001B[94m";
        public static final String ROXO_BRILHANTE = "\u001B[95m";
        public static final String CIANO_BRILHANTE = "\u001B[96m";
        public static final String BRANCO_BRILHANTE = "\u001B[97m";

        public static void main(String[] args) {

            limparTerminal();

            System.out.println("╔══════════════════════════════════════════════════════════╗\n" + //
                    "║                                                          ║\n" + //
                    "║      " + AZUL + "____  _   _ ____   ___  _  ___   _" + RESET + "                  ║\n" + //
                    "║     " + AZUL + "/ ___|| | | |  _ \\ / _ \\| |/ / | | |" + RESET + "                 ║\n" + //
                    "║     " + AZUL + "\\___ \\| | | | | | | | | | ' /| | | |" + RESET + "                 ║\n" + //
                    "║      " + AZUL + "___) | |_| | |_| | |_| | . \\| |_| |" + RESET + "                 ║\n" + //
                    "║     " + AZUL + "|____/ \\___/|____/ \\___/|_|\\_\\\\___/" + RESET + "                  ║\n" + //
                    "║                                                          ║\n" + //
                    "╠══════════════════════════════════════════════════════════╣\n" + //
                    "║                                                          ║\n" + //
                    "║   " + VERMELHO_BRILHANTE + "SELECIONE O TAMANHO DO TABULEIRO:" + RESET + "                      ║\n" + //
                    "║                                                          ║\n" + //
                    "║   " + AZUL_BRILHANTE + "[ 1 ]  Tabuleiro 6x6  (Blocos 2x3 - Números de 1 a 6)" + RESET + "  ║\n" + //
                    "║          " + CIANO + "┌─────┬─────┐" + RESET + "                                   ║\n" + //
                    "║          " + CIANO + "│1 2 3│4 5 6│" + RESET + "                                   ║\n" + //
                    "║          " + CIANO + "│4 5 6│1 2 3│" + RESET + "                                   ║\n" + //
                    "║          " + CIANO + "├─────┼─────┤" + RESET + "                                   ║\n" + //
                    "║          " + CIANO + "│2 3 1│5 6 4│" + RESET + "                                   ║\n" + //
                    "║          " + CIANO + "│5 6 4│2 3 1│" + RESET + "                                   ║\n" + //
                    "║          " + CIANO + "├─────┼─────┤" + RESET + "                                   ║\n" + //
                    "║          " + CIANO + "│3 1 2│6 4 5│" + RESET + "                                   ║\n" + //
                    "║          " + CIANO + "│6 4 5│3 1 2│" + RESET + "                                   ║\n" + //
                    "║          " + CIANO + "└─────┴─────┘" + RESET + "                                   ║\n" + //
                    "║                                                          ║\n" + //
                    "║   " + AZUL + "[ 2 ]  Tabuleiro 9x9  (Blocos 3x3 - Números de 1 a 9)" + RESET + "  ║\n" + //
                    "║          " + CIANO + "┌─────┬─────┬─────┐" + RESET + "                             ║\n" + //
                    "║          " + CIANO + "│1 2 3│4 5 6│7 8 9│" + RESET + "                             ║\n" + //
                    "║          " + CIANO + "│4 5 6│7 8 9│1 2 3│" + RESET + "                             ║\n" + //
                    "║          " + CIANO + "│7 8 9│1 2 3│4 5 6│" + RESET + "                             ║\n" + //
                    "║          " + CIANO + "├─────┼─────┼─────┤" + RESET + "                             ║\n" + //
                    "║          " + CIANO + "│2 3 1│5 6 4│8 9 7│" + RESET + "                             ║\n" + //
                    "║          " + CIANO + "│5 6 4│8 9 7│2 3 1│" + RESET + "                             ║\n" + //
                    "║          " + CIANO + "│8 9 7│2 3 1│5 6 4│" + RESET + "                             ║\n" + //
                    "║          " + CIANO + "├─────┼─────┼─────┤" + RESET + "                             ║\n" + //
                    "║          " + CIANO + "│3 1 2│6 4 5│9 7 8│" + RESET + "                             ║\n" + //
                    "║          " + CIANO + "│6 4 5│9 7 8│3 1 2│" + RESET + "                             ║\n" + //
                    "║          " + CIANO + "│9 7 8│3 1 2│6 4 5│" + RESET + "                             ║\n" + //
                    "║          " + CIANO + "└─────┴─────┴─────┘" + RESET + "                             ║\n" + //
                    "║                                                          ║\n" + //
                    "║                                                          ║\n" + //
                    "║                                                          ║\n" + //
                    "╚══════════════════════════════════════════════════════════╝\n" + //
                    "Digite sua opção: ");

            int opcao = lerValorInteiro();

            if (opcao == 2) {

                int casas = 9;
                int blocoLinha = 3;
                int blocoColuna = 3;

                Random random = new Random();

                int quantidadeFixos = random.nextInt(20) + 20;

                int vida = casas;

                boolean[][] tabuleiro9x9fixos = new boolean[9][9];

                int[][] tabuleiro9x9 = {
                        { 5, 3, 4, 6, 7, 8, 9, 1, 2 },
                        { 6, 7, 2, 1, 9, 5, 3, 4, 8 },
                        { 1, 9, 8, 3, 4, 2, 5, 6, 7 },
                        { 8, 5, 9, 7, 6, 1, 4, 2, 3 },
                        { 4, 2, 6, 8, 5, 3, 7, 9, 1 },
                        { 7, 1, 3, 9, 2, 4, 8, 5, 6 },
                        { 9, 6, 1, 5, 3, 7, 2, 8, 4 },
                        { 2, 8, 7, 4, 1, 9, 6, 3, 5 },
                        { 3, 4, 5, 2, 8, 6, 1, 7, 9 }
                };

                int[] posicao = { 0, 0 };

                sortearPosicoesFixas(tabuleiro9x9, tabuleiro9x9fixos, quantidadeFixos, casas);

                while (vida > 0 && !jogoVencido(tabuleiro9x9, casas)) {

                    limparTerminal();

                    imprimirTabuleiro(tabuleiro9x9, posicao[0], posicao[1], vida, blocoLinha);

                    System.out.println("\nUse W A S D para se movimentar");
                    System.out.println("Digite números de 1-" + casas);
                    System.out.println("Use 0 para apagar um número");

                    String tecla = lerValorTexto();

                    vida = preencherTabulerio(
                            tabuleiro9x9,
                            tabuleiro9x9fixos,
                            posicao,
                            tecla,
                            vida,
                            blocoColuna,
                            blocoLinha,
                            casas);
                }

                limparTerminal();

                if (jogoVencido(tabuleiro9x9, casas)) {

                    System.out.println("█   █ █████ █   █  ███  █████ █   █    █ █ \n" + //
                            "█   █ █     ██  █ █     █     █   █    █ █ \n" + //
                            "█   █ ████  █ █ █ █     ████  █   █    █ █ \n" + //
                            " █ █  █     █  ██ █     █     █   █        \n" + //
                            "  █   █████ █   █  ███  █████  ███     █ █ ");

                    tocarSom("sons/Vitoria.wav", true);

                    tocarSom("sons/MusicaVitoria.wav", true);

                } else {

                    System.out.println("█   █  ███   ███  █████    ████  █████ ████  ████  █████ █   █ █ █ \n" + //
                            "█   █ █   █ █     █        █   █ █     █   █ █   █ █     █   █ █ █ \n" + //
                            "█   █ █   █ █     ████     ████  ████  ████  █   █ ████  █   █ █ █ \n" + //
                            " █ █  █   █ █     █        █     █     █  █  █   █ █     █   █     \n" + //
                            "  █    ███   ███  █████    █     █████ █   █ ████  █████  ███  █ █ ");

                    tocarSom("sons/Derrota.wav", true);
                }
            } else if (opcao == 1) {

                int casas = 6;
                int blocoLinha = 2;
                int blocoColuna = 3;

                Random random = new Random();

                int quantidadeFixos = random.nextInt(10) + 10;

                int vida = casas;

                boolean[][] tabuleiro6x6fixos = new boolean[6][6];

                int[][] tabuleiro6x6 = {
                        { 1, 3, 6, 4, 5, 2 },
                        { 2, 4, 5, 6, 1, 3 },
                        { 6, 5, 3, 1, 2, 4 },
                        { 4, 1, 2, 3, 6, 5 },
                        { 5, 6, 4, 2, 3, 1 },
                        { 3, 2, 1, 5, 4, 6 }
                };

                int[] posicao = { 0, 0 };

                sortearPosicoesFixas(tabuleiro6x6, tabuleiro6x6fixos, quantidadeFixos, casas);

                while (vida > 0 && !jogoVencido(tabuleiro6x6, casas)) {

                    limparTerminal();

                    imprimirTabuleiro(tabuleiro6x6, posicao[0], posicao[1], vida, blocoLinha);

                    System.out.println("\nUse W A S D para se movimentar");
                    System.out.println("Digite números de 1-" + casas);
                    System.out.println("Use 0 para apagar um número");

                    String tecla = lerValorTexto();

                    vida = preencherTabulerio(
                            tabuleiro6x6,
                            tabuleiro6x6fixos,
                            posicao,
                            tecla,
                            vida,
                            blocoColuna,
                            blocoLinha,
                            casas);
                }

                limparTerminal();

                if (jogoVencido(tabuleiro6x6, casas)) {

                    System.out.println("█   █ █████ █   █  ███  █████ █   █    █ █ \n" + //
                            "█   █ █     ██  █ █     █     █   █    █ █ \n" + //
                            "█   █ ████  █ █ █ █     ████  █   █    █ █ \n" + //
                            " █ █  █     █  ██ █     █     █   █        \n" + //
                            "  █   █████ █   █  ███  █████  ███     █ █ ");

                    tocarSom("sons/Vitoria.wav", true);
                    tocarSom("sons/MusicaVitoria.wav", true);

                } else {

                    System.out.println("█   █  ███   ███  █████    ████  █████ ████  ████  █████ █   █ █ █ \n" + //
                            "█   █ █   █ █     █        █   █ █     █   █ █   █ █     █   █ █ █ \n" + //
                            "█   █ █   █ █     ████     ████  ████  ████  █   █ ████  █   █ █ █ \n" + //
                            " █ █  █   █ █     █        █     █     █  █  █   █ █     █   █     \n" + //
                            "  █    ███   ███  █████    █     █████ █   █ ████  █████  ███  █ █ ");

                    tocarSom("sons/Derrota.wav", true);
                }

            } else {
                System.out.println("Digite um valor válido");

            }

        }
    }
}