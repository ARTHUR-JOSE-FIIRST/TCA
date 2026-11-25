import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import javax.sound.sampled.*;
import com.sun.jna.Library;
import com.sun.jna.Native;

public class SudokuTCA {

    // COMO COMPILAR E RODAR PELO TERMINAL (dentro da pasta do projeto):
    //   Mac e Linux:
    //     javac -cp "lib/*" SudokuTCA.java
    //     java --enable-native-access=ALL-UNNAMED -cp ".:lib/*" SudokuTCA
    //   Windows:
    //     javac -cp "lib/*" SudokuTCA.java
    //     java --enable-native-access=ALL-UNNAMED -cp ".;lib/*" SudokuTCA

    // metodo movimento Ló
    public interface Crt extends Library {
        Crt INSTANCE = Native.load("msvcrt", Crt.class);

        int _kbhit();

        int _getch();
    }

    static boolean windows = System.getProperty("os.name").toLowerCase().contains("win");
    static boolean tecladoPreparado = false;

    public static boolean pressionouTecla() {
        if (windows) {
            return Crt.INSTANCE._kbhit() != 0;
        }
        prepararTecladoMacLinux();
        try {
            return System.in.available() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static int obtemTeclaPressionada() {
        if (windows) {
            return Crt.INSTANCE._getch();
        }
        prepararTecladoMacLinux();
        try {
            return System.in.read();
        } catch (Exception e) {
            return -1;
        }
    }

    public static void esperarTecla() {
        System.out.println("Pressione uma tecla para continuar...");
        obtemTeclaPressionada(); // fica esperando a tecla
    }

    static void prepararTecladoMacLinux() {
        if (tecladoPreparado) {
            return;
        }
        tecladoPreparado = true;
        executarStty("-icanon -echo -icrnl min 1");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> executarStty("sane")));
    }

    static void executarStty(String opcoes) {
        try {
            new ProcessBuilder("sh", "-c", "stty " + opcoes + " < /dev/tty").start().waitFor();
        } catch (Exception e) {

        }
    }

    // método cronometro Ló
    static long inicioCronometro = 0;

    public static void iniciarCronometro() {
        inicioCronometro = System.currentTimeMillis();
    }

    public static double segundosPassados() {
        long agora = System.currentTimeMillis();
        return (agora - inicioCronometro) / 1000.0;
    }

    public static void esperar(int milissegundos) {
        try {
            Thread.sleep(milissegundos);
        } catch (InterruptedException e) {
        }
    }

    // cores
    public static final String RESET = "\u001B[0m";
    public static final String CINZA = "\u001B[90m";
    public static final String VERMELHO = "\u001B[91m";
    public static final String VERDE = "\u001B[92m";
    public static final String AMARELO = "\u001B[93m";

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
    public static void imprimirTabuleiro(int[][] tabuleiro, int linhaAtual, int colunaAtual, int vida,
            int blocoLinha, boolean[][] tabuleirofixos) {

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
                    System.out.print(VERDE + " " + valor + " " + RESET);
                } else if (tabuleirofixos[i][j]) {
                    System.out.print(AMARELO + " " + valor + " " + RESET);
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

    // Método de movimentação e preenchimento do tabluleiro
    public static int preencherTabulerio(int[][] tabuleiro, boolean[][] tabuleirofixos,
            int[] posicao, char tecla, int vida, int blocoColuna, int blocoLinha, int casas) {

        char t = Character.toLowerCase(tecla);

        switch (t) {
            case 'w':
                if (posicao[0] > 0) {
                    posicao[0]--;
                }
                return vida;

            case 's':
                if (posicao[0] < (casas - 1)) {
                    posicao[0]++;
                }
                return vida;

            case 'a':
                if (posicao[1] > 0) {
                    posicao[1]--;
                }
                return vida;

            case 'd':
                if (posicao[1] < (casas - 1)) {
                    posicao[1]++;
                }
                return vida;

            case '0':
                if (!tabuleirofixos[posicao[0]][posicao[1]]) {
                    tabuleiro[posicao[0]][posicao[1]] = 0;
                }
                return vida;

            default:
                break;
        }

        if (t >= '1' && t <= ('0' + casas)) {
            int numero = t - '0';
            int linha = posicao[0];
            int coluna = posicao[1];

            if (!tabuleirofixos[linha][coluna] && tabuleiro[linha][coluna] == 0) {
                if (!valorExisteNaLinha(tabuleiro, linha, numero)
                        && !valorExisteNaColuna(tabuleiro, coluna, numero)
                        && !valorExisteNoBloco(tabuleiro, linha, coluna, numero, blocoLinha, blocoColuna)) {
                    tabuleiro[linha][coluna] = numero;
                } else {
                    vida--;
                }
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

    // metodo para remover tres numeros a cada 30 seg do tabuleiro
    public static boolean removerNumeros(int[][] tabuleiro, boolean[][] tabuleirofixos, int casas) {
        int totalCandidatos = 0;
        for (int i = 0; i < casas; i++) {
            for (int j = 0; j < casas; j++) {
                if (!tabuleirofixos[i][j] && tabuleiro[i][j] != 0) {
                    totalCandidatos++;
                }
            }
        }

        if (totalCandidatos == 0) {
            return false;
        }

        int[] linhas = new int[totalCandidatos];
        int[] colunas = new int[totalCandidatos];
        int cont = 0;

        for (int i = 0; i < casas; i++) {
            for (int j = 0; j < casas; j++) {
                if (!tabuleirofixos[i][j] && tabuleiro[i][j] != 0) {
                    linhas[cont] = i;
                    colunas[cont] = j;
                    cont++;
                }
            }
        }

        Random random = new Random();
        int quantidadeARemover = Math.min(3, totalCandidatos);

        for (int i = totalCandidatos - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int tempL = linhas[i];
            int tempC = colunas[i];
            linhas[i] = linhas[j];
            colunas[i] = colunas[j];
            linhas[j] = tempL;
            colunas[j] = tempC;
        }

        for (int k = 0; k < quantidadeARemover; k++) {
            tabuleiro[linhas[k]][colunas[k]] = 0;
        }

        return quantidadeARemover > 0;
    }

    public static void main(String[] args) {

        System.out.print("\033[H\033[2J\033[3J");

        System.out.println("╔══════════════════════════════════════════════════════════╗\n" + //
                "║                                                          ║\n" + //
                "║      ____  _   _ ____   ___  _  ___   _                  ║\n" + //
                "║     / ___|| | | |  _ \\ / _ \\| |/ / | | |                 ║\n" + //
                "║     \\___ \\| | | | | | | | | | ' /| | | |                 ║\n" + //
                "║      ___) | |_| | |_| | |_| | . \\| |_| |                 ║\n" + //
                "║     |____/ \\___/|____/ \\___/|_|\\_\\\\___/                  ║\n" + //
                "║                                                          ║\n" + //
                "╠══════════════════════════════════════════════════════════╣\n" + //
                "║                                                          ║\n" + //
                "║    " + CINZA + "SELECIONE O TAMANHO DO TABULEIRO:" + RESET + "                     ║\n" + //
                "║                                                          ║\n" + //
                "║   [ 1 ]  Tabuleiro 6x6  (Blocos 2x3 - Números de 1 a 6)  ║\n" + //
                "║          " + CINZA + "┌─────┬─────┐" + RESET + "                                   ║\n" + //
                "║          " + CINZA + "│1 2 3│4 5 6│" + RESET + "                                   ║\n" + //
                "║          " + CINZA + "│4 5 6│1 2 3│" + RESET + "                                   ║\n" + //
                "║          " + CINZA + "├─────┼─────┤" + RESET + "                                   ║\n" + //
                "║          " + CINZA + "│2 3 1│5 6 4│" + RESET + "                                   ║\n" + //
                "║          " + CINZA + "│5 6 4│2 3 1│" + RESET + "                                   ║\n" + //
                "║          " + CINZA + "├─────┼─────┤" + RESET + "                                   ║\n" + //
                "║          " + CINZA + "│3 1 2│6 4 5│" + RESET + "                                   ║\n" + //
                "║          " + CINZA + "│6 4 5│3 1 2│" + RESET + "                                   ║\n" + //
                "║          " + CINZA + "└─────┴─────┘" + RESET + "                                   ║\n" + //
                "║                                                          ║\n" + //
                "║   [ 2 ]  Tabuleiro 9x9  (Blocos 3x3 - Números de 1 a 9)  ║\n" + //
                "║          " + CINZA + "┌─────┬─────┬─────┐" + RESET + "                             ║\n" + //
                "║          " + CINZA + "│1 2 3│4 5 6│7 8 9│" + RESET + "                             ║\n" + //
                "║          " + CINZA + "│4 5 6│7 8 9│1 2 3│" + RESET + "                             ║\n" + //
                "║          " + CINZA + "│7 8 9│1 2 3│4 5 6│" + RESET + "                             ║\n" + //
                "║          " + CINZA + "├─────┼─────┼─────┤" + RESET + "                             ║\n" + //
                "║          " + CINZA + "│2 3 1│5 6 4│8 9 7│" + RESET + "                             ║\n" + //
                "║          " + CINZA + "│5 6 4│8 9 7│2 3 1│" + RESET + "                             ║\n" + //
                "║          " + CINZA + "│8 9 7│2 3 1│5 6 4│" + RESET + "                             ║\n" + //
                "║          " + CINZA + "├─────┼─────┼─────┤" + RESET + "                             ║\n" + //
                "║          " + CINZA + "│3 1 2│6 4 5│9 7 8│" + RESET + "                             ║\n" + //
                "║          " + CINZA + "│6 4 5│9 7 8│3 1 2│" + RESET + "                             ║\n" + //
                "║          " + CINZA + "│9 7 8│3 1 2│6 4 5│" + RESET + "                             ║\n" + //
                "║          " + CINZA + "└─────┴─────┴─────┘" + RESET + "                             ║\n" + //
                "║                                                          ║\n" + //
                "║                                                          ║\n" + //
                "║                                                          ║\n" + //
                "╚══════════════════════════════════════════════════════════╝\n" + //
                "Digite sua opção: ");

        char opcao = 0;
        while (opcao != '1' && opcao != '2') {
            if (pressionouTecla()) {
                int tecla = obtemTeclaPressionada();
                if (tecla >= 0) {
                    opcao = (char) tecla;
                }
            }
            esperar(50);
        }

        if (opcao == '2') {

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

            final double LIMITE_SEGUNDOS = 18 * 60.0;

            iniciarCronometro();

            double tempoRestante = LIMITE_SEGUNDOS;

            int proxApagao = 120;

            System.out.print("\033[H\033[2J\033[3J");
            imprimirTabuleiro(tabuleiro9x9, posicao[0], posicao[1], vida, blocoLinha, tabuleiro9x9fixos);
            System.out.println("\nTempo limite de 9 minutos");
            System.out.println("Use W A S D para se movimentar");
            System.out.println("Digite números de 1-" + casas);
            System.out.println("Use 0 para apagar um número");
            System.out.flush();

            while (vida > 0 && tempoRestante > 0 && !jogoVencido(tabuleiro9x9, casas)) {

                if (pressionouTecla()) {
                    int codigoTecla = obtemTeclaPressionada();
                    if (codigoTecla >= 0) {
                        int linhaAntes = posicao[0];
                        int colunaAntes = posicao[1];
                        int vidaAntes = vida;
                        int valorAntes = tabuleiro9x9[linhaAntes][colunaAntes];

                        vida = preencherTabulerio(
                                tabuleiro9x9,
                                tabuleiro9x9fixos,
                                posicao,
                                (char) codigoTecla,
                                vida,
                                blocoColuna,
                                blocoLinha,
                                casas);

                        boolean mudouPosicao = (posicao[0] != linhaAntes) || (posicao[1] != colunaAntes);
                        boolean mudouValorOuVida = (tabuleiro9x9[linhaAntes][colunaAntes] != valorAntes)
                                || (vida != vidaAntes);

                        if (mudouPosicao || mudouValorOuVida) {
                            System.out.print("\033[H\033[2J\033[3J");
                            imprimirTabuleiro(tabuleiro9x9, posicao[0], posicao[1], vida, blocoLinha,
                                    tabuleiro9x9fixos);
                            System.out.println("\nTempo limite de 9 minutos");
                            System.out.println("Use W A S D para se movimentar");
                            System.out.println("Digite números de 1-" + casas);
                            System.out.println("Use 0 para apagar um número");
                            System.out.flush();
                        }
                    }
                }

                tempoRestante = LIMITE_SEGUNDOS - segundosPassados();

                if (segundosPassados() >= proxApagao && vida > 0 && tempoRestante > 0
                        && !jogoVencido(tabuleiro9x9, casas)) {
                    proxApagao += 60;
                    boolean removeu = removerNumeros(tabuleiro9x9, tabuleiro9x9fixos, casas);
                    if (removeu) {
                        System.out.print("\033[H\033[2J\033[3J");
                        imprimirTabuleiro(tabuleiro9x9, posicao[0], posicao[1], vida, blocoLinha, tabuleiro9x9fixos);
                        System.out.println("\n3 números foram apagados!");
                        System.out.println("Use W A S D para se movimentar");
                        System.out.println("Digite números de 1-" + casas);
                        System.out.println("Use 0 para apagar um número");
                        System.out.flush();
                    }
                }

                if (vida > 0 && tempoRestante > 0 && !jogoVencido(tabuleiro9x9, casas)) {
                    esperar(100);
                    tempoRestante = LIMITE_SEGUNDOS - segundosPassados();
                }
            }

            System.out.print("\033[H\033[2J\033[3J");

            if (jogoVencido(tabuleiro9x9, casas)) {

                double tempoGasto = segundosPassados();
                int totalSegundosGastos = (int) Math.round(tempoGasto);
                int minutosGastos = totalSegundosGastos / 60;
                int segundosGastos = totalSegundosGastos % 60;

                System.out.println("█   █ █████ █   █  ███  █████ █   █    █ █ \n" + //
                        "█   █ █     ██  █ █     █     █   █    █ █ \n" + //
                        "█   █ ████  █ █ █ █     ████  █   █    █ █ \n" + //
                        " █ █  █     █  ██ █     █     █   █        \n" + //
                        "  █   █████ █   █  ███  █████  ███     █ █ ");

                System.out.printf("%nTempo total: %02d:%02d (%.1f s)%n", minutosGastos, segundosGastos, tempoGasto);

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
        } else if (opcao == '1') {

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

            final double LIMITE_SEGUNDOS = 9 * 60.0;

            iniciarCronometro();

            double tempoRestante = LIMITE_SEGUNDOS;

            int proxApagao = 60;

            System.out.print("\033[H\033[2J\033[3J");
            imprimirTabuleiro(tabuleiro6x6, posicao[0], posicao[1], vida, blocoLinha, tabuleiro6x6fixos);
            System.out.println("\nTempo limite de 9 minutos");
            System.out.println("Use W A S D para se movimentar");
            System.out.println("Digite números de 1-" + casas);
            System.out.println("Use 0 para apagar um número");
            System.out.flush();

            while (vida > 0 && tempoRestante > 0 && !jogoVencido(tabuleiro6x6, casas)) {

                if (pressionouTecla()) {
                    int codigoTecla = obtemTeclaPressionada();
                    if (codigoTecla >= 0) {
                        int linhaAntes = posicao[0];
                        int colunaAntes = posicao[1];
                        int vidaAntes = vida;
                        int valorAntes = tabuleiro6x6[linhaAntes][colunaAntes];

                        vida = preencherTabulerio(
                                tabuleiro6x6,
                                tabuleiro6x6fixos,
                                posicao,
                                (char) codigoTecla,
                                vida,
                                blocoColuna,
                                blocoLinha,
                                casas);

                        boolean mudouPosicao = (posicao[0] != linhaAntes) || (posicao[1] != colunaAntes);
                        boolean mudouValorOuVida = (tabuleiro6x6[linhaAntes][colunaAntes] != valorAntes)
                                || (vida != vidaAntes);

                        if (mudouPosicao || mudouValorOuVida) {
                            System.out.print("\033[H\033[2J\033[3J");
                            imprimirTabuleiro(tabuleiro6x6, posicao[0], posicao[1], vida, blocoLinha,
                                    tabuleiro6x6fixos);
                            System.out.println("\nTempo limite de 9 minutos");
                            System.out.println("Use W A S D para se movimentar");
                            System.out.println("Digite números de 1-" + casas);
                            System.out.println("Use 0 para apagar um número");
                            System.out.flush();
                        }
                    }
                }

                tempoRestante = LIMITE_SEGUNDOS - segundosPassados();

                if (segundosPassados() >= proxApagao && vida > 0 && tempoRestante > 0
                        && !jogoVencido(tabuleiro6x6, casas)) {
                    proxApagao += 60;
                    boolean removeu = removerNumeros(tabuleiro6x6, tabuleiro6x6fixos, casas);
                    if (removeu) {
                        System.out.print("\033[H\033[2J\033[3J");
                        imprimirTabuleiro(tabuleiro6x6, posicao[0], posicao[1], vida, blocoLinha, tabuleiro6x6fixos);
                        System.out.println("\n3 números foram apagados!");
                        System.out.println("Use W A S D para se movimentar");
                        System.out.println("Digite números de 1-" + casas);
                        System.out.println("Use 0 para apagar um número");
                        System.out.flush();
                    }
                }

                if (vida > 0 && tempoRestante > 0 && !jogoVencido(tabuleiro6x6, casas)) {
                    esperar(100);
                    tempoRestante = LIMITE_SEGUNDOS - segundosPassados();
                }
            }

            System.out.print("\033[H\033[2J\033[3J");

            if (jogoVencido(tabuleiro6x6, casas)) {

                double tempoGasto = segundosPassados();
                int totalSegundosGastos = (int) Math.round(tempoGasto);
                int minutosGastos = totalSegundosGastos / 60;
                int segundosGastos = totalSegundosGastos % 60;

                System.out.println("█   █ █████ █   █  ███  █████ █   █    █ █ \n" + //
                        "█   █ █     ██  █ █     █     █   █    █ █ \n" + //
                        "█   █ ████  █ █ █ █     ████  █   █    █ █ \n" + //
                        " █ █  █     █  ██ █     █     █   █        \n" + //
                        "  █   █████ █   █  ███  █████  ███     █ █ ");

                System.out.printf("%nTempo total: %02d:%02d (%.1f s)%n", minutosGastos, segundosGastos, tempoGasto);

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
