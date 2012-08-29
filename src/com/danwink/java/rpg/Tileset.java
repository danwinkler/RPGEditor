package com.danwink.java.rpg;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.vecmath.Point2i;

public class Tileset 
{
	public ArrayList<BufferedImage> autoTiles = new ArrayList<BufferedImage>();
	public ArrayList<String> autoTileFiles = new ArrayList<String>();
	public BufferedImage mainTile;
	public String mainTileFile;
	public HashMap<Integer, TileInfo> info = new HashMap<Integer, TileInfo>();
	
	public String configFile;
	
	public int tileSize = 32;
	public int tilesAcross = 8;
	
	public Tileset()
	{
		
	}
	
	public int getWidth()
	{
		return tileSize * tilesAcross;
	}
	
	public int getHeight()
	{
		return getAutoOffset() * tileSize + mainTile.getHeight();
	}
	
	public void render( Graphics2D g )
	{
		if( autoTiles != null )
		{
			for( int i = 1; i <= autoTiles.size(); i++ )
			{
				BufferedImage at = autoTiles.get( i-1 );
				int dstX = (i%tilesAcross)*tileSize;
				int dstY = (i / tilesAcross) * tileSize;
				g.drawImage( at, dstX, dstY, dstX+tileSize, dstY+tileSize, 0, 0, tileSize, tileSize, null );
			}
		}
		
		if( mainTile != null )
		{
			g.drawImage( mainTile, 0, getAutoOffset() * tileSize, null );
		}
	}
	
	public int getTile( int x, int y )
	{
		int tileSetOffset = getAutoOffset();
		if( y >= tileSetOffset )
		{
			return (y-tileSetOffset)*tilesAcross+x + 1;
		}
		else
		{
			int autoTile = -(y*tilesAcross+x);
			if( -autoTile -1 >= autoTiles.size() )
			{
				return 0;
			}
			else
			{
				return autoTile;
			}
		}
	}
	
	public int getAutoOffset()
	{
		return (autoTiles.size() / tilesAcross)+1;
	}
	
	public int getNumVerticalTiles()
	{
		return (getHeight() / tileSize);
	}
	
	public int getNumHorizontalTiles()
	{
		return tilesAcross;
	}
	
	public int getMinTile()
	{
		return -autoTiles.size();
	}
	
	public int getMaxTile()
	{
		return (getAutoOffset() + (mainTile.getHeight() / tileSize) * tilesAcross) - 1;
	}
	
	public class TileInfo
	{
		public int tile;
		public int elevation;
		public boolean[] enter; //NESW
		public boolean[] exit; //NESW
		public boolean light;
		public int lr = 255;
		public int lg = 255;
		public int lb = 255;
		
		public TileInfo( int tile )
		{
			this.tile = tile;
			enter = new boolean[] { true, true, true, true };
			exit = new boolean[] { true, true, true, true };
		}
		
		public void setElevation( Elevation e )
		{
			this.elevation = e.getInt();
		}
		
		public void setEnter( boolean north, boolean east, boolean south, boolean west )
		{
			enter[0] = north;
			enter[1] = east;
			enter[2] = south;
			enter[3] = west;
		}
		
		public void setExit( boolean north, boolean east, boolean south, boolean west )
		{
			exit[0] = north;
			exit[1] = east;
			exit[2] = south;
			exit[3] = west;
		}

		public void drawEnter( Graphics2D g ) 
		{
			for( int i = 0; i < 4; i++ )
			{
				AffineTransform at = g.getTransform();
				switch( i )
				{
				case 0: g.translate( tileSize/2, tileSize/4 ); g.rotate( 0 ); break;
				case 1: g.translate( tileSize/4 * 3, tileSize/2 ); g.rotate( Math.PI / 2 ); break;
				case 2: g.translate( tileSize/2, tileSize/4 * 3 ); g.rotate( Math.PI ); break;
				case 3: g.translate( tileSize/4, tileSize/2 ); g.rotate( Math.PI / 2 * 3 ); break;
				}
				if( enter[i] ) 
					drawEnterArrow( g ); 
				else 
					drawNo( g );
				g.setTransform( at );
			}
		}
		
		public void drawExit( Graphics2D g )
		{
			for( int i = 0; i < 4; i++ )
			{
				AffineTransform at = g.getTransform();
				switch( i )
				{
				case 0: g.translate( tileSize/2, tileSize/4 ); g.rotate( 0 ); break;
				case 1: g.translate( tileSize/4 * 3, tileSize/2 ); g.rotate( Math.PI / 2 ); break;
				case 2: g.translate( tileSize/2, tileSize/4 * 3 ); g.rotate( Math.PI ); break;
				case 3: g.translate( tileSize/4, tileSize/2 ); g.rotate( Math.PI / 2 * 3 ); break;
				}
				if( exit[i] ) 
					drawExitArrow( g ); 
				else 
					drawNo( g );
				g.setTransform( at );
			}
		}
		
		public void drawElevation( Graphics2D g ) 
		{
			AffineTransform at = g.getTransform();
			g.translate( tileSize/2 - 8, tileSize/2 + 8 );
			g.scale( 2, 2 );
			g.drawString( "" + elevation, 0, 0 );
			
			g.setTransform( at );
		}
		
		public void drawLight( Graphics2D g ) 
		{
			AffineTransform at = g.getTransform();
			g.setColor( Color.BLACK );
			g.drawRect( 6, 6, tileSize-12, tileSize-12 );
			if( light )
			{
				g.setColor( new Color( lr, lg, lb ) );
				g.fillRect( 6, 6, tileSize-12, tileSize-12 );
			}
			
			g.setTransform( at );
		}
		
		private void drawEnterArrow( Graphics2D g )
		{
			g.setColor( Color.BLACK );
			g.setStroke( new BasicStroke( 3 ) );
			//g.drawOval( -4, -4, 8, 8 );
			g.drawLine( 0, 4, -4, 0 );
			g.drawLine( 0, 4, 4, 0 );
			g.drawLine( 0, 4, 0, -4 );
			
			g.setColor( Color.WHITE );
			g.setStroke( new BasicStroke( 1 ) );
			//g.drawOval( -4, -4, 8, 8 );
			g.drawLine( 0, 4, -4, 0 );
			g.drawLine( 0, 4, 4, 0 );
			g.drawLine( 0, 4, 0, -4 );
		}
		
		private void drawExitArrow( Graphics2D g )
		{
			g.setColor( Color.BLACK );
			g.setStroke( new BasicStroke( 3 ) );
			//g.drawOval( -4, -4, 8, 8 );
			g.drawLine( 0, -4, -4, 0 );
			g.drawLine( 0, -4, 4, 0 );
			g.drawLine( 0, -4, 0, 4 );
			
			g.setColor( Color.WHITE );
			g.setStroke( new BasicStroke( 1 ) );
			//g.drawOval( -4, -4, 8, 8 );
			g.drawLine( 0, -4, -4, 0 );
			g.drawLine( 0, -4, 4, 0 );
			g.drawLine( 0, -4, 0, 4 );
		}
		
		private void drawNo( Graphics2D g )
		{
			g.setColor( Color.BLACK );
			g.setStroke( new BasicStroke( 3 ) );
			g.drawOval( -1, -1, 2, 2 );
			g.setColor( Color.WHITE );
			g.setStroke( new BasicStroke( 1 ) );
			g.drawOval( -1, -1, 2, 2 );
		}

		public void flipEnter( int dir ) 
		{
			enter[dir] = !enter[dir];
		}

		public void increaseElevation() 
		{
			elevation++;
			if( elevation > 5 )
				elevation = -5;
		}

		public void decreaseElevation() 
		{
			elevation--;
			if( elevation < -5 )
				elevation = 5;
		}

		public void flipExit( int dir )
		{
			exit[dir] = !exit[dir];
		}
	}
	
	public enum Elevation
	{
		ONE(1),
		TWO(2),
		THREE(3), 
		FOUR(4),
		FIVE(5);
		
		int e;
		
		Elevation( int e )
		{
			this.e = e;
		}
		
		public int getInt()
		{
			return e;
		}
		
		public static Elevation get( int elevation )
		{
			switch( elevation )
			{
			case 1:
				return ONE;
			case 2:
				return TWO;
			case 3:
				return THREE;
			case 4:
				return FOUR;
			case 5:
				return FIVE;
			}
			return null;
		}
	}

	public int getDirection( int x, int y ) 
	{
		boolean ne = x > y;
		boolean ws = !ne;
		boolean wn = tileSize-x > y;
		boolean se = !wn;
		
		if( ne && wn ) return 0;
		if( ne && se ) return 1;
		if( ws && se ) return 2;
		if( ws && wn ) return 3;
		return 0;
	}
}
