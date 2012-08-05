package com.danwink.java.rpg;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.danwink.java.rpg.Map.TileEvent;
import com.phyloa.dlib.util.DFile;

public class MapFileHelper 
{
	public static Map loadMap( File file ) throws DocumentException
	{
		SAXReader reader = new SAXReader();
		Document doc = reader.read( file );
		Node map = doc.selectSingleNode( "//map" );
		List<? extends Node> layers = doc.selectNodes( "//map/layers/layer" );
		Map m = new Map( Integer.parseInt( map.valueOf( "@width" ) ), Integer.parseInt( map.valueOf( "@height" ) ), layers.size() );
		m.configFile = map.valueOf( "@config" );
		
		//Load layers
		for( int i = 0; i < layers.size(); i++ )
		{
			List<? extends Node> rows = layers.get( i ).selectNodes( "row" );
			for( int y = 0; y < m.height; y++ )
			{
				String[] vals = rows.get( y ).getText().split( "," );
				for( int x = 0; x < m.width; x++ )
				{
					m.layers[x][y][i] = Integer.parseInt( vals[x] );
				}
			}
		}
		
		//Load Events
		List<? extends Node> events = doc.selectNodes( "//map/events/event" );
		for( Node n : events )
		{
			TileEvent te = new TileEvent( Integer.parseInt( n.valueOf( "@x" ) ), Integer.parseInt( n.valueOf( "@y" ) ) );
			te.code = n.getText();
			m.events.add( te );
		}
		
		//Load onload
		Node n = doc.selectSingleNode( "//map/onload" );
		if( n != null )
		{
			m.onLoadCode = n.getText();
		}
		
		return m;
	}
	
	public static void saveMap( File file, Map m ) throws IOException
	{
		Document doc = DocumentHelper.createDocument();
		Element map = doc.addElement( "map" );
		map.addAttribute( "config", m.configFile );
		map.addAttribute( "width", Integer.toString( m.width ) );
		map.addAttribute( "height", Integer.toString( m.height ) );
		
		//ADD layers
		Element layers = map.addElement( "layers" );
		for( int i = 0; i < m.layers[0][0].length; i++ )
		{
			Element layer = layers.addElement( "layer" );
			for( int y = 0; y < m.height; y++ )
			{
				Element row = layer.addElement( "row" );
				StringBuilder rows = new StringBuilder();
				for( int x = 0; x < m.width; x++ )
				{
					rows.append( m.getTile( x, y, i ) + "," );
				}
				row.setText( rows.toString() );
			}
		}
		
		//ADD events
		Element events = map.addElement( "events" );
		for( int i = 0; i < m.events.size(); i++ )
		{
			TileEvent te = m.events.get( i );
			Element event = events.addElement( "event" );
			event.addAttribute( "x", Integer.toString( te.x ) );
			event.addAttribute( "y", Integer.toString( te.y ) );
			event.setText( te.code );
		}
		
		//ADD onload
		if( m.onLoadCode != null )
		{
			Element onload = map.addElement( "onload" );
			onload.setText( m.onLoadCode );
		}
		
		XMLWriter writer = new XMLWriter(
	            new FileWriter( file )
	        );
	    writer.write( doc );
	    writer.close();
	}
	
	public static Tileset loadTileConfig( File file ) throws IOException, DocumentException
	{
		Tileset tc = new Tileset();
		Document document = null;
		SAXReader reader = new SAXReader();
		
		document = reader.read( file );
		
		List<? extends Node> autotiles = document.selectNodes( "//tpconf/autotile/tile" );
		
		tc.autoTiles.clear();
		
		for( Node n : autotiles )
		{
			tc.autoTiles.add( DFile.loadImage( n.getText() ) );
		}
		
		Node tileSet = document.selectSingleNode( "//tpconf/tileset" );
		tc.mainTile = DFile.loadImage( tileSet.getText() );
		tc.configFile = file;
		return tc;
	}
}
