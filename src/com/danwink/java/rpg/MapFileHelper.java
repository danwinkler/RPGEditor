package com.danwink.java.rpg;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.danwink.java.rpg.Map.TileEvent;
import com.danwink.java.rpg.Tileset.TileInfo;
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
		m.name = file.getName();
		if( m.name.endsWith( ".xml" ) )
		{
			m.name = m.name.substring( 0, m.name.length()-4 );
		}
		
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
			TileEvent te = m.new TileEvent( Integer.parseInt( n.valueOf( "@x" ) ), Integer.parseInt( n.valueOf( "@y" ) ) );
			te.code = n.getText();
			m.events.add( te );
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
			event.addAttribute( "x", Integer.toString( te.xTile ) );
			event.addAttribute( "y", Integer.toString( te.yTile ) );
			event.setText( te.code );
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
			tc.autoTiles.add( DFile.loadImage( "graphics/autotiles/" + n.getText() ) );
			tc.autoTileFiles.add( n.getText() );
		}
		
		Node tileSet = document.selectSingleNode( "//tpconf/tileset" );
		tc.mainTile = DFile.loadImage( "graphics/tilesets/" + tileSet.getText() );
		tc.mainTileFile = tileSet.getText();
		tc.configFile = file.getName();
		
		List<? extends Node> tileinfo = document.selectNodes( "//tpconf/tileinfo/tile" );
		
		tc.info.clear();
		
		for( Node n : tileinfo )
		{
			TileInfo ti = tc.new TileInfo( Integer.valueOf( n.valueOf( "@id" ) ) );
			ti.elevation = Integer.parseInt( n.selectSingleNode( "elevation" ).getText() );
			String[] enterS = n.selectSingleNode( "enter" ).getText().split( "," );
			for( int i = 0; i < enterS.length; i++ )
			{
				ti.enter[i] = Boolean.parseBoolean( enterS[i].trim() );
			}
			
			String[] exitS = n.selectSingleNode( "exit" ).getText().split( "," );
			for( int i = 0; i < exitS.length; i++ )
			{
				ti.exit[i] = Boolean.parseBoolean( exitS[i].trim() );
			}
			
			Node lightNode = n.selectSingleNode( "light" );
			if( lightNode != null )
			{
				String[] lightS = lightNode.getText().split( "," );
				ti.light = true;
				ti.lr = Integer.parseInt( lightS[0] );
				ti.lg = Integer.parseInt( lightS[1] );
				ti.lb = Integer.parseInt( lightS[2] );
			}
			tc.info.put( ti.tile, ti );
		}
		
		return tc;
	}
	
	public static void saveTileConfig( File file, Tileset t ) throws IOException
	{
		Document doc = DocumentHelper.createDocument();
		Element tpconf = doc.addElement( "tpconf" );
		Element autotiles = tpconf.addElement( "autotile" );
		for( int i = 0; i < t.autoTileFiles.size(); i++ )
		{
			Element tile = autotiles.addElement( "tile" );
			tile.setText( t.autoTileFiles.get( i ) );
		}
		
		Element mainTile = tpconf.addElement( "tileset" );
		mainTile.setText( t.mainTileFile );
		
		Element tileInfo = tpconf.addElement( "tileinfo" );
		Set<Entry<Integer, TileInfo>> tileinfos = t.info.entrySet();
		for( Entry<Integer, TileInfo> e : tileinfos )
		{
			int id = e.getKey();
			TileInfo ti = e.getValue();
			
			Element tile = tileInfo.addElement( "tile" );
			tile.addAttribute( "id", Integer.toString( id ) );
			tile.addElement( "elevation" ).setText( Integer.toString( ti.elevation ) );
			tile.addElement( "enter" ).setText( ti.enter[0] + "," + ti.enter[1] + "," + ti.enter[2] + "," + ti.enter[3] );
			tile.addElement( "exit" ).setText( ti.exit[0] + "," + ti.exit[1] + "," + ti.exit[2] + "," + ti.exit[3] );
			if( ti.light )
			{
				tile.addElement( "light" ).setText( ti.lr + "," + ti.lg + "," + ti.lb );
			}
		}
		
		XMLWriter writer = new XMLWriter(
			new FileWriter( file )
	    );
	    writer.write( doc );
	    writer.close();
	}
}
