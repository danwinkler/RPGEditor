package com.danwink.java.rpg;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;


public class AutoTileDrawer 
{
	private static Graphics2D gg;
	private static BufferedImage imim;
	private static int ht;
	private static int f;
	public static void draw( Graphics2D g, BufferedImage im, int tileSize, int frame, boolean nw, boolean n, boolean ne, boolean w, boolean e, boolean sw, boolean s, boolean se )
	{
		ht = tileSize/2;
		gg = g;
		imim = im;
		f = frame*tileSize*3;
		
		//topLeft
		if( w && nw && n )
		{
			tl( 2, 4 );
		}
		else if( w && !nw && n )
		{
			tl( 4, 0 );
		}
		else if( w && !n )
		{
			tl( 2, 2 );
		}
		else if( n && !w )
		{
			tl( 0, 4 );
		}
		else if( !n && !w )
		{
			tl( 0, 0 );
		}
		
		//topRight
		if( e && ne && n )
		{
			tr( 3, 4 );
		}
		else if( e && !ne && n )
		{
			tr( 5, 0 );
		}
		else if( e && !n )
		{
			tr( 3, 2 );
		}
		else if( n && !e )
		{
			tr( 5, 4 );
		}
		else if( !n && !e )
		{
			tr( 1, 0 );
		}
		
		//bottomLeft
		if( w && sw && s )
		{
			bl( 2, 5 );
		}
		else if( w && !sw && s )
		{
			bl( 4, 1 );
		}
		else if( w && !s )
		{
			bl( 2, 7 );
		}
		else if( s && !w )
		{
			bl( 0, 5 );
		}
		else if( !s && !w )
		{
			bl( 0, 1 );
		}
		
		//bottomRight
		if( e && se && s )
		{
			br( 3, 5 );
		}
		else if( e && !se && s )
		{
			br( 5, 1 );
		}
		else if( e && !s )
		{
			br( 3, 7 );
		}
		else if( s && !e )
		{
			br( 5, 5 );
		}
		else if( !s && !e )
		{
			br( 1, 1 );
		}
	}
	
	private static void tl( int x, int y )
	{
		gg.drawImage( imim, 0, 0, ht, ht, f+ht*x, ht*y, f+ht*(x+1), ht*(y+1), null );
	}
	
	private static void tr( int x, int y )
	{
		gg.drawImage( imim, ht, 0, ht+ht, ht, f+ht*x, ht*y, f+ht*(x+1), ht*(y+1), null );
	}
	
	private static void bl( int x, int y )
	{
		gg.drawImage( imim, 0, ht, ht, ht+ht, f+ht*x, ht*y, f+ht*(x+1), ht*(y+1), null );
	}
	
	private static void br( int x, int y )
	{
		gg.drawImage( imim, ht, ht, ht+ht, ht+ht, f+ht*x, ht*y, f+ht*(x+1), ht*(y+1), null );
	}
}
