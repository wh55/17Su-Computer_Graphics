// PointPoly.java: Point-in-Polygon test
// This program can test if a point P is inside or out of a polygon.
//
// - Every mouse click point is placed on the nearest grid point of the grid space.
// - Click on the grid space to draw a polygon, when the polygon is finished, click again to get a point P:   
//    the program will draw a horizontal half-line from P to the rightmost to do the test,
//    the intersection points of the half-line and polygon edges will be highlighted, the number of intersections
//    and the test result will be displayed on the right navigation space.
// - Drag on the grid space when P and the intersections are ready and displayed:
//    the program will reset point P along the cursor so that user can observe the variation of the results during the move.
// - User can choose to take upper or lower endpoints to do the test by clicking on the button.
// - A coordinate display switch button is also provided. 
//
// Chapter 2.9, homework3
// author: Hao WAN, Jul,2017


import java.awt.*;
import java.awt.event.*;
import java.util.*;


public class PointPoly extends Frame {
   public static void main(String[] args) {new PointPoly();}

   PointPoly() {
      super("Point-in-Polygon Test");
      addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent e) {System.exit(0);}
      });
      setSize(870, 600);
      add("Center", new CvPointPoly());
      setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
      setVisible(true);
   }
}


class CvPointPoly extends Canvas{

   Vector<Point2D> v = new Vector<Point2D>(); //polygon vertices container
   Vector<Float> vX = new Vector<Float>();    //x coordinates of intersections container
   float x0, y0, rWidth = 10.0F, rHeight = 7.5F, pixelSize;
   boolean ready = true, upperLower = true, coord = true;
   int centerX, centerY, dGrid = 10, maxX, maxY, navWidth = 200, navPaddingLeft1 = 50, navPaddingLeft2 = 10, maxX0, recX, strX, cn = 0;
   Point2D p = new Point2D(0.0F,-1.0F);


   CvPointPoly() {
      addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent evt) {
            int i=0, j=0;
            i=evt.getX(); j=evt.getY(); //get x and y coordinates of cursor

            if(i <= maxX0){ //if click on grid space

              float xA = fx(i), yA = fy(j);

               if(ready){ //ready = true, when polygon is finished or when begin
                  if(p.x==-1.0F){ //when p is not obtained yet
                     p.x = xA; p.y = yA; //then get a point P
                  }
                  else { //when p is obtained
                     v.removeAllElements(); //empty the previous polygon
                     p.x = -1.0F; //initialize point p
                     x0 = xA; y0 = yA; //take the current click as vertex 0
                     ready = false;
                  }              
               }
           
               if(!ready){ //ready = false, polygon is not finished
                  float dx = xA - x0, dy = yA - y0; //distance between 0 and vertex A, on x and y
            
                  if (v.size() > 0 && dx * dx + dy * dy < 100 * pixelSize * pixelSize){ //set a clicking range around vertex 0
                     ready = true; //if click back on vertex 0, finish the polygon
                  }  
                  else{
                     v.addElement(new Point2D(xA, yA)); //or add new vertex to the polygon
                  }

               //if right button is pressed, finish the polygon:
               if(evt.getModifiers()==InputEvent.BUTTON3_MASK){ 
                  ready = true;
               }

             }


            }

            //if click on buttons on the navi space:
            if(recX+12<=i && i<=recX+67 && centerY-70<=j && j<=centerY-50) { //UPPER button
               upperLower = true;
            }

            if(recX+12<=i && i<=recX+67 && centerY-30<=j && j<=centerY-10) { //LOWER button
               upperLower = false;
            }

            if(recX<=i && i<=recX+83 && centerY+10<=j && j<=centerY+30) { //coordinate OFF button
               coord = false;
            }

            if(recX<=i && i<=recX+83 && centerY+50<=j && j<=centerY+70) { //coordinate ON button
               coord = true;
            }

            repaint();
         }

      }); //addMouseListener end


      addMouseMotionListener(new MouseAdapter() {
         public void mouseDragged(MouseEvent evt) {

            int i=evt.getX(), j=evt.getY();

            if(p.x!=-1 && i<=maxX0){ //drag on grid space only, and when P is obtained
               p.x = fx(i);
               p.y = fy(j);
            }

            repaint();
         }

      }); //addMouseMotionListener end

   }


   class Point2D {
      float x, y;
      Point2D(float x, float y) {this.x = x; this.y = y;}
   }


   class Tools2D {

      float area2(Point2D a, Point2D b, Point2D c) {
         return (fx2(a.x) - fx2(c.x))*(fy2(b.y) - fy2(c.y)) - (fy2(a.y) - fy2(c.y))*(fx2(b.x) - fx2(c.x));
      }
     
      //count the number of intersections and calculate their coordinates:
      void interCount(Point2D p, Vector<Point2D> v){

         int n = v.size();
         Point2D a = (Point2D) (v.elementAt(0)); //vertex 0
         cn = 0; //initialize
         vX.removeAllElements(); //initialize

         if(upperLower == true){ //take the upper endpoints
            for (int i=1; i<=n; i++){ 
               Point2D b = (Point2D) (v.elementAt(i%n)); //loop from vertex 1 to n-1, then when i=n, we go back to vertex 0
               if((fy2(a.y) < fy2(p.y) && fy2(p.y) <= fy2(b.y) && area2(a, b, p)>0)||(fy2(b.y) < fy2(p.y) && fy2(p.y) <= fy2(a.y) && area2(b, a, p)>0)){
                  cn++; //cn is the count number. area2 value 0 won't count (if P on edge or on vertex, P considered outside) 
                  float x = ((fy2(p.y) - fy2(a.y)) * (fx2(b.x) - fx2(a.x)))/(fy2(b.y)-fy2(a.y)) + fx2(a.x); //x coordinate of intersection, logical
                  vX.addElement(x); //then store it in vector vX
               }     
               a=b; //move to next line segment
            }
         }
         else{ //take the lower endpoints
            for (int i=1; i<=n; i++){ 
               Point2D b = (Point2D) (v.elementAt(i%n)); 
               if((fy2(a.y) <= fy2(p.y) && fy2(p.y) < fy2(b.y) && area2(a, b, p)>0)||(fy2(b.y) <= fy2(p.y) && fy2(p.y) < fy2(a.y) && area2(b, a, p)>0)){
                  cn++; 
                  float x = ((fy2(p.y) - fy2(a.y)) * (fx2(b.x) - fx2(a.x)))/(fy2(b.y)-fy2(a.y)) + fx2(a.x);
                  vX.addElement(x); 
               }     
               a=b; 
            }
         }
      }
  
   }

   

   void initgr() {
      Dimension d = getSize();
      maxX = d.width - 1; maxY = d.height - 1;
      maxX0 = maxX - navWidth;
      pixelSize = Math.max(rWidth / maxX0, rHeight / maxY);
      centerX = maxX0 / 2; centerY = maxY / 2;     
      recX = maxX0 + navPaddingLeft1;
      strX = maxX0 + navPaddingLeft2;
   }


   int iX(float x) {return Math.round(centerX + x / pixelSize);} //logical to device, on x
   int iY(float y) {return Math.round(centerY - y / pixelSize);} //logical to device, on y
   float fx(int x) {return (x - centerX) * pixelSize;} //device or grid to logical, on x
   float fy(int y) {return (centerY - y) * pixelSize;} //device or grid to logcial, on y

   int iGrid(int x) { //device to grid, because we place mouse click on the nearest grid point
      int m = x%dGrid; 
      if (m >= dGrid/2) //if in the middle, go to right or bottom   
         return x + dGrid - m;
      else
         return x - m;
   }

   int iGx(float x) { return iGrid(iX(x));}  //logical to grid display, on x
   int iGy(float y) { return iGrid(iY(y));}  //logical to grid dispaly, on y

   //logical of mouse click(float x) -> device(int iX) -> grid display(int iGrid) -> logical coordinate(float fx2):
   //for Tools2D use only, we don't utilize the mouse click coordinate but the grid display coordinate to calculate intersections
   float fx2(float x) {return fx(iGx(x));} 
   float fy2(float y) {return fy(iGy(y));}


   void showGrid(Graphics g) { //draw grid 10x10
      g.setColor(new Color(70, 210, 240)); //a light blue color
      for (int x=dGrid; x<=maxX0; x+=dGrid){
         g.drawLine(x, 0, x, maxY);
      }
      for (int y=dGrid; y<=maxY; y+=dGrid){
         g.drawLine(0, y, maxX0, y);
      }
      g.setColor(Color.gray);
      g.drawLine(maxX0, 0, maxX0, maxY); //the separating line between grid and navi space
   }
                                                            

   void drawTinyRec(Graphics g, Point2D a) { //draw the tiny rectangle of vertex 0
      g.drawRect(iGx(a.x) - 4, iGy(a.y) - 4, 8, 8); 
   }

  
   void showNavi(Graphics g) { //the navigation space on the right of the canvas

      //bottons:
      g.drawRect(recX+12, centerY - 70, 55, 20);
      g.drawString("UPPER", recX+17, centerY -54);
      g.drawRect(recX+12, centerY - 30, 55, 20);
      g.drawString("LOWER", recX+17, centerY -14);
      g.drawRect(recX, centerY + 10, 83, 20);
      g.drawString("COORD OFF", recX+5, centerY + 26);
      g.drawRect(recX, centerY + 50, 83, 20);
      g.drawString("COORD ON", recX+5, centerY + 66);

      //when one of the upperlower buttons is clicked:
      if(upperLower==true){
         g.setColor(Color.black);
         g.drawString("UPPER endpoints selected", strX, 30);
         g.drawRect(recX+12, centerY - 70, 55, 20);
         g.drawString("UPPER", recX+17, centerY -54);

      }
      else{
         g.setColor(Color.black);
         g.drawString("LOWER endpoints selected", strX, 30);
         g.drawRect(recX+12, centerY - 30, 55, 20);
         g.drawString("LOWER", recX+17, centerY -14);
      }

      //when one of the coordi buttons is clicked:
      if(coord==false){
         g.setColor(Color.black);
         g.drawString("coordinates OFF", strX, 50);
         g.drawRect(recX, centerY + 10, 83, 20);
         g.drawString("COORD OFF", recX+5, centerY + 26);
      }
      else{
         g.setColor(Color.black);
         g.drawString("coordinates ON", strX, 50);
         g.drawRect(recX, centerY + 50, 83, 20);
         g.drawString("COORD ON", recX+5, centerY + 66);
      }

      //write some simple instructions for users:
      g.drawString("mouse on grid space:", strX, centerY + 110);
      g.setColor(Color.red);
      g.drawString("CLICK (PRESS-AND-RELEASE)", strX, centerY + 130);
      g.setColor(Color.black);
      g.drawString("To set polygon vertices", strX + 10, centerY + 150);
      g.drawString("and point P", strX + 10, centerY + 170);
      g.setColor(Color.red);
      g.drawString("DRAG (PRESS-AND-HOLD)", strX, centerY + 190);
      g.setColor(Color.black);
      g.drawString("To reset point P", strX + 10, centerY + 210);
      g.drawString("and view the variation", strX + 10, centerY + 230);

   }


   public void paint(Graphics g) {

      Tools2D tools2D = new Tools2D(); //new instance of Tools2D

      initgr(); 
     
      showGrid(g); //draw grid
      showNavi(g); //draw consistent navigation contents at the right


      //draw polygon:
      g.setColor(Color.black);
      int n = v.size(); //get number of vertices of the polygon

      if (n == 0)
         return;

      Point2D a = (Point2D) (v.elementAt(0)); //get first vertex

      drawTinyRec(g,a); //draw tiny rectangle around first vertex
      
      for (int i = 1; i <= n; i++) { //draw each edges
         if (i == n && !ready)
            break;
         Point2D b = (Point2D) (v.elementAt(i % n)); //vertex n is actually vertex 0
         g.drawLine(iGx(a.x), iGy(a.y), iGx(b.x), iGy(b.y)); //draw edge a-b       
         a = b; //move to next vertex
         
         if(coord==true) //show coordinates of vertices
            g.drawString(""+(i%n)+" ("+iGx(b.x)+","+iGy(b.y)+")", iGx(b.x), iGy(b.y));
         //g.drawString(""+(i%n)+": ("+iX(b.x)+","+iY(b.y)+"); ("+iGx(b.x)+","+iGy(b.y)+")", iGx(b.x), iGy(b.y)); //test use
      }

      //draw P and the half-line etc.:     
      if(p.x!=-1.0F){ //make sure P is pressed and stored
         tools2D.interCount(p,v); //count and calculate intersection

         g.setColor(Color.red);
         g.drawLine(iGx(p.x), iGy(p.y), maxX0, iGy(p.y)); //draw a red half-line from p to rightmost
         g.fillOval(iGx(p.x)-4, iGy(p.y)-4, 8, 8); //draw the point P
         
         if(coord==true) //show coordinates of P
            g.drawString("P"+" ("+iGx(p.x)+","+iGy(p.y)+")",iX(p.x)-15, iY(p.y)-5);
         //g.drawString("P"+": ("+iX(p.x)+","+iY(p.y)+"); ("+iGx(p.x)+","+iGy(p.y)+")",iX(p.x)-10, iY(p.y)-10); //test use
         
         
         g.drawString("intersection count: "+cn, strX, 70);//show intersection count on navi space
         
         if(cn%2==1){ //show if P is inside or outside of the polygon on navi space
            g.drawString("P is inside.", strX, 90);
         }
         else{
            g.drawString("P is outside.", strX, 90);
         }
         
         for(int i=0; i<cn; i++){ //draw intersection point(s)
            int x = iX(vX.elementAt(i)), y = iGy(p.y);
            g.setColor(Color.blue);
            g.fillOval(x-4, y-4, 8, 8);
            if(coord==true)
               g.drawString("("+x+","+y+")",x-15, y-5);
         } //x coordinate drawn on canvas is the actual device coordinate, not the grid one

         //test use:
         /*
         Point2D a0 = (Point2D)(v.elementAt(0));
         Point2D a1 = (Point2D)(v.elementAt(1));
         Point2D a2 = (Point2D)(v.elementAt(2));
         float area1 = tools2D.area2(a0,a1,p);
         float area2 = tools2D.area2(a0,a2,p); 
         float co0x = fx2(a0.x);
         float co0y = fy2(a0.y);
         float co1x = fx2(a1.x);
         float co1y = fy2(a1.y);        
         g.drawString("intersection count: "+cn+" area1: "+area1+" area2: "+area2, 200, 60); //check area value
         g.drawString("a0.x: "+a0.x+" a0.y: "+a0.y+" a1.x: "+a1.x+ " a1.y: "+a1.y, 200, 80); //acutal clicking coordi, logical
         g.drawString("fx2a0: "+co0x+" fy2a0: "+co0y+" fx2a1: "+co1x+ " fy2a1: "+co1y, 200, 100); //grid to logical
         */        
      }


      
   }

}



