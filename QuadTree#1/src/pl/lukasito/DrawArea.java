package pl.lukasito;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class DrawArea extends JComponent {
    private Image image;
    private Graphics2D g2d;
    private int currentX, currentY, oldX, oldY;
    ArrayList<Point> pointss = new ArrayList<>();
    QuadTree qt;
    Rectangle rectangle;
    int capacity;
    ArrayList<Point> blackPointsdetected;

    @Override
    protected void paintComponent(Graphics g) {
        if(image == null){
            image = createImage(getSize().width,getSize().height);
            g2d = (Graphics2D) image.getGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setPaint(Color.white);
            g2d.fillRect(0,0,getSize().width,getSize().height);
            g2d.setPaint(Color.black);
            repaint();
        }else{
            g2d = (Graphics2D) image.getGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setPaint(Color.black);
            repaint();
        }
        g.drawImage(image,0,0,null);
        if(blackPointsdetected != null){
            for (int i = 0;i < blackPointsdetected.size(); i++){
                drawCenteredCircle(g2d,blackPointsdetected.get(i).getX(),blackPointsdetected.get(i).getY(),7);
            }
        }

    }

    public void clear(){
        g2d.setPaint(Color.white);
        g2d.fillRect(0,0,getSize().width,getSize().height);
        g2d.setPaint(Color.black);
        qt = null;
        this.qt = new QuadTree(rectangle,capacity);
        System.out.println("REC = " + rectangle + " capacity = " + capacity);
        pointss = null;
        pointss = new ArrayList<>();
        repaint();
    }
        //COLOR DETECTION TODO
    /*public Color whatsColor(int x, int y){
        Color color = null;
        try {
            color = new Robot().getPixelColor(x,y);
            System.out.println(color);
        }catch (AWTException e){
            e.printStackTrace();
        }
        return color;
    }

    public ArrayList<Point> blackPoints(){
        ArrayList<Point> ans = new ArrayList<>();
        try {
            for (int i = 0; i < image.getWidth(null); i++) {
                for (int j = 0; j < image.getHeight(null); j++) {
                    System.out.println(i + " " + j);
                    if (whatsColor(i,j) == Color.black && whatsColor(i,j-1) == Color.white){
                        ans.add(new Point(i,j));
                        System.out.println("DETECTED " + i + " " + j);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ans;
    }*/

    public void loadImage(){

        try {
            JFrame jFrame = new JFrame("Wczytywanie pliku");
            String getMessage = JOptionPane.showInputDialog(jFrame, "Podaj nazwe obrazka");
            Image img = ImageIO.read(new File(getMessage));
            image = img;
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void insertPoint(int x, int y){
        if(this.qt == null){
            this.qt = new QuadTree(rectangle,capacity);
        }
        Point tmp = new Point(x,y);
        this.qt.inssert(new Point(x,y));
        this.pointss.add(tmp);
    }

    public DrawArea(QuadTree qt, Rectangle rec, int cap){
        this.qt = qt;

        //IMPORTING RANDOM POINTS
        /*for (int i = 0; i < 4; i++){
            Random random = new Random();
            Point newone = new Point(random.nextInt(950) + 50 , random.nextInt(random.nextInt(950))+50);
            pointss.add(newone);
            //System.out.println(newone);
            this.qt.inssert(newone);
        }*/

        rectangle = rec;
        capacity = cap;
        setDoubleBuffered(false);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                oldX = e.getX();
                oldY = e.getY();
                insertPoint(oldX,oldY);
                //whatsColor(e.getX(),e.getY());
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                currentX = e.getX();
                currentY = e.getY();

                insertPoint(currentX,currentY);

                if(g2d != null){
                    g2d.drawLine(oldX,oldY,currentX,currentY);
                    repaint();
                    oldX = currentX;
                    oldY = currentY;
                }
            }
        });


    }

    public QuadTree getQt(){
        return this.qt;
    }

    public void drawQT(){

        g2d.setColor(Color.green);
        g2d.drawRect(qt.rectangle.x - (qt.rectangle.width), qt.rectangle.y - (qt.rectangle.height), qt.rectangle.width*2, qt.rectangle.height*2);
        if(qt.divided) {
            qt.northwest.drawMe(g2d);
            qt.northeast.drawMe(g2d);
            qt.southwest.drawMe(g2d);
            qt.southeast.drawMe(g2d);
        }
        g2d.setColor(Color.green);
    }

    /*public void detect(){
        blackPointsdetected = blackPoints();
    }*/

    public void printQT(){
        System.out.println("PRINTING QT "+qt);
        if(image != null)
        System.out.println("PICTURE WIDTH: " + image.getWidth(null) + "\n" + "PICTURE HEIGHT: " + image.getHeight(null));
    }

    public void saveImage(){
        try{
            JFrame jFrame = new JFrame("Zapisywanie pliku");
            String getMessage = JOptionPane.showInputDialog(jFrame, "Podaj nazwe obrazka");
            BufferedImage bi = toBufferedImage(image);
            File outputfile = new File(new String(getMessage+".png"));
            ImageIO.write(bi,"png",outputfile);
        }catch (IOException e){
            System.out.println(e);
        }
    }
    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    public void drawCenteredCircle(Graphics2D g, int x, int y, int r) {
        g.setPaint(Color.RED);
        x = x-(r/2);
        y = y-(r/2);
        g.fillOval(x,y,r,r);
        g.setPaint(Color.BLACK);
    }
}
