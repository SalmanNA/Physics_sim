import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import javax.swing.*;
public class MyPanel extends JPanel implements ActionListener {

    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final int screenWidth = screenSize.width;
    private final int screenHeight = screenSize.height-25;
    private final int width = screenWidth;
    private final int height = screenHeight;
    private boolean isMouseHeld = false; // Track mouse hold state
    private  boolean isMoving = false;
    Timer timer;
    int fps = 165;
    int frameTime = (1000/fps);
    double deltaTime = 0.0;
    long lastFrameTime = System.nanoTime();
    ArrayList<Object> Objects = new ArrayList<>();
    int mouseX = 0;
    int mouseY = 0;
    int heldIndex = -1;
    double gravity = 9810;
    MyPanel(){
        this.setPreferredSize(new Dimension(width,height));
        timer = new Timer(frameTime, this);
        timer.start();
        Objects.add(new Circle(10.0,10.0,50.0,50.0,0,0));
//        Objects.add(new Circle(60.0,10.0,50.0,50.0,0,0));
//        Objects.add(new Circle(110.0,10.0,50.0,50.0,0,0));
//        Objects.add(new Circle(160.0,10.0,50.0,50.0,0,0));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                isMouseHeld = true; // Mouse is being held down
                handleMouseClick(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isMouseHeld = false; // Mouse is released
                handleMouseRelease(e);
            }
        });

        // Add a MouseMotionListener to handle dragging while holding down the mouse
        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isMouseHeld) {
                    handleMouseDrag(e);
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                // Optional: Handle mouse movement without holding
            }
        });
        
    }
    public void paint(Graphics g){
        super.paint(g);
        Graphics2D g2D = (Graphics2D) g;
        g2D.clearRect(0,0,width,height);
        for(Object object: Objects){
            g2D.fill(new Ellipse2D.Double(object.xPos, object.yPos, object.height, object.width));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int i = 0;
        long currentFrameTime = System.nanoTime();
        deltaTime = (currentFrameTime - lastFrameTime) / 1_000_000_000.0f; // Convert nanoseconds to seconds
        lastFrameTime = currentFrameTime;
        for(Object object: Objects){
            object.xPosPrev = object.xPos;
            object.yPosPrev = object.yPos;
            if(!isMoving && (isMouseHeld && ((mouseX >= object.xPos && mouseX < object.xPos +50) && (mouseY >= object.yPos && mouseY < object.yPos +50)))){
                isMoving = true;
                object.isHeld = true;
                heldIndex = i;
            }
            if(isMoving && object.isHeld){
                object.xPos = mouseX-25;
                object.yPos = mouseY-25;
                object.hVel = 0;
                object.vVel = 0;
            }else{

                object.yPos += object.vVel * deltaTime;
                object.xPos += object.hVel * deltaTime;
                object.vVel+= gravity * deltaTime;
            }
            collisionDetect(object);
            i++;
        }
        repaint();
    }
    public void collisionDetect(Object object){
        System.out.println(deltaTime);
        if(object.yPos +50 >= height){
//            object.yPos = height-50;
            object.vVel = object.vVel*-1;
            object.hVel = object.hVel*0.75;
        }

//        if(object.xPos +50+ object.hVel* deltaTime >= width){
//            object.xPos = width-50;
//            object.hVel = object.hVel*-0.75;
//        }else if(object.xPos +50 >= width){
//            object.hVel = object.hVel*-0.75;
//        }
//        if(object.xPos + object.hVel* deltaTime <= 0){
//            object.xPos = 0;
//            object.hVel = object.hVel*-0.75;
//        }else if(object.xPos <= 0){
//            object.hVel = object.hVel*-0.75;
//        }
    }



    private void handleMouseClick(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    private void handleMouseRelease(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        isMoving = false;
        System.out.println((Objects.get(heldIndex).xPos - Objects.get(heldIndex).xPosPrev)/deltaTime);
        Objects.get(heldIndex).hVel = (Objects.get(heldIndex).xPos - Objects.get(heldIndex).xPosPrev)/deltaTime;
        Objects.get(heldIndex).vVel = (Objects.get(heldIndex).yPos - Objects.get(heldIndex).yPosPrev)/deltaTime;
        Objects.get(heldIndex).isHeld = false;
    }

    private void handleMouseDrag(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();

    }

}

//take prevVel only when moving with click
