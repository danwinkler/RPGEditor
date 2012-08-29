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
		
		if( !n && !e && !w && !s )
		{
			//Dot
			tl( 0, 0 );
			tr( 1, 0 );
			bl( 0, 1 );
			br( 1, 1 );
		}
		else
		{
			for( int i = 0; i < 4; i++ )
			{
				switch( i )
				{
				case 0:
					if( w & n & !nw ) //inside corner
					{
						tl( 4, 0 );
						continue;
					}
					break;
				case 1:
					if( e & n & !ne ) //inside corner
					{
						tr( 5, 0 );
						continue;
					}
					break;
				case 2:
					if( w & s & !sw ) //inside corner
					{
						bl( 4, 1 );
						continue;
					}
					break;
				case 3:
					if( e & s & !se ) //inside corner
					{
						br( 5, 1 );
						continue;
					}
					break;
				}
				if( !w && !n && e ) //nw
				{
					t( 0, 2, i );
				}
				else if( w && !n && e ) //n
				{
					t( 2, 2, i );
				}
				else if( w && !n && !e ) //ne
				{
					t( 4, 2, i );
				}
				else if( !w && n && s && e ) //w
				{	
					t( 0, 4, i );
				}
				else if( w && n && s && e ) //center
				{
					t( 2, 4, i );
				}
				else if( w && s && n && !e ) //e
				{
					t( 4, 4, i );
				}
				else if( !w && !s && e ) //sw
				{
					t( 0, 6, i );
				}
				else if( w && !s && e ) //s
				{
					t( 2, 6, i );
				}
				else if( w && !s && !e ) //se
				{
					t( 4, 6, i );
				}
			}
		}
		
		/*
		//topLeft
		if( w && nw && n )
		{
			tl( 2, 4 );
		}
		else if( w && !nw && n )
		{
			tl( 4, 0 );
		}
		else if( w && !n && !e )
		{
			tl( 4, 2 );
		}
		else if( w && !n && e )
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
		else if( e && !n && !w )
		{
			tr( 1, 2 );
		}
		else if( e && !n && w )
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
		else if( w && !s && !e )
		{
			bl( 4, 7 );
		}
		else if( w && !s && e )
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
		else if( e && !s && !w )
		{
			br( 1, 7 );
		}
		else if( e && !s && w )
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
		*/
	}
	
	private static void t( int x, int y, int i )
	{
		switch( i )
		{
		case 0: tl( x, y ); break;
		case 1: tr( x+1, y ); break;
		case 2: bl( x, y+1 ); break;
		case 3: br( x+1, y+1 ); break;
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
