package yasith.visualizer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Visualizer implements ApplicationListener {
	
	private final float STEP_TIME = 3.0f;
	private final int INFINITY = 99999;
	private SpriteBatch mBatch;
	private Pixmap mPixmap;
	private Texture mTexture;
	
	private float mWidth, mHeight;
	
	// source, destination, distance
	int connections[][] = {{1,3,2},{2,3,3},{3,4,5},{3,5,4},{3,6,3},{4,5,2},{5,7,1},{5,9,2},{6,9,10},{8,9,20},{9,10,1}};
	int nodes = 11;
	int map[][] = new int[nodes][nodes];
	int state[] = new int[nodes];
	boolean visited[] = new boolean[nodes];
	int positions[][] = new int[nodes][2];
	
	int barrier[] = new int[nodes];
	
	int start = 1, end = 9;
	
	float updateTime = 0.0f;
	
	int visualizationState = 0;
	
	boolean setPositions = false;
	
	@Override
	public void create() {		
		mWidth = Gdx.graphics.getWidth();
		mHeight = Gdx.graphics.getHeight();
		
		mBatch = new SpriteBatch();
		
		mPixmap = new Pixmap(1024, 1024, Pixmap.Format.RGBA8888);
		
		mTexture = new Texture(mPixmap);
	}

	@Override
	public void dispose() {
		mPixmap.dispose();
		mBatch.dispose();
	}

	private void drawGraph() {
		
		Color c0 = new Color(1, 1, 1, 1);
		Color c1 = new Color(1, 0, 0, 1);
		Color c2 = new Color(0, 1, 0, 1);
		Color c3 = new Color(0, 0, 1, 1);
		Color c4 = new Color(0.5f, 0, 0, 1);
		Color c5 = new Color(1, 0, 1, 1);
		
		Color vertexColors[] = {c0, c1, c2, c3, c4,c5};
		
		int node = 1;
		
		for(int i = 1; i <= nodes / 5; i++){
			for(int j = 1; j <= 5; j++){
				
				mPixmap.setColor(vertexColors[state[node]]);
				
				if(! setPositions){
					int dx = 300;
					int dy = 140;
					
					positions[node][0] = dx * i + (int)(Math.random() * (dx-10));
					positions[node][1] = dy * (j-1) + (int)(Math.random() * (dy-10));
					
				}
				
				/*
				positions[node][0] = j % 2 == 0 ? 140*i : 140*i + 70;
				positions[node][1] = 140*j;
				*/
				
				mPixmap.fillCircle(positions[node][0], positions[node][1], 10);
				
				node ++;
			}
		}
		setPositions = true;
		
		for(int i = 0;i < connections.length; i++){
			Color cLine = new Color(0.5f, 0.5f, 0.5f, 1);
			mPixmap.setColor(cLine);
			int n1 = connections[i][0];
			int n2 = connections[i][1];
			mPixmap.drawLine(positions[n1][0], positions[n1][1], 
					positions[n2][0], positions[n2][1]);
		}
		
		mTexture.draw(mPixmap, 0, 0);
		mTexture.bind();
	}
	
	void dijkstraSetup(){
		for(int i = 0; i < nodes; i++){
			barrier[i] = i == start ? 0 : INFINITY;
		}
		
		for(int i = 0; i < connections.length; i++){
			int a, b, d;
			a = connections[i][0];
			b = connections[i][1];
			d = connections[i][2];
			
			map[a][b] = map[b][a] = d;
		}
		
		for(int i = 0; i < nodes; i++){
			for(int j = 0; j < nodes; j++){
				System.out.print(map[i][j] + " ");
			}
			System.out.println();
		}
	}
	
	void runDijkstra(){
		
		if(visualizationState == 0){
			dijkstraSetup();
			
			visualizationState ++;
		}
		
		if(visualizationState == 2){
			return;
		}
		
		int selNode = 0;
		
		for(int i = 1; i < nodes; i++){
			if(visited[i]) continue;
			selNode = barrier[selNode] < barrier[i] ? selNode : i;
		}
		state[selNode] = 1;
		
		for(int i = 1; i < nodes; i++){
			if(i == selNode) continue;
			
			if(map[selNode][i] != 0){
				barrier[i] = barrier[selNode] + map[selNode][i];
				state[i] = 2;
			}
		}
		
		for(int i = 1; i < nodes; i++){
			if(visited[i]){
				state[i] = 4;
			}
		}
		
		visited[selNode] = true;
		
		if(selNode == end){
			state[selNode] = 3;
			visualizationState ++;
		}
		
		
		int nextNode = 0;
		for(int i = 1; i < nodes; i++){
			if(visited[i]) continue;
			nextNode = barrier[nextNode] < barrier[i] ? nextNode : i;
		}
		state[nextNode] = 5;
		
		System.out.println("Sel: " + selNode + " Next: " + nextNode);
		
		drawGraph();
	}
	
	@Override
	public void render() {		
		//drawCircle();
		float dt = Gdx.graphics.getDeltaTime();
		
		updateTime += dt;
		if(updateTime >= STEP_TIME){
			updateTime = 0.0f;
			
			runDijkstra();
		}
			
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		mBatch.begin();
		mBatch.draw(mTexture, 0, mHeight - 1024);
		mBatch.end();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
