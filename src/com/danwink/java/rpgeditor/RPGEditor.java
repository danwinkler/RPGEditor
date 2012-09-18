package com.danwink.java.rpgeditor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FileDialog;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.dom4j.DocumentException;

import com.danwink.java.rpg.Map;
import com.danwink.java.rpg.MapFileHelper;
import com.danwink.java.rpg.Tileset;
import com.danwink.java.rpgeditor.MapEditor.EditorTool;


import net.miginfocom.swing.MigLayout;

public class RPGEditor 
{
	JFrame window;
	
	TilePicker tp;
	MapEditor ed;
	
	int onLayer = 0;
	
	Action layer1 = new Layer1Action();
	Action layer2 = new Layer2Action();
	Action layer3 = new Layer3Action();
	
	Action saveAction = new SaveAction();
	Action openAction = new OpenAction();
	Action newAction = new NewAction();
	
	Action openTileConfig = new OpenTileConfigAction();
	
	FileDialog fd;
	
	String defTileConfig = "deftpconf.xml";
	

	public RPGEditor()
	{
		try
		{
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
		}
		catch( ClassNotFoundException e )
		{
			e.printStackTrace();
		}
		catch( InstantiationException e )
		{
			e.printStackTrace();
		}
		catch( IllegalAccessException e )
		{
			e.printStackTrace();
		}
		catch( UnsupportedLookAndFeelException e )
		{
			e.printStackTrace();
		}
		
		window = new JFrame( "RPG Editor" );
		window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		MigLayout layout = new MigLayout( "", "[][grow]", "[][grow]");
		window.setLayout( layout );
		
		JMenuBar bar = new JMenuBar();
		window.setJMenuBar( bar );
		
		JMenu fileMenu = new JMenu( "File" );
		fileMenu.setMnemonic( KeyEvent.VK_F );
		bar.add( fileMenu );
		fileMenu.add( new JMenuItem( newAction ) );
		fileMenu.add( new JMenuItem( openAction ) );
		fileMenu.add( new JMenuItem( saveAction ) );
		fileMenu.addSeparator();
		fileMenu.add( new JMenuItem( new SetTileConfigAction() ) );
		
		JMenu toolsMenu = new JMenu( "Tools" );
		toolsMenu.setMnemonic( KeyEvent.VK_T );
		bar.add( toolsMenu );
		toolsMenu.add( new JMenuItem( openTileConfig ) );
		
		JToolBar toolbar = new JToolBar();
		JToggleButton l1 = new JToggleButton( layer1 );
		JToggleButton l2 = new JToggleButton( layer2 );
		JToggleButton l3 = new JToggleButton( layer3 );
		ButtonGroup bg = new ButtonGroup();
		bg.add( l1 );
		bg.add( l2 );
		bg.add( l3 );
		toolbar.add( l1 );
		toolbar.add( l2 );
		toolbar.add( l3 );
		
		toolbar.addSeparator();
		bg = new ButtonGroup();
		for( EditorTool t : EditorTool.values() )
		{
			JToggleButton tool = new JToggleButton( new ToolAction( t ) );
			bg.add( tool );
			toolbar.add( tool );
		}
		
		toolbar.setFloatable( false );
		
		window.add( toolbar, "span 2, wrap, growx" );
		
		tp = new TilePicker();
		
		try {
			Tileset ts = MapFileHelper.loadTileConfig( new File( defTileConfig ) );
			tp.setTileset( ts );
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ed = new MapEditor( this );
		
		JScrollPane tpscroll = new JScrollPane( tp );
		tpscroll.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
		tpscroll.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
		JScrollPane edscroll = new JScrollPane( ed );
		edscroll.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
		edscroll.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS );
		
		window.add( tpscroll, "height 128::, growy" );
		window.add( edscroll, "grow" );
	}
	
	public void launch()
	{
		window.pack();
		window.setVisible( true );
		window.setSize( 800, 600 );
	}
	
	public static void main( String[] args )
	{
		RPGEditor re = new RPGEditor();
		re.launch();
	}
	
	class Layer1Action extends AbstractAction
	{
		public Layer1Action()
		{
			super( "Layer 1" );
			putValue( SELECTED_KEY, true );
		}
		
		public void actionPerformed( ActionEvent e ) 
		{
			ed.selectedLayer = 0;
		}
	}
	
	class Layer2Action extends AbstractAction
	{
		public Layer2Action()
		{
			super( "Layer 2" );
		}
		
		public void actionPerformed( ActionEvent e ) 
		{
			ed.selectedLayer = 1;
		}
		
	}
	
	class Layer3Action extends AbstractAction
	{
		public Layer3Action()
		{
			super( "Layer 3" );
		}
		
		public void actionPerformed( ActionEvent e ) 
		{
			ed.selectedLayer = 2;
		}
	}
	
	class ToolAction extends AbstractAction
	{
		EditorTool t;
		public ToolAction( EditorTool t )
		{
			super( t.toString() );
			this.t = t;
			putValue( SELECTED_KEY, true );
		}
		
		public void actionPerformed( ActionEvent e ) 
		{
			ed.tool = EditorTool.PENCIL;
		}
	}
	
	class NewAction extends AbstractAction
	{
		public NewAction()
		{
			super( "New" );
			putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_N, Event.CTRL_MASK ) );
			putValue( MNEMONIC_KEY, new Integer( KeyEvent.VK_N ) );
		}
		
		public void actionPerformed( ActionEvent e ) 
		{
			new NewMapDialog( RPGEditor.this ).show();
		}
	}
	
	class OpenAction extends AbstractAction
	{
		public OpenAction()
		{
			super( "Open" );
			putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_O, Event.CTRL_MASK ) );
		}
		
		public void actionPerformed( ActionEvent e ) 
		{
			fd = new FileDialog( window, "Load Map", FileDialog.LOAD );
			fd.setVisible( true );
			
			String dir = fd.getDirectory();
			String f = fd.getFile();
			if( f != null && dir != null )
			{
				try {
					ed.m = MapFileHelper.loadMap( new File( dir+f ) );
					ed.m.setTileset( MapFileHelper.loadTileConfig( new File( "tileconfigs/" + ed.m.configFile ) ) );
					tp.tileset = ed.m.t;
					ed.setPreferredSize( new Dimension( ed.m.width * ed.m.tileSize, ed.m.height * ed.m.tileSize ) );
					ed.repaint();
					tp.repaint();
				} catch ( DocumentException e1 ) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException ex ) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}
			fd.dispose();
		}
	}
	
	class SaveAction extends AbstractAction
	{
		public SaveAction()
		{
			super( "Save" );
			putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_S, Event.CTRL_MASK ) );
		}
		
		public void actionPerformed( ActionEvent e ) 
		{
			fd = new FileDialog( window, "Save Map", FileDialog.SAVE );
			fd.setVisible( true );
			
			String dir = fd.getDirectory();
			String f = fd.getFile();
			if( f != null && dir != null )
			{
				try {
					MapFileHelper.saveMap( new File( dir+f ), ed.m );
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			fd.dispose();
		}
	}
	
	class SetTileConfigAction extends AbstractAction
	{
		public SetTileConfigAction()
		{
			super( "Set Tile Config" );
		}
		
		public void actionPerformed( ActionEvent e ) 
		{
			fd = new FileDialog( window, "Load Tile Config", FileDialog.LOAD );
			fd.setVisible( true );
			
			String dir = fd.getDirectory();
			String f = fd.getFile();
			if( f != null && dir != null )
			{
				try {
					ed.setTileset( MapFileHelper.loadTileConfig( new File( dir+f ) ) );
					tp.setTileset( ed.m.t );
					tp.repaint();
					ed.repaint();
				} catch ( DocumentException e1 ) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException ex ) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}
			fd.dispose();
		}
	}
	
	class OpenTileConfigAction extends AbstractAction
	{
		public OpenTileConfigAction()
		{
			super( "Open Tile Config" );
		}
		
		public void actionPerformed( ActionEvent e ) 
		{
			TilesetConfigCreator tcc = new TilesetConfigCreator( RPGEditor.this );
		}
	}

	public void newMap( int width, int height, int layers )
	{
		ed.m = new Map( width, height, layers );
		ed.setTileset( tp.tileset );
	}
}
