package com.danwink.java.rpg;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class MapObject 
{
	public Map m;
	public BufferedImage im;
	public int xTile;
	public int yTile;
	
	public int xScreen;
	public int yScreen;
	
	public Face facing;
	public int moveFrame = 0;
	public int framePhase = 2;
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
		xScreen = xTile * m.tileSize;
		yScreen = yTile * m.tileSize;
	}
	
	public void move( Face dir )
	{
		if( !moving )
		{
			if( m.canWalk( xTile + dir.x, yTile + dir.y ) )
			{
				facing = dir;
				moving = true;
			}
		}
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
				moveFrame = 0;
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
	}
	
	public void render( Graphics2D g )
	{
		g.setColor( Color.BLACK );
		g.drawRect( xScreen, yScreen, 3, 3 );
	}
	
	public static enum Face
	{
		NORTH( 0, -1, 3 ),
		SOUTH( 0, 1, 0 ),
		EAST( 1, 0, 2 ),
		WEST( -1, 0, 1 );
		
		public int yFrame;
		public int x;
		public int y;
		
		Face( int x, int y, int yFrame )
		{
			this.x = x;
			this.y = y;
			this.yFrame = yFrame;
		}
	}
}
