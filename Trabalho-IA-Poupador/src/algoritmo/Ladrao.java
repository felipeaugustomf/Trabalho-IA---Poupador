package algoritmo;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
	
	private int moedas = 0;
	
	private List <Point> ultimasPosicoes = new ArrayList<>();
	
	@SuppressWarnings("unused")
	public int acao() {
		
		int[] visao = sensor.getVisaoIdentificacao();
		int[] olfatoPoupador = sensor.getAmbienteOlfatoPoupador();
		int[] olfatoLadrao = sensor.getAmbienteOlfatoLadrao();
		moedas += sensor.getNumeroDeMoedas();
		Point posicao = sensor.getPosicao();
		
		Map <Integer, Integer> acao = new HashMap <>();
		
		acao.put(CIMA, valorUtilidadeCima(visao, olfatoPoupador, olfatoLadrao));
		acao.put(BAIXO, valorUtilidadeBaixo(visao, olfatoPoupador, olfatoLadrao));
		acao.put(DIREITA, valorUtilidadeDireita(visao, olfatoPoupador, olfatoLadrao));
		acao.put(ESQUERDA, valorUtilidadeEsquerda(visao, olfatoPoupador, olfatoLadrao));
		
		int direcao = 0;
		
		int maiorUtilidade = (Collections.max(acao.values()));  
        for (Entry<Integer, Integer> entry : acao.entrySet()) { 
            if (entry.getValue() == maiorUtilidade) {
                return entry.getKey();
            }
        }
				
		return new Random().nextInt(5 + 1);
	}
	
	private int valorUtilidadeCima(int[] visao, int[] olfatoPoupador, int[] olfatoLadrao){
		int valorUtilidade = 0;
		
		if ( visao[7] == FORA_DO_AMBIENTE || visao[7] == PAREDE || visao[7] == SEM_VISAO || visao[7] == MOEDA || visao[7] == BANCO || visao[7] == PASTILHA_PODER) {
			valorUtilidade = -1;
		} else {	
			int[] posicoesVisao = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
			int[] posicoesOlfato = {0, 1, 2};
			
			for (int i : posicoesVisao) {
				switch (visao[i]){
					case POUPADOR:
						valorUtilidade += 100;
						break;					
					case LADRAO:
						valorUtilidade += 5;
						break;					
				}		
			}
			
			for (int i : posicoesOlfato) {
				switch (olfatoPoupador[i]){
					case SEM_CHEIRO:
						valorUtilidade += 0;
						break;
					case CHEIRO_1_UNIDADE_ATRAS:
						valorUtilidade += 50;
						break;
					case CHEIRO_2_UNIDADE_ATRAS:
						valorUtilidade += 40;
						break;
					case CHEIRO_3_UNIDADE_ATRAS:
						valorUtilidade += 30;
						break;
					case CHEIRO_4_UNIDADE_ATRAS:
						valorUtilidade += 20;
						break;
					case CHEIRO_5_UNIDADE_ATRAS:
						valorUtilidade += 10;
						break;
				}
				switch (olfatoLadrao[i]){
					case SEM_CHEIRO:
						valorUtilidade += 0;
						break;
					case CHEIRO_1_UNIDADE_ATRAS:
						valorUtilidade += 5;
						break;
					case CHEIRO_2_UNIDADE_ATRAS:
						valorUtilidade += 4;
						break;
					case CHEIRO_3_UNIDADE_ATRAS:
						valorUtilidade += 3;
						break;
					case CHEIRO_4_UNIDADE_ATRAS:
						valorUtilidade += 2;
						break;
					case CHEIRO_5_UNIDADE_ATRAS:
						valorUtilidade += 1;
						break;
				}
			}
		}
		
		return valorUtilidade;
	}
	
	private int valorUtilidadeBaixo(int[] visao, int[] olfatoPoupador, int[] olfatoLadrao){
		int valorUtilidade = 0;
		
		if ( visao[16] == FORA_DO_AMBIENTE || visao[16] == PAREDE || visao[16] == SEM_VISAO  || visao[16] == MOEDA || visao[16] == BANCO || visao[16] == PASTILHA_PODER ) {
			valorUtilidade = -1;
		} else {
			int[] posicoesVisao = {14, 15, 16, 17, 18, 19, 20, 21, 22, 23};
			int[] posicoesOlfato = {5, 6, 7};
			
			for (int i : posicoesVisao) {
				switch (visao[i]){
					case POUPADOR:
						valorUtilidade += 100;
						break;
					case LADRAO:
						valorUtilidade += 5;
						break;					
				}		
			}
			
			for (int i : posicoesOlfato) {
				switch (olfatoPoupador[i]){
					case SEM_CHEIRO:
						valorUtilidade += 0;
						break;
					case CHEIRO_1_UNIDADE_ATRAS:
						valorUtilidade += 50;
						break;
					case CHEIRO_2_UNIDADE_ATRAS:
						valorUtilidade += 40;
						break;
					case CHEIRO_3_UNIDADE_ATRAS:
						valorUtilidade += 30;
						break;
					case CHEIRO_4_UNIDADE_ATRAS:
						valorUtilidade += 20;
						break;
					case CHEIRO_5_UNIDADE_ATRAS:
						valorUtilidade += 10;
						break;
				}
				switch (olfatoLadrao[i]){
					case SEM_CHEIRO:
						valorUtilidade += 0;
						break;
					case CHEIRO_1_UNIDADE_ATRAS:
						valorUtilidade += 5;
						break;
					case CHEIRO_2_UNIDADE_ATRAS:
						valorUtilidade += 4;
						break;
					case CHEIRO_3_UNIDADE_ATRAS:
						valorUtilidade += 3;
						break;
					case CHEIRO_4_UNIDADE_ATRAS:
						valorUtilidade += 2;
						break;
					case CHEIRO_5_UNIDADE_ATRAS:
						valorUtilidade += 1;
						break;
				}
			}
		}
		
		return valorUtilidade;
	}
	
	private int valorUtilidadeDireita(int[] visao, int[] olfatoPoupador, int[] olfatoLadrao){
		int valorUtilidade = 0;
		
		if ( visao[12] == FORA_DO_AMBIENTE || visao[12] == PAREDE || visao[12] == SEM_VISAO || visao[12] == MOEDA || visao[12] == BANCO || visao[12] == PASTILHA_PODER ) {
			valorUtilidade = -1;
		} else {
			int[] posicoesVisao = {3, 4, 8, 9, 12, 13, 17, 18, 22, 23};
			int[] posicoesOlfato = {2, 4, 7};
			
			for (int i : posicoesVisao) {
				switch (visao[i]){
					case POUPADOR:
						valorUtilidade += 100;
						break;
					case LADRAO:
						valorUtilidade += 5;
						break;					
				}		
			}
			
			for (int i : posicoesOlfato) {
				switch (olfatoPoupador[i]){
					case SEM_CHEIRO:
						valorUtilidade += 0;
						break;
					case CHEIRO_1_UNIDADE_ATRAS:
						valorUtilidade += 50;
						break;
					case CHEIRO_2_UNIDADE_ATRAS:
						valorUtilidade += 40;
						break;
					case CHEIRO_3_UNIDADE_ATRAS:
						valorUtilidade += 30;
						break;
					case CHEIRO_4_UNIDADE_ATRAS:
						valorUtilidade += 20;
						break;
					case CHEIRO_5_UNIDADE_ATRAS:
						valorUtilidade += 10;
						break;
				}
				switch (olfatoLadrao[i]){
					case SEM_CHEIRO:
						valorUtilidade += 0;
						break;
					case CHEIRO_1_UNIDADE_ATRAS:
						valorUtilidade += 5;
						break;
					case CHEIRO_2_UNIDADE_ATRAS:
						valorUtilidade += 4;
						break;
					case CHEIRO_3_UNIDADE_ATRAS:
						valorUtilidade += 3;
						break;
					case CHEIRO_4_UNIDADE_ATRAS:
						valorUtilidade += 2;
						break;
					case CHEIRO_5_UNIDADE_ATRAS:
						valorUtilidade += 1;
						break;
				}
			}
		}
		
		return valorUtilidade;
	}
	
	private int valorUtilidadeEsquerda(int[] visao, int[] olfatoPoupador, int[] olfatoLadrao){
		int valorUtilidade = 0;
		
		if ( visao[11] == FORA_DO_AMBIENTE || visao[11] == PAREDE || visao[11] == SEM_VISAO  || visao[11] == MOEDA || visao[11] == BANCO || visao[11] == PASTILHA_PODER ) {
			valorUtilidade = -1;
		} else {
			int[] posicoesVisao = {0, 1, 5, 6, 10, 11, 14, 15, 19, 20};
			int[] posicoesOlfato = {0, 3, 5};
			
			for (int i : posicoesVisao) {
				switch (visao[i]){
					case POUPADOR:
						valorUtilidade += 100;
						break;
					case LADRAO:
						valorUtilidade += 5;
						break;					
				}		
			}
			
			for (int i : posicoesOlfato) {
				switch (olfatoPoupador[i]){
					case SEM_CHEIRO:
						valorUtilidade += 0;
						break;
					case CHEIRO_1_UNIDADE_ATRAS:
						valorUtilidade += 50;
						break;
					case CHEIRO_2_UNIDADE_ATRAS:
						valorUtilidade += 40;
						break;
					case CHEIRO_3_UNIDADE_ATRAS:
						valorUtilidade += 30;
						break;
					case CHEIRO_4_UNIDADE_ATRAS:
						valorUtilidade += 20;
						break;
					case CHEIRO_5_UNIDADE_ATRAS:
						valorUtilidade += 10;
						break;
				}
				switch (olfatoLadrao[i]){
					case SEM_CHEIRO:
						valorUtilidade += 0;
						break;
					case CHEIRO_1_UNIDADE_ATRAS:
						valorUtilidade += 5;
						break;
					case CHEIRO_2_UNIDADE_ATRAS:
						valorUtilidade += 4;
						break;
					case CHEIRO_3_UNIDADE_ATRAS:
						valorUtilidade += 3;
						break;
					case CHEIRO_4_UNIDADE_ATRAS:
						valorUtilidade += 2;
						break;
					case CHEIRO_5_UNIDADE_ATRAS:
						valorUtilidade += 1;
						break;
				}
			}
		
		}	
		
		return valorUtilidade;
	}
	
	
	//Fazer função para retornar a região da visão de interesse
	
	//Fazer função para retornar a região do olfato de interesse
	

}