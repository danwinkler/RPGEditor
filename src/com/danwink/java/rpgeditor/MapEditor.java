package com.danwink.java.rpgeditor;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;

import com.danwink.java.rpg.Map;
import com.danwink.java.rpg.Map.TileEvent;
import com.danwink.java.rpg.Tileset;
import com.phyloa.dlib.util.DMath;


public class MapEditor extends JPanel implements MouseListener, MouseMotionListener
{
	RPGEditor rpged;
	TilePicker tp;
	Map m = new Map( 20, 20, 3 );
	
	int selectX, selectY;
	
	int selectedLayer = 0;
	public EditorTool tool = EditorTool.PENCIL;
	private boolean dragDrawing = false;
	
	int scale = 1;
	
	public MapEditor( RPGEditor rpged )
	{
		super( true );
		this.addMouseListener( this );
		this.addMouseMotionListener( this );
		
		this.rpged = rpged;
		this.tp = rpged.tp;
		m.setTileset( tp.tileset );
		if( tp.tileset.tileSize == 16 )
		{
			scale = 2;
		}
		else
		{
			scale = 1;
		}
	}
	
	public void paintComponent( Graphics gg )
	{
		Graphics2D g = (Graphics2D)gg;
		g.setColor( Color.WHITE );
		g.fillRect( 0, 0, getWidth(), getHeight() );
		g.scale( scale, scale );
		m.render( g, null );

		setAlpha( g, .5f );
		int[][] selected = tp.getSelected();
		for( int xx = Math.max( selectX, 0 ); xx < Math.min( selectX+selected.length, m.width ); xx++ )
		{
			for( int yy = Math.max( selectY, 0 ); yy < Math.min( selectY+selected[xx-selectX].length, m.height ); yy++ )
			{
				m.renderTile( g, xx, yy, selectedLayer, selected[xx-selectX][yy-selectY] ); 
			}
		}
		setAlpha( g, 1.0f );
		
		g.setColor( Color.black );
		g.setFont( new Font( "Arial", Font.BOLD, 20 ) );
		for( int i = 0; i < m.events.size(); i++ )
		{
			TileEvent te = m.events.get( i );
			g.drawString( "E", te.xTile * m.tileSize + m.tileSize/2 - 6, te.yTile * m.tileSize +m. tileSize/2 + 8 );
		}
		
		g.setColor( Color.BLACK );
		g.drawRect( -1, -1, m.width*m.tileSize + 2, m.height*m.tileSize + 2 );
	}
	
	private void setAlpha( Graphics2D g2, float alpha ) 
	{
		int type = AlphaComposite.SRC_OVER;
		g2.setComposite( ( AlphaComposite.getInstance( type, alpha ) ) );
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited( MouseEvent e ) 
	{
		selectX = -2;
		selectY = -2;
	}

	@Override
	public void mousePressed( MouseEvent e ) 
	{
		if( e.getButton() == MouseEvent.BUTTON1 )
		{
			selectX = xMouseToTile( e.getX() );
			selectY = yMouseToTile( e.getY() );
			m.setTiles( selectX, selectY, selectedLayer, tp.getSelected() );
			repaint();
			dragDrawing  = true;
		}
		else if( e.getButton() == MouseEvent.BUTTON3 )
		{
			new EventEditorDialog( rpged ).show();
			dragDrawing = false;
		}
	}

	@Override
	public void mouseReleased( MouseEvent e ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged( MouseEvent e ) 
	{
		if( dragDrawing )
		{
			selectX = xMouseToTile( e.getX() );
			selectY = yMouseToTile( e.getY() );
			m.setTiles( selectX, selectY, selectedLayer, tp.getSelected() );
			repaint();
		}
	}

	@Override
	public void mouseMoved( MouseEvent e ) 
	{
		selectX = xMouseToTile( e.getX() );
		selectY = yMouseToTile( e.getY() );
		repaint();
	}
	
	public int xMouseToTile( int x )
	{
		return (x/scale) / m.tileSize;
	}
	
	public int yMouseToTile( int y )
	{
		return (y/scale) / m.tileSize;
	}
	
	public static enum EditorTool
	{
		PENCIL,
		FILL,
		BOX
	}

	public void setTileset( Tileset tileset )
	{
		m.setTileset( tileset );
		if( tileset.tileSize == 16 )
		{
			scale = 2;
		}
		else
		{
			scale = 1;
		}
		setPreferredSize( new Dimension( m.width * m.tileSize * scale, m.height * m.tileSize * scale ) );
		repaint();
	}
}
