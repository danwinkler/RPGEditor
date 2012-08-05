package com.danwink.java.rpgeditor;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.danwink.java.rpg.Tileset;

import net.miginfocom.swing.MigLayout;

public class TilesetConfigCreator 
{
	JDialog dialog;
	TilesetEditorPane tep;
	JButton loadConfig;
	JButton loadMainTile;
	JButton loadAutotile;
	JButton elevationMode;
	JButton passabilityMode;
	JButton save;
	JButton cancel;
	
	Tileset t = new Tileset();
	
	public TilesetConfigCreator( RPGEditor rpged )
	{
		dialog = new JDialog( rpged.window );
		dialog.setLayout( new MigLayout( "", "[][][][][][grow]", "[][grow][]" ) );
		
		loadConfig = new JButton( "Load Config" );
		save = new JButton( "Save Config" );
		loadMainTile = new JButton( "Load Main Tile Image" );
		loadAutotile = new JButton( "Load Autotile" );
		elevationMode = new JButton( "Elevation Mode" );
		passabilityMode = new JButton( "Passability Mode" );
		
		cancel = new JButton( "Exit" );
		
		dialog.add(  loadConfig );
		dialog.add( save );
		dialog.add( loadMainTile );
		dialog.add( loadAutotile );
		dialog.add( elevationMode );
		dialog.add( passabilityMode, "wrap" );
		
		TilesetEditorPane tep = new TilesetEditorPane();
		JScrollPane scroll = new JScrollPane( tep );
		scroll.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
		scroll.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
		
		dialog.add( scroll, "span, grow, wrap, height 400::, width 256::" );
		
		dialog.add( cancel );
		
		dialog.pack();
		
		dialog.setVisible( true );
	}
	
	class TilesetEditorPane extends JPanel
	{
		int[][] heights;
		
		public TilesetEditorPane()
		{
			
		}
		
		public void paintComponent( Graphics gg )
		{
			t.render( (Graphics2D)gg );
		}
	}
}
