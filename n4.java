import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class n4 {

    public static void main(String[] args) {
        
        Scanner sc = new Scanner(System.in);
        System.out.println("Digite o nome do arquivo de teste:");
        String nomeArquivo = sc.nextLine();
        sc.close();
        try {
            // Lê o arquivo que contém os dados do problema de transporte
            Scanner scanner = new Scanner(new File(nomeArquivo));
            int m = scanner.nextInt(); // número de pontos de oferta
            int n = scanner.nextInt(); // número de pontos de demanda

            int[] oferta = new int[m];
            int[] demanda = new int[n];
            int[][] custo = new int[m][n];

            // Lê os valores de oferta
            for (int i = 0; i < m; i++) {
                oferta[i] = scanner.nextInt();
            }

            // Lê os valores de demanda
            for (int j = 0; j < n; j++) {
                demanda[j] = scanner.nextInt();
            }

            // Lê a matriz de custos
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    custo[i][j] = scanner.nextInt();
                }
            }
            scanner.close();

            // Resolve o problema de transporte usando o método dual
            resolverProblemaTransporte(oferta, demanda, custo);

        } catch (FileNotFoundException e) {
            System.out.println("Arquivo não encontrado: " + nomeArquivo);
        }
    }

    public static void resolverProblemaTransporte(int[] oferta, int[] demanda, int[][] custo) {
        int ofertaTotal = 0, demandaTotal = 0;

        // Calcula o total de oferta e demanda
        for (int valor : oferta) ofertaTotal += valor;
        for (int valor : demanda) demandaTotal += valor;

        int m = oferta.length;
        int n = demanda.length;

        // Step 0: Inicialização
        // Balanceamento do problema de transporte se necessário
        if (ofertaTotal < demandaTotal) {
            oferta = expandirArray(oferta, demandaTotal - ofertaTotal);
            custo = expandirMatriz(custo, 0);
            m++;
        } else if (demandaTotal < ofertaTotal) {
            demanda = expandirArray(demanda, ofertaTotal - demandaTotal);
            custo = expandirMatriz(custo, 1);
            n++;
        }

        // Inicializa a matriz de alocação e os vetores u e v
        int[][] alocacao = new int[m][n];
        int[] u = new int[m];
        int[] v = new int[n];
        boolean[] uDefinido = new boolean[m];
        boolean[] vDefinido = new boolean[n];

        // Inicializa todos os valores de uDefinido e vDefinido como falso
        for (int i = 0; i < m; i++) uDefinido[i] = false;
        for (int j = 0; j < n; j++) vDefinido[j] = false;

        // Define u[0] como 0 e marca uDefinido[0] como verdadeiro
        u[0] = 0;
        uDefinido[0] = true;

        // Step 1: Determinação das variáveis duais
        // Define os valores de u e v até que todos estejam definidos
        while (!todasVariaveisDefinidas(uDefinido, vDefinido)) {
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    if (uDefinido[i] && !vDefinido[j]) {
                        v[j] = custo[i][j] - u[i];
                        vDefinido[j] = true;
                    } else if (!uDefinido[i] && vDefinido[j]) {
                        u[i] = custo[i][j] - v[j];
                        uDefinido[i] = true;
                    }
                }
            }
        }

        // Step 2: Determinação da célula de entrada
        // Enquanto houver demanda a ser atendida, encontre a célula de custo mínimo e aloque a quantidade mínima entre oferta e demanda
        while (!todaDemandaAtendida(demanda)) {
            int custoMinimo = Integer.MAX_VALUE;
            int minI = -1;
            int minJ = -1;

            // Encontra a célula de custo mínimo que não foi satisfeita
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    if (oferta[i] > 0 && demanda[j] > 0 && custo[i][j] - u[i] - v[j] < custoMinimo) {
                        custoMinimo = custo[i][j] - u[i] - v[j];
                        minI = i;
                        minJ = j;
                    }
                }
            }

            // Aloca a quantidade mínima entre oferta e demanda na célula de custo mínimo
            int quantidade = Math.min(oferta[minI], demanda[minJ]);
            alocacao[minI][minJ] = quantidade;
            oferta[minI] -= quantidade;
            demanda[minJ] -= quantidade;
        }

        // Step 3: Atualização
        // Calcula o custo total de transporte
        int custoTotal = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                custoTotal += alocacao[i][j] * custo[i][j];
            }
        }

        // Exibe o custo total e a alocação final
        System.out.println("Custo total de transporte: " + custoTotal);
        System.out.println("Alocação de transporte:");
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(alocacao[i][j] + " ");
            }
            System.out.println();
        }
    }

    // Método para expandir o array de oferta ou demanda
    private static int[] expandirArray(int[] original, int tamanhoExtra) {
        int[] novoArray = new int[original.length + 1];
        System.arraycopy(original, 0, novoArray, 0, original.length);
        novoArray[original.length] = tamanhoExtra;
        return novoArray;
    }

    // Método para expandir a matriz de custo
    private static int[][] expandirMatriz(int[][] original, int tipo) {
        int m = original.length;
        int n = original[0].length;
        int[][] novaMatriz;

        if (tipo == 0) { // Expande a matriz adicionando uma linha
            novaMatriz = new int[m + 1][n];
            for (int i = 0; i < m; i++) {
                System.arraycopy(original[i], 0, novaMatriz[i], 0, n);
            }
        } else { // Expande a matriz adicionando uma coluna
            novaMatriz = new int[m][n + 1];
            for (int i = 0; i < m; i++) {
                System.arraycopy(original[i], 0, novaMatriz[i], 0, n);
            }
        }
        return novaMatriz;
    }

    // Verifica se todas as variáveis u e v foram definidas
    private static boolean todasVariaveisDefinidas(boolean[] uDefinido, boolean[] vDefinido) {
        for (boolean b : uDefinido) {
            if (!b) return false;
        }
        for (boolean b : vDefinido) {
            if (!b) return false;
        }
        return true;
    }

    // Verifica se toda a demanda foi atendida
    private static boolean todaDemandaAtendida(int[] demanda) {
        for (int d : demanda) {
            if (d > 0) return false;
        }
        return true;
    }
}
