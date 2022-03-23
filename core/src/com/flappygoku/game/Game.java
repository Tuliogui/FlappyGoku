package com.flappygoku.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class Game extends ApplicationAdapter {

	//Texturas
	private SpriteBatch batch;
	private Texture[] gokus;
	private Texture fundo;
	private Texture torreBaixo;
	private Texture torreCima;
	private Texture gameOver;
	private Texture play;

	//Formas para colisão
	private ShapeRenderer shapeRenderer;
	private Circle circuloGoku;
	private Rectangle retanguloTorreCima;
	private Rectangle retanguloTorreBaixo;

	//Atributos
	private float larguraDispositivo;
	private float alturaDispositivo;
	private float variação = 0;
	private float gravidade = 1;
	private float posicaoInicialVerticalGoku = 0;
	private float posicaoTorreHorizontal;
	private float posicaoTorreVertical;
	private float espacoEntreTorre;
	private Random random;
	private int pontos = 0;
	private int pontuacaoMaxima = 0;
	private boolean passouTorre = false;
	private boolean passouMoeda = false;
	private int estadoJogo = 0;
	private float posiçaoHorizontalGoku = 0;


	//Exibi texto
	BitmapFont textoPontuaçao;
	BitmapFont textoReiniciar;
	BitmapFont textoMelhorPontuaçao;
	BitmapFont textoIniciar;


	//Configuração dos sons
	Sound somVoando;
	Sound somColisao;
	Sound somPontuaçao;

	//Objeto Salvar pontuação
	Preferences preferencia;

	//Objeto para camera
	private OrthographicCamera camera;
	private Viewport viewport;
	private final float VIRTUAL_WIDTH = 720;
	private  final float VIRTUAL_HEIGHT = 1280;

	@Override
	public void create () {
		inicializarTexturas();
		inicializarObjetos();


	}

	@Override
	public void render () {

		//Limpar frames
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		verificarEstadoJogo();
		validarPontos();
		desenharTexturas();
		detectarColisao();

	}
	private void verificarEstadoJogo() {

		boolean toqueTela = Gdx.input.justTouched();

		if (estadoJogo == 0) {
			if (toqueTela) {
				gravidade = -15;
				estadoJogo = 1;
				somVoando.play();
			}

		} else if (estadoJogo == 1) {

			if (toqueTela) {
				gravidade = -15;
				somVoando.play();
			}

			//movimentação do cano
			posicaoTorreHorizontal -= Gdx.graphics.getDeltaTime() * 200;
			if (posicaoTorreHorizontal < -torreCima.getWidth()) {
				posicaoTorreHorizontal = larguraDispositivo;
				posicaoTorreVertical = random.nextInt(1000) - 400;
				passouTorre = false;
			}
			// Aplicando gravidade no Goku
			if (posicaoInicialVerticalGoku > 0 || toqueTela)
				posicaoInicialVerticalGoku = posicaoInicialVerticalGoku - gravidade;

			gravidade++;


		} else if (estadoJogo == 2) {
			if (pontos > pontuacaoMaxima){
				pontuacaoMaxima = pontos;
				preferencia.putInteger("pontuaçaoMaxima", pontuacaoMaxima);

				preferencia.flush();

			}


			posiçaoHorizontalGoku -= Gdx.graphics.getDeltaTime()*500;


			if (toqueTela){
				estadoJogo = 0;
				pontos = 0;
				gravidade = 0;
				posiçaoHorizontalGoku = 0;
				posicaoInicialVerticalGoku = alturaDispositivo /2;
				posicaoTorreHorizontal = larguraDispositivo;


			}

		}


	}

	/*
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(Color.RED);
		shapeRenderer.circle(30 + gokus[0].getWidth() / 2,posicaoInicialVerticalGoku + gokus[0].getHeight()/2,gokus[0].getWidth()/2);
		//cima
		shapeRenderer.rect(posicaoTorreHorizontal ,alturaDispositivo / 2 + espacoEntreTorre /2 + posicaoTorreVertical,
				torreCima.getWidth(), torreCima.getHeight());
		//baixo
		shapeRenderer.rect(posicaoTorreHorizontal ,alturaDispositivo / 2 - torreBaixo.getHeight() - espacoEntreTorre/2 + posicaoTorreVertical,
				torreBaixo.getWidth(), torreBaixo.getHeight());
		shapeRenderer.end();

		 */

	private void detectarColisao(){

		circuloGoku.set(
				30 +posiçaoHorizontalGoku+ gokus[0].getWidth()/2,
				posicaoInicialVerticalGoku + gokus[0].getHeight()/2,
				gokus[0].getWidth()/2
		);
		retanguloTorreCima.set(
				posicaoTorreHorizontal ,
				alturaDispositivo / 2 + espacoEntreTorre /2 + posicaoTorreVertical,
				torreCima.getWidth(),
				torreCima.getHeight()
		);
		retanguloTorreBaixo.set(
				posicaoTorreHorizontal ,
				alturaDispositivo / 2 - torreBaixo.getHeight() - espacoEntreTorre/2 + posicaoTorreVertical,
				torreBaixo.getWidth(), torreBaixo.getHeight()
		);
		boolean colidiuCanoCima = (Intersector.overlaps(circuloGoku, retanguloTorreCima));
		boolean colidiuCanoBaixo = (Intersector.overlaps(circuloGoku, retanguloTorreBaixo));
		if (colidiuCanoCima || colidiuCanoBaixo){
			Gdx.app.log("Log", "Colidiu");
			if (estadoJogo == 1){
				somColisao.play();
				estadoJogo = 2;
			}

		}

	}



	private void desenharTexturas(){
		batch.setProjectionMatrix( camera.combined );
		batch.begin();
		batch.draw(fundo, 0,0, larguraDispositivo,alturaDispositivo);

		batch.draw(gokus[(int) variação],30 + posiçaoHorizontalGoku, posicaoInicialVerticalGoku);
		batch.draw(torreBaixo,posicaoTorreHorizontal ,alturaDispositivo / 2 - torreBaixo.getHeight() - espacoEntreTorre/2 + posicaoTorreVertical);
		batch.draw(torreCima, posicaoTorreHorizontal ,alturaDispositivo / 2 + espacoEntreTorre /2 + posicaoTorreVertical);
		textoPontuaçao.draw(batch, String.valueOf(pontos), larguraDispositivo/ 2, alturaDispositivo -60);

		if (estadoJogo == 0){
			batch.draw(fundo, 0,0, larguraDispositivo,alturaDispositivo);
			batch.draw(play,larguraDispositivo/2 -play.getWidth()/2, alturaDispositivo/2 - play.getHeight()/2);
			textoIniciar.draw(batch, "Toque para Iniciar", larguraDispositivo/2 - play.getWidth()/2, alturaDispositivo/2 + play.getHeight());
		}

		if (estadoJogo == 2){
			batch.draw(gameOver, larguraDispositivo/2 - gameOver.getWidth()/2, alturaDispositivo/2);
			textoReiniciar.draw(batch, "Toque para reiniciar", larguraDispositivo/2 -210, alturaDispositivo/2 - gameOver.getHeight());
			textoMelhorPontuaçao.draw(batch,"Sua Pontuação é: "+ pontuacaoMaxima+  " pontos ",larguraDispositivo/2 -210,alturaDispositivo/2 - gameOver.getHeight()*2 );

		}

		batch.end();

	}
	public void validarPontos(){
		if (posicaoTorreHorizontal < 50 - gokus[0].getWidth()) {
			if (!passouTorre){
				pontos++;
				passouTorre = true;
				somPontuaçao.play();
			}
		}
		variação += Gdx.graphics.getDeltaTime() * 8;

		if (variação > 3)
			variação = 0;


	}

	private void inicializarTexturas(){
		gokus = new Texture[4];
		gokus[0] = new Texture("goku1.png");
		gokus[1] = new Texture("goku2.png");
		gokus[2] = new Texture("goku3.png");
		fundo = new Texture("fundo.png");
		torreBaixo = new Texture("torre_baixo.png");
		torreCima = new Texture("torre_cima.png");
		play = new Texture("play.png");
		gameOver = new Texture("game_over.png");

	}
	private void inicializarObjetos(){
		batch = new SpriteBatch();

		random = new Random();
		larguraDispositivo= VIRTUAL_WIDTH;
		alturaDispositivo= VIRTUAL_HEIGHT;
		posicaoInicialVerticalGoku = alturaDispositivo /2;
		posicaoTorreHorizontal = larguraDispositivo;
		espacoEntreTorre =  360;

		//configurações dos textos
		textoPontuaçao = new BitmapFont();
		textoPontuaçao.setColor(Color.BLACK);
		textoPontuaçao.getData().setScale(5);
		textoReiniciar = new BitmapFont();
		textoReiniciar.setColor(Color.SCARLET);
		textoReiniciar.getData().setScale(4);
		textoIniciar = new BitmapFont();
		textoIniciar.setColor(Color.ORANGE);
		textoIniciar.getData().setScale(5);

		textoMelhorPontuaçao = new BitmapFont();
		textoMelhorPontuaçao.setColor(Color.NAVY);
		textoMelhorPontuaçao.getData().setScale(3);

		//Formas Geometricas para colisao
		shapeRenderer = new ShapeRenderer();
		circuloGoku = new Circle();
		retanguloTorreBaixo = new Rectangle();
		retanguloTorreCima = new Rectangle();

		//Inicializar sons
		somVoando = Gdx.audio.newSound( Gdx.files.internal("som_nuvem.wav"));
		somColisao = Gdx.audio.newSound( Gdx.files.internal("som_colisao.wav"));
		somPontuaçao= Gdx.audio.newSound( Gdx.files.internal("som_ponto.wav"));


		//Configura preferencias dos objetos
		preferencia = Gdx.app.getPreferences("Pontuação");
		pontuacaoMaxima = preferencia.getInteger("pontuaçaoMaxima", 0);

		//Configuração da câmera
		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT/2, 0);
		viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	@Override
	public void dispose () {


	}
}