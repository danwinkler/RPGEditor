package com.danwink.java.rpgeditor;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.CodeEditorPane;
import javax.swing.JButton;
import javax.swing.JDialog;

import com.danwink.java.rpg.Map.TileEvent;

import net.miginfocom.swing.MigLayout;

public class EventEditorDialog implements ActionListener 
{
	RPGEditor ed;
	JDialog d;
	CodeEditorPane codePane;
	JButton submit;
	
	TileEvent ev;
	
	HashMap<String, Color> syntax = new HashMap<String, Color>() {{
		String[] keywords = { "break", "do", "end", "else", "elseif", "function", "if", "local", "nil", "not", "or", "repeat", "return", "then", "until", "while" };
		for( String k : keywords )
		{
			put( k, Color.BLUE );
		}
	}};
	public EventEditorDialog( RPGEditor ed )
	{
		this.ed = ed;
		d = new JDialog( ed.window, "Edit Event" );
		d.setLayout( new MigLayout( "", "[grow]", "[grow][]") );
		
		codePane = new CodeEditorPane();
		codePane.setKeywordColor( syntax );
		codePane.setFont( new Font( "Courier", Font.PLAIN, 12 ) );
		
		submit = new JButton( "Done" );
		submit.addActionListener( this );
		
		d.add( codePane.getContainerWithLines(), "width 100:300:, height 30:200:, wrap, grow" );
		d.add( submit );
		
		d.pack();
		d.setLocationRelativeTo( ed.window );
		
		ev = ed.ed.m.getTileEvent( ed.ed.selectX, ed.ed.selectY );
		if( ev == null )
		{
			ev = ed.ed.m.new TileEvent( ed.ed.selectX, ed.ed.selectY );
			ed.ed.m.addTileEvent( ev );
		}
		codePane.setText( ev.code );
	}

	public void show() 
	{
		d.setVisible( true );
	}

	public void actionPerformed( ActionEvent e ) 
	{
		if( e.getSource() == submit )
		{
			ev.code = codePane.getText();
			d.setVisible( false );
			d.dispose();
		}
	}
}
