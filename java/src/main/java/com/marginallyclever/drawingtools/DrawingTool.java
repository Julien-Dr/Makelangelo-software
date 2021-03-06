package com.marginallyclever.drawingtools;

import java.awt.BasicStroke;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.prefs.Preferences;

import javax.swing.JPanel;

import com.jogamp.opengl.GL2;
import com.marginallyclever.makelangeloRobot.MakelangeloRobot;

public abstract class DrawingTool {
	protected float diameter; // mm

	DecimalFormat df;

	// used while drawing to the GUI
	protected float feedRate;
	protected String name;
	protected MakelangeloRobot robot;
	// Every tool must have a unique number.
	protected int toolNumber;

	protected float zOff;
	protected float zOn;
	protected float zRate;

	public DrawingTool(MakelangeloRobot robot) {
		this.robot = robot;
		diameter = 1;
		
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
		otherSymbols.setDecimalSeparator('.');
		df = new DecimalFormat("#.###",otherSymbols);
		df.setGroupingUsed(false);
	}

	public void drawLine(GL2 gl2, double x1, double y1, double x2, double y2) {
		gl2.glBegin(GL2.GL_LINES);
		gl2.glVertex2d(x1, y1);
		gl2.glVertex2d(x2, y2);
		gl2.glEnd();
	}

	public float getDiameter() {
		return diameter;
	}

	public float getFeedRate() {
		return feedRate;
	}

	public String getName() {
		return name;
	}

	// load a configure menu and return it to the caller for embedding.
	public JPanel getPanel() {
		return null;
	}

	public float getPenDownAngle() {
		return zOn;
	}

	public float getPenUpAngle() {
		return zOff;
	}

	public BasicStroke getStroke() {
		return new BasicStroke(diameter * 10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	}

	public void loadConfig(Preferences prefs) {
		prefs = prefs.node(name);
		setDiameter(Float.parseFloat(prefs.get("diameter", Float.toString(diameter))));
		zRate = Float.parseFloat(prefs.get("z_rate", Float.toString(zRate)));
		zOn = Float.parseFloat(prefs.get("z_on", Float.toString(zOn)));
		zOff = Float.parseFloat(prefs.get("z_off", Float.toString(zOff)));
		// tool_number =
		// Integer.parseInt(prefs.get("tool_number",Integer.toString(tool_number)));
		feedRate = Float.parseFloat(prefs.get("feed_rate", Float.toString(feedRate)));
	}

	public void saveConfig(Preferences prefs) {
		prefs = prefs.node(name);
		prefs.put("diameter", Float.toString(getDiameter()));
		prefs.put("z_rate", Float.toString(zRate));
		prefs.put("z_on", Float.toString(zOn));
		prefs.put("z_off", Float.toString(zOff));
		prefs.put("tool_number", Integer.toString(toolNumber));
		prefs.put("feed_rate", Float.toString(feedRate));
	}

	public void setDiameter(float d) {
		diameter = d;
	}

	public String getPenDownString() {
		return "G00 Z" + df.format(getPenDownAngle()) + ";\n";
	}

	public String getPenUpString() {
		return "G00 Z" + df.format(getPenUpAngle()) + ";\n";
	}

	public void writeChangeTo(Writer out) throws IOException {
		out.write("M06 T" + toolNumber + ";\n");
		out.write("G00 F" + getFeedRate() + " A" + robot.getSettings().getAcceleration() + ";\n");
	}

	public void writeChangeTo(Writer out,String name) throws IOException {
		out.write("M06 T" + toolNumber + "; //"+name+"\n");
		out.write("G00 F" + getFeedRate() + " A" + robot.getSettings().getAcceleration() + ";\n");
	}

	public void writeMoveTo(Writer out, double x, double y) throws IOException {
		out.write("G00 X" + df.format(x) + " Y" + df.format(y) + ";\n");
	}

	// lift the pen
	public void writeOff(Writer out) throws IOException {
		out.write(getPenUpString());
	}

	// lower the pen
	public void writeOn(Writer out) throws IOException {
		out.write(getPenDownString());
	}
	
	public void save() {}
	
	public void cancel() {}
}
