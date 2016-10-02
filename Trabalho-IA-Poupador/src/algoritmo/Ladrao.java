package algoritmo;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;


public class Ladrao extends ProgramaLadrao {
	
	// MOVIMENTOS
	public static final int PARADO = 0;
	public static final int CIMA = 1;
	public static final int BAIXO = 2;
	public static final int DIREITA = 3;
	public static final int ESQUERDA = 4;
	
	//VISAO
	public static final int SEM_VISAO = -2;
	public static final int FORA_DO_AMBIENTE = -1;
	public static final int CELULA_VAZIA = 0;
	public static final int PAREDE = 1;
	public static final int BANCO = 3;
	public static final int MOEDA = 4;
	public static final int PASTILHA_PODER = 5;
	public static final int POUPADOR = 100;
	public static final int LADRAO = 200;
	
	//OLFATO
	public static final int SEM_CHEIRO = 0;
	public static final int CHEIRO_1_UNIDADE_ATRAS = 1;
	public static final int CHEIRO_2_UNIDADE_ATRAS = 2;
	public static final int CHEIRO_3_UNIDADE_ATRAS = 3;
	public static final int CHEIRO_4_UNIDADE_ATRAS = 4;
	public static final int CHEIRO_5_UNIDADE_ATRAS = 5;
	
	
	@SuppressWarnings("unused")
	private int id;
	private Memoria memoria = new Memoria();
	private int moedasRoubadas = 0;
	private int vontadeDeRoubar = 0;
	private int jogadasAtrasDePoupador = 0;
	
	private static int instancias = 0;
	
	
	public Ladrao() {
		id = instancias++ - 4;
	}
	
	
	// Fun��o que determina a a��o do ladr�o
	public int acao() {
		
		// Verifica se o n�mero de moedas aumentou em rela��o a rodada passada. Caso tenha aumentado, atualiza o n�mero de moedas 
		// e seta a vontade de roubar para -20, deixando o poupador livre para pegar mais moedas antes de tent�-lo roubar novamente.
		if (sensor.getNumeroDeMoedas() > moedasRoubadas){
        	moedasRoubadas = sensor.getNumeroDeMoedas();
        	vontadeDeRoubar = -20;
        }
		
		
		//Pega a posi��o atual e insere na mem�ria
		Point posicao = sensor.getPosicao();		
		memoria.insere(posicao);
				
		//Calcula as posi��es vizinhas
		Point posicaoCima = new Point(posicao.x, posicao.y - 1);
		Point posicaoBaixo = new Point(posicao.x, posicao.y + 1);
		Point posicaoDireita = new Point(posicao.x + 1, posicao.y);
		Point posicaoEsquerda = new Point(posicao.x - 1, posicao.y);
		
		
		//Cria um map onde a chave � o movimento e o valor � o valor de utilidade mais o peso de mem�ria(peso negativo) para aquela posicao		
		Map <Integer, Double> acao = new HashMap <>();		
		acao.put(CIMA, valorUtilidade(CIMA) + memoria.pesoMemoria(posicaoCima));
		acao.put(BAIXO, valorUtilidade(BAIXO) + memoria.pesoMemoria(posicaoBaixo));
		acao.put(DIREITA, valorUtilidade(DIREITA) + memoria.pesoMemoria(posicaoDireita));
		acao.put(ESQUERDA, valorUtilidade(ESQUERDA) + memoria.pesoMemoria(posicaoEsquerda));			
		
		
		//Cria uma lista com os movimentos de maior valor utilidade
		List<Integer> movimentosCandidatos = new ArrayList<>();
		
		// Busca o maior valor utilidade
		double maiorUtilidade = (Collections.max(acao.values()));  
        
		// Encontra os movimentos que possuem o maior valor utilidade
		for (Entry<Integer, Double> entry : acao.entrySet()) { 
            if (entry.getValue() == maiorUtilidade) {
                movimentosCandidatos.add(entry.getKey());
            }
        }
		
        
        // No caso de mais de um movimento ter o maior valor utilidade, escolhe aleatoriamente o movimento
        int index = new Random().nextInt(movimentosCandidatos.size());      
                
		return movimentosCandidatos.get(index);		
		
	}	
	
	
	// Fun��o que determina o valor de utilidade de um movimento
	private double valorUtilidade(int movimento){
		
		// Pega do sensor as informa��es de vis�o e olfato
		int[] visao = sensor.getVisaoIdentificacao();
		int[] olfatoPoupador = sensor.getAmbienteOlfatoPoupador();
		int[] olfatoLadrao = sensor.getAmbienteOlfatoLadrao();		
		
		
		// Filtra as posi��es da vis�o e do olfato que interessam para o movimento
		int[] visaoDoMovimento = this.regiaoDaVisao(visao, movimento);
		int[] olfatoPoupadorDoMovimento = this.regiaoDoOlfato(olfatoPoupador, movimento);
		int[] olfatoLadraoDoMovimento = this.regiaoDoOlfato(olfatoLadrao, movimento);

		
		double valorUtilidade = 0;
		int qtdeDePercepcoesRelevantes = 0;
		
		// Vontade de roubar aumenta a cada vez que o Ladr�o calcula o valor utilidade de um movimento
		vontadeDeRoubar++;
				
		// Verifica se um Poupador est� na vizinhan�a do Ladr�o. Caso o Ladr�o esteja a 20 rodadas com um Poupador em sua vizinha�a, 
		// a vontade de roubar � setada para -20 para que o Ladr�o passe a explorar o mapa ao inv�s de ficar sempre atr�s do mesmo Poupador. 
		if (visaoDoMovimento[0] == POUPADOR) {
			if (jogadasAtrasDePoupador == 20) {
				vontadeDeRoubar = -20;
				jogadasAtrasDePoupador = 0;
			}else {
				jogadasAtrasDePoupador++;
			}
		} 
		
		// Para evitar que o Ladr�o tente ir para uma posi��o que ele n�o pode ocupar
		if ( visaoDoMovimento[0] != POUPADOR && visaoDoMovimento[0] != CELULA_VAZIA ) {
			valorUtilidade = -10000;
		
		} else {
						
			// Faz a an�lise da vis�o
			for (int i = 0; i < visaoDoMovimento.length; i++) {
				switch (visaoDoMovimento[i]){
					case POUPADOR:
						valorUtilidade += POUPADOR * vontadeDeRoubar * 1000.0/distancia(i);
						qtdeDePercepcoesRelevantes++;
						break;
					case LADRAO:
						valorUtilidade += LADRAO/100.0;
						qtdeDePercepcoesRelevantes++;
						break;
					case PASTILHA_PODER:
						valorUtilidade += -PASTILHA_PODER;
						qtdeDePercepcoesRelevantes++;
						break;
					case MOEDA:
						valorUtilidade += MOEDA * fatorRandomico(0, 1);
						qtdeDePercepcoesRelevantes++;
						break;
					case BANCO:
						valorUtilidade += BANCO * fatorRandomico(0, 1);
						qtdeDePercepcoesRelevantes++;
						break;
					case SEM_VISAO:
						valorUtilidade += SEM_VISAO;
						qtdeDePercepcoesRelevantes++;
						break;
					case FORA_DO_AMBIENTE:
						valorUtilidade += FORA_DO_AMBIENTE;
						qtdeDePercepcoesRelevantes++;
						break;
				}		
			}
			
			// Faz a an�lise do rastro dos Poupadores
			for (int i = 0; i < olfatoPoupadorDoMovimento.length; i++) {
				
				switch (olfatoPoupadorDoMovimento[i]){
					case CHEIRO_1_UNIDADE_ATRAS:
						valorUtilidade += POUPADOR * vontadeDeRoubar * 80/distancia(i);
						qtdeDePercepcoesRelevantes++;
						break;
					case CHEIRO_2_UNIDADE_ATRAS:
						valorUtilidade += POUPADOR * vontadeDeRoubar * 64/distancia(i);
						qtdeDePercepcoesRelevantes++;
						break;
					case CHEIRO_3_UNIDADE_ATRAS:
						valorUtilidade += POUPADOR * vontadeDeRoubar * 48/distancia(i);
						qtdeDePercepcoesRelevantes++;
						break;
					case CHEIRO_4_UNIDADE_ATRAS:
						valorUtilidade += POUPADOR * vontadeDeRoubar * 32/distancia(i);
						qtdeDePercepcoesRelevantes++;
						break;
					case CHEIRO_5_UNIDADE_ATRAS:
						valorUtilidade += POUPADOR * vontadeDeRoubar * 16/distancia(i);
						qtdeDePercepcoesRelevantes++;
						break;
				}
			}	
			
			// Faz a an�lise do rastro dos Ladr�es
			for (int i = 0; i < olfatoLadraoDoMovimento.length; i++) {
				
				switch (olfatoLadraoDoMovimento[i]){
					case CHEIRO_1_UNIDADE_ATRAS:
						valorUtilidade += 5/5.0;
						qtdeDePercepcoesRelevantes++;
						break;
					case CHEIRO_2_UNIDADE_ATRAS:
						valorUtilidade += 4/5.0;
						qtdeDePercepcoesRelevantes++;
						break;
					case CHEIRO_3_UNIDADE_ATRAS:
						valorUtilidade += 3/5.0;
						qtdeDePercepcoesRelevantes++;
						break;
					case CHEIRO_4_UNIDADE_ATRAS:
						valorUtilidade += 2/5.0;
						qtdeDePercepcoesRelevantes++;
						break;
					case CHEIRO_5_UNIDADE_ATRAS:
						valorUtilidade += 1/5.0;
						qtdeDePercepcoesRelevantes++;
						break;
				}
			}
		
		}	
		
		if (qtdeDePercepcoesRelevantes > 0) {
			return valorUtilidade/qtdeDePercepcoesRelevantes;
		}
		
		return valorUtilidade;
	}
	
	
	
	/**
	 * 
	 * @param visao
	 * @param movimento
	 * @return regiao da visao que interessa para o movimento 
	 * 
	 * A regiao retornada � ordenada de acordo com a distancia do agente, 
	 * onde a posicao 0 = distancia 1, posicoes 1 a 3 = distancia 2
	 * posicoes 4 a 7 = distancia 3 e posicoes 8 e 9 = distancia 4
	 */
	private int[] regiaoDaVisao(int[] visao, int movimento) {
		
		int[] regiaoDeInteresse = {};
		
		switch (movimento) {
		case CIMA:			
			int [] cima = {visao[7], visao[2], visao[6], visao[8], visao[1], visao[3], visao[5], visao[9], visao[0], visao[4]};
			regiaoDeInteresse = cima;			
			break;
		case BAIXO:			
			int [] baixo = {visao[16], visao[15], visao[17], visao[21], visao[14], visao[18], visao[20], visao[22], visao[19], visao[23]};
			regiaoDeInteresse = baixo;			
			break;
		case DIREITA:			
			int [] direita = {visao[12], visao[8], visao[13], visao[17], visao[3], visao[9], visao[18], visao[22], visao[4], visao[23]};
			regiaoDeInteresse = direita;	
			break;
		case ESQUERDA:	
			int [] esquerda = {visao[11], visao[6], visao[10], visao[15], visao[1], visao[5], visao[14], visao[20], visao[0], visao[19]};
			regiaoDeInteresse = esquerda;			
			break;
		}
		
		return regiaoDeInteresse;
		
	}
	
	
	
	/**
	 * 
	 * @param olfato
	 * @param movimento
	 * @return regiao do olfato que interessa para o movimento 
	 * 
	 * A regi�o retornada � ordenada de acordo com a dist�ncia do agente, 
	 * onde a posicao 0 = distancia 1, posicoes 1 e 2 = distancia 2
	 */
	private int[] regiaoDoOlfato(int[] olfato, int movimento) {
		
		int[] regiaoDeInteresse = {};
		
		switch (movimento) {
		case CIMA:			
			int [] cima = {olfato[1], olfato[0], olfato[2]};
			regiaoDeInteresse = cima;
			break;
		case BAIXO:
			int [] baixo = {olfato[6], olfato[5], olfato[7]};
			regiaoDeInteresse = baixo;
			break;
		case DIREITA:
			int [] direta = {olfato[4], olfato[2], olfato[7]};
			regiaoDeInteresse = direta;
			break;
		case ESQUERDA:
			int [] esquerda = {olfato[3], olfato[0], olfato[5]};
			regiaoDeInteresse = esquerda;
			break;
		}
		
		return regiaoDeInteresse;
		
	}
		
	// M�todo para calcular a dist�ncia da posi��o para o agente dado o indice do vetor
	private int distancia(int posicao){
		if (posicao == 0){
			return 1;
		}else if (posicao >= 1 || posicao <= 3){
			return 2;
		}else if (posicao >= 4 || posicao <= 7){
			return 3;
		}else {
			return 4;
		}
	}
		
	// Retorna um valor rand�mico dentro de um intervalo
	private Double fatorRandomico(double min, double max) {
		Random r = new Random();
		double randomValue = min + (max - min) * r.nextDouble();
		System.out.println(randomValue);
		return randomValue;		
	}

}


// Classe que implementa a mem�ria do Ladr�o tendo o comportamento de uma Fila
class Memoria {
	
	private List<Point> points;
	
	
	public Memoria (){
		points = new LinkedList<Point>();
	}
	
	// Insere um ponto sempre no final. Se o tamanho for 30 remove o primeiro, por ser o ponto que est� a mais tempo na mem�ria
	public void insere(Point point) {
				
		if (points.size() >= 30) {
			points.remove(0);
		}	
			
		points.add(point);
		
	}
	
	// Calcula um peso negativo relacionado as ocorrencias de um ponto na mem�ria e ao tempo que est� na mem�ria.
	public double pesoMemoria(Point point) {
		
		double peso = 0;		
		
		// Varre a mem�ria procurando o ponto
		for (int i = 0; i < points.size(); i++) {
			
			// para todas as vezes que o ponto for encontrado na mem�ria, o peso � incrementado com um valor relativo a posi��o do ponto da mem�ria.
			if(points.get(i).getX() == point.getX() && points.get(i).getY() == point.getY()){
				peso = peso + (new Double(i + 1)/points.size());				
			}	
		}
		
		return - peso * 10;		
	}
	
	
	public String toString() {
		return points.size() + " posicoes visitadas : " + points.toString();
	}
	
}