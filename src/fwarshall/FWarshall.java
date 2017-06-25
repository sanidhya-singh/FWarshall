/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fwarshall;

//Imported packages
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;


/*****************************************************************************
 *                     JAVA Program to implement Floyd Warshall              *
 *                      Author : Sanidhya Pratap Singh                       *
 *                             SNU ID : AAA0403                              *                                                                         
 *****************************************************************************/


public class FWarshall extends JFrame implements ActionListener {
    
    /**
     * @param args the command line arguments
     */
    static boolean dragged = false;                         //boolean variable to check if the mouse is being dragged
    Point pStart;                                           //global variables which stores
    LinkedList nodePoints = new LinkedList();               //List containing the center coordinates of the nodes
    ArrayList<Point> linePoints = new ArrayList<>();        //List containing the starting and ending coodinates of all the edges
    LinkedList weights = new LinkedList<>();                //List containing the weights of corresponding edges
    private JButton finish = new JButton("Finish");
    private JButton compute = new JButton("Compute");
    private FirstPanel clickPanel = new FirstPanel();       //Object of the FirstPanel class, creates the clickable panel
    static int[][] arr;                                     //Adjaceny Matrix
    static int[][][] stepWise;                              //3D array containing all the steps of the Floyd-Warshall algorithm
    Font font = new Font("Verdana", Font.PLAIN, 12);
    
    public FWarshall() {
        //Constructor for the main class
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //clcikPanel directly added to the frame
        add(clickPanel, BorderLayout.CENTER);
        
        //Panel containin the Finish and Compute buttons
        JPanel p = new JPanel();
        compute.setEnabled(false);
        p.add(finish, BorderLayout.WEST);
        p.add(compute, BorderLayout.EAST);
        add(p, BorderLayout.SOUTH);
        
        //Setting the color scheme
        finish.setForeground(Color.yellow);
        finish.setBackground(Color.black);
        compute.setForeground(Color.yellow);
        compute.setBackground(Color.black);
        
        //adding ActionListeners
        finish.addActionListener(this);
        compute.addActionListener(this);
        setLocationRelativeTo(null);
        setTitle("1st Panel");
        pack();
    }
    public static void main(String[] args) {
        // TODO code application logic here
        new FWarshall();
    }

    public void fixMatrix() {
        //Function to make all i not equal to j 0 values in the adjacency matrix as 99999
        for(int i=0; i<arr.length; i++) {
            for(int j=0; j<arr[i].length; j++) {
                if(i != j && arr[i][j] == 0)
                    arr[i][j] = 99999;
            }
        }
    }
    
    public void FloydWarshall() {
            //Function to implement the Floyd-Warshall algorithm
            //Stores each step in a 3D array
            int c = 0;
            int k = 0;
            
            //Setting the first step as the adjaceny matrix
            for(int i=0; i<arr.length; i++) {
                for(int j=0; j<arr.length; j++) {
                    stepWise[c][i][j] = arr[i][j];
                }
            }
            c++;
            
            //Floyd-Warshall
	    for (k=0; k<arr.length; k++)
	    {   for (int i=0; i<arr.length; i++)
	        {   for (int j=0; j<arr.length; j++)
                    {	if ((arr[i][k]*arr[k][j]!=0) && (i!=j))
	             	{   if ((arr[i][k] + arr[k][j] < arr[i][j]) || (arr[i][j] == 0))
                                 arr[i][j] = arr[i][k] + arr[k][j];                            
                        }
                        stepWise[c][i][j] = arr[i][j];
                    }                    
	        }
                c++;
	    }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
            if(e.getSource() == finish) {
                //Enable the compute button
                compute.setEnabled(true);
                
                //removing listeners from the clickable panel
                clickPanel.removeMouseListener(clickPanel);
                clickPanel.removeMouseMotionListener(clickPanel);
            }
            if(e.getSource() == compute) {
                try {
                    determineMatrix();              //Determining the adjacency matrix
                    fixMatrix();                    //Set 0 values to 99999
                    FloydWarshall();                //performing Floyd-Warshall
                    new SecondPanel();              //Creating SecondPanel object
                    dispose();                      //disposing current object
                } 
                catch (Exception ex) {    JOptionPane.showMessageDialog(null, "No graph");                  //Handle exception where there are no edges 
                                            clickPanel.addMouseListener(clickPanel);
                                            clickPanel.addMouseMotionListener(clickPanel);
                                            compute.setEnabled(false);
                }
            }
    }
    
    public void determineMatrix() {
        //Function to determine the adjacency matrix 
        if(!linePoints.isEmpty()) {
            
            //initializing the arrays
            arr = new int[nodePoints.size()][nodePoints.size()];
            stepWise = new int[nodePoints.size()+1][nodePoints.size()][nodePoints.size()];
            
            //determining matrix
            for(int i=0; i<linePoints.size(); i += 2) {
                //Get a point from the vertex center list and find its vertex
                Point p = linePoints.get(i);
                int x = searchPoint(p);               
                Point p1 = linePoints.get(i+1);
                int y = searchPoint(p1);
                
                //set adjacency matrix
                if(i == 0)
                    arr[x-1][y-1] = (Integer)weights.get(i); 
                else 
                    arr[x-1][y-1] = (Integer)weights.get(i/2); 
            }
        }
    }
    public int searchPoint(Point p) {
        //Function to determine the vertex of passed point 
        for(int i=0; i<nodePoints.size(); i++) {
            Point temp = (Point)nodePoints.get(i);
            //Check if point is inside the area of the vertex
            if(p.x > temp.x - 20 && p.x < temp.x + 20 && p.y > temp.y - 20 && p.y < temp.y + 20)
                return i+1;
        }
        return -1;        
    }
    
    public boolean pointRight(Point p) {
        //Function to check if the passed point lies within a vertex
        for(int i=0; i<nodePoints.size(); i++) {
            //Check if point is inside the area of the vertex
            Point temp = (Point)nodePoints.get(i);
            if(p.x > temp.x - 20 && p.x < temp.x + 20 && p.y > temp.y - 20 && p.y < temp.y + 20)
                return true;
        }
        return false;
    }
    
    public boolean nodeRight(Point p) {
        //Function to check if the possible vertex lies within an already existing vertex
        for(int i=0; i<nodePoints.size(); i++) {
            Point temp = (Point)nodePoints.get(i);
            //Check if point is inside the area of the vertex
            if(p.x > temp.x - 40 && p.x < temp.x + 40 && p.y > temp.y - 40 && p.y < temp.y + 40)
                return false;
        }
        return true;
    }
    
    
    class FirstPanel extends JPanel implements MouseListener, MouseMotionListener {
        //Class to create the clickable panel to create the graph
        
        public FirstPanel() {
            //Create GUI for clickable panel
            setVisible(true);
            //setBackground(Color.white);
            addMouseListener(this);
            addMouseMotionListener(this);
            setPreferredSize(new Dimension(500,400));
            setBackground(Color.black);
        }
        
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);        
            
            //Iterate through list containing the vertex centers and draw them
            if(!nodePoints.isEmpty()) {
                for(int i=0; i<nodePoints.size(); i++) {
                    Point temp = (Point)nodePoints.get(i);
                    g.setColor(Color.yellow);
                    g.fillOval(temp.x - 20, temp.y - 20, 40, 40);
                    g.setColor(Color.black);
                    g.drawString(String.valueOf(i+1), temp.x - 4, temp.y + 5);
                }
            }
            
            //Iterate through list containing the line coordinates and draw them
            if(!linePoints.isEmpty()) {
                for(int i=0, k=0; i<linePoints.size(); i=i+2, k++) {
                    Point pTemp = linePoints.get(i);
                    Point pTemp2 = linePoints.get(i+1);
                    g.setColor(Color.red);
                    g.drawLine(pTemp.x, pTemp.y, pTemp2.x, pTemp2.y);
                    g.setColor(Color.white);
                    g.drawString(String.valueOf(weights.get(k)), (pTemp.x + pTemp2.x)/2, (pTemp.y + pTemp2.y)/2);
                    g.setColor(Color.red);
                    g.fillOval(pTemp2.x - 2, pTemp2.y - 2, 4, 4);
                }
            }
        }
        
        @Override
        public void mouseClicked(MouseEvent e) {
            //Draw vertex on mouseClick and add center to the list
            Point p = e.getPoint();
            if(nodeRight(p)) {
                nodePoints.add(p);
                repaint();
            }
            else 
                JOptionPane.showMessageDialog(null, "Cannot draw vertex here");
                //If it is not possible to draw vertex here, alert user
        }

        @Override
        public void mousePressed(MouseEvent e) {
            
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            //Storing the location when the mouse was released
            //Storing the location only if it starts and ends in a vertex
            if(dragged) {                
                //Get location of the mouse pointer
                Point p = e.getPoint();
                if(pointRight(pStart) && pointRight(p)) {
                    linePoints.add(pStart);
                    linePoints.add(p);
                    String str = JOptionPane.showInputDialog(null, "Enter weight");
                    weights.add(Integer.parseInt(str));
                    dragged = false;
                    repaint();
                }
                else {
                    JOptionPane.showMessageDialog(null, "Cannot draw edge here");
                    dragged = false;
                }
            }       
        }

        @Override
        public void mouseEntered(MouseEvent e) {
           
        }

        @Override
        public void mouseExited(MouseEvent e) {
         
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            //Stores the first point from where the mouse is dragged
            //ignores subsequent drag points
            if(!dragged) {   
                pStart = e.getPoint();
                dragged = true;
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        
        }        
    }
    
    class SecondPanel extends JFrame implements ActionListener {
        private FirstPanel displayPanel = new FirstPanel();                     //Adding draph panel to the 2ndPanel
        private JPanel buttonMat = new JPanel();                                //Button array to display adjacency matrix
        private JPanel topPanel = new JPanel();
        private JTextArea area = new JTextArea(10,10);   
        private JButton back = new JButton("Back");
        public SecondPanel() {
            //SecondPanel constructor
            //Addding the text area to a scroll pane
            JScrollPane scroll = new JScrollPane(area);
            scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
            
            //Creating the Adjacency Matrix buttons
            buttonMat.setLayout(new GridLayout(arr.length,arr.length));
            for(int i=0; i<arr.length; i++) {
                for(int j=0; j<arr[i].length; j++) {
                    JButton temp = new JButton();
                    temp.setPreferredSize(new Dimension(70,70));
                    buttonMat.add(temp);
                    temp.setForeground(Color.yellow);
                    if(stepWise[0][i][j] != 99999)
                        temp.setText(String.valueOf(stepWise[0][i][j]));
                    else
                        temp.setText("∞");
                    temp.setBackground(Color.black);
                }
            }
            
            //Top Panel containing the Graph and the Adjaceny Matrix
            add(topPanel, BorderLayout.NORTH);
            topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));            
            JLabel label = new JLabel("Adjacency Matrix\n\n");
            label.setForeground(Color.yellow);
            JPanel p = new JPanel();
            p.setLayout(new BorderLayout());
            p.setBackground(Color.black);
            p.add(buttonMat, BorderLayout.SOUTH);
            p.add(label, BorderLayout.CENTER);            
            topPanel.add(p);
            topPanel.add(displayPanel);  
            
            
            
            //Creating the GUI
            back.setForeground(Color.yellow);
            back.setBackground(Color.black);
            area.setEditable(false);
            area.setFont(font);
            area.setForeground(Color.yellow);
            area.setBackground(Color.BLACK);
            displayPanel.removeMouseListener(displayPanel);
            displayPanel.removeMouseMotionListener(displayPanel);
            
            //adding scrollpane 
            add(scroll, BorderLayout.CENTER);
            
            //adding the back button
            add(back, BorderLayout.SOUTH);
            area.setBorder(new TitledBorder("Matrices"));
            
            //adding ActionListener
            back.addActionListener(this);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setVisible(true);            
            displayMatrix();
            setLocationRelativeTo(null);
            setTitle("2nd Panel");
            pack();
        }
        
        public String computeChanges(int x) {
            //Function to find changes between 2 matrices and add the changes to a string
            //the final string is returned 
            String changes = "";
            for(int i=0; i<arr.length; i++) {
                for(int j=0; j<arr.length; j++) {
                    if(stepWise[x-1][i][j] != stepWise[x][i][j]) 
                        changes += "\nValue changed at (" + (i+1) + "," + (j+1) + ") from " + (stepWise[x-1][i][j] == 99999 ? "∞" : stepWise[x-1][i][j])  + " to " + stepWise[x][i][j]; 
                }
            }
            return changes;
        }
        
        public void displayMatrix() {
            //Function to display the matrix step wise in the text area and also display the
            //changes in value (if any)
            for(int k=1; k<stepWise.length; k++) {
                area.setText(area.getText() + "Matrix for K : " + k + "\n\n       ");
                for(int i=0; i<arr.length; i++) {
                    for(int j=0; j<arr.length; j++) {
                        if(stepWise[k][i][j] != 99999) {
                            
                            //Switch for large numbers
                            switch(String.valueOf(stepWise[k][i][j]).length()) {
                                case 1:
                                    area.setText(area.getText() + stepWise[k][i][j] + "         ");
                                    break;
                                case 2:
                                    area.setText(area.getText() + stepWise[k][i][j] + "        ");
                                    break;
                                case 3:
                                    area.setText(area.getText() + stepWise[k][i][j] + "      ");
                                    break;
                                case 4:
                                    area.setText(area.getText() + stepWise[k][i][j] + "    ");
                                    break;
                                case 5:
                                    area.setText(area.getText() + stepWise[k][i][j] + "    ");                 
                                    break;
                            }
                        }
                        else
                            area.setText(area.getText() + "∞         ");
                    }
                    area.setText(area.getText() + "\n       ");
                }
                
                //Write changes in the matrix to the text area
                area.setText(area.getText() + "\n" + computeChanges(k));
                area.setText(area.getText() + "\n\n\n");
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
           if(e.getSource() == back) {
               //Disposing current object
               //Creating new FWarshall object
               dispose();
               new FWarshall();
           }
        }
    }
}
