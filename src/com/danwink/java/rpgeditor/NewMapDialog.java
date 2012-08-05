package com.danwink.java.rpgeditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import net.miginfocom.swing.MigLayout;

public class NewMapDialog implements ActionListener
{
	RPGEditor ed;
	
	JDialog d;
	JSpinner width;
	JSpinner height;
	JButton submit;
	
	public NewMapDialog( RPGEditor editor )
	{
		this.ed = editor;
		
		d = new JDialog( editor.window, "New File" );
		d.setLayout( new MigLayout() );
		
		JLabel widthLabel = new JLabel( "Width:" );
		width = new JSpinner( new SpinnerNumberModel( 20, 0, 1000, 1 ) );
		
		JLabel heightLabel = new JLabel( "Height:" );
		height = new JSpinner( new SpinnerNumberModel( 20, 0, 1000, 1 ) );
		
		submit = new JButton( "Done" );
		submit.addActionListener( this );
		
		d.add( widthLabel );
		d.add( width, "wrap" );
		
		d.add( heightLabel );
		d.add( height, "wrap" );
		
		d.add( submit, "span 2" );
		d.pack();
		d.setLocationRelativeTo( editor.window );
	}
	
	public void show()
	{
		d.setVisible( true );
	}

	public void actionPerformed( ActionEvent e ) 
	{
		if( e.getSource() == submit )
		{
			ed.newMap( (Integer)width.getValue(), (Integer)height.getValue(), 3 );
		}
		d.setVisible( false );
		d.dispose();
		ed.ed.repaint();
	}
	
}
