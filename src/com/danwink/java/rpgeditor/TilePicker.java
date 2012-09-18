package com.danwink.java.rpgeditor;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

import com.danwink.java.rpg.Tileset;
import com.phyloa.dlib.util.DMath;


public class TilePicker extends JPanel implements MouseListener, MouseMotionListener
{
	Tileset tileset;
	
	int selectedX = 0;
	int selectedY = 0;
	
	int selectedWidth = 1;
	int selectedHeight = 1;
	
	int pressX;
	int pressY;
	
	int scale = 1;
	
	public TilePicker()
	{
		super( true );
		if( tileset != null ){
			this.setPreferredSize( new Dimension( tileset.getWidth(), tileset.getHeight() ) );
		}	
		addMouseListener( this );
		addMouseMotionListener( this );
	}
	
	public void paintComponent( Graphics gg )
	{
		int windowX = tileset.getWidth() * scale;
		int windowY = tileset.getHeight() * scale;
		if( windowX != getWidth() || windowY != getHeight() )
		{
			this.setPreferredSize( new Dimension( windowX, windowY ) );
		}
		
		Graphics2D g = (Graphics2D)gg;
		
		g.setColor( Color.WHITE );
		g.fillRect( 0, 0, getWidth(), getHeight() );
		
		g.translate( 2, 2 );
		g.scale( scale, scale );
		tileset.render( g );
		
		g.setColor( Color.RED );
		g.setStroke( new BasicStroke( 2 ) );
		g.drawRect( (selectedX * tileset.tileSize)-1, (selectedY * tileset.tileSize)-1, selectedWidth*tileset.tileSize+2, selectedHeight*tileset.tileSize+2 );
	}
	
	public void setTileset( Tileset t )
	{
		this.tileset = t;
		if( t.tileSize == 16 )
		{
			scale = 2;
		}
		else
		{
			scale = 1;
		}
	}

	public void mouseClicked( MouseEvent e ) 
	{
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed( MouseEvent e ) 
	{
		selectedX = xMouseToTile( e.getX() );
		selectedY = yMouseToTile( e.getY() );
		pressX = selectedX;
		pressY = selectedY;
		selectedWidth = 1;
		selectedHeight = 1;
		this.repaint();
	}

	public void mouseReleased( MouseEvent e ) 
	{
		this.repaint();
	}
	
	@Override
	public void mouseDragged( MouseEvent e )
	{
		int tx = xMouseToTile( e.getX() );
		int ty = yMouseToTile( e.getY() );
		int difX = tx - pressX;
		int difY = ty - pressY;
		if( difX < 0 )
		{
			selectedX = tx;
			selectedWidth = -difX + 1;
		} 
		else
		{
			selectedWidth = difX + 1;
		}
		if( difY < 0 )
		{
			selectedY = ty;
			selectedHeight = -difY + 1;
		} 
		else
		{
			selectedHeight = difY + 1;
		}
		this.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public int xMouseToTile( int x )
	{
		return DMath.bound( ((x/scale)-2) / tileset.tileSize, 0, tileset.tilesAcross );
	}
	
	public int yMouseToTile( int y )
	{
		return ((y/scale)-2) / tileset.tileSize;
	}

	public int[][] getSelected() 
	{
		int[][] ret = new int[selectedWidth][selectedHeight];
		for( int x = selectedX; x < selectedX+selectedWidth; x++ )
		{
			for( int y = selectedY; y < selectedY+selectedHeight; y++ )
			{
				ret[x-selectedX][y-selectedY] = tileset.getTile( x, y );
			}
		}
		
		return ret;
	}
	
	public void renderSelected( Graphics2D g, int mx, int my )
	{
		for( int y = selectedY; y < selectedY + selectedHeight; y++ )
		{
			for( int x = selectedX; x < selectedX + selectedWidth; x++ )
			{
				if( y > 0 );
			}
		}
	}
}
