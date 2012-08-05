package com.danwink.java.rpg;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;



public class Map 
{
	public int[][][] layers;
	public int width;
	public int height;
	public ArrayList<BufferedImage> autoTiles;
	public int[] autoTileState;
	public ArrayList<TileEvent> events = new ArrayList<TileEvent>();
	public BufferedImage tileset;
	public String configFile;
	public int tileSize = 32;
	public int tilesAcross = 8;
	public String onLoadCode;
	public String name;
	
	public Tileset t;
	
	public Map( int width, int height, int layer )
	{
		layers = new int[width][height][layer];
		this.width = width;
		this.height = height;
	}
	
	public void setTileset( Tileset ts )
	{
		this.autoTiles = ts.autoTiles;
		this.tileset = ts.mainTile;
		t = ts;
	}
	
	public void render( Graphics2D g )
	{
		for( int y = 0; y < height; y++ )
		{
			for( int x = 0; x < width; x++ )
			{
				for( int i = 0; i < layers[x][y].length; i++ )
				{
					int tile = layers[x][y][i];
					renderTile( g, x, y, i, tile );
				}
			}
		}
		
		g.setColor( Color.black );
		g.setFont( new Font( "Arial", Font.BOLD, 20 ) );
		
		for( int i = 0; i < events.size(); i++ )
		{
			TileEvent te = events.get( i );
			g.drawString( "E", te.x * tileSize + tileSize/2 - 6, te.y * tileSize + tileSize/2 + 8 );
		}
	}
	
	public void prepareAutoTilesState()
	{
		autoTileState = new int[autoTiles.size()];
	}
	
	public void updateAutoTileState()
	{
		for( int i = 0; i < autoTileState.length; i++ )
		{
			autoTileState[i] = (autoTileState[i]+1) % ((autoTiles.get( i ).getWidth() / (tileSize*3)));
		}
	}
	
	public void renderTile( Graphics2D g, int x, int y, int layer, int tile )
	{
		if( tile < 0 )
		{
			drawAutoTile( g, x, y, layer, tile );
		}
		else if( tile > 0 )
		{
			tile--;
			int dstx = x*tileSize;
			int dsty = y*tileSize;
			int srcx = (tile % tilesAcross)*tileSize;
			int srcy = (tile / tilesAcross)*tileSize;
			g.drawImage( tileset, dstx, dsty, dstx+tileSize, dsty+tileSize, srcx, srcy, srcx+tileSize, srcy+tileSize, null );
		}
	}
	
	public void drawAutoTile( Graphics2D g, int x, int y, int layer, int autoTile )
	{
		g.translate( x*tileSize, y*tileSize );
		AutoTileDrawer.draw( g, autoTiles.get( -autoTile - 1 ), tileSize, autoTileState == null ? 0 : autoTileState[-autoTile-1], 	
																				getTile( x-1, y-1, layer ) == autoTile, 
																				getTile( x, y-1, layer ) == autoTile, 
																				getTile( x+1, y-1, layer ) == autoTile, 
																				getTile( x-1, y, layer ) == autoTile, 
																				getTile( x+1, y, layer ) == autoTile, 
																				getTile( x-1, y+1, layer ) == autoTile, 
																				getTile( x, y+1, layer ) == autoTile, 
																				getTile( x+1, y+1, layer ) == autoTile 
							);
		g.translate( -x*tileSize, -y*tileSize );
	}

	public void setTile( int x, int y, int layer, int tile ) 
	{
		if( x >= 0 && x < width && y >= 0 && y < height )
		{
			layers[x][y][layer] = tile;
		}
	}
	
	public int getTile( int x, int y, int layer )
	{
		if( x < 0 || x >= width || y < 0 || y >= height )
		{
			return Integer.MAX_VALUE;
		}
		return layers[x][y][layer];
	}

	public void setTiles( int x, int y, int layer, int[][] selected )
	{
		for( int xx = Math.max( x, 0 ); xx < Math.min( x+selected.length, width ); xx++ )
		{
			for( int yy = Math.max( y, 0 ); yy < Math.min( y+selected[xx-x].length, height ); yy++ )
			{
				layers[xx][yy][layer] = selected[xx-x][yy-y]; 
			}
		}
	}
	
	public void addTileEvent( TileEvent t )
	{
		events.add( t );
	}
	
	public TileEvent getTileEvent( int x, int y )
	{
		for( TileEvent t : events )
		{
			if( t.x == x && t.y == y )
				return t;
		}
		return null;
	}
	
	public static class TileEvent
	{
		public int x;
		public int y;
		public String code;
		
		public TileEvent( int x, int y )
		{
			this.x = x;
			this.y = y;
		}
	}

	public boolean canWalk( int x, int y )
	{
		return true;
	}
}
