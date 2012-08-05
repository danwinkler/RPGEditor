package com.danwink.java.rpg;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class Tileset 
{
	public ArrayList<BufferedImage> autoTiles = new ArrayList<BufferedImage>();
	public BufferedImage mainTile;
	public File configFile;
	
	public int tileSize = 32;
	public int tilesAcross = 8;
	
	public Tileset()
	{
		
	}
	
	public int getWidth()
	{
		return tileSize * tilesAcross + 4;
	}
	
	public int getHeight()
	{
		return getAutoOffset() * tileSize + mainTile.getHeight() + 4;
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
		return getAutoOffset() + getHeight() / tileSize;
	}
	
	public int getNumHorizontalTiles()
	{
		return tilesAcross;
	}
}
