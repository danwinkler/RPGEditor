package com.danwink.java.rpgeditor;

import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.vecmath.Point2i;

import org.dom4j.DocumentException;

import net.miginfocom.swing.MigLayout;

import com.danwink.java.rpg.MapFileHelper;
import com.danwink.java.rpg.Tileset;
import com.danwink.java.rpg.Tileset.TileInfo;
import com.phyloa.dlib.util.DFile;

public class TilesetConfigCreator 
{
	RPGEditor rpged;
	JDialog dialog;
	TilesetEditorPane tep;
	JScrollPane scroll;
	JButton loadConfig;
	JButton loadMainTile;
	JButton loadAutotile;
	JToggleButton elevationMode;
	JToggleButton enterMode;
	JToggleButton exitMode;
	JToggleButton lightMode;
	JButton save;
	JButton cancel;
	
	TCCMode editMode = TCCMode.ENTER;
	
	Tileset t = new Tileset();
	
	LoadMainTileImage lmtiaction = new LoadMainTileImage();
	
	int scale = 1;
	
	public TilesetConfigCreator( RPGEditor rpged )
	{
		this.rpged = rpged;
		dialog = new JDialog( rpged.window );
		dialog.setLayout( new MigLayout( "", "[][][][][][grow]", "[][grow][]" ) );
		
		loadConfig = new JButton( new LoadConfig() );
		save = new JButton( new SaveConfig() );
		loadMainTile = new JButton( lmtiaction );
		loadAutotile = new JButton( new LoadAutoTileImage() );
		elevationMode = new JToggleButton( new SetElevationMode() );
		enterMode = new JToggleButton( new SetEnterMode() );
		exitMode = new JToggleButton( new SetExitMode() );
		lightMode = new JToggleButton( new SetLightMode() );
		
		ButtonGroup bg = new ButtonGroup();
		bg.add( elevationMode );
		bg.add( enterMode );
		bg.add( exitMode );
		bg.add( lightMode );
		
		cancel = new JButton( "Exit" );
		
		dialog.add( loadConfig );
		dialog.add( save );
		dialog.add( loadMainTile );
		dialog.add( loadAutotile );
		dialog.add( elevationMode );
		dialog.add( enterMode );
		dialog.add( exitMode );
		dialog.add( lightMode, "wrap" );
		
		tep = new TilesetEditorPane();
		scroll = new JScrollPane( tep );
		scroll.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
		scroll.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
		
		dialog.add( scroll, "span, grow, wrap, height 400::, width 256::" );
		
		dialog.add( cancel );
		
		dialog.pack();
		
		dialog.setVisible( true );
	}
	
	class TilesetEditorPane extends JPanel implements MouseListener
	{	
		private static final long serialVersionUID = 4728580137148983308L;

		public TilesetEditorPane()
		{
			super();
			this.addMouseListener( this );
		}
		
		public void paintComponent( Graphics gg )
		{
			Graphics2D g = (Graphics2D) gg;
			g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
			g.clearRect( 0, 0, getWidth(), getHeight() );
			
			t.render( (Graphics2D)gg );
			if( t.mainTile != null )
			{
				for( int y = 0; y < t.getNumVerticalTiles(); y++ )
				{
					for( int x = 0; x < t.getNumHorizontalTiles(); x++ )
					{
						int i = t.getTile( x, y );
						TileInfo ti = t.info.get( i );
						if( ti == null )
						{
							ti = t.new TileInfo( i );
							t.info.put( i, ti );
						}
						AffineTransform at = g.getTransform();
						g.translate( x*t.tileSize, y*t.tileSize );
						switch( editMode )
						{
						case ELEVATION:
							ti.drawElevation( g );
							break;
						case ENTER:
							ti.drawEnter( g );
							break;
						case EXIT:
							ti.drawExit( g );
							break;
						case LIGHT:
							ti.drawLight( g );
						}
						g.setTransform( at );
					}
				}
			}
		}

		public void mouseClicked( MouseEvent e ) 
		{
			
		}

		public void mouseEntered( MouseEvent e )
		{
			
		}

		public void mouseExited( MouseEvent e ) 
		{
			
		}

		public void mousePressed( MouseEvent e ) 
		{
			int xTile = e.getX() / t.tileSize;
			int yTile = e.getY() / t.tileSize;
			
			int tile = t.getTile( xTile, yTile );
			if( editMode == TCCMode.LIGHT )
			{
				TileInfo ti = t.info.get( tile );
				ti.light = !ti.light;
				repaint();
				return;
			}
			else if( editMode == TCCMode.ELEVATION )
			{
				if( e.getButton() == MouseEvent.BUTTON1 )
				{
					t.info.get( tile ).increaseElevation();
				}
				else if( e.getButton() == MouseEvent.BUTTON3 )
				{
					t.info.get( tile ).decreaseElevation();
				}
				repaint();
				return;
			}
			
			int x = e.getX() - (xTile*t.tileSize);
			int y = e.getY() - (yTile*t.tileSize);
			
			int dir = t.getDirection( x, y );
			
			if( editMode == TCCMode.ENTER )
			{
				t.info.get( tile ).flipEnter( dir );
			}
			else if( editMode == TCCMode.EXIT )
			{
				t.info.get( tile ).flipExit( dir );
			}
			
			repaint();
		}

		public void mouseReleased( MouseEvent e ) 
		{
			
		}
	}
	
	class LoadMainTileImage extends AbstractAction
	{
		private static final long serialVersionUID = -6347845977920719354L;
		public LoadMainTileImage()
		{
			super( "Load Main Tile Image" );
		}
		public void actionPerformed( ActionEvent e ) 
		{
			rpged.fd = new FileDialog( rpged.window, "Load Main Tile Image", FileDialog.LOAD );
			rpged.fd.setVisible( true );
			rpged.fd.setFilenameFilter( new FilenameFilter() {
				public boolean accept( File f, String s ) {
					return s.endsWith( "png" ) || s.endsWith( "jpg" ) || s.endsWith( "bmp" ); 
				}
			});
			
			String dir = rpged.fd.getDirectory();
			String f = rpged.fd.getFile();
			if( f != null && dir != null )
			{
				try {
					t.mainTile = DFile.loadImage( dir+f );
					t.mainTileFile = f;
					t.tileSize = t.mainTile.getWidth() / 8;
					
					scale = t.tileSize < 32 ? 2 : 1;
					tep.setPreferredSize( new Dimension( t.getWidth() * scale, t.getHeight() * scale ) );
					scroll.repaint();
				} catch ( IOException e1 ) {
					e1.printStackTrace();
				}
			}
			rpged.fd.dispose();
		}
	}
	
	class LoadAutoTileImage extends AbstractAction
	{
		private static final long serialVersionUID = -6347845977920719354L;
		public LoadAutoTileImage()
		{
			super( "Load Auto Tile Image" );
		}
		public void actionPerformed( ActionEvent e ) 
		{
			rpged.fd = new FileDialog( rpged.window, "Load AutoTile Image", FileDialog.LOAD );
			rpged.fd.setVisible( true );
			rpged.fd.setFilenameFilter( new FilenameFilter() {
				public boolean accept( File f, String s ) {
					return s.endsWith( "png" ) || s.endsWith( "jpg" ) || s.endsWith( "bmp" ); 
				}
			});
			
			String dir = rpged.fd.getDirectory();
			String f = rpged.fd.getFile();
			if( f != null && dir != null )
			{
				try {
					t.autoTiles.add( DFile.loadImage( dir+f ) );
					t.autoTileFiles.add( f );
					tep.setPreferredSize( new Dimension( t.getWidth(), t.getHeight() ) );
					scroll.repaint();
					
				} catch ( IOException e1 ) {
					e1.printStackTrace();
				}
			}
			rpged.fd.dispose();
		}
	}
	
	class SetElevationMode extends AbstractAction
	{
		public SetElevationMode()
		{
			super( "Elevation Mode" );
		}
		
		public void actionPerformed( ActionEvent e )
		{
			editMode = TCCMode.ELEVATION;
			tep.repaint();
		}
	}
	
	class SetEnterMode extends AbstractAction
	{
		public SetEnterMode()
		{
			super( "Enter Mode" );
		}
		
		public void actionPerformed( ActionEvent e )
		{
			editMode = TCCMode.ENTER;
			tep.repaint();
		}
	}
	
	class SetExitMode extends AbstractAction
	{
		public SetExitMode()
		{
			super( "Exit Mode" );
		}
		
		public void actionPerformed( ActionEvent e )
		{
			editMode = TCCMode.EXIT;
			tep.repaint();
		}
	}
	
	class SetLightMode extends AbstractAction
	{
		public SetLightMode()
		{
			super( "Light Mode" );
		}
		
		public void actionPerformed( ActionEvent e )
		{
			editMode = TCCMode.LIGHT;
			tep.repaint();
		}
	}
	
	class SaveConfig extends AbstractAction
	{
		public SaveConfig()
		{
			super( "Save Config" );
		}

		public void actionPerformed( ActionEvent e ) 
		{
			rpged.fd = new FileDialog( rpged.window, "Save Config", FileDialog.SAVE );
			rpged.fd.setVisible( true );
			
			String dir = rpged.fd.getDirectory();
			String f = rpged.fd.getFile();
			if( f != null && dir != null )
			{
				try {
					t.configFile = f;
					MapFileHelper.saveTileConfig( new File( dir+f ), t );
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			rpged.fd.dispose();
		}
	}
	
	class LoadConfig extends AbstractAction
	{
		public LoadConfig()
		{
			super( "Load Config" );
		}

		public void actionPerformed( ActionEvent e ) 
		{
			rpged.fd = new FileDialog( rpged.window, "Load Map", FileDialog.LOAD );
			rpged.fd.setVisible( true );
			
			String dir = rpged.fd.getDirectory();
			String f = rpged.fd.getFile();
			if( f != null && dir != null )
			{
				try {
					t = MapFileHelper.loadTileConfig( new File( dir+f ) );
				} catch ( DocumentException e1 ) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException ex ) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}
			rpged.fd.dispose();
		}
	}
	
	enum TCCMode {
		ELEVATION,
		ENTER,
		EXIT,
		LIGHT;
	}
}
