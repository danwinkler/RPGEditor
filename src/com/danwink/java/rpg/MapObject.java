package com.danwink.java.rpg;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.danwink.java.rpg.MapObject.Face;

public class MapObject 
{
	public Map m;
	public BufferedImage im;
	public int xFrameSize;
	public int yFrameSize;
	
	public int xTile;
	public int yTile;
	
	public int xScreen;
	public int yScreen;
	
	public Face facing;
	public int moveFrame = 0;
	public int framePhase = 4;
	public int moveSpeed = 4;
	public boolean moving;
	
	public boolean lockAnim = false;
	public int lockAnimX;
	public int lockAnimY;
	
	public MapObject( Map m, BufferedImage im, int x, int y )
	{
		this.m = m;
		this.xTile = x;
		this.yTile = y;
		this.im = im;
		if( im != null )
		{
			xFrameSize = im.getWidth() / 4;
			yFrameSize = im.getHeight() / 4;
		}
		if( m != null )
		{
			xScreen = xTile * m.tileSize;
			yScreen = yTile * m.tileSize;
		}
		facing = Face.SOUTH;
	}
	
	public void move( Face dir )
	{
		if( !moving )
		{
			if( this.facing != dir )
			{
				facing = dir;
			}
			else if( m.canWalk( xTile, yTile, dir ) )
			{
				moving = true;
			}
			facing = dir;
		}
	}
	
	public void teleport( Map m, int x, int y, Face dir )
	{
		this.m = m;
		this.xTile = x;
		this.yTile = y;
		this.xScreen = xTile * m.tileSize;
		this.yScreen = yTile * m.tileSize;
		this.facing = dir;
	}
	
	public void update( int frame )
	{
		if( moving )
		{
			int xDest = (facing.x+xTile) * m.tileSize;
			int yDest = (facing.y+yTile) * m.tileSize;
			
			//distance calculated like this because always on axis
			if( Math.abs( xScreen - xDest ) + Math.abs( yScreen - yDest ) <= moveSpeed )
			{
				xTile += facing.x;
				yTile += facing.y;
				xScreen = xTile * m.tileSize;
				yScreen = yTile * m.tileSize;
				moving = false;
				arrived();
			}
			else
			{
				xScreen += facing.x * moveSpeed;
				yScreen += facing.y * moveSpeed;
				if( frame % framePhase == 0 )
				{
					moveFrame = (moveFrame+1) % 4;
				}
			}
		}
		else
		{
			moveFrame = 0;
		}
	}
	
	public void render( Graphics2D g )
	{
		int xim = xFrameSize * moveFrame;
		int yim = yFrameSize * facing.yFrame;
		int xsc = xScreen - (xFrameSize - m.tileSize);
		int ysc = yScreen - (yFrameSize - m.tileSize);
		g.drawImage( im, 
				xsc, ysc, 
				xsc+xFrameSize, ysc+yFrameSize,
				xim, yim, 
				xim+xFrameSize, yim+yFrameSize, 
				null );
	}
	
	public static enum Face
	{
		NORTH( 0, -1, 3, 0 ),
		SOUTH( 0, 1, 0, 2 ),
		EAST( 1, 0, 2, 1 ),
		WEST( -1, 0, 1, 3 );
		
		public int yFrame;
		public int x;
		public int y;
		public int tilesetDir;
		
		Face( int x, int y, int yFrame, int tilesetDir )
		{
			this.x = x;
			this.y = y;
			this.yFrame = yFrame;
			this.tilesetDir = tilesetDir;
		}
		
		public Face getOpposite()
		{
			switch( this )
			{
			case NORTH: return SOUTH;
			case SOUTH: return NORTH;
			case EAST: return WEST;
			case WEST: return EAST;
			}
			return null;
		}

		public static Face getByName( String face ) {
			face = face.trim().toLowerCase();
			if( face.equals( "north" ) )
				return NORTH;
			else if( face.equals( "south" ) )
				return SOUTH;
			else if( face.equals( "east" ) )
				return EAST;
			else if( face.equals( "west" ) )
				return WEST;
			return null;
		}
	}
	
	public interface TileArriveListener
	{
		public void onMapObjectArrive( MapObject mo );
	}
	
	private ArrayList<TileArriveListener> tals = new ArrayList<TileArriveListener>();
	
	public void addTileArriveListener( TileArriveListener tal )
	{
		tals.add( tal );
	}
	
	private void arrived()
	{
		for( TileArriveListener tal : tals )
		{
			tal.onMapObjectArrive( this );
		}
	}
}
