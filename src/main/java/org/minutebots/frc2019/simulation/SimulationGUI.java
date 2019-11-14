package org.minutebots.frc2019.simulation;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Image;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.util.Vector;

public class SimulationGUI {
    private static SimulationGUI instance;
    
    // Frame and feild attributes
    private JFrame frame;
    private JPanel panel;
    private int frameWidth, frameHeight;
    
    // Robot attributes
    static VirtualBot robot;
    static double rotation = 0;
    private static double robotX = 0, robotY = 0;
    private static double rX = 0, rY = 0;
    private static final int robotSpeed = 20;
	private static final int robotRadius = 50;
	private static double centerX = 50, centerY = 50;
	
	// Trig values
	private static int speedX = 0, speedY = 0;
	private static int velocityX = 0, velocityY = 0;
	private static double direction = Math.PI/2;
	private static double spinSpeed = 2;
	private static int acceleration = 3;
	private static int speedForce = 0;
	private static int friction = 1;
	
    public SimulationGUI() {
        
    }
    
    private static Image blurImageBorder(BufferedImage input, double border) {
        int w = input.getWidth();
        int h = input.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = output.createGraphics(); // We create a new Graphics2D object
        g.drawImage(input, 0, 0, null); // We draw the object

        g.setComposite(AlphaComposite.DstOut);
        Color c0 = new Color(0x000000FF);

        // Left
        g.setPaint(new GradientPaint(new Point2D.Double(0, border), c0, new Point2D.Double(border, border), c0));
        g.fill(new Rectangle2D.Double(0, border, border, h- border - border));

        // Right
        g.setPaint(new GradientPaint(new Point2D.Double(w - border, border), c0, new Point2D.Double(w, border), c0));
        g.fill(new Rectangle2D.Double(w- border, border, border, h- border - border));

        // Top
        g.setPaint(new GradientPaint(new Point2D.Double(border, 0), c0, new Point2D.Double(border, border), c0));
        g.fill(new Rectangle2D.Double(border, 0, w - border - border, border));

        // Bottom
        g.setPaint(new GradientPaint(new Point2D.Double(border, h - border), c0, new Point2D.Double(border, h), c0));
        g.fill(new Rectangle2D.Double(border, h - border, w - border - border, border));

        final float[] floatArray = new float[]{ 0, 1 };
        final Color[] colorArray = new Color[]{ c0, c0 };

        // Top Left
        g.setPaint(new RadialGradientPaint(new Rectangle2D.Double(0, 0, border + border, border + border), floatArray, colorArray, MultipleGradientPaint.CycleMethod.NO_CYCLE));
        g.fill(new Rectangle2D.Double(0, 0, border, border));

        // Top Right
        g.setPaint(new RadialGradientPaint(new Rectangle2D.Double(w - border - border, 0, border + border, border + border), floatArray, colorArray, MultipleGradientPaint.CycleMethod.NO_CYCLE));
        g.fill(new Rectangle2D.Double(w - border, 0, border, border));

        // Bottom Left
        g.setPaint(new RadialGradientPaint(new Rectangle2D.Double(0, h - border - border, border + border, border + border), floatArray, colorArray, MultipleGradientPaint.CycleMethod.NO_CYCLE));
        g.fill(new Rectangle2D.Double(0, h - border, border, border));

        // Bottom Right
        g.setPaint(new RadialGradientPaint(new Rectangle2D.Double(w - border - border, h - border - border, border + border, border + border), floatArray, colorArray, MultipleGradientPaint.CycleMethod.NO_CYCLE));
        g.fill(new Rectangle2D.Double(w - border, h - border, border, border));
        
        
        g.dispose(); // Get rid of the Graphics object

        return output;
    }
    
    public SimulationGUI(String windowTitle, VirtualBot newRobot) {
        try {
            this.robot = newRobot;
            String s = newRobot.getImagePath();
            guiInitHelper(windowTitle, s);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    private void guiInitHelper(String windowTitle, String imgPath) throws IOException {
        BufferedImage rawChasis = ImageIO.read(new File(imgPath));
        Image drivetrain = blurImageBorder(rawChasis, 1);
        
        frame = new JFrame(windowTitle);
        frame.add(panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D)g;
                
                g2d.setColor(Color.BLUE);
                
                // Change the origin point
                g2d.translate(getWidth() / 2, getHeight() / 2);
                
                // Draw the Center point
                g.drawOval(0, 0, 3, 3);
                
                // Rotate
                //AffineTransform backup = g2d.getTransform();
                AffineTransform affineTransform = new AffineTransform();
                
                //toRadians(rotation)  1=right, 1.4 = down, 6=left
                affineTransform.rotate(Math.toRadians(rotation), robotX + 50, robotY + 50);
                g2d.setTransform(affineTransform);
                
                // Set the rendering settings
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                //Draw our image like normal

                if (drivetrain != null) g2d.drawImage(drivetrain, (int)robotX, (int)robotY, 100, 100, null);
                //g2d.drawImage(drivetrain, robotX, robotY, 100, 100, null);
                
                //Reset our graphics object so we can draw it again
                g2d.dispose();
            }
        });
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close on “close”
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen
        frame.setUndecorated(true); // Undecorated
        frame.setLocationRelativeTo(null); // Use if you know what this is
        frame.setVisible(true);

        frameWidth = frame.getWidth();
        frameHeight = frame.getHeight();
        System.out.println(frameWidth);
    }
    
    public SimulationGUI getInstance() {
        if (instance == null) instance = new SimulationGUI();
        return instance;
    }
    
    public void enable() {
        if (frame != null) frame.setVisible(true);
    }

    public void disable() {
        if (frame != null) frame.setVisible(false);
    }

    /*
     * Method for updating the position.
    */
    public void setPosition(double inputX, double inputY) {
        setPosition(inputX, inputY,0);
    }
    
    /*
     * Method for updating the position and the rotation.
    */
    public void setPosition(double inputX, double inputY, double inputAngle) {
        // Set the position
        // if (inputX > 0.9 || inputY > 0.9 || inputAngle > 0.9 && inputX != 0.000 || inputAngle != 0.000 || inputY != 0.000) {
        //     robotX += (int)(inputX*robotSpeed);
        //     robotY += (int)(inputY*robotSpeed);
        // }
        
        // DeadCode
        //rX = Math.cos(Math.toRadians(rotation));
        //rY = Math.sin(Math.toRadians(rotation));

        robot.setSpeed(20);
        robot.setPosition(inputX, inputY, inputAngle);
        robotX = robot.getX();
        robotY = robot.getY();
        rotation = robot.getAngle();


        // Set the rotation
        // rotate(inputAngle);
        
        // System.out.println("robotX: "+robotX+" robotY: "+robotY+" inputX: "+inputX+" inputY: "+inputY+" rotation: "+rotation+" input rotation: "+inputAngle);
    }
    
    public void refresh() {
        if (frame != null) {
            frame.repaint();
            panel.repaint();
        } else System.out.println("4536 Simulation Error: JFrame object not found");
	}
}
