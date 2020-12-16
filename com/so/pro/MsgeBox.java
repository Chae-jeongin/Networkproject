package com.so.pro;

import java.awt.Component;

import javax.swing.JOptionPane;

public class MsgeBox {

	public void messageBox(Object obj , String message){
        JOptionPane.showMessageDialog( (Component)obj , message);
    }
	
}
