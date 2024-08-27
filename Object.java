public class Object {
    double xPos;
    double yPos;
    double hVel;
    double vVel;
    double xPosPrev;
    double yPosPrev;
    double width;
    double height;
    boolean isHeld = false;

    Object(double xPos, double yPos, double width, double height, double hVel, double vVel){
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.height = height;
        this.hVel = hVel;
        this.vVel = vVel;
    }

}
