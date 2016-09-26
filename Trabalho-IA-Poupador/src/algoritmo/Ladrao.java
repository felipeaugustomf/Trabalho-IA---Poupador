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
	
	
	private int id;
	private Memoria memoria = new Memoria();
	private int moedasRoubadas = 0;
	private int vontadeDeRoubar = 0;
	private int jogadasAtrasDePoupador = 0;
	
	private static int instancias = 0;
	
	
	public Ladrao() {
		id = instancias++ - 4;
	}
	
	public int acao() {
		
		
		Point posicao = sensor.getPosicao();
				
		
		if (id == 3) {
			System.out.println("id: " + id );
			System.out.println("posicao atual: (" + posicao.x + "," + posicao.y + ")");
			System.out.println("visao:");
			imprimirVisao();
		}
		
		
		Point posicaoCima = new Point(posicao.x, posicao.y - 1);
		Point posicaoBaixo = new Point(posicao.x, posicao.y + 1);
		Point posicaoDireita = new Point(posicao.x + 1, posicao.y);
		Point posicaoEsquerda = new Point(posicao.x - 1, posicao.y);
		
				
		Map <Integer, Double> acao = new HashMap <>();	
		
		
		acao.put(CIMA, valorUtilidade(CIMA) + memoria.pesoMemoria(posicaoCima));
		acao.put(BAIXO, valorUtilidade(BAIXO) + memoria.pesoMemoria(posicaoBaixo));
		acao.put(DIREITA, valorUtilidade(DIREITA) + memoria.pesoMemoria(posicaoDireita));
		acao.put(ESQUERDA, valorUtilidade(ESQUERDA) + memoria.pesoMemoria(posicaoEsquerda));			
		
		
		if (id == 3) {
			System.out.println("posicao cima: " + posicaoCima + " valor utilidade: " + acao.get(1));
			System.out.println("posicao baixo: " + posicaoBaixo + " valor utilidade: " + acao.get(2));
			System.out.println("posicao direita: " + posicaoDireita + " valor utilidade: " + acao.get(3));
			System.out.println("posicao esquerda: " + posicaoEsquerda + " valor utilidade: " + acao.get(4));
		}
		
		
		
		List<Integer> movimentosCandidatos = new ArrayList<>();
				
		double maiorUtilidade = (Collections.max(acao.values()));  
        for (Entry<Integer, Double> entry : acao.entrySet()) { 
            if (entry.getValue() == maiorUtilidade) {
                movimentosCandidatos.add(entry.getKey());
            }
        }
		
        int index = new Random().nextInt(movimentosCandidatos.size());
        
        
        
        memoria.insere(posicao);
        if (sensor.getNumeroDeMoedas() > moedasRoubadas){
        	moedasRoubadas = sensor.getNumeroDeMoedas();
        	vontadeDeRoubar = -10;
        }	
        
        
        
        if (id == 3) {
        	System.out.println("movimentos candidatos: " + movimentosCandidatos);
        	System.out.println("proximo movimento: " + movimentosCandidatos.get(index));
        	System.out.println("memoria: " + memoria);
        	System.out.println("moedas roubadas: " + moedasRoubadas);
        	System.out.println("moedas na rodada: " + sensor.getNumeroDeMoedas());
        	System.out.println("vontade de roubar: " + vontadeDeRoubar);
        	System.out.println("jogadas atras de poupador: " + jogadasAtrasDePoupador);
        }
        
        
                
		return movimentosCandidatos.get(index);
		
		
	}	
	
	
	
	private double valorUtilidade(int movimento){
		
		
		int[] visao = sensor.getVisaoIdentificacao();
		int[] olfatoPoupador = sensor.getAmbienteOlfatoPoupador();
		int[] olfatoLadrao = sensor.getAmbienteOlfatoLadrao();		
		
		int[] visaoDoMovimento = this.regiaoDaVisao(visao, movimento);
		int[] olfatoPoupadorDoMovimento = this.regiaoDoOlfato(olfatoPoupador, movimento);
		int[] olfatoLadraoDoMovimento = this.regiaoDoOlfato(olfatoLadrao, movimento);

		
		double valorUtilidade = 0;
		int qtdeDePercepcoesRelevantes = 0;		
		
		if (visaoDoMovimento[0] == POUPADOR) {
			if (jogadasAtrasDePoupador == 5) {
				vontadeDeRoubar = -10;
				jogadasAtrasDePoupador = 0;
			}else {
				vontadeDeRoubar++;
				jogadasAtrasDePoupador++;
			}
		} else {
			vontadeDeRoubar++;
		}
				
		if ( visaoDoMovimento[0] != POUPADOR && visaoDoMovimento[0] != CELULA_VAZIA ) {
			valorUtilidade = -10000;
		} else {
						
			
			for (int i = 0; i < visaoDoMovimento.length; i++) {
				switch (visaoDoMovimento[i]){
					case POUPADOR:
						valorUtilidade += POUPADOR * vontadeDeRoubar * 80.0/distancia(i);
						qtdeDePercepcoesRelevantes++;
						break;
					case LADRAO:
						valorUtilidade += LADRAO/100.0;
						qtdeDePercepcoesRelevantes++;
						break;
					case PASTILHA_PODER:
						valorUtilidade += PASTILHA_PODER;
						qtdeDePercepcoesRelevantes++;
						break;
					case MOEDA:
						valorUtilidade += MOEDA;
						qtdeDePercepcoesRelevantes++;
						break;
					case BANCO:
						valorUtilidade += BANCO;
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
	 * A regirao retornada é ordenada de acordo com a distancia do agente, 
	 * onde a posicao 0 = distancia 1, posicoes 1 - 3 = distancia 2
	 * posicoes 4 - 7 distancia 3 e posicoes 8 e 9 = distancia 4
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
	 * A regirao retornada é ordenada de acordo com a distancia do agente, 
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
	
	
	
	private void imprimirVisao(){
		
		int[] visao = sensor.getVisaoIdentificacao();
		
		System.out.println(visao [0] + " " + visao [1] + " " + visao [2] + " " + visao [3] + " " + visao [4] + "\n ");
		System.out.println(visao [5] + " " + visao [6] + " " + visao [7] + " " + visao [8] + " " + visao [9] + "\n ");
		System.out.println(visao [10] + " " + visao [11] + " " + "X" + " " + visao [12] + " " + visao [13] + "\n ");
		System.out.println(visao [14] + " " + visao [15] + " " + visao [16] + " " + visao [17] + " " + visao [18] + "\n ");
		System.out.println(visao [19] + " " + visao [20] + " " + visao [21] + " " + visao [22] + " " + visao [23] + "\n ");
		
	}
	

}



class Memoria {
	
	private List<Point> points;
	
	
	public Memoria (){
		points = new LinkedList<Point>();
	}
	
	
	public void insere(Point point) {
				
		if (points.size() >= 30) {
			points.remove(0);
		}	
			
		points.add(point);
		
	}
	
	
	public double pesoMemoria(Point point) {
		
		double peso = 0;		
		
		for (int i = 0; i < points.size(); i++) {
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