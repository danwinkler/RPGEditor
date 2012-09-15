package com.danwink.java.rpg;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import com.danwink.java.rpg.MapObject.Face;
import com.danwink.java.rpg.Tileset.TileInfo;
import com.phyloa.dlib.util.DGraphics;



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
	
	BufferedImage nightOverlay;
	
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
		configFile = t.configFile;
		tileSize = t.tileSize;
	}
	
	public void render( Graphics2D g, ArrayList<MapObject> mos )
	{
		render( g, mos, false );
	}
	
	public void render( Graphics2D g, ArrayList<MapObject> mos, boolean night )
	{
		for( int e = -5; e <= 5; e++ )
		{
			for( int y = 0; y < height; y++ )
			{
				for( int x = 0; x < width; x++ )
				{
					for( int i = 0; i < layers[x][y].length; i++ )
					{
						int tile = layers[x][y][i];
						TileInfo ti = t.info.get( tile );
						if( ti == null )
						{
							System.out.println( tile );
							System.exit( 0 );
						}
						if( ti.elevation == e )
						{
							renderTile( g, x, y, i, tile );
						}
					}
				}
			}
			if( e == 0 && mos != null )
			{
				for( int i = 0; i < events.size(); i++ )
				{
					MapObject mo = events.get( i );
					mo.render( g );
				}
				for( int i = 0; i < mos.size(); i++ )
				{
					MapObject mo = mos.get( i );
					mo.render( g );
				}
			}
		}
		if( night )
		{
			if( nightOverlay == null )
			{
				buildNightOverlay();
			}
			g.drawImage( nightOverlay, 0, 0, null );
		}
	}
	
	private void buildNightOverlay() 
	{
		nightOverlay = DGraphics.createBufferedImage( width * tileSize, height * tileSize );
		Graphics2D g = nightOverlay.createGraphics();
		g.setBackground( new Color( 0, 0, 0, 200 ) );
		g.clearRect( 0, 0, nightOverlay.getWidth(), nightOverlay.getHeight() );
		
		for( int y = 0; y < height; y++ )
		{
			for( int x = 0; x < width; x++ )
			{
				for( int i = 0; i < layers[x][y].length; i++ )
				{
					TileInfo ti = t.info.get( layers[x][y][i] );
					if( ti.light )
					{
						for( int yy = Math.max( y*tileSize+(tileSize/2)-(10*tileSize), 0 ); yy < Math.min( y*tileSize+(tileSize/2)+(10*tileSize), nightOverlay.getHeight() ); yy++ )
						{
							for( int xx = Math.max( x*tileSize+(tileSize/2)-(10*tileSize), 0 ); xx < Math.min( x*tileSize+(tileSize/2)+(10*tileSize), nightOverlay.getWidth() ); xx++ )
							{
								int dx = xx-(x*tileSize+(tileSize/2));
								int dy = yy-(y*tileSize+(tileSize/2));
								double dist = Math.sqrt( dx*dx + dy*dy );
								dist /= tileSize;
								int alpha = (int)Math.min( (dist / 8.0) * 255, 255 );
								int calpha = DGraphics.getAlpha( nightOverlay.getRGB( xx, yy ) );
								if( calpha > alpha )
								{
									nightOverlay.setRGB( xx, yy, DGraphics.rgba( 0, 0, 0, alpha ) );
								}
							}
						}
					}
				}
			}
		}
		g.dispose();
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
	
	public TileEvent getEvent( int x, int y )
	{
		for( TileEvent te : events )
		{
			if( te.xTile == x && te.yTile == y )
			{
				return te;
			}
		}
		return null;
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
																				getTile( x-1, y-1, layer, autoTile ) == autoTile, 
																				getTile( x, y-1, layer, autoTile ) == autoTile, 
																				getTile( x+1, y-1, layer, autoTile ) == autoTile, 
																				getTile( x-1, y, layer, autoTile ) == autoTile, 
																				getTile( x+1, y, layer, autoTile ) == autoTile, 
																				getTile( x-1, y+1, layer, autoTile ) == autoTile, 
																				getTile( x, y+1, layer, autoTile ) == autoTile, 
																				getTile( x+1, y+1, layer, autoTile ) == autoTile 
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
	
	public int getTile( int x, int y, int layer, int def )
	{
		if( x < 0 || x >= width || y < 0 || y >= height )
		{
			return def;
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
			if( t.xTile == x && t.yTile == y )
				return t;
		}
		return null;
	}
	
	public class TileEvent extends MapObject
	{
		public String code;
		
		public TileEvent( int x, int y )
		{
			super( Map.this, null, x, y );
		}

		public void face( MapObject player )
		{
			if( player.xTile == xTile )
			{
				if( player.yTile > yTile )
				{
					facing = Face.SOUTH;
				}
				else
				{
					facing = Face.NORTH;
				}
			}
			else if( player.yTile == yTile )
			{
				if( player.xTile > xTile )
				{
					facing = Face.EAST;
				}
				else
				{
					facing = Face.WEST;
				}
			}
			else
			{
				facing = player.facing.getOpposite();
			}
		}
	}

	public boolean canWalk( int sx, int sy, Face dir )
	{
		int x = sx + dir.x;
		int y = sy + dir.y;
		
		if( x < 0 || y < 0 || x >= width || y >= height )
			return false;
		
		
		int layerCount = layers[0][0].length;
		
		for( int i = 0; i < layerCount; i++ )
		{
			TileInfo cti = t.info.get( layers[sx][sy][i] );
			TileInfo ti = t.info.get( layers[x][y][i] );
			if( !cti.exit[dir.tilesetDir] || !ti.enter[dir.getOpposite().tilesetDir] )
			{
				return false;
			}
		}
		
		for( int i = 0; i < events.size(); i++ )
		{
			TileEvent te = events.get( i );
			if( te.xTile == x && te.yTile == y && !te.passable )
			{
				return false;
			}
		}
		
		return true;
	}

	public void update( int time, ArrayList<MapObject> mos )
	{
		for( int i = 0; i < mos.size(); i++ )
		{
			mos.get( i ).update( time );
		}
		
		for( int i = 0; i < events.size(); i++ )
		{
			events.get( i ).update( time );
		}
	}
}
